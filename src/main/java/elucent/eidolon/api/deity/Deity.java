package elucent.eidolon.api.deity;

import elucent.eidolon.api.research.Research;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.util.KnowledgeUtil;
import elucent.eidolon.util.RGBProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static elucent.eidolon.Eidolon.prefix;

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
            Stage s = next(prev);
            if (current > s.rep) {
                if (s.satisfiedBy(player)) return next(current);
                else {
                    rep.setReputation(player.getUUID(), Deity.this.getId(), s.rep);
                }
            }
            return s;
        }

        public void regress(IReputation rep, Player player) {
            double level = rep.getReputation(player, Deity.this.getId());
            Stage s = prev(level);
            rep.setReputation(player, Deity.this.getId(), Math.min(level, s.rep));
        }
    }

    public void onReputationChange(Player player, IReputation rep, double prev, double updated) {

        Stage nextStage = progression.tryProgress(rep, player, prev, updated);
        Stage currStage = progression.next(prev);
        //we maxed out
        if (nextStage == null) {
            rep.setReputation(player.getUUID(), id, progression.max);
            return;
        }
        //we advanced a stage
        if (nextStage.rep > currStage.rep) {
            onReputationUnlock(player, rep, currStage.id());
        }
        double curr = rep.getReputation(player, getId()); //update after we may have changed it

        //we didn't advance a stage, if the cap was reached then we need to grant the next step
        if (curr == nextStage.rep() && updated != curr) {
            onReputationLock(player, rep, currStage.id());
        }
    }

    public abstract void onReputationUnlock(Player player, IReputation rep, ResourceLocation lock);

    public abstract void onReputationLock(Player player, IReputation rep, ResourceLocation lock);
}
