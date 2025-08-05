package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class Networking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        var reg = event.registrar(Eidolon.MODID);

        reg.playToClient(GenericParticlePacket.TYPE, GenericParticlePacket.CODEC, Networking::handle);
        reg.playToClient(ChilledEffectPacket.TYPE, ChilledEffectPacket.CODEC, Networking::handle);
        reg.playToClient(TESyncPacket.TYPE, TESyncPacket.CODEC, Networking::handle);
        reg.playToClient(ExtinguishEffectPacket.TYPE, ExtinguishEffectPacket.CODEC, Networking::handle);
        reg.playToClient(IgniteEffectPacket.TYPE, IgniteEffectPacket.CODEC, Networking::handle);
        reg.playToClient(FlameEffectPacket.TYPE, FlameEffectPacket.CODEC, Networking::handle);
        reg.playToClient(RitualCompletePacket.TYPE, RitualCompletePacket.CODEC, Networking::handle);
        reg.playToClient(RitualConsumePacket.TYPE, RitualConsumePacket.CODEC, Networking::handle);
        reg.playToClient(CrystallizeEffectPacket.TYPE, CrystallizeEffectPacket.CODEC, Networking::handle);
        reg.playToClient(CrucibleFailPacket.TYPE, CrucibleFailPacket.CODEC, Networking::handle);
        reg.playToClient(CrucibleSuccessPacket.TYPE, CrucibleSuccessPacket.CODEC, Networking::handle);
        reg.playToClient(LifestealEffectPacket.TYPE, LifestealEffectPacket.CODEC, Networking::handle);
        reg.playToClient(MagicBurstEffectPacket.TYPE, MagicBurstEffectPacket.CODEC, Networking::handle);
        reg.playToClient(KnowledgeUpdatePacket.TYPE, KnowledgeUpdatePacket.CODEC, Networking::handle);
        reg.playToServer(AttemptCastPacket.TYPE, AttemptCastPacket.CODEC, Networking::handle);
        reg.playToClient(SpellCastPacket.TYPE, SpellCastPacket.CODEC, Networking::handle);
        reg.playToServer(ResearchActionPacket.TYPE, ResearchActionPacket.CODEC, Networking::handle);
        reg.playToClient(DeathbringerSlashEffectPacket.TYPE, DeathbringerSlashEffectPacket.CODEC, Networking::handle);
        reg.playToClient(SoulUpdatePacket.TYPE, SoulUpdatePacket.CODEC, Networking::handle);
        reg.playToServer(WingsFlapPacket.TYPE, WingsFlapPacket.CODEC, Networking::handle);
        reg.playToServer(WingsDashPacket.TYPE, WingsDashPacket.CODEC, Networking::handle);
        reg.playToClient(WingsDataUpdatePacket.TYPE, WingsDataUpdatePacket.CODEC, Networking::handle);
        reg.playToClient(FeatherEffectPacket.TYPE, FeatherEffectPacket.CODEC, Networking::handle);
        reg.playToClient(OpenCodexPacket.TYPE, OpenCodexPacket.CODEC, Networking::handle);
        reg.playBidirectional(InitCodexPacket.TYPE, InitCodexPacket.CODEC, Networking::handle);
        reg.playToClient(InscribePacket.TYPE, InscribePacket.CODEC, Networking::handle);
    }


    public static <T extends AbstractPacket> void handle(T message, IPayloadContext ctx) {
        if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
            handleServer(message, ctx);
        } else {
            //separate class to avoid loading client code on server.
            //Using OnlyIn on a method in this class would work too, but is discouraged
            ClientMessageHandler.handleClient(message, ctx);
        }
    }

    private static <T extends AbstractPacket> void handleServer(T message, IPayloadContext ctx) {
        MinecraftServer server = ctx.player().getServer();
        message.onServerReceived(server, (ServerPlayer) ctx.player());
    }

    private static class ClientMessageHandler {

        public static <T extends AbstractPacket> void handleClient(T message, IPayloadContext ctx) {
            Minecraft minecraft = Minecraft.getInstance();
            message.onClientReceived(minecraft, minecraft.player);
        }
    }

    public static void sendToNearbyClient(Level world, BlockPos pos, CustomPacketPayload toSend) {
        if (world instanceof ServerLevel ws) {
            PacketDistributor.sendToPlayersTrackingChunk(ws, new ChunkPos(pos), toSend);
        }
    }

    public static void sendToNearbyClient(Level world, Entity e, CustomPacketPayload toSend) {
        sendToNearbyClient(world, e.blockPosition(), toSend);
    }

    public static void sendToPlayerClient(CustomPacketPayload msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }
}
