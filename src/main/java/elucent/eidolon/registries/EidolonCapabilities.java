package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.capability.*;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class EidolonCapabilities {


    public static final EntityCapability<PatronManaImpl, @Nullable Void> MANA_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("mana"), PatronManaImpl.class);
    public static final EntityCapability<SoulImpl, @Nullable Void> SOUL_HEART_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("soul"), SoulImpl.class);
    public static final EntityCapability<KnowledgeImpl, @Nullable Void> KNOWLEDGE_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("knowledge"), KnowledgeImpl.class);
    public static final EntityCapability<ReputationImpl, @Nullable Void> REPUTATION_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("reputation"), ReputationImpl.class);
    public static final EntityCapability<WingsDataImpl, @Nullable Void> WINGS_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("player"), WingsDataImpl.class);

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(MANA_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new PatronManaImpl(player));
    }

}
