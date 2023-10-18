package elucent.eidolon.registries;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.research.Research;
import elucent.eidolon.api.research.ResearchTask;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static elucent.eidolon.api.research.ResearchTask.TaskItems.fromTag;
import static elucent.eidolon.datagen.EidItemTagProvider.SCRIBE_ITEMS;
import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class Researches {
    static final Map<ResourceLocation, Research> researches = new HashMap<>();
    static final Multimap<ResourceLocation, Research> blockResearches = HashMultimap.create();
    static final Multimap<ResourceLocation, Research> entityResearches = HashMultimap.create();
    static final List<Function<Random, ResearchTask>> taskPool = new ArrayList<>();

    public static Research register(Research r, Object... sources) {
        researches.put(r.getRegistryName(), r);
        for (Object o : sources) {
            if (o instanceof Block b) {
                blockResearches.put(getRegistryName(b), r);
            } else if (o instanceof EntityType<?> e) {
                entityResearches.put(getRegistryName(e), r);
            }
        }
        return r;
    }

    public static void addTask(Function<Random, ResearchTask> task) {
        taskPool.add(task);
    }

    public static @NotNull Collection<Research> getBlockResearches(Block b) {
        return blockResearches.get(getRegistryName(b));
    }

    public static @NotNull Collection<Research> getEntityResearches(Entity e) {
        return entityResearches.get(getRegistryName(e.getType()));
    }

    public static Collection<Research> getResearches() {
        return researches.values();
    }

    @Nullable
    public static Research find(ResourceLocation location) {
        return researches.getOrDefault(location, null);
    }

    public static ResearchTask getRandomTask(Random random) {
        return taskPool.get(random.nextInt(taskPool.size())).apply(random);
    }

    public static Research FIRE_SPELL;
    public static Research FROST_SPELL;


    public static void init() {
        addTask(fromTag(SCRIBE_ITEMS, 3));
        addTask(fromTag(SCRIBE_ITEMS, 3));
        addTask(fromTag(SCRIBE_ITEMS, 3));
        addTask(ResearchTask.XP::new);
        addTask(ResearchTask.XP::new);


        register(new Research(new ResourceLocation(Eidolon.MODID, "gluttony"), 3), EntityType.PIG);
        FIRE_SPELL = register(new Research(new ResourceLocation(Eidolon.MODID, "flames"), 5) {
            @Override
            public void onLearned(ServerPlayer serverPlayer) {
                KnowledgeUtil.grantSign(serverPlayer, Signs.FLAME_SIGN);
            }
        }.addSpecialTasks(5, new ResearchTask.TaskItems(new ItemStack(Items.BLAZE_ROD, 3)),
                new ResearchTask.TaskItems(new ItemStack(Items.FIRE_CHARGE, 3)),
                new ResearchTask.XP(6)
        ), EntityType.BLAZE);

        FROST_SPELL = register(new Research(new ResourceLocation(Eidolon.MODID, "frost"), 5) {
            @Override
            public void onLearned(ServerPlayer serverPlayer) {
                KnowledgeUtil.grantSign(serverPlayer, Signs.WINTER_SIGN);
            }
        }.addSpecialTasks(4, new ResearchTask.TaskItems(new ItemStack(Items.ICE, 10)),
                new ResearchTask.TaskItems(new ItemStack(Items.SNOW, 10)),
                new ResearchTask.XP(6)
        ).addSpecialTasks(5, new ResearchTask.TaskItems(new ItemStack(Registry.WRAITH_HEART.get(), 3))
        ), EntityType.STRAY, EidolonEntities.WRAITH.get());
    }

}
