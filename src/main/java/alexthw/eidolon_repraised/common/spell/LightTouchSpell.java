package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.capability.IMana;
import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.recipe.ChantConversionRecipe;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class LightTouchSpell extends DarkTouchSpell {

    public LightTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, signs);
        NeoForge.EVENT_BUS.addListener(LightTouchSpell::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity caster && Eidolon.isValidUndead(event.getEntity())) {
            var tag = caster.getMainHandItem();
            if (tag.isEmpty() && tag.getOrDefault(EidolonDataComponents.CONSECRATED.get(), 0) > 0) {
                event.setNewDamage(event.getNewDamage() * 1.5f);
                tag.set(EidolonDataComponents.CONSECRATED, tag.getOrDefault(EidolonDataComponents.CONSECRATED.get(), 1) - 1);
            }
        }
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (reputation == null) return false;
        if (reputation.getReputation(Deities.LIGHT_DEITY.getId()) < 10.0) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.not_enough_reputation"), true);
            return false;
        }

        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() != 1) return false;
        ItemStack stack = items.getFirst().getItem();
        return stack.getCount() == 1 && canTouch(stack, world, player);
    }

    boolean canTouch(ItemStack stack, Level world, Player player) {
        if (stack.isDamageableItem() && stack.getMaxStackSize() == 1) return true;
        var conversions = world.getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get());
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (reputation == null) return false;
        var lightRep = reputation.getReputation(Deities.LIGHT_DEITY_ID);
        return conversions.stream().map(RecipeHolder::value).filter(
                r -> r.input.test(stack) && (Deities.DUMMY_ID.equals(r.deity) || Deities.LIGHT_DEITY_ID.equals(r.deity))
        ).anyMatch(r -> lightRep >= r.minDevotion);
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (reputation != null) {
            var lightRep = reputation.getReputation(Deities.LIGHT_DEITY_ID);
            for (RecipeHolder<ChantConversionRecipe> holder : player.level().getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get())) {
                var r = holder.value();
                if (r.input.test(stack) && (Deities.DUMMY_ID.equals(r.deity) || Deities.LIGHT_DEITY_ID.equals(r.deity)) && lightRep >= r.minDevotion) {
                    IMana.expendMana(player, getCost());
                    return r.getResultItem(player.level().registryAccess());
                }
            }
        }
        IMana.expendMana(player, getCost());
        stack.set(EidolonDataComponents.CONSECRATED, 50);
        return stack;
    }

}
