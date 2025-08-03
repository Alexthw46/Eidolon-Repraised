package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FeatherEffectPacket extends AbstractPacket {

    public static final Type<FeatherEffectPacket> TYPE = new Type<>(Eidolon.prefix("feather_effect"));
    public static final StreamCodec<FriendlyByteBuf, FeatherEffectPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, p -> p.x,
            ByteBufCodecs.FLOAT, p -> p.y,
            ByteBufCodecs.FLOAT, p -> p.z,
            FeatherEffectPacket::new
    );

    final float x;
    final float y;
    final float z;

    public FeatherEffectPacket(BlockPos pos) {
        this(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public FeatherEffectPacket(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public static void encode(FeatherEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeFloat(object.x).writeFloat(object.y).writeFloat(object.z);
    }

    public static FeatherEffectPacket decode(FriendlyByteBuf buffer) {
        return new FeatherEffectPacket(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        Level world = player.level();
        double x = this.x, y = this.y + 0.5, z = this.z;

        Particles.create(EidolonParticles.FEATHER_PARTICLE.get())
                .setAlpha(0.5f, 0).setScale(0.5f, 0).setLifetime(20)
                .randomOffset(0.125, 0.125).randomVelocity(0.0625f)
                .setColor(0.2F, 0.2F, 0.7F)
                .repeat(world, x, y, z, 6);

        Particles.create(EidolonParticles.FEATHER_PARTICLE.get())
                .setAlpha(0.25f, 0).setScale(0.5f, 0).setLifetime(10)
                .randomOffset(0.125, 0.125).randomVelocity(0.0625f)
                .setColor(0.3F, 0.3F, 0.7F)
                .repeat(world, x + 0.5, y, z + 0.5, 6);

        Particles.create(EidolonParticles.FEATHER_PARTICLE.get())
                .setAlpha(0.5f, 0).setScale(0.5f, 0).setLifetime(10)
                .randomOffset(0.125, 0.125).randomVelocity(0.0625f)
                .setColor(0.3F, 0.3F, 0.7F)
                .repeat(world, x - 0.5, y, z - 0.5, 6);

        Particles.create(EidolonParticles.SPARKLE_PARTICLE.get())
                .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(80)
                .randomOffset(0.0625, 0).randomVelocity(0.125f, 0.125f)
                .addVelocity(0, 0.25f, 0)
                .setColor(0.2F, 0.2F, 0.7F)
                .enableGravity().setSpin(0.4f)
                .repeat(world, x, y, z, world.random.nextInt(4) + 3);
        Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                .setAlpha(0.25f, 0).setScale(0.375f, 0).setLifetime(20)
                .randomOffset(0.25, 0.25).randomVelocity(0.015625f, 0.015625f)
                .setColor(0.2F, 0.2F, 0.7F)
                .repeat(world, x, y, z, 6);

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}