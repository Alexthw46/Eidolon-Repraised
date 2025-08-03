package elucent.eidolon.capability;

import elucent.eidolon.api.capability.IKnowledge;
import elucent.eidolon.api.spells.Rune;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.registries.EidolonAttachments;
import elucent.eidolon.registries.Runes;
import elucent.eidolon.registries.Signs;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class KnowledgeImpl implements IKnowledge {

    LivingEntity entity;
    KnowledgeData knowledgeData;

    public KnowledgeImpl(LivingEntity entity) {
        this.entity = entity;
        this.knowledgeData = entity.getData(EidolonAttachments.KNOWLEDGE_ATTACHMENT.get());
    }


    @Override
    public boolean knowsSign(Sign sign) {
        return knowledgeData.getKnownSigns().contains(sign);
    }

    @Override
    public void addSign(Sign sign) {
        knowledgeData.addSign(sign);
    }

    @Override
    public void removeSign(Sign sign) {
        knowledgeData.removeSign(sign);
    }

    @Override
    public void resetSigns() {
        knowledgeData.resetSigns();
    }

    @Override
    public Set<Sign> getKnownSigns() {
        return knowledgeData.getKnownSigns();
    }

    @Override
    public boolean knowsFact(ResourceLocation fact) {
        return knowledgeData.getKnownFacts().contains(fact);
    }

    @Override
    public void addFact(ResourceLocation fact) {
        knowledgeData.addFact(fact);
    }

    @Override
    public void removeFact(ResourceLocation fact) {
        knowledgeData.removeFact(fact);
    }

    @Override
    public void resetFacts() {
        knowledgeData.resetFacts();
    }

    @Override
    public Set<ResourceLocation> getKnownFacts() {
        return knowledgeData.getKnownFacts();
    }

    @Override
    public boolean knowsResearch(ResourceLocation research) {
        return knowledgeData.getKnownResearches().contains(research);
    }

    @Override
    public void addResearch(ResourceLocation research) {
        knowledgeData.addResearch(research);
    }

    @Override
    public void removeResearch(ResourceLocation research) {
        knowledgeData.removeResearch(research);
    }

    @Override
    public void resetResearch() {
        knowledgeData.resetResearches();
    }

    @Override
    public Set<ResourceLocation> getKnownResearches() {
        return knowledgeData.getKnownResearches();
    }

    @Override
    public boolean knowsRune(Rune rune) {
        return knowledgeData.getKnownRunes().contains(rune);
    }

    @Override
    public void addRune(Rune rune) {
        knowledgeData.addRune(rune);
    }

    @Override
    public void removeRune(Rune rune) {
        knowledgeData.removeRune(rune);
    }

    @Override
    public void resetRunes() {
        knowledgeData.resetRunes();
    }

    @Override
    public Set<Rune> getKnownRunes() {
        return knowledgeData.getKnownRunes();
    }

    public KnowledgeData getKnowledgeData() {
        return knowledgeData;
    }

    public static class KnowledgeData implements INBTSerializable<CompoundTag> {
        private final Set<Sign> signs = new HashSet<>();
        private final Set<ResourceLocation> facts = new HashSet<>();
        private final Set<ResourceLocation> researches = new HashSet<>();
        private final Set<Rune> runes = new HashSet<>();

        // Getter e metodi per Sign
        public Set<Sign> getKnownSigns() {
            return signs;
        }

        public void addSign(Sign sign) {
            signs.add(sign);
        }

        public void removeSign(Sign sign) {
            signs.remove(sign);
        }

        public void resetSigns() {
            signs.clear();
        }

        // Getter e metodi per Facts
        public Set<ResourceLocation> getKnownFacts() {
            return facts;
        }

        public void addFact(ResourceLocation fact) {
            facts.add(fact);
        }

        public void removeFact(ResourceLocation fact) {
            facts.remove(fact);
        }

        public void resetFacts() {
            facts.clear();
        }

        // Getter e metodi per Researches
        public Set<ResourceLocation> getKnownResearches() {
            return researches;
        }

        public void addResearch(ResourceLocation research) {
            researches.add(research);
        }

        public void removeResearch(ResourceLocation research) {
            researches.remove(research);
        }

        public void resetResearches() {
            researches.clear();
        }

        // Getter e metodi per Runes
        public Set<Rune> getKnownRunes() {
            return runes;
        }

        public void addRune(Rune rune) {
            runes.add(rune);
        }

        public void removeRune(Rune rune) {
            runes.remove(rune);
        }

        public void resetRunes() {
            runes.clear();
        }

        // Serializzazione
        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            ListTag signs = new ListTag();
            for (Sign s : getKnownSigns()) {
                signs.add(StringTag.valueOf(s.getRegistryName().toString()));
            }

            ListTag facts = new ListTag();
            for (ResourceLocation s : getKnownFacts()) {
                facts.add(StringTag.valueOf(s.toString()));
            }

            ListTag researches = new ListTag();
            for (ResourceLocation s : getKnownResearches()) {
                researches.add(StringTag.valueOf(s.toString()));
            }

            ListTag runes = new ListTag();
            for (Rune r : getKnownRunes()) {
                runes.add(StringTag.valueOf(r.getRegistryName().toString()));
            }

            CompoundTag wrapper = new CompoundTag();
            wrapper.put("signs", signs);
            wrapper.put("facts", facts);
            wrapper.put("researches", researches);
            wrapper.put("runes", runes);
            return wrapper;
        }

        // Deserializzazione
        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
            resetSigns();
            resetFacts();
            resetResearches();
            resetRunes();

            if (nbt.contains("signs")) {
                ListTag list = nbt.getList("signs", Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    ResourceLocation loc = ResourceLocation.tryParse(list.getString(i));
                    Sign s = Signs.find(loc);
                    if (s != null) addSign(s);
                }
            }

            if (nbt.contains("facts")) {
                ListTag list = nbt.getList("facts", Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    addFact(ResourceLocation.tryParse(list.getString(i)));
                }
            }

            if (nbt.contains("researches")) {
                ListTag list = nbt.getList("researches", Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    addResearch(ResourceLocation.tryParse(list.getString(i)));
                }
            }

            if (nbt.contains("runes")) {
                ListTag list = nbt.getList("runes", Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    ResourceLocation loc = ResourceLocation.tryParse(list.getString(i));
                    Rune r = Runes.find(loc);
                    if (r != null) addRune(r);
                }
            }
        }
    }
}
