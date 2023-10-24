package elucent.eidolon.compat.irons_spellbooks;

import elucent.eidolon.compat.CompatHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

public class IronsSpellbooks {
    private static Attribute SPELL_POWER;
    private static Attribute COOLDOWN_REDUCTION;

    public static float getSpellPower(final Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 1;
        }

        if (SPELL_POWER == null) {
            SPELL_POWER = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(CompatHandler.IRONS_SPELLBOOKS, "spell_power"));
        }

        return (float) livingEntity.getAttributeValue(SPELL_POWER);
    }

    public static float getCooldownReduction(final Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 1;
        }

        if (COOLDOWN_REDUCTION == null) {
            COOLDOWN_REDUCTION = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(CompatHandler.IRONS_SPELLBOOKS, "cooldown_reduction"));
        }

        return (float) livingEntity.getAttributeValue(COOLDOWN_REDUCTION);
    }

    public static void initialize() { /* Nothing to do */ }
}
