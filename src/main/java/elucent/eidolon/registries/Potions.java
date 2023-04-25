package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.Registry;
import elucent.eidolon.mixin.PotionBrewingMixin;
import elucent.eidolon.potion.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Potions {
    public static final DeferredRegister<MobEffect> POTIONS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Eidolon.MODID);
    public static final DeferredRegister<Potion> POTION_TYPES = DeferredRegister.create(ForgeRegistries.POTIONS, Eidolon.MODID);

    public static final RegistryObject<MobEffect> UNDEATH_EFFECT = POTIONS.register("undeath", UndeathEffect::new);
    public static final RegistryObject<Potion> LONG_UNDEATH_POTION = POTION_TYPES.register("long_undeath", () -> new Potion(new MobEffectInstance(UNDEATH_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> UNDEATH_POTION = POTION_TYPES.register("undeath", () -> new Potion(new MobEffectInstance(UNDEATH_EFFECT.get(), 3600)));
    public static final RegistryObject<MobEffect> VULNERABLE_EFFECT = POTIONS.register("vulnerable", () -> new VulnerableEffect()
            .addAttributeModifier(Attributes.ARMOR, "e5bae4de-2019-4316-b8cc-b4d879d676f9", -0.25, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<Potion> STRONG_VULNERABLE_POTION = POTION_TYPES.register("strong_vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT.get(), 1800, 1)));
    public static final RegistryObject<Potion> LONG_VULNERABLE_POTION = POTION_TYPES.register("long_vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> VULNERABLE_POTION = POTION_TYPES.register("vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT.get(), 3600)));
    public static final RegistryObject<MobEffect> REINFORCED_EFFECT = POTIONS.register("reinforced", () -> new ReinforcedEffect()
            .addAttributeModifier(Attributes.ARMOR, "483b6415-421e-45d1-ab28-d85d11a19c70", 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<Potion> STRONG_REINFORCED_POTION = POTION_TYPES.register("strong_reinforced", () -> new Potion(new MobEffectInstance(REINFORCED_EFFECT.get(), 1800, 1)));
    public static final RegistryObject<Potion> LONG_REINFORCED_POTION = POTION_TYPES.register("long_reinforced", () -> new Potion(new MobEffectInstance(REINFORCED_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> REINFORCED_POTION = POTION_TYPES.register("reinforced", () -> new Potion(new MobEffectInstance(REINFORCED_EFFECT.get(), 3600)));
    public static final RegistryObject<MobEffect> ANCHORED_EFFECT = POTIONS.register("anchored", AnchoredEffect::new);
    public static final RegistryObject<Potion> LONG_ANCHORED_POTION = POTION_TYPES.register("long_anchored", () -> new Potion(new MobEffectInstance(ANCHORED_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> ANCHORED_POTION = POTION_TYPES.register("anchored", () -> new Potion(new MobEffectInstance(ANCHORED_EFFECT.get(), 3600)));
    public static final RegistryObject<MobEffect>
            CHILLED_EFFECT = POTIONS.register("chilled", ChilledEffect::new);
    public static final RegistryObject<Potion> LONG_CHILLED_POTION = POTION_TYPES.register("long_chilled", () -> new Potion(new MobEffectInstance(CHILLED_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion>
            CHILLED_POTION = POTION_TYPES.register("chilled", () -> new Potion(new MobEffectInstance(CHILLED_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> STRONG_DECAY_POTION = POTION_TYPES.register("strong_decay", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 450, 1)));
    public static final RegistryObject<Potion> LONG_DECAY_POTION = POTION_TYPES.register("long_decay", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 1800)));
    public static final RegistryObject<Potion> DECAY_POTION = POTION_TYPES.register("decay", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 900)));

    public static void addBrewingRecipes() {
        PotionBrewingMixin.callAddMix(net.minecraft.world.item.alchemy.Potions.WATER, Registry.FUNGUS_SPROUTS.get(), net.minecraft.world.item.alchemy.Potions.AWKWARD);
        PotionBrewingMixin.callAddMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Registry.WRAITH_HEART.get(), CHILLED_POTION.get());
        PotionBrewingMixin.callAddMix(CHILLED_POTION.get(), Items.REDSTONE, LONG_CHILLED_POTION.get());
        PotionBrewingMixin.callAddMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Registry.WARPED_SPROUTS.get(), ANCHORED_POTION.get());
        PotionBrewingMixin.callAddMix(ANCHORED_POTION.get(), Items.REDSTONE, LONG_ANCHORED_POTION.get());
        PotionBrewingMixin.callAddMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.NAUTILUS_SHELL, REINFORCED_POTION.get());
        PotionBrewingMixin.callAddMix(REINFORCED_POTION.get(), Items.REDSTONE, LONG_REINFORCED_POTION.get());
        PotionBrewingMixin.callAddMix(REINFORCED_POTION.get(), Items.GLOWSTONE, STRONG_REINFORCED_POTION.get());
        PotionBrewingMixin.callAddMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Registry.TATTERED_CLOTH.get(), VULNERABLE_POTION.get());
        PotionBrewingMixin.callAddMix(VULNERABLE_POTION.get(), Items.REDSTONE, LONG_VULNERABLE_POTION.get());
        PotionBrewingMixin.callAddMix(VULNERABLE_POTION.get(), Items.GLOWSTONE, STRONG_VULNERABLE_POTION.get());
        PotionBrewingMixin.callAddMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Registry.DEATH_ESSENCE.get(), UNDEATH_POTION.get());
        PotionBrewingMixin.callAddMix(UNDEATH_POTION.get(), Items.REDSTONE, LONG_UNDEATH_POTION.get());
        PotionBrewingMixin.callAddMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Registry.WITHERED_HEART.get(), DECAY_POTION.get());
        PotionBrewingMixin.callAddMix(DECAY_POTION.get(), Items.REDSTONE, LONG_DECAY_POTION.get());
        PotionBrewingMixin.callAddMix(DECAY_POTION.get(), Items.GLOWSTONE, STRONG_DECAY_POTION.get());
    }
}
