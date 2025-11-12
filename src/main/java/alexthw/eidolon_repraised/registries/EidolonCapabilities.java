package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.capability.KnowledgeImpl;
import alexthw.eidolon_repraised.capability.PatronManaImpl;
import alexthw.eidolon_repraised.capability.ReputationImpl;
import alexthw.eidolon_repraised.capability.SoulImpl;
import alexthw.eidolon_repraised.capability.WingsDataImpl;
import alexthw.eidolon_repraised.network.KnowledgeUpdatePacket;
import alexthw.eidolon_repraised.network.ManaUpdatePacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.ReputationUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static alexthw.eidolon_repraised.registries.Registry.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Eidolon.MODID)
public class EidolonCapabilities {

    public static final EntityCapability<PatronManaImpl, @Nullable Void> MANA_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("mana"), PatronManaImpl.class);
    public static final EntityCapability<SoulImpl, @Nullable Void> SOUL_HEART_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("soul"), SoulImpl.class);
    public static final EntityCapability<KnowledgeImpl, @Nullable Void> KNOWLEDGE_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("knowledge"), KnowledgeImpl.class);
    public static final EntityCapability<ReputationImpl, @Nullable Void> REPUTATION_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("reputation"), ReputationImpl.class);
    public static final EntityCapability<WingsDataImpl, @Nullable Void> WINGS_CAPABILITY = EntityCapability.createVoid(Eidolon.prefix("player"), WingsDataImpl.class);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(MANA_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new PatronManaImpl(player));
        event.registerEntity(SOUL_HEART_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new SoulImpl(player));
        event.registerEntity(KNOWLEDGE_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new KnowledgeImpl(player));
        event.registerEntity(REPUTATION_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new ReputationImpl(player));
        event.registerEntity(WINGS_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new WingsDataImpl(player));

        var invWrappers = List.of(HAND_TILE_ENTITY.get(), CENSER_TILE_ENTITY.get(), NECROTIC_FOCUS_TILE_ENTITY.get(), BRAZIER_TILE_ENTITY.get());

        for (var tileEntity : invWrappers) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, tileEntity, (blockEntity, ctx) -> new InvWrapper(blockEntity));
        }

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CRUCIBLE_TILE_ENTITY.get(), (blockEntity, ctx) -> blockEntity.tank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CRUCIBLE_TILE_ENTITY.get(), (blockEntity, ctx) -> new InvWrapper(blockEntity));

    }

    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncCaps(player);
        }
    }


    @SubscribeEvent
    public static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof ServerPlayer player) {
            syncCaps(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDimChangedEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncCaps(player);
        }
    }

    public static void syncCaps(ServerPlayer player) {
        Networking.sendToPlayerClient(new KnowledgeUpdatePacket(player, false), player);
        Networking.sendToPlayerClient(new ReputationUpdatePacket(player, false), player);
        Networking.sendToPlayerClient(new ManaUpdatePacket(player), player);
    }
}
