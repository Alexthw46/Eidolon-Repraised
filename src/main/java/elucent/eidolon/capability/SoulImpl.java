package elucent.eidolon.capability;

import elucent.eidolon.Config;
import elucent.eidolon.api.capability.ISoul;
import elucent.eidolon.registries.EidolonAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class SoulImpl implements ISoul {

    SoulHeartData soulHeartData;
    LivingEntity entity;

    public SoulImpl(LivingEntity entity) {
        this.entity = entity;
        this.soulHeartData = entity.getData(EidolonAttachments.SOUL_ATTACHMENT.get());
    }

    @Override
    public boolean hasEtherealHealth() {
        return soulHeartData.maxEtherealHealth > 0;
    }

    @Override
    public float getMaxEtherealHealth() {
        return soulHeartData.maxEtherealHealth;
    }

    @Override
    public float getEtherealHealth() {
        return soulHeartData.etherealHealth;
    }

    @Override
    public void setEtherealHealth(float health) {
        this.soulHeartData.etherealHealth = Mth.clamp(health, 0, soulHeartData.maxEtherealHealth);
        entity.setData(EidolonAttachments.SOUL_ATTACHMENT, soulHeartData);
    }

    @Override
    public void setMaxEtherealHealth(float max) {
        this.soulHeartData.maxEtherealHealth = Mth.clamp(max, 0, Config.MAX_ETHEREAL_HEALTH.get());
        this.soulHeartData.etherealHealth = Math.min(soulHeartData.maxEtherealHealth, soulHeartData.etherealHealth);
        entity.setData(EidolonAttachments.SOUL_ATTACHMENT, soulHeartData);

    }

    public static class SoulHeartData implements INBTSerializable<CompoundTag> {
        float maxEtherealHealth, etherealHealth;

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("maxEtherealHealth", maxEtherealHealth);
            tag.putFloat("etherealHealth", etherealHealth);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
            maxEtherealHealth = nbt.contains("maxEtherealHealth") ? nbt.getFloat("maxEtherealHealth") : 0;
            etherealHealth = nbt.contains("etherealHealth") ? nbt.getFloat("etherealHealth") : 0;
        }

    }

}
