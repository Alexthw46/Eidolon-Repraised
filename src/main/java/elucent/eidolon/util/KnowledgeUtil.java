package elucent.eidolon.util;

import elucent.eidolon.api.research.Research;
import elucent.eidolon.api.spells.Rune;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.capability.IKnowledge;
import elucent.eidolon.api.capability.IReputation;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.network.KnowledgeUpdatePacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.AdvancementTriggers;
import elucent.eidolon.registries.EidolonAttachments;
import elucent.eidolon.registries.EidolonCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class KnowledgeUtil {

    public static void grantSign(Entity entity, Sign sign) {
        if (!(entity instanceof ServerPlayer player)) return;

        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null || knowledge.knowsSign(sign)) return;

        knowledge.addSign(sign);
        player.connection.send(new ClientboundSetActionBarTextPacket(
                Component.translatable("eidolon.title.new_sign",
                        Component.translatable(sign.getRegistryName().getNamespace() + ".sign." + sign.getRegistryName().getPath()))
        ));
        AdvancementTriggers.triggerSign(sign, player);
        Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
    }

    public static void grantFact(Entity entity, ResourceLocation fact) {
        if (!(entity instanceof ServerPlayer player)) return;

        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null || knowledge.knowsFact(fact)) return;

        knowledge.addFact(fact);
        player.connection.send(new ClientboundSetActionBarTextPacket(
                Component.translatable("eidolon.title.new_fact")
        ));
        Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        AdvancementTriggers.triggerResearch(fact.getPath(), player);
    }

    public static void grantResearch(Entity entity, @NotNull Research research) {
        if (!(entity instanceof ServerPlayer player)) return;

        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null || knowledge.knowsResearch(research)) return;

        knowledge.addResearch(research.getRegistryName());
        research.onLearned(player);
        player.connection.send(new ClientboundSetActionBarTextPacket(
                Component.translatable("eidolon.title.new_research",
                        ChatFormatting.GOLD + research.getName())
        ));
        Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        AdvancementTriggers.triggerResearch(research.getRegistryName().toString(), player);
    }

    public static void grantResearchNoToast(Entity entity, @NotNull ResourceLocation research) {
        if (!(entity instanceof ServerPlayer player)) return;

        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null || knowledge.knowsResearch(research)) return;

        knowledge.addResearch(research);
        Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        AdvancementTriggers.triggerResearch(research.getPath(), player);
    }


    public static void grantRune(Entity entity, Rune rune) {
        if (!(entity instanceof ServerPlayer player)) return;

        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null || knowledge.knowsRune(rune)) return;

        knowledge.addRune(rune);
        player.connection.send(new ClientboundSetActionBarTextPacket(
                Component.translatable("eidolon.title.new_rune",
                        Component.translatable(rune.getRegistryName().getNamespace() + ".rune." + rune.getRegistryName().getPath()))
        ));
        Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
    }

    public static List<Sign> getKnownSigns(Player player) {
        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        return knowledge != null ? new ArrayList<>(knowledge.getKnownSigns()) : Collections.emptyList();
    }

    public static boolean knowsSign(Player player, Sign sign) {
        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        return knowledge != null && knowledge.knowsSign(sign);
    }

    public static boolean knowsFact(Player player, ResourceLocation fact) {
        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        return knowledge != null && knowledge.knowsFact(fact);
    }

    public static boolean knowsResearch(Player player, ResourceLocation research) {
        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        return knowledge != null && knowledge.knowsResearch(research);
    }

    public static boolean knowsRune(Player player, Rune rune) {
        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        return knowledge != null && knowledge.knowsRune(rune);
    }

    public static void removeSign(Entity entity, Sign sign) {
        removeItem(entity, k -> k.knowsSign(sign), k -> k.removeSign(sign));
    }

    public static void removeFact(Entity entity, ResourceLocation fact) {
        removeItem(entity, k -> k.knowsFact(fact), k -> k.removeFact(fact));
    }

    public static void removeResearch(Entity entity, ResourceLocation research) {
        removeItem(entity, k -> k.knowsResearch(research), k -> k.removeResearch(research));
    }

    public static void removeRune(Entity entity, Rune rune) {
        removeItem(entity, k -> k.knowsRune(rune), k -> k.removeRune(rune));
    }

    public static void resetSigns(Entity entity) {
        resetPart(entity, IKnowledge::resetSigns);
    }

    public static void resetFacts(Entity entity) {
        resetPart(entity, IKnowledge::resetFacts);
    }

    public static void resetResearch(Entity entity) {
        resetPart(entity, IKnowledge::resetResearch);
    }

    public static void resetRunes(Entity entity) {
        resetPart(entity, IKnowledge::resetRunes);
    }

    // --- Internal Utility Methods ---

    private static void removeItem(Entity entity, Predicate<IKnowledge> check, Consumer<IKnowledge> removeAction) {
        if (!(entity instanceof ServerPlayer player)) return;

        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null || !check.test(knowledge)) return;

        removeAction.accept(knowledge);
        Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
    }

    private static void resetPart(Entity entity, Consumer<IKnowledge> resetAction) {
        if (!(entity instanceof ServerPlayer player)) return;

        IKnowledge knowledge = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null) return;

        resetAction.accept(knowledge);
        Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
    }

    public static void tryFix(Player player) {
        if (!(player instanceof ServerPlayer && player.level() instanceof ServerLevel server)) return;
        var devotion = server.getData(EidolonAttachments.REPUTATION);
        if (devotion != null) {
            IReputation d = devotion;
            Deities.getDeities().forEach((deity) -> {
                var rep = d.getReputation(player, deity.getId());
                var curStage = deity.getProgression().last(rep);
                double fakeRep = 1;
                int counter = 0; // Prevent infinite loops, just in case, I don't trust this enough to leave it unchecked
                while (fakeRep < curStage.rep() && counter++ < 20) {
                    var next = deity.getProgression().next(fakeRep);
                    if (next == null) break;
                    deity.onReputationLock(player, next.id());
                    deity.onReputationUnlock(player, next.id());
                    fakeRep = next.rep() + 1;
                }
            });
        }
    }

}
