package elucent.eidolon.common.spell;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.network.MagicBurstEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.util.DamageTypeData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;

public class DarkTouchSpell extends StaticSpell {
    public static final String NECROTIC_KEY = new ResourceLocation(Eidolon.MODID, "necrotic").toString();

    public DarkTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, signs);

        MinecraftForge.EVENT_BUS.addListener(DarkTouchSpell::onHurt);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(DarkTouchSpell::tooltip);
            return new Object();
        });
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living
            && !event.getSource().getMsgId().equals(living.damageSources().wither().getMsgId())
            && living.getMainHandItem().hasTag()
            && living.getMainHandItem().getOrCreateTag().contains(NECROTIC_KEY)) {
            float amount = Math.min(1, event.getAmount());
            event.setAmount(event.getAmount() - amount);
            if (event.getAmount() <= 0) event.setCanceled(true);
            int prevHurtResist = event.getEntity().invulnerableTime;
            event.getEntity().invulnerableTime = 0;
            if (event.getEntity().hurt(DamageTypeData.source(living.level, DamageTypes.WITHER, living, null), amount)) {
                if (living.getHealth() <= 0) event.setCanceled(true);
                else living.invulnerableTime = prevHurtResist;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        if (event.getItemStack().hasTag() && event.getItemStack().getOrCreateTag().contains(NECROTIC_KEY)) {
            event.getToolTip().add(Component.translatable("eidolon.tooltip.necrotic").withStyle(ChatFormatting.DARK_BLUE));
        }
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (!world.getCapability(IReputation.INSTANCE).isPresent()) return false;
        if (world.getCapability(IReputation.INSTANCE).resolve().get().getReputation(player, Deities.DARK_DEITY.getId()) < 4.0)
            return false;

        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() != 1) return false;
        ItemStack stack = items.get(0).getItem();
        return stack.getCount() == 1 && canTouch(stack);
    }

    boolean canTouch(ItemStack stack) {
        return stack.getItem() == Registry.PEWTER_INLAY.get()
               || stack.getItem() == Items.BLACK_WOOL
               || (stack.getItem() instanceof RecordItem && stack.getItem() != Registry.PAROUSIA_DISC.get())
                ;//|| (stack.isDamageableItem() && stack.getMaxStackSize() == 1); // is a tool
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        if (stack.getItem() == Registry.PEWTER_INLAY.get())
            return new ItemStack(Registry.UNHOLY_SYMBOL.get());
        else if (stack.getItem() == Items.BLACK_WOOL)
            return new ItemStack(Registry.TOP_HAT.get());
        else if (stack.getItem() instanceof RecordItem && stack.getItem() != Registry.PAROUSIA_DISC.get())
            return new ItemStack(Registry.PAROUSIA_DISC.get());
        /*
        else {
            ISoul.expendMana(player, getCost());
            stack.getOrCreateTag().putBoolean(NECROTIC_KEY, true);
            return stack;
        }*/
        else return stack;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() == 1) {
            if (!world.isClientSide) {
                ItemStack stack = items.get(0).getItem();
                if (canTouch(stack)) {
                    items.get(0).setItem(touchResult(stack, player));
                    Vec3 p = items.get(0).position();
                    items.get(0).setDefaultPickUpDelay();
                    Networking.sendToTracking(world, items.get(0).blockPosition(), new MagicBurstEffectPacket(p.x, p.y, p.z, Signs.WICKED_SIGN.getColor(), Signs.BLOOD_SIGN.getColor()));
                }
            } else {
                world.playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.0F, 0.6F + world.random.nextFloat() * 0.2F);
            }
        }
    }

}
