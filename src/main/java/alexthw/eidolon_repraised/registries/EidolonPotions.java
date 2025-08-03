package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.common.potion.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static alexthw.eidolon_repraised.Eidolon.MODID;
import static net.minecraft.world.item.alchemy.Potions.AWKWARD;
import static net.minecraft.world.item.alchemy.Potions.WATER;

@EventBusSubscriber(modid = MODID)
public class EidolonPotions {
    public static final DeferredRegister<MobEffect> POTIONS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
    public static final DeferredRegister<Potion> POTION_TYPES = DeferredRegister.create(Registries.POTION, MODID);

    public static final DeferredHolder<MobEffect, MobEffect> UNDEATH_EFFECT = POTIONS.register("undeath", UndeathEffect::new);
    public static final DeferredHolder<Potion, Potion> LONG_UNDEATH_POTION = POTION_TYPES.register("long_undeath", () -> new Potion(new MobEffectInstance(UNDEATH_EFFECT, 9600)));
    public static final DeferredHolder<Potion, Potion> UNDEATH_POTION = POTION_TYPES.register("undeath", () -> new Potion(new MobEffectInstance(UNDEATH_EFFECT, 3600)));
    public static final DeferredHolder<MobEffect, MobEffect> VULNERABLE_EFFECT = POTIONS.register("vulnerable", () -> new VulnerableEffect().addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(MODID, "vulnerable"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<Potion, Potion> STRONG_VULNERABLE_POTION = POTION_TYPES.register("strong_vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> LONG_VULNERABLE_POTION = POTION_TYPES.register("long_vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT, 9600)));
    public static final DeferredHolder<Potion, Potion> VULNERABLE_POTION = POTION_TYPES.register("vulnerable", () -> new Potion(new MobEffectInstance(VULNERABLE_EFFECT, 3600)));
    public static final DeferredHolder<MobEffect, MobEffect> REINFORCED_EFFECT = POTIONS.register("reinforced", () -> new ReinforcedEffect().addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(MODID, "reinforced"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
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

    @SubscribeEvent
    private static void addRecipes(final RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addMix(WATER, Registry.FUNGUS_SPROUTS.get(), AWKWARD);
        builder.addMix(AWKWARD, Registry.WRAITH_HEART.get(), CHILLED_POTION);
        builder.addMix(CHILLED_POTION, Items.REDSTONE, LONG_CHILLED_POTION);
        builder.addMix(AWKWARD, Registry.WARPED_SPROUTS.get(), ANCHORED_POTION);
        builder.addMix(ANCHORED_POTION, Items.REDSTONE, LONG_ANCHORED_POTION);
        builder.addMix(AWKWARD, Items.NAUTILUS_SHELL, REINFORCED_POTION);
        builder.addMix(REINFORCED_POTION, Items.REDSTONE, LONG_REINFORCED_POTION);
        builder.addMix(REINFORCED_POTION, Items.GLOWSTONE_DUST, STRONG_REINFORCED_POTION);
        builder.addMix(AWKWARD, Registry.TATTERED_CLOTH.get(), VULNERABLE_POTION);
        builder.addMix(VULNERABLE_POTION, Items.REDSTONE, LONG_VULNERABLE_POTION);
        builder.addMix(VULNERABLE_POTION, Items.GLOWSTONE_DUST, STRONG_VULNERABLE_POTION);
        builder.addMix(AWKWARD, Registry.DEATH_ESSENCE.get(), UNDEATH_POTION);
        builder.addMix(UNDEATH_POTION, Items.REDSTONE, LONG_UNDEATH_POTION);
        builder.addMix(AWKWARD, Registry.WITHERED_HEART.get(), DECAY_POTION);
        builder.addMix(DECAY_POTION, Items.REDSTONE, LONG_DECAY_POTION);
        builder.addMix(DECAY_POTION, Items.GLOWSTONE_DUST, STRONG_DECAY_POTION);
    }
}
