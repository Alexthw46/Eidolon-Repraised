package alexthw.eidolon_repraised.api.capability;

import alexthw.eidolon_repraised.registries.EidolonAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public interface ISoul {

    boolean hasEtherealHealth();

    float getMaxEtherealHealth();

    float getEtherealHealth();

    void setEtherealHealth(float health);

    void setMaxEtherealHealth(float max);

    default float hurtEtherealHealth(float amount, float persistentHealth) {
        amount = Math.max(0, amount);
        float oldHealth = getEtherealHealth();
        setMaxEtherealHealth(Math.max(getMaxEtherealHealth() - amount, Math.min(persistentHealth, getMaxEtherealHealth())));
        setEtherealHealth(oldHealth - amount);
        return Math.max(0, amount - oldHealth);
    }

    default void healEtherealHealth(float amount, float persistentHealth) {
        amount = Math.max(0, amount);
        setEtherealHealth(Math.min(Math.max(getEtherealHealth(), persistentHealth), getEtherealHealth() + amount));
    }

    static float getPersistentHealth(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(EidolonAttributes.PERSISTENT_SOUL_HEARTS);
        if (attr != null) return (float) attr.getValue();
        else return 0;
    }

}
