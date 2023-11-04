package elucent.eidolon.common.item;

import elucent.eidolon.registries.EidolonEntities;
import elucent.eidolon.registries.EidolonSounds;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SoulfireWandItem extends WandItem {
    static final Random random = new Random();

    public SoulfireWandItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull final Level world, @NotNull final Player entity, @NotNull final InteractionHand hand) {
        return handleCast(world, entity, hand, EidolonEntities.SOULFIRE_PROJECTILE.get(), EidolonSounds.CAST_SOULFIRE_EVENT.get());
    }
}
