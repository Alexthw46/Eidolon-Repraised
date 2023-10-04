package elucent.eidolon.common.ritual;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.network.CrystallizeEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SummonRitual extends Ritual {
    public static final ResourceLocation SYMBOL = new ResourceLocation(Eidolon.MODID, "particle/summon_ritual");

    public EntityType<?> getEntityType() {
        return entity;
    }

    final EntityType<?> entity;

    public int getCount() {
        return count;
    }

    int count = 1;

    public SummonRitual(EntityType<?> entity) {
        super(SYMBOL, ColorUtil.packColor(255, 121, 94, 255));
        this.entity = entity;
    }

    public SummonRitual(EntityType<?> entity, int count) {
        super(SYMBOL, ColorUtil.packColor(255, 121, 94, 255));
        this.entity = entity;
        this.count = count;
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            Networking.sendToTracking(world, pos, new CrystallizeEffectPacket(pos));
            for (int i = 0; i < count; i++) {
                Entity e = entity.create(world);
                if (e == null) continue;
                e.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                world.addFreshEntity(e);
            }
        }
        return RitualResult.TERMINATE;
    }
}
