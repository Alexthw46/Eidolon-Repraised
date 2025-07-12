package elucent.eidolon.mixin;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

@Mixin(PotionBrewing.class)
public interface PotionBrewingMixin {
    @Invoker
    static void callAddMix(Holder<Potion> input, Item ingredient, Holder<Potion> output) {
    }
}
