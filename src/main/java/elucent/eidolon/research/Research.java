package elucent.eidolon.research;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class Research {
    final ResourceLocation rl;
    final int stars;
    final Map<Integer, List<ResearchTask>> specialTasks = new HashMap<>();

    public Research(ResourceLocation rl, int stars) {
        this.rl = rl;
        this.stars = stars;
        if (stars <= 0) throw new IllegalArgumentException("Research difficulty must be at least one star!");
        if (stars > 10) throw new IllegalArgumentException("Research difficulty cannot exceed ten stars!");
    }

    public Research addSpecialTasks(int step, ResearchTask... tasks) {
        specialTasks.put(step, List.of(tasks));
        return this;
    }

    public ResourceLocation getRegistryName() {
        return rl;
    }

    public int getStars() {
        return stars;
    }

    public String getName() {
        return I18n.get("research." + rl.getNamespace() + "." + rl.getPath());
    }

    public List<ResearchTask> getTasks(int rootSeed, int done) {
        if (specialTasks.containsKey(done)) return specialTasks.get(done);
        List<ResearchTask> tasks = new ArrayList<>();
        int seed = getSeed(rootSeed, done);
        Random random = new Random(seed);
        for (int i = 0; i < 3; i++) {
            tasks.add(Researches.getRandomTask(random));
        }
        return tasks;
    }

    public int getSeed(int rootSeed, int done) {
        return rl.hashCode() * 384780223 ^ done * 844955129 ^ rootSeed * 112041199 + 6;
    }

    public void onLearned(ServerPlayer serverPlayer) {
    }
}
