package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.capability.KnowledgeImpl;
import alexthw.eidolon_repraised.capability.PatronManaImpl.ManaData;
import alexthw.eidolon_repraised.capability.ReputationImpl;
import alexthw.eidolon_repraised.capability.SoulImpl;
import alexthw.eidolon_repraised.capability.WingsDataImpl;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EidolonAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Eidolon.MODID);

    public static final Supplier<AttachmentType<ManaData>> MANA_ATTACHMENT =
            ATTACHMENT_TYPES.register("mana_cap",
                    () -> AttachmentType.serializable(ManaData::new)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<SoulImpl.SoulHeartData>> SOUL_ATTACHMENT =
            ATTACHMENT_TYPES.register("soul_heart",
                    () -> AttachmentType.serializable(SoulImpl.SoulHeartData::new)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<KnowledgeImpl.KnowledgeData>> KNOWLEDGE_ATTACHMENT =
            ATTACHMENT_TYPES.register("knowledge",
                    () -> AttachmentType.serializable(KnowledgeImpl.KnowledgeData::new)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<WingsDataImpl.WingsData>> WINGS_ATTACHMENT =
            ATTACHMENT_TYPES.register("wings",
                    () -> AttachmentType.serializable(WingsDataImpl.WingsData::new)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<ReputationImpl.ReputationData>> REPUTATION_ATTACHMENT =
            ATTACHMENT_TYPES.register("reputation",
                    () -> AttachmentType.serializable(ReputationImpl.ReputationData::new)
                            .copyOnDeath()
                            .build());


}
