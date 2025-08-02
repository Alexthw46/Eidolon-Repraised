package elucent.eidolon.common.spell;

import elucent.eidolon.api.capability.IMana;
import elucent.eidolon.api.capability.IReputation;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.registries.EidolonDataComponents;
import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
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

public class LightTouchSpell extends DarkTouchSpell {

    public LightTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, signs);
        NeoForge.EVENT_BUS.addListener(LightTouchSpell::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity caster && event.getEntity().getType().is(EntityTypeTags.UNDEAD)) {
            var tag = caster.getMainHandItem();
            if (tag.isEmpty() && tag.getOrDefault(EidolonDataComponents.CONSECRATED.get(), 0) > 0) {
                event.setNewDamage(event.getNewDamage() * 1.5f);
                tag.set(EidolonDataComponents.CONSECRATED, tag.getOrDefault(EidolonDataComponents.CONSECRATED.get(), 1) - 1);
            }
        }
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (!world.getCapability(IReputation.INSTANCE).isPresent()) return false;
        if (world.getCapability(IReputation.INSTANCE).resolve().get().getReputation(player, Deities.LIGHT_DEITY.getId()) < 10.0) {
            player.displayClientMessage(Component.translatable("eidolon.message.not_enough_reputation"), true);
            return false;
        }

        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() != 1) return false;
        ItemStack stack = items.get(0).getItem();
        return stack.getCount() == 1 && canTouch(stack);
    }

    boolean canTouch(ItemStack stack) {
        return stack.getItem() == Registry.GOLD_INLAY.get()
                || stack.getItem() == Items.BLACK_WOOL
                || (stack.is(Tags.Items.MUSIC_DISCS) && stack.getItem() != Registry.PAROUSIA_DISC.get())
                || (stack.isDamageableItem() && stack.getMaxStackSize() == 1); // is a tool
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        if (stack.getItem() == Registry.GOLD_INLAY.get())
            return new ItemStack(Registry.HOLY_SYMBOL.get());
        else if (stack.getItem() == Items.BLACK_WOOL)
            return new ItemStack(Registry.TOP_HAT.get());
        else if (stack.is(Tags.Items.MUSIC_DISCS) && stack.getItem() != Registry.PAROUSIA_DISC.get())
            return new ItemStack(Registry.PAROUSIA_DISC.get());
        else {
            IMana.expendMana(player, getCost());
            stack.set(EidolonDataComponents.CONSECRATED, 50);
            return stack;
        }

    }
}
