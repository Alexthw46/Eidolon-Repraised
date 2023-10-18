package elucent.eidolon.common.ritual;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.network.CrystallizeEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class CrystalRitual extends Ritual {
    public static final ResourceLocation SYMBOL = new ResourceLocation(Eidolon.MODID, "particle/crystal_ritual");

    public CrystalRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 247, 156, 220));
    }


    @Override
    public Ritual cloneRitual() {
        return new CrystalRitual();
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, getSearchBounds(pos), LivingEntity::isInvertedHealAndHarm);
        for (LivingEntity e : entities) {
            e.hurt(Registry.RITUAL_DAMAGE, e.getMaxHealth() * 1000);
            if (!world.isClientSide) {
                Networking.sendToTracking(world, e.blockPosition(), new CrystallizeEffectPacket(e.blockPosition()));
                world.addFreshEntity(new ItemEntity(world, e.getX(), e.getY(), e.getZ(), new ItemStack(Registry.SOUL_SHARD.get(), 1 + world.random.nextInt(3))));
            }
        }
        return RitualResult.TERMINATE;
    }
}
