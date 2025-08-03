package alexthw.eidolon_repraised.common.item.curio;

import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.registries.Registry;
import alexthw.eidolon_repraised.util.TargetMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Predicate;

public class AngelSightItem extends EidolonCurio {
    public AngelSightItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(AngelSightItem::addMode);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !slotContext.entity().isShiftKeyDown();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pPlayer.isShiftKeyDown() && !pLevel.isClientSide) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            int mode = stack.getOrDefault(EidolonDataComponents.TARGET_MODE, 0);
            mode = (mode + 1) % 3;
            stack.set(EidolonDataComponents.TARGET_MODE, mode);
            pPlayer.sendSystemMessage(Component.translatable("eidolon_repraised.angels_sight.mode." + mode));
            return InteractionResultHolder.success(stack);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(@NotNull final ItemStack stack, final @NotNull TooltipContext level, @NotNull final List<Component> tooltip, @NotNull final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        String modeTooltip = switch (stack.getOrDefault(EidolonDataComponents.TARGET_MODE, 0)) {
            case 1 -> "lore.eidolon_repraised.angels_sight.mode.1";
            case 2 -> "lore.eidolon_repraised.angels_sight.mode.2";
            default -> "lore.eidolon_repraised.angels_sight.mode.3";
        };

        tooltip.add(Component.translatable(modeTooltip).withStyle(ChatFormatting.DARK_GRAY));
    }

    @SubscribeEvent
    public static void addMode(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile && projectile.getOwner() instanceof Player player) {
            CuriosApi.getCuriosInventory(player).flatMap(inventory -> inventory.findFirstCurio(Registry.ANGELS_SIGHT.get()))
                    .ifPresent(ring -> {
                                Predicate<Entity> targetMode = switch (ring.stack().getOrDefault(EidolonDataComponents.TARGET_MODE, 0)) {
                                    case 1 -> target -> target instanceof LivingEntity && !(target instanceof Player);
                                    case 2 -> target -> target instanceof Enemy;
                                    default -> target -> target instanceof LivingEntity;
                                };

                                if (projectile instanceof TargetMode mode) {
                                    mode.eidolonrepraised$setMode(targetMode);
                                }
                            }
                    );
        }
    }
}
