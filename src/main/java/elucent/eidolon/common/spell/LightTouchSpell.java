package elucent.eidolon.common.spell;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.capability.IMana;
import elucent.eidolon.api.capability.IReputation;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.deity.Deities;
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

import java.util.List;

public class LightTouchSpell extends DarkTouchSpell {

    public static final String SACRED_KEY = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "sacred").toString();

    public LightTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, signs);
        NeoForge.EVENT_BUS.addListener(LightTouchSpell::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity caster && event.getEntity().getType().is(EntityTypeTags.UNDEAD)) {
            var tag = caster.getMainHandItem().getTag();
            if (tag != null && tag.contains(SACRED_KEY)) {
                event.setAmount(event.getAmount() * 1.5f);
                tag.putInt(SACRED_KEY, tag.getInt(SACRED_KEY) - 1);
                if (tag.getInt(SACRED_KEY) <= 0) tag.remove(SACRED_KEY);
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
                || (stack.getItem() instanceof RecordItem && stack.getItem() != Registry.PAROUSIA_DISC.get())
                || (stack.isDamageableItem() && stack.getMaxStackSize() == 1); // is a tool
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        if (stack.getItem() == Registry.GOLD_INLAY.get())
            return new ItemStack(Registry.HOLY_SYMBOL.get());
        else if (stack.getItem() == Items.BLACK_WOOL)
            return new ItemStack(Registry.TOP_HAT.get());
        else if (stack.getItem() instanceof RecordItem && stack.getItem() != Registry.PAROUSIA_DISC.get())
            return new ItemStack(Registry.PAROUSIA_DISC.get());
        else {
            IMana.expendMana(player, getCost());
            stack.getOrCreateTag().putInt(SACRED_KEY, 50);
            return stack;
        }

    }
}
