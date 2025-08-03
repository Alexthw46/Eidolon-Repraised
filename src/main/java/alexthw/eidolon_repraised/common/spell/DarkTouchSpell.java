package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.capability.IMana;
import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.network.MagicBurstEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.registries.Registry;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.DamageTypeData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class DarkTouchSpell extends StaticSpell {

    public DarkTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, 20, signs);

        NeoForge.EVENT_BUS.addListener(DarkTouchSpell::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity living && !event.getSource().is(DamageTypes.WITHER)) {
            ItemStack itemStack = living.getMainHandItem();
            if (itemStack.has(EidolonDataComponents.NECROTIC)) {
                float amount = Math.min(1, event.getNewDamage());
                event.setNewDamage(event.getNewDamage() - amount);
                int prevHurtResist = event.getEntity().invulnerableTime;
                event.getEntity().invulnerableTime = 0;
                if (event.getEntity().hurt(DamageTypeData.source(living.level(), DamageTypes.WITHER, living, null), amount)) {
                    itemStack.set(EidolonDataComponents.NECROTIC, itemStack.getOrDefault(EidolonDataComponents.NECROTIC, 1) - 1);
                    living.invulnerableTime = prevHurtResist;
                }
            }
        }
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (reputation == null) return false;
        if (reputation.getReputation(Deities.DARK_DEITY.getId()) < 10.0) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.not_enough_reputation"), true);
            return false;
        }

        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() != 1) return false;
        ItemStack stack = items.getFirst().getItem();
        return stack.getCount() == 1 && canTouch(stack);
    }

    boolean canTouch(ItemStack stack) {
        return stack.getItem() == Registry.PEWTER_INLAY.get()
                || stack.getItem() == Items.BLACK_WOOL
                || (stack.is(Tags.Items.MUSIC_DISCS) && stack.getItem() != Registry.PAROUSIA_DISC.get())
                || (stack.isDamageableItem() && stack.getMaxStackSize() == 1); // is a tool
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        if (stack.getItem() == Registry.PEWTER_INLAY.get())
            return new ItemStack(Registry.UNHOLY_SYMBOL.get());
        else if (stack.getItem() == Items.BLACK_WOOL)
            return new ItemStack(Registry.TOP_HAT.get());
        else if (stack.is(Tags.Items.MUSIC_DISCS) && stack.getItem() != Registry.PAROUSIA_DISC.get())
            return new ItemStack(Registry.PAROUSIA_DISC.get());
        else {
            IMana.expendMana(player, getCost());
            stack.set(EidolonDataComponents.NECROTIC, 50);
            return stack;
        }

    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() == 1) {
            if (!world.isClientSide) {
                ItemStack stack = items.getFirst().getItem();
                if (canTouch(stack)) {
                    items.getFirst().setItem(touchResult(stack, player));
                    Vec3 p = items.getFirst().position();
                    items.getFirst().setDefaultPickUpDelay();
                    Networking.sendToNearbyClient(world, items.getFirst().blockPosition(), new MagicBurstEffectPacket(p.x, p.y, p.z, Signs.WICKED_SIGN.color(), Signs.BLOOD_SIGN.color()));
                }
            } else {
                world.playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.0F, 0.6F + world.random.nextFloat() * 0.2F);
            }
        }
    }

}
