package alexthw.eidolon_repraised.api.deity;

import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.research.Research;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import alexthw.eidolon_repraised.util.RGBProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static alexthw.eidolon_repraised.Eidolon.prefix;

public abstract class Deity implements RGBProvider {
    protected final ResourceLocation id;
    final int red;
    final int green;
    final int blue;

    public Progression getProgression() {
        return progression;
    }

    protected final Progression progression;

    public Deity(ResourceLocation id, int red, int green, int blue) {
        this.id = id;
        this.red = red;
        this.green = green;
        this.blue = blue;
        progression = new Progression(new Stage(prefix("start"), 0, true));
    }


    public float getRed() {
        return red / 255.0f;
    }

    public float getGreen() {
        return green / 255.0f;
    }

    public float getBlue() {
        return blue / 255.0f;
    }

    public ResourceLocation getId() {
        return id;
    }

    public interface StageRequirement {
        boolean isMet(Player player);
    }

    public static class ResearchRequirement implements StageRequirement {
        final ResourceLocation r;

        public ResearchRequirement(Research r) {
            this.r = r.getRegistryName();
        }

        public ResearchRequirement(ResourceLocation r) {
            this.r = r;
        }

        @Override
        public boolean isMet(Player player) {
            return KnowledgeUtil.knowsResearch(player, r);
        }
    }

    public static class SignRequirement implements StageRequirement {
        final Sign sign;

        public SignRequirement(Sign sign) {
            this.sign = sign;
        }

        @Override
        public boolean isMet(Player player) {
            return KnowledgeUtil.knowsSign(player, sign);
        }
    }

    public record Stage(ResourceLocation id, int rep, boolean major, List<StageRequirement> reqs) {

        public Stage(ResourceLocation id, int rep, boolean major) {
            this(id, rep, major, new ArrayList<>());
        }

        public Stage requirement(StageRequirement req) {
            reqs.add(req);
            return this;
        }

        boolean satisfiedBy(Player player) {
            boolean satisfied = true;
            for (StageRequirement req : reqs) {
                if (!req.isMet(player)) {
                    satisfied = false;
                    break;
                }
            }
            return satisfied;
        }
    }

    public class Progression {
        public TreeMap<Integer, Stage> getSteps() {
            return steps;
        }

        final TreeMap<Integer, Stage> steps;

        public void setMax(int max) {
            steps.put(max, new Stage(prefix("end"), max, true));
            this.max = max;
        }

        public int max;

        public Progression(Stage... stages) {
            this.steps = new TreeMap<>();
            for (Stage s : stages) this.steps.put(s.rep, s);
            max = this.steps.lastKey();
        }

        public Progression add(Stage stage) {
            steps.put(stage.rep, stage);
            max = this.steps.lastKey();
            return this;
        }

        public Stage next(double rep) {
            return steps.ceilingEntry(Mth.clamp((int) (rep + 0.5f), 0, max)).getValue();
        }

        public Stage last(double rep) {
            return steps.floorEntry((int) rep).getValue();
        }

        public Stage prev(double rep) {
            return steps.floorEntry(steps.floorKey((int) Math.max(rep - 0.5F, 0F))).getValue();
        }


        public Stage tryProgress(IReputation rep, Player player, double prev, double current) {
            if (current >= max) return null; // Can't progress past max.
            Stage s = next(prev == 0 ? 1 : prev); //get the current stage
            if (current > s.rep) { // we have completed this stage
                if (s.satisfiedBy(player)) { // current stage requirements are satisfied
                    Stage next = next(s.rep + 1); //next stage and clamp rep to next stage to avoid skipping
                    rep.setReputation(Deity.this.getId(), Math.min(current, next.rep));
                    return next;
                } else { // we have not satisfied the requirements yet, so cap the rep to the limit of the current stage
                    rep.setReputation(Deity.this.getId(), s.rep);
                }
            }
            return s;
        }

        public Stage tryRegress(IReputation rep, Player player, double prev, double current) {
            if (current <= 0) return null; // Can't regress past min.
            Stage s = last(prev); //get the current stage
            if (current < s.rep) { // we have dropped below this stage
                Stage previous = prev(s.rep - 1);
                rep.setReputation(Deity.this.getId(), Math.min(current, previous.rep));
                return previous;
            }
            return s;
        }

        public void regress(IReputation rep, Player player) {
            double level = rep.getReputation(Deity.this.getId());
            Stage s = prev(level);
            rep.setReputation(Deity.this.getId(), Math.min(level, s.rep));
        }
    }

    /**
     * Called when a player's reputation with this deity changes, but before the change is applied.
     *
     * @param player  Player whose reputation is changing
     * @param rep     The IReputation capability of the player
     * @param prev    The previous reputation value
     * @param updated The updated reputation value
     * @return true to allow the change, false to cancel it (ex. if an event cancels it or the max is reached)
     */
    public boolean onReputationChange(Player player, IReputation rep, double prev, double updated) {

        if (NeoForge.EVENT_BUS.post(new ReputationEvent.Change(this, player, prev, updated)).isCanceled())
            return false;

        if (updated < prev) {
            // Handle regression
            Stage s = progression.tryRegress(rep, player, prev, updated);
            Stage currStage = progression.last(prev);
            // No stage change, simply allow the rep change
            return s != null && currStage == s;
        }

        // Fetch the next and current stages, if any
        Stage nextStage = progression.tryProgress(rep, player, prev, updated);
        Stage currStage = progression.next(prev == 0 ? 1 : prev);
        // We maxed out, just set to max and return
        if (nextStage == null) {
            rep.setReputation(id, progression.max);
            return false; // Reputation change handled internally
        }

        // We can let the player advance a stage
        if (nextStage.rep > currStage.rep) {
            if (NeoForge.EVENT_BUS.post(new ReputationEvent.Unlock(this, player, nextStage)).isCanceled()) {
                rep.setReputation(id, currStage.rep); // Revert to current stage rep
                return false;
            }
            // Grant the new stage
            rep.unlock(player, id, nextStage.id());
            onReputationUnlock(player, currStage.id());
            // Update reputation, clamping to the next stage rep to avoid skipping stages
            rep.setReputation(id, Math.min(updated, nextStage.rep));
        }

        double curr = rep.getReputation(getId()); //update after we may have changed it

        // If the cap was reached then we need to lock the stage and grant the knowledge for the next stage
        if (curr == nextStage.rep() && updated != curr) {
            if (!NeoForge.EVENT_BUS.post(new ReputationEvent.Lock(this, player, currStage)).isCanceled()) {
                rep.lock(player, id, currStage.id());
                onReputationLock(player, currStage.id());
            }
        }

        return true;
    }

    public abstract void onReputationUnlock(Player player, ResourceLocation lock);

    public abstract void onReputationLock(Player player, ResourceLocation lock);
}
