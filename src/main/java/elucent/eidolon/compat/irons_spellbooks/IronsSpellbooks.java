package elucent.eidolon.compat.irons_spellbooks;

import elucent.eidolon.compat.CompatHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

public class IronsSpellbooks {
    private static Attribute SPELL_POWER;

    public static float getSpellPower(final Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 1;
        }

        if (SPELL_POWER == null) {
            // Initialize is probably too early for this
            SPELL_POWER = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(CompatHandler.IRONS_SPELLBOOKS, "spell_power"));
        }

        return (float) livingEntity.getAttributeValue(SPELL_POWER);
    }

    public static void initialize() { /* Nothing to do */ }
}
