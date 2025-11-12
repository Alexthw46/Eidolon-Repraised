package alexthw.eidolon_repraised.common.ritual;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.network.CrystallizeEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.registries.Registry;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class CrystalRitual extends Ritual {
    public static final ResourceLocation SYMBOL = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "particle/crystal_ritual");

    public CrystalRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 247, 156, 220));
    }


    @Override
    public Ritual cloneRitual() {
        return new CrystalRitual();
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, getSearchBounds(pos), Eidolon::isValidUndead);
            for (LivingEntity e : entities) {
                if (e.hurt(Registry.RITUAL_DAMAGE.source(world), e.getMaxHealth() * 1000)) {
                    // only if damage was not cancelled
                    Networking.sendToNearbyClient(world, e.blockPosition(), new CrystallizeEffectPacket(e.blockPosition()));
                    world.addFreshEntity(new ItemEntity(world, e.getX(), e.getY(), e.getZ(), new ItemStack(Registry.SOUL_SHARD.get(), 1 + world.random.nextInt(3))));
                }
            }
        }
        return RitualResult.TERMINATE;
    }
}
