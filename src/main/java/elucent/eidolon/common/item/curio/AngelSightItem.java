package elucent.eidolon.common.item.curio;

import elucent.eidolon.registries.Registry;
import elucent.eidolon.util.TargetMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.function.Predicate;

public class AngelSightItem extends EidolonCurio {
    public AngelSightItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(AngelSightItem::addMode);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !slotContext.entity().isShiftKeyDown();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pPlayer.isShiftKeyDown() && !pLevel.isClientSide) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            CompoundTag tag = stack.getOrCreateTag();
            int mode = tag.getInt("mode");
            mode = (mode + 1) % 3;
            tag.putInt("mode", mode);
            stack.setTag(tag);
            pPlayer.sendSystemMessage(Component.translatable("eidolon.angels_sight.mode." + mode));
            return InteractionResultHolder.success(stack);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @SubscribeEvent
    public static void addMode(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile && projectile.getOwner() instanceof Player player) {
            CuriosApi.getCuriosHelper().findFirstCurio(player, Registry.ANGELS_SIGHT.get()).ifPresent(ring -> {
                Predicate<Entity> targetMode = switch (ring.stack().getOrCreateTag().getInt("mode")) {
                    case 1 -> target -> target instanceof LivingEntity && !(target instanceof Player);
                    case 2 -> target -> target instanceof Enemy;
                    default -> target -> target instanceof LivingEntity;
                };

                if (projectile instanceof TargetMode mode) {
                    mode.eidolon$setMode(targetMode);
                }
            });
        }
    }
}
