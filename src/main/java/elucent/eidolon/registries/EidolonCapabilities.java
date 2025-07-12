package elucent.eidolon.registries;

import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.capability.KnowledgeImpl;
import elucent.eidolon.capability.PatronManaImpl;
import elucent.eidolon.capability.ReputationImpl;
import elucent.eidolon.capability.SoulImpl;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class EidolonCapabilities {


    public static final EntityCapability<PatronManaImpl, @Nullable Void> MANA_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("mana"), PatronManaImpl.class);
    public static final EntityCapability<SoulImpl, @Nullable Void> SOUL_HEART_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("soul"), SoulImpl.class);
    public static final EntityCapability<KnowledgeImpl, @Nullable Void> KNOWLEDGE_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("knowledge"), KnowledgeImpl.class);
    public static final EntityCapability<ReputationImpl, @Nullable Void> REPUTATION_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("reputation"), ReputationImpl.class);

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(MANA_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new PatronManaImpl(player));
    }

}
