package elucent.eidolon.common.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.network.MagicBurstEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.EidolonDataComponents;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.util.ColorUtil;
import elucent.eidolon.util.EntityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SummoningStaffItem extends ItemBase {
    public SummoningStaffItem(Properties builderIn) {
        super(builderIn);
    }

    public record ThrallData(List<CompoundTag> thralls, int selected) {
        public ThrallData(List<CompoundTag> thralls) {
            this(thralls, 0);
        }

        public static final Codec<ThrallData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.list(CompoundTag.CODEC).fieldOf("thralls").forGetter(ThrallData::thralls),
                        Codec.INT.optionalFieldOf("selected", 0).forGetter(ThrallData::selected)
                ).apply(instance, ThrallData::new)
        );
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, LivingEntity entity, @NotNull ItemStack stack, int time) {
        if (entity.level().isClientSide) {
            HitResult hit = entity.pick(16, 0, false);
            if (hit.getType() != Type.MISS) {
                Vec3 pos = hit.getLocation();
                time = 72000 - time;
                float alpha = Mth.clamp(time / 40.0f, 0, 1);
                float a = Mth.DEG_TO_RAD * (entity.level().getGameTime() % 360 + 12 * time);
                float r = 0.3f + 0.3f * alpha;
                float sa = r * Mth.sin(a), ca = r * Mth.cos(a);
                if (time == 40) {
                    entity.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_3.value(), 1, 1);
                }
                Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                        .randomVelocity(0.025f * alpha, 0.0125f * alpha)
                        .setColor(33.0f / 255, 26.0f / 255, 23.0f / 255, 0.125f, 10.0f / 255, 10.0f / 255, 12.0f / 255, 0)
                        .setAlpha(0.25f * alpha, 0)
                        .randomOffset(0.05f + 0.05f * alpha)
                        .setScale(0.25f + 0.25f * alpha, alpha * 0.125f)
                        .repeat(entity.level(), pos.x + sa, pos.y, pos.z + ca, 2)
                        .repeat(entity.level(), pos.x - sa, pos.y, pos.z - ca, 2);
            }
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int time) {
        if ((72000 - time) >= 20 && hasCharges(stack)) {
            ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
            if (thrallData == null || thrallData.thralls().isEmpty()) return;
            int selected = thrallData.selected();
            CompoundTag tag = thrallData.thralls().get(selected);
            HitResult hit = entity.pick(16, 0, false);
            if (hit.getType() != Type.MISS) {
                Vec3 pos = hit.getLocation();
                Optional<EntityType<?>> etype = EntityType.by(tag);
                if (etype.isPresent() && !level.isClientSide) {
                    tag.remove("UUID");
                    @SuppressWarnings("AccessStaticViaInstance") Optional<Entity> e = etype.get().create(tag, level);
                    if (e.isPresent()) {
                        e.get().setPos(pos);
                        EntityUtil.enthrall(entity, (LivingEntity) e.get());
                        level.addFreshEntity(e.get());
                        Networking.sendToNearbyClient(entity.level(), e.get().blockPosition(), new MagicBurstEffectPacket(e.get().getX(), e.get().getY() + e.get().getBbHeight() / 2, e.get().getZ(),
                                ColorUtil.packColor(255, 61, 70, 35), ColorUtil.packColor(255, 36, 24, 41)));
                        level.playSound(null, e.get().blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.75f, 0.1f);
                    }
                }

                entity.setItemInHand(entity.getUsedItemHand(), entity instanceof Player && ((Player) entity).getAbilities().instabuild ? stack : consumeCharge(stack, selected));
                entity.swing(entity.getUsedItemHand());
                entity.stopUsingItem();
            }
        }
    }

    public int getSelected(ItemStack stack) {
        ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
        if (thrallData == null) return 0;
        int selected = thrallData.selected();
        if (selected >= thrallData.thralls().size()) selected = 0;
        return selected;
    }

    public int changeSelection(ItemStack stack, int diff) {
        ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
        if (thrallData == null || thrallData.thralls().isEmpty()) return 0;
        int size = thrallData.thralls().size();
        int selected = (thrallData.selected() + diff) % size;
        if (selected < 0) selected += size;
        ThrallData newData = new ThrallData(thrallData.thralls(), selected);
        stack.set(EidolonDataComponents.THRALLS, newData);
        return selected;
    }

    public ItemStack addCharges(ItemStack stack, ListTag charges) {
        ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
        if (thrallData == null) {
            thrallData = new ThrallData(new ArrayList<>(), 0);
        }
        List<CompoundTag> thralls = thrallData.thralls();
        while (thralls.size() + charges.size() > 100) charges.removeLast();
        for (int i = 0; i < charges.size(); i++) thralls.add(charges.getCompound(i));
        stack.set(EidolonDataComponents.THRALLS, new ThrallData(thralls, thrallData.selected()));
        return stack;
    }

    public void addCharge(ItemStack stack, CompoundTag tag) {
        ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
        if (thrallData == null) {
            thrallData = new ThrallData(new ArrayList<>(), 0);
        }
        List<CompoundTag> thralls = thrallData.thralls();
        if (thralls.size() < 100) thralls.add(tag);
        stack.set(EidolonDataComponents.THRALLS, new ThrallData(thralls, thrallData.selected()));
    }

    public boolean hasCharges(ItemStack stack) {
        ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
        return thrallData != null && !thrallData.thralls().isEmpty();
    }

    public ItemStack consumeCharge(ItemStack stack, int index) {
        ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
        if (thrallData == null) return stack;
        List<CompoundTag> thralls = new ArrayList<>(thrallData.thralls());
        if (index < thralls.size()) thralls.remove(index);
        int selected = thrallData.selected();
        if (selected >= thralls.size()) selected = 0;
        stack.set(EidolonDataComponents.THRALLS, new ThrallData(thralls, selected));
        return stack;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hasCharges(stack)) {
            if (player.isShiftKeyDown()) {
                int sel = changeSelection(stack, 1);
                ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
                if (thrallData == null || thrallData.thralls().isEmpty()) return InteractionResultHolder.fail(stack);
                CompoundTag tag = thrallData.thralls().get(sel);
                ResourceLocation id = ResourceLocation.parse(tag.getString("id"));
                String summonKey = "entity." + id.getNamespace() + "." + id.getPath();
                player.setItemInHand(hand, stack);
                if (!world.isClientSide) {
                    ((ServerPlayer) player).connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon.tooltip.active_summon").append(
                            Component.translatable(summonKey).withStyle(ChatFormatting.LIGHT_PURPLE)
                    )));
                    player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 1.0f);
                }
                return InteractionResultHolder.fail(stack);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else return InteractionResultHolder.fail(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext tooltipContext, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        boolean charge = hasCharges(stack);
        int selected = getSelected(stack);
        String summonKey = "eidolon.tooltip.no_selected_summon";
        if (charge) {
            ThrallData thrallData = stack.get(EidolonDataComponents.THRALLS);
            if (thrallData == null || thrallData.thralls().isEmpty()) return;
            CompoundTag tag = thrallData.thralls().get(selected);
            String ids = tag.getString("id");
            ResourceLocation id = ResourceLocation.parse(tag.getString("id"));
            summonKey = "entity." + id.getNamespace() + "." + id.getPath();
        }
        tooltip.add(Component.translatable("eidolon.tooltip.active_summon").append(
                Component.translatable(summonKey).withStyle(charge ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.DARK_PURPLE)
        ));
    }
}
