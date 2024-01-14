package elucent.eidolon.common.tile;

import elucent.eidolon.api.altar.AltarInfo;
import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.network.ExtinguishEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

import static elucent.eidolon.Eidolon.prefix;
import static elucent.eidolon.common.spell.PrayerSpell.updateMagic;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class CenserTileEntity extends TileEntityBase implements IBurner {
    public CenserTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    ItemStack incense = ItemStack.EMPTY;

    boolean isBurning;
    int burnCounter;

    public CenserTileEntity(BlockPos pos, BlockState state) {
        this(Registry.CENSER_TILE_ENTITY.get(), pos, state);
    }

    public boolean canStartBurning() {
        return !isBurning && !incense.isEmpty();
    }

    public void tick() {
        if (level == null) return;
        if (!level.isClientSide && isBurning) {
            burnCounter++;
            if (burnCounter >= 400) {
                if (incense.isEmpty()) {
                    isBurning = false;
                    level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(LIT, isBurning));
                    burnCounter = 0;
                    Networking.sendToTracking(level, worldPosition, new ExtinguishEffectPacket(worldPosition));
                }
            }
            sync();
        }
        if (burnCounter == 80) {
            incense = ItemStack.EMPTY;
        }
        if (level.isClientSide && isBurning) {

            float x = getBlockPos().getX() + 0.5f, y = getBlockPos().getY() + 0.45f, z = getBlockPos().getZ() + 0.5f;
            float r = 1.0f;
            float g = 0.5f;
            float b = 0.25f;

            if (burnCounter < 160) Particles.create(EidolonParticles.FLAME_PARTICLE)
                    .setAlpha(0.5f, 0).setScale(0.25f, 0.125f).setLifetime(20)
                    .randomOffset(0.125, 0.125).randomVelocity(0.00625f, 0.01875f)
                    .addVelocity(0, 0.00625f, 0)
                    .setColor(r, g, b, r, g * 0.5f, b * 1.5f)
                    .spawn(level, x, y, z);

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
                    .setColor(0.5f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f)
                    .spawn(level, x, y + 0.125, z);
        }
    }

    @Override
    public InteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level instanceof ServerLevel && !isBurning) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if (itemInHand.isEmpty() && !incense.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, incense);
                incense = ItemStack.EMPTY;
                if (!level.isClientSide) sync();
                return InteractionResult.SUCCESS;
            } else if (!itemInHand.isEmpty() && incense.isEmpty()) {
                if (itemInHand.getItem() == Registry.OFFERING_INCENSE.get()) {
                    incense = itemInHand.split(1);
                    if (!level.isClientSide) sync();
                    return InteractionResult.SUCCESS;
                }
            } else if (!itemInHand.isEmpty() && !incense.isEmpty()) {
                if (itemInHand.getItem() instanceof FlintAndSteelItem) {
                    if (!level.isClientSide && canStartBurning()) this.startBurning(player, level, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void startBurning(Player player, @NotNull Level world, BlockPos pos) {
        if (!world.getCapability(IReputation.INSTANCE).isPresent()) return;
        if (!world.getCapability(IReputation.INSTANCE).resolve().get().canPray(player, prefix("basic_incense"), world.getGameTime())) {
            player.displayClientMessage(Component.translatable("eidolon.message.prayer_cooldown"), true);
            return;
        }
        List<EffigyTileEntity> effigies = Ritual.getTilesWithinAABB(EffigyTileEntity.class, world, new AABB(pos.offset(-4, -4, -4), pos.offset(5, 5, 5)));
        if (effigies.isEmpty()) return;
        EffigyTileEntity effigy = effigies.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
        if (effigy.ready()) {
            Deity deity = Deities.LIGHT_DEITY;
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            world.getCapability(IReputation.INSTANCE, null).ifPresent((rep) -> {
                if (rep.getReputation(player, deity.getId()) < 3) {
                    player.displayClientMessage(Component.translatable("eidolon.message.not_enough_reputation"), true);
                    return;
                }
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.BASIC_INCENSE_PRAYER);
                rep.pray(player, prefix("basic_incense"), world.getGameTime());
                rep.addReputation(player, deity.getId(), 2.0 + 0.5 * info.getPower());
                updateMagic(info, player, world, rep.getReputation(player, deity.getId()));
            });
            isBurning = true;
            world.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(LIT, isBurning));
            burnCounter = 0;
            sync();
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("incense")) {
            incense = ItemStack.of(pTag.getCompound("incense"));
        }
        burnCounter = pTag.getInt("burnCounter");
        isBurning = pTag.getBoolean("isBurning");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!incense.isEmpty()) {
            pTag.put("incense", incense.save(new CompoundTag()));
        }
        pTag.putInt("burnCounter", burnCounter);
        pTag.putBoolean("isBurning", isBurning);
    }
}
