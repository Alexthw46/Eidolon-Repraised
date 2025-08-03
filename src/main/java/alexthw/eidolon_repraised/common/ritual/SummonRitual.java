package alexthw.eidolon_repraised.common.ritual;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.network.CrystallizeEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.event.EventHooks;

public class SummonRitual extends Ritual {
    public static final ResourceLocation SYMBOL = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"particle/summon_ritual" );

    public EntityType<?> getEntityType() {
        return entity;
    }

    final EntityType<?> entity;

    public int getCount() {
        return count;
    }

    final int count;

    public SummonRitual(EntityType<?> entity) {
        super(SYMBOL, ColorUtil.packColor(255, 121, 94, 255));
        this.entity = entity;
        this.count = 1;
    }

    public SummonRitual(EntityType<?> entity, int count) {
        super(SYMBOL, ColorUtil.packColor(255, 121, 94, 255));
        this.entity = entity;
        this.count = count;
    }

    @Override
    public Component getName() {
        return Component.translatable(Eidolon.MODID + ".ritual" + ".summon", getEntityType().getDescription());
    }

    @Override
    public Ritual cloneRitual() {
        return new SummonRitual(entity, count);
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            Networking.sendToNearbyClient(world, pos, new CrystallizeEffectPacket(pos));
            for (int i = 0; i < count; i++) {
                Entity e = entity.create(world);
                if (e == null) continue;
                if (e instanceof Mob m && world instanceof ServerLevelAccessor l) {
                    EventHooks.finalizeMobSpawn(m, l, world.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
                    m.setCanPickUpLoot(true);
                }
                e.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                world.addFreshEntity(e);
            }
        }
        return RitualResult.TERMINATE;
    }
}
