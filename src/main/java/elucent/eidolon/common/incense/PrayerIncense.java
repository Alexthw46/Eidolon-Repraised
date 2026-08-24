package elucent.eidolon.common.incense;

import elucent.eidolon.api.altar.AltarInfo;
import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.api.ritual.IncenseRitual;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.common.spell.PrayerSpell;
import elucent.eidolon.common.tile.CenserTileEntity;
import elucent.eidolon.common.tile.EffigyTileEntity;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.registries.Spells;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class PrayerIncense extends IncenseRitual {
    public PrayerIncense(ResourceLocation registryName) {
        super(800, registryName);
    }

    @Override
    public boolean start(@Nullable Player player, CenserTileEntity censer) {
        super.start(player, censer);
        Level world = censer.getLevel();
        BlockPos pos = censer.getBlockPos();
        if (world == null || player == null || player.getServer() == null) return false;
        LazyOptional<IReputation> reputationLazyOptional = player.getServer().overworld().getCapability(IReputation.INSTANCE);
        if (!reputationLazyOptional.isPresent() || reputationLazyOptional.resolve().isEmpty()) return false;
        if (!reputationLazyOptional.resolve().get().canPray(player, Spells.CENSER, world.getGameTime())) {
            player.displayClientMessage(Component.translatable("eidolon.message.prayer_cooldown"), true);
            return false;
        }
        List<EffigyTileEntity> effigies = Ritual.getTilesWithinAABB(EffigyTileEntity.class, world, new AABB(pos.offset(-4, -4, -4), pos.offset(5, 5, 5)));
        if (effigies.isEmpty()) {
            player.displayClientMessage(Component.translatable("eidolon.message.no_effigy"), true);
            return false;
        }
        EffigyTileEntity effigy = effigies.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
        if (effigy.ready()) {
            Deity deity = Deities.LIGHT_DEITY;
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            reputationLazyOptional.ifPresent((rep) -> {
                if (rep.getReputation(player, deity.getId()) < 3) {
                    player.displayClientMessage(Component.translatable("eidolon.message.not_enough_reputation"), true);
                    return;
                }
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.BASIC_INCENSE_PRAYER);
                rep.pray(player, Spells.CENSER, world.getGameTime());
                rep.addReputation(player, deity.getId(), 2.0 + 0.5 * info.getPower());
                PrayerSpell.updateMagic(info, player, world, rep.getReputation(player, deity.getId()));
            });
            return true;
        }
        return false;
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        float x = blockPos.getX() + 0.5f, y = blockPos.getY() + 0.45f, z = blockPos.getZ() + 0.5f;
        float r = getRed();
        float g = getGreen();
        float b = getBlue();

        if (burnCounter < 160) Particles.create(EidolonParticles.FLAME_PARTICLE)
                .setAlpha(0.5f, 0).setScale(0.25f, 0.125f).setLifetime(20)
                .randomOffset(0.125, 0.125).randomVelocity(0.00625f, 0.01875f)
                .addVelocity(0, 0.00625f, 0)
                .setColor(r, g, b, r, g * 0.5f, b * 1.5f)
                .spawn(level, x, y, z);

        assert level != null;
        if (level.random.nextInt(20) == 0) Particles.create(EidolonParticles.SPARKLE_PARTICLE)
                .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(40)
                .randomOffset(0.0625, 0).randomVelocity(0.125f, 0)
                .addVelocity(0, 0.125f, 0)
                .setColor(r, g * 1.5f, b * 2, r, g, b)
                .enableGravity().setSpin(0.4f)
                .spawn(level, x, y, z);

        if (level.random.nextInt(5) == 0) Particles.create(EidolonParticles.SMOKE_PARTICLE)
                .setAlpha(0.25f, 0).setScale(0.375f, 0.125f).setLifetime(160)
                .randomOffset(0.25, 0.125).randomVelocity(0.025f, 0.025f)
                .addVelocity(0, 0.0125f, 0)
                .setColor(0.75f, 0.75f, 0.75f, 0.25f, 0.25f, 0.25f)
                .spawn(level, x, y + 0.125, z);

        if (level.random.nextInt(5) == 0) Particles.createRune(ResourceLocation.tryParse("eidolon:purity"))
                .setAlpha(0.75f, 0).setScale(0.475f, 0.25f).setLifetime(160)
                .randomOffset(range() * 0.15, 0.25).randomVelocity(0.025f, 0.025f)
                .addVelocity(0, -0.0125f, 0)
                .setColor(0.95F, 0.95F, 0.95F, 0.005f, 0.005f, 0.005f)
                .repeat(level, x, y + .75, z, 2);
    }
}
