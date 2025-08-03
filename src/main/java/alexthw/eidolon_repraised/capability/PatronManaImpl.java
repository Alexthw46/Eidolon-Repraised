package alexthw.eidolon_repraised.capability;

import alexthw.eidolon_repraised.api.capability.IMana;
import alexthw.eidolon_repraised.registries.EidolonAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class PatronManaImpl implements IMana {

    public static final class ManaData implements INBTSerializable<CompoundTag> {
        private float maxMagic;
        private float magic;

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("maxMagic", maxMagic);
            tag.putFloat("magic", magic);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
            maxMagic = nbt.contains("maxMagic") ? nbt.getFloat("maxMagic") : 0;
            magic = nbt.contains("magic") ? nbt.getFloat("magic") : 0;
        }

        public float maxMagic() {
            return maxMagic;
        }

        public float magic() {
            return magic;
        }

        public void setMagic(float magic) {
            this.magic = magic;
        }

        public void setMaxMagic(float maxMagic) {
            this.maxMagic = maxMagic;
        }

    }

    ManaData manaData;
    LivingEntity entity;

    public PatronManaImpl(LivingEntity entity) {
        this.entity = entity;
        this.manaData = entity.getData(EidolonAttachments.MANA_ATTACHMENT);
    }


    @Override
    public boolean hasMagic() {
        return manaData.maxMagic() > 0;
    }

    @Override
    public float getMaxMagic() {
        return manaData.maxMagic();
    }

    @Override
    public float getMagic() {
        return manaData.magic();
    }

    @Override
    public void setMagic(float magic) {
        this.manaData.setMagic(Mth.clamp(magic, 0, manaData.maxMagic()));
        entity.setData(EidolonAttachments.MANA_ATTACHMENT, manaData);

    }

    @Override
    public void setMaxMagic(float max) {
        this.manaData.setMaxMagic(Math.max(0, max));
        this.manaData.setMagic(Math.min(manaData.magic(), manaData.maxMagic()));
        entity.setData(EidolonAttachments.MANA_ATTACHMENT, manaData);
    }


}
