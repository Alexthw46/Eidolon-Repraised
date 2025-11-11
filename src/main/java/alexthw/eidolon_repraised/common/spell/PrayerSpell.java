package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.altar.AltarInfo;
import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.deity.Deity;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.common.block.HorizontalBlockBase;
import alexthw.eidolon_repraised.common.tile.EffigyTileEntity;
import alexthw.eidolon_repraised.common.tile.GobletTileEntity;
import alexthw.eidolon_repraised.network.ManaUpdatePacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.util.RGBProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

import static alexthw.eidolon_repraised.registries.EidolonParticles.FLAME_PARTICLE;

public class PrayerSpell extends StaticSpell {
    final Deity deity;
    int baseRep = 1;
    double powerMultiplier = 0.25;
    public @Nullable ModConfigSpec.IntValue COOLDOWN;
    public @Nullable ModConfigSpec.IntValue BASE_REP;
    public @Nullable ModConfigSpec.DoubleValue POWER_MULTIPLIER;


    public PrayerSpell(ResourceLocation name, Deity deity, Sign... signs) {
        super(name, signs);
        this.deity = deity;
    }

    public PrayerSpell(ResourceLocation name, Deity deity, int reputation, double powerMult, Sign... signs) {
        super(name, signs);
        this.deity = deity;
        this.baseRep = reputation;
        this.powerMultiplier = powerMult;
    }

    public PrayerSpell(ResourceLocation name, Deity deity, int cost, int reputation, double powerMult, Sign... signs) {
        super(name, cost, signs);
        this.deity = deity;
        this.baseRep = reputation;
        this.powerMultiplier = powerMult;
    }


    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (reputationCheck(world, player, 0)) return false;
        EffigyTileEntity effigy = getEffigy(world, pos);
        return effigy != null && effigy.ready();
    }

    public static void updateMagic(AltarInfo altarInfo, Player player, Level world, double reputation) {
        var patronMana = player.getCapability(EidolonCapabilities.MANA_CAPABILITY);
        if (patronMana != null) {
            var capacity = altarInfo.getCapacity();
            var power = altarInfo.getPower();
            patronMana.setMaxMagic((float) Math.max(patronMana.getMaxMagic(), 20 + reputation * (1 + capacity / 2)));
            patronMana.setMagic((float) Math.max(patronMana.getMagic(), patronMana.getMagic() + reputation + power * 2));
            if (player instanceof ServerPlayer serverPlayer)
                Networking.sendToPlayerClient(new ManaUpdatePacket(player), serverPlayer);
        }
    }

    protected boolean reputationCheck(Level world, Player player, double minDevotion) {
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (reputation == null) return true;
        if (!reputation.canPray(this, world.getGameTime())) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.prayer_cooldown"), true);
            return true;
        }
        if (reputation.getReputation(deity.getId()) < minDevotion) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.not_enough_reputation"), true);
            return true;
        }
        return false;
    }

    @Nullable
    protected static GobletTileEntity getGoblet(Level world, BlockPos pos) {
        List<GobletTileEntity> goblets = Ritual.getTilesWithinAABB(GobletTileEntity.class, world, new AABB(pos.offset(-4, -4, -4).getBottomCenter(), pos.offset(5, 5, 5).getCenter()));
        if (goblets.isEmpty()) return null;
        return goblets.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
    }

    @Nullable
    protected static EffigyTileEntity getEffigy(Level world, BlockPos pos) {
        List<EffigyTileEntity> effigies = Ritual.getTilesWithinAABB(EffigyTileEntity.class, world, new AABB(pos.offset(-4, -4, -4).getBottomCenter(), pos.offset(5, 5, 5).getCenter()));
        if (effigies.isEmpty()) return null;
        return effigies.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        EffigyTileEntity effigy = getEffigy(world, pos);
        if (effigy == null) return;
        if (!world.isClientSide) {
            effigy.pray();
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
            if (reputation != null) {
                reputation.pray(this, world.getGameTime());
                reputation.addReputation(deity.getId(), getBaseRep() + getPowerMultiplier() * info.getPower());
                updateMagic(info, player, world, reputation.getReputation(deity.getId()));
            }
        } else {
            playSuccessSound(world, player, effigy, deity);
        }
    }

    protected void playSuccessSound(Level world, Player player, EffigyTileEntity effigy, RGBProvider color) {
        world.playSound(player, effigy.getBlockPos(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.NEUTRAL, 10000.0F, 0.6F + world.random.nextFloat() * 0.2F);
        world.playSound(player, effigy.getBlockPos(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.NEUTRAL, 2.0F, 0.5F + world.random.nextFloat() * 0.2F);
        BlockState state = world.getBlockState(effigy.getBlockPos());
        Direction dir = state.getValue(HorizontalBlockBase.HORIZONTAL_FACING);
        Direction tangent = dir.getClockWise();
        float x = effigy.getBlockPos().getX() + 0.5f + dir.getStepX() * 0.21875f;
        float y = effigy.getBlockPos().getY() + 0.8125f;
        float z = effigy.getBlockPos().getZ() + 0.5f + dir.getStepZ() * 0.21875f;
        Particles.create(FLAME_PARTICLE.get())
                .setColor(color.getRed(), color.getGreen(), color.getBlue())
                .setAlpha(0.5f, 0)
                .setScale(0.125f, 0.0625f)
                .randomOffset(0.01f)
                .randomVelocity(0.0025f).addVelocity(0, 0.005f, 0)
                .repeat(world, x + 0.09375f * tangent.getStepX(), y, z + 0.09375f * tangent.getStepZ(), 8);
        Particles.create(FLAME_PARTICLE.get())
                .setColor(color.getRed(), color.getGreen(), color.getBlue())
                .setAlpha(0.5f, 0)
                .setScale(0.1875f, 0.125f)
                .randomOffset(0.01f)
                .randomVelocity(0.0025f).addVelocity(0, 0.005f, 0)
                .repeat(world, x - 0.09375f * tangent.getStepX(), y, z - 0.09375f * tangent.getStepZ(), 8);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder spellBuilder) {
        super.buildConfig(spellBuilder);
        COOLDOWN = spellBuilder.comment("Cooldown for this prayer spell").defineInRange("cooldown", 0, 21000, Integer.MAX_VALUE);
        BASE_REP = spellBuilder.comment("Base reputation gained from this prayer spell").defineInRange("base_reputation", baseRep, 0, Integer.MAX_VALUE);
        POWER_MULTIPLIER = spellBuilder.comment("Altar Power multiplier for reputation gained with this prayer spell").defineInRange("power_multiplier", powerMultiplier, 0, Double.MAX_VALUE);
    }

    public int getCooldown() {
        return COOLDOWN == null ? 21000 : COOLDOWN.get();
    }

    public int getBaseRep() {
        return BASE_REP == null ? baseRep : BASE_REP.get();
    }

    public double getPowerMultiplier() {
        return POWER_MULTIPLIER == null ? powerMultiplier : POWER_MULTIPLIER.get();
    }

}
