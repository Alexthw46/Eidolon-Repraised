package elucent.eidolon.api.capability;

import elucent.eidolon.api.research.Research;
import elucent.eidolon.api.spells.Rune;
import elucent.eidolon.api.spells.Sign;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public interface IKnowledge {

    boolean knowsSign(Sign sign);

    void addSign(Sign sign);

    void removeSign(Sign sign);

    void resetSigns();

    Set<Sign> getKnownSigns();

    boolean knowsFact(ResourceLocation fact);

    void addFact(ResourceLocation fact);

    void removeFact(ResourceLocation fact);

    void resetFacts();

    Set<ResourceLocation> getKnownFacts();

    boolean knowsResearch(ResourceLocation research);

    default boolean knowsResearch(Research research) {
        return knowsResearch(research.getRegistryName());
    }

    void addResearch(ResourceLocation research);

    void removeResearch(ResourceLocation research);

    void resetResearch();

    Set<ResourceLocation> getKnownResearches();

    boolean knowsRune(Rune rune);

    void addRune(Rune rune);

    void removeRune(Rune rune);

    void resetRunes();

    Set<Rune> getKnownRunes();
}
