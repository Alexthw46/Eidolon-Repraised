package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.common.potion.*;
import elucent.eidolon.mixin.PotionBrewingMixin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.minecraft.world.item.alchemy.Potions.AWKWARD;
import static net.minecraft.world.item.alchemy.Potions.WATER;

public class EidolonPotions {
    public static final DeferredRegister<MobEffect> POTIONS = DeferredRegister.create(Registries.MOB_EFFECT, Eidolon.MODID);
    public static final DeferredRegister<Potion> POTION_TYPES = DeferredRegister.create(Registries.POTION, Eidolon.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> UNDEATH_EFFECT = POTIONS.register("undeath", UndeathEffect::new);
    public static final DeferredHolder<Potion, Potion> LONG_UNDEATH_POTION = POTION_TYPES.register("long_undeath", () -> new Potion(new MobEffectInstance(UNDEATH_EFFECT, 9600)));
    public static final DeferredHolder<Potion, Potion> UNDEATH_POTION = POTION_TYPES.register("undeath", () -> new Potion(new MobEffectInstance(UNDEATH_EFFECT, 3600)));
    public static final DeferredHolder<MobEffect, MobEffect> VULNERABLE_EFFECT = POTIONS.register("vulnerable", () -> new VulnerableEffect().addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "vulnerable"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<Potion, Potion> STRONG_VULNERABLE_POTION = POTION_TYPES.register("strong_vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> LONG_VULNERABLE_POTION = POTION_TYPES.register("long_vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT, 9600)));
    public static final DeferredHolder<Potion, Potion> VULNERABLE_POTION = POTION_TYPES.register("vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT, 3600)));
    public static final DeferredHolder<MobEffect, MobEffect> REINFORCED_EFFECT = POTIONS.register("reinforced", () -> new ReinforcedEffect().addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "reinforced"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<Potion, Potion> STRONG_REINFORCED_POTION = POTION_TYPES.register("strong_reinforced", () -> new Potion(new MobEffectInstance(REINFORCED_EFFECT, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> LONG_REINFORCED_POTION = POTION_TYPES.register("long_reinforced", () -> new Potion(new MobEffectInstance(REINFORCED_EFFECT, 9600)));
    public static final DeferredHolder<Potion, Potion> REINFORCED_POTION = POTION_TYPES.register("reinforced", () -> new Potion(new MobEffectInstance(REINFORCED_EFFECT, 3600)));
    public static final DeferredHolder<MobEffect, MobEffect> ANCHORED_EFFECT = POTIONS.register("anchored", AnchoredEffect::new);
    public static final DeferredHolder<Potion, Potion> LONG_ANCHORED_POTION = POTION_TYPES.register("long_anchored", () -> new Potion(new MobEffectInstance(ANCHORED_EFFECT, 9600)));
    public static final DeferredHolder<Potion, Potion> ANCHORED_POTION = POTION_TYPES.register("anchored", () -> new Potion(new MobEffectInstance(ANCHORED_EFFECT, 3600)));
    public static final DeferredHolder<MobEffect, MobEffect> CHILLED_EFFECT = POTIONS.register("chilled", ChilledEffect::new);
    public static final DeferredHolder<Potion, Potion> LONG_CHILLED_POTION = POTION_TYPES.register("long_chilled", () -> new Potion(new MobEffectInstance(CHILLED_EFFECT, 9600)));
    public static final DeferredHolder<Potion, Potion> CHILLED_POTION = POTION_TYPES.register("chilled", () -> new Potion(new MobEffectInstance(CHILLED_EFFECT, 3600)));
    public static final DeferredHolder<Potion, Potion> STRONG_DECAY_POTION = POTION_TYPES.register("strong_decay", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 450, 1)));
    public static final DeferredHolder<Potion, Potion> LONG_DECAY_POTION = POTION_TYPES.register("long_decay", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 1800)));
    public static final DeferredHolder<Potion, Potion> DECAY_POTION = POTION_TYPES.register("decay", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 900)));
    public static final DeferredHolder<MobEffect, MobEffect> LIGHT_BLESSED = POTIONS.register("light_blessing", BlessedEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> SOUL_HARVEST = POTIONS.register("soul_harvest", SHarvestEffect::new);

    public static void addBrewingRecipes() {
        PotionBrewingMixin.callAddMix(WATER, Registry.FUNGUS_SPROUTS.get(), AWKWARD);
        PotionBrewingMixin.callAddMix(AWKWARD, Registry.WRAITH_HEART.get(), CHILLED_POTION);
        PotionBrewingMixin.callAddMix(CHILLED_POTION, Items.REDSTONE, LONG_CHILLED_POTION);
        PotionBrewingMixin.callAddMix(AWKWARD, Registry.WARPED_SPROUTS.get(), ANCHORED_POTION);
        PotionBrewingMixin.callAddMix(ANCHORED_POTION, Items.REDSTONE, LONG_ANCHORED_POTION);
        PotionBrewingMixin.callAddMix(AWKWARD, Items.NAUTILUS_SHELL, REINFORCED_POTION);
        PotionBrewingMixin.callAddMix(REINFORCED_POTION, Items.REDSTONE, LONG_REINFORCED_POTION);
        PotionBrewingMixin.callAddMix(REINFORCED_POTION, Items.GLOWSTONE_DUST, STRONG_REINFORCED_POTION);
        PotionBrewingMixin.callAddMix(AWKWARD, Registry.TATTERED_CLOTH.get(), VULNERABLE_POTION);
        PotionBrewingMixin.callAddMix(VULNERABLE_POTION, Items.REDSTONE, LONG_VULNERABLE_POTION);
        PotionBrewingMixin.callAddMix(VULNERABLE_POTION, Items.GLOWSTONE_DUST, STRONG_VULNERABLE_POTION);
        PotionBrewingMixin.callAddMix(AWKWARD, Registry.DEATH_ESSENCE.get(), UNDEATH_POTION);
        PotionBrewingMixin.callAddMix(UNDEATH_POTION, Items.REDSTONE, LONG_UNDEATH_POTION);
        PotionBrewingMixin.callAddMix(AWKWARD, Registry.WITHERED_HEART.get(), DECAY_POTION);
        PotionBrewingMixin.callAddMix(DECAY_POTION, Items.REDSTONE, LONG_DECAY_POTION);
        PotionBrewingMixin.callAddMix(DECAY_POTION, Items.GLOWSTONE_DUST, STRONG_DECAY_POTION);
    }
}
