package elucent.eidolon.common.spell;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.network.MagicBurstEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.recipe.ChantConversionRecipe;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.util.DamageTypeData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class DarkTouchSpell extends StaticSpell {
    public static final String NECROTIC_KEY = new ResourceLocation(Eidolon.MODID, "necrotic").toString();

    public DarkTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, 20, signs);

        MinecraftForge.EVENT_BUS.addListener(DarkTouchSpell::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living && !event.getSource().is(DamageTypes.WITHER)) {
            var tag = living.getMainHandItem().getTag();
            if (tag != null && tag.contains(NECROTIC_KEY)) {
                float amount = Math.min(1, event.getAmount());
                event.setAmount(event.getAmount() - amount);
                if (event.getAmount() <= 0) event.setCanceled(true);
                int prevHurtResist = event.getEntity().invulnerableTime;
                event.getEntity().invulnerableTime = 0;
                if (event.getEntity().hurt(DamageTypeData.source(living.level, DamageTypes.WITHER, living, null), amount)) {

                    tag.putInt(NECROTIC_KEY, -1);
                    if (tag.getInt(NECROTIC_KEY) <= 0) tag.remove(NECROTIC_KEY);

                    if (living.getHealth() <= 0) event.setCanceled(true);
                    else living.invulnerableTime = prevHurtResist;
                }
            }
        }
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (!world.getCapability(IReputation.INSTANCE).isPresent()) return false;
        if (world.getCapability(IReputation.INSTANCE).resolve().get().getReputation(player, Deities.DARK_DEITY.getId()) < 10.0) {
            player.displayClientMessage(Component.translatable("eidolon.message.not_enough_reputation"), true);
            return false;
        }

        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() != 1) return false;
        ItemStack stack = items.get(0).getItem();
        return stack.getCount() == 1 && canTouch(stack, world, player);
    }

    boolean canTouch(ItemStack stack, Level world, Player player) {
        if (stack.isDamageableItem() && stack.getMaxStackSize() == 1) return true;
        var conversions = world.getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get());
        var darkRep = world.getCapability(IReputation.INSTANCE).resolve().get().getReputation(player, Deities.DARK_DEITY_ID);
        return conversions.stream().filter(
                r -> r.input.test(stack) && (r.deity == null || Deities.DARK_DEITY_ID.equals(r.deity))
        ).anyMatch(r -> darkRep >= r.minDevotion);
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        var darkRep = player.level().getCapability(IReputation.INSTANCE).resolve().get().getReputation(player, Deities.DARK_DEITY_ID);

        for (ChantConversionRecipe r : player.level().getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get())) {
            if (r.input.test(stack) && (r.deity == null || Deities.DARK_DEITY_ID.equals(r.deity)) && darkRep >= r.minDevotion) {
                ISoul.expendMana(player, getCost());
                return r.getResultItem(player.level().registryAccess());
            }
        }
//
//        if (stack.getItem() == Registry.PEWTER_INLAY.get())
//            return new ItemStack(Registry.UNHOLY_SYMBOL.get());
//        else if (stack.getItem() == Items.BLACK_WOOL)
//            return new ItemStack(Registry.TOP_HAT.get());
//        else if (stack.getItem() instanceof RecordItem && stack.getItem() != Registry.PAROUSIA_DISC.get())
//            return new ItemStack(Registry.PAROUSIA_DISC.get());
//        else


        // No recipe found; apply necrotic touch. Validated by canTouch beforehand.
        ISoul.expendMana(player, getCost());
        stack.getOrCreateTag().putInt(NECROTIC_KEY, 50);
        return stack;

    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() == 1) {
            if (!world.isClientSide) {
                ItemStack stack = items.get(0).getItem();
                if (canTouch(stack, world, player)) {
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
