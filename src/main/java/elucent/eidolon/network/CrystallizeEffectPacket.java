package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrystallizeEffectPacket extends AbstractPacket {
    public static final Type<CrystallizeEffectPacket> TYPE = new Type<>(Eidolon.prefix("crystallize_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrystallizeEffectPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            pkt -> pkt.pos,
            CrystallizeEffectPacket::new
    );

    final BlockPos pos;

    public CrystallizeEffectPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(CrystallizeEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
    }

    public static CrystallizeEffectPacket decode(FriendlyByteBuf buffer) {
        return new CrystallizeEffectPacket(buffer.readBlockPos());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        BlockPos pos = this.pos;
        double x = pos.getX() + 0.5, y = pos.getY() + 0.1, z = pos.getZ() + 0.5;

        float r = 247 / 255.0f, g = 156 / 255.0f, b = 220 / 255.0f;

        Particles.create(EidolonParticles.SPARKLE_PARTICLE.get())
                    .setAlpha(1.0f, 0).setScale(0.25f, 0).setLifetime(20)
                    .randomOffset(0.5, 0).randomVelocity(0, 0.375f)
                    .addVelocity(0, 0.125f, 0)
                    .setColor(r, g, b, r, g * 0.5f, b * 1.5f)
                    .setSpin(0.4f)
                    .repeat(world, x, y, z, 20);

    }

    public @NotNull Type<CrystallizeEffectPacket> type() {
        return TYPE;
    }

}
