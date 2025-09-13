package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.api.spells.SignSequence;
import alexthw.eidolon_repraised.api.spells.Spell;
import alexthw.eidolon_repraised.api.spells.SpellCastEvent;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StaticSpell extends Spell {
    public SignSequence signs;
    private int cost;
    private int delay = 10;
    public @Nullable ModConfigSpec.ConfigValue<Integer> COST;


    public StaticSpell(ResourceLocation name, Sign... signs) {
        super(name);
        // this.signs = new SignSequence(signs);
    }

    @Override
    public void setSigns(SignSequence signs) {
        this.signs = signs;
    }

    public StaticSpell(ResourceLocation name, int cost, Sign... signs) {
        this(name, signs);
        this.cost = cost;
    }

    public StaticSpell(ResourceLocation name, int cost, int delay, Sign... signs) {
        this(name, signs);
        this.cost = cost;
        this.delay = delay;
    }

    public int getCost() {
        return COST == null ? cost : COST.get();
    }

    @NotNull
    protected static Vec3 getVector(Level world, Player player) {
        HitResult ray = world.clip(new ClipContext(player.getEyePosition(0), player.getEyePosition(0).add(player.getLookAngle().scale(4)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        return ray.getType() == HitResult.Type.BLOCK ? ray.getLocation() : player.getEyePosition(0).add(player.getLookAngle().scale(4));
    }

    public static HitResult rayTrace(Entity entity, double length, float lookOffset, boolean hitLiquids) {
        HitResult result = entity.pick(length, lookOffset, hitLiquids);
        EntityHitResult entityLookedAt = MathUtil.getLookedAtEntity(entity, 25);
        return entityLookedAt == null ? result : entityLookedAt;
    }

    @Override
    public boolean matches(SignSequence signs) {
        return this.signs.equals(signs);
    }

    public abstract boolean canCast(Level world, BlockPos pos, Player player);

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player, SignSequence signs) {
        if (NeoForge.EVENT_BUS.post(new SpellCastEvent.Pre(this, world, pos, player, signs)).isCanceled()) return false;
        if (getCost() > 0 && !player.isCreative()) {
            var mana = player.getCapability(EidolonCapabilities.MANA_CAPABILITY);
            if (mana != null && mana.getMagic() < getCost()) {
                if (player instanceof ServerPlayer serverPlayer)
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon_repraised.title.no_mana")));
                return false;
            }
        }
        return canCast(world, pos, player);
    }

    public abstract void cast(Level world, BlockPos pos, Player player);

    @Override
    public void cast(Level world, BlockPos pos, Player player, SignSequence signs) {
        cast(world, pos, player);
        NeoForge.EVENT_BUS.post(new SpellCastEvent.Post(this, world, pos, player, signs));
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder spellBuilder) {
        DELAY = spellBuilder.comment("The delay in ticks before the spell is cast").defineInRange("delay", delay, 0, Integer.MAX_VALUE);
        COST = spellBuilder.comment("The cost of casting this spell").defineInRange("cost", cost, 0, Integer.MAX_VALUE);
    }
}
