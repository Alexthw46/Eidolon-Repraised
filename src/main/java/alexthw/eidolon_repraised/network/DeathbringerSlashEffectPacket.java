package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.client.particle.SlashParticleData;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DeathbringerSlashEffectPacket extends AbstractPacket {
    public static final Type<DeathbringerSlashEffectPacket> TYPE = new Type<>(Eidolon.prefix("deathbringer_slash_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeathbringerSlashEffectPacket> CODEC = StreamCodec.ofMember(
            DeathbringerSlashEffectPacket::encode,
            DeathbringerSlashEffectPacket::decode
    );

    final double x1;
    final double y1;
    final double z1;
    final double x2;
    final double y2;
    final double z2;
    final int c1;
    final int c2;
    final int c3;
    final int c4;

    public DeathbringerSlashEffectPacket(double x1, double y1, double z1, double x2, double y2, double z2, int color1, int color2, int color3, int color4) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.c1 = color1;
        this.c2 = color2;
        this.c3 = color3;
        this.c4 = color4;
    }

    public static void encode(DeathbringerSlashEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeDouble(object.x1).writeDouble(object.y1).writeDouble(object.z1);
        buffer.writeDouble(object.x2).writeDouble(object.y2).writeDouble(object.z2);
        buffer.writeInt(object.c1).writeInt(object.c2).writeInt(object.c3).writeInt(object.c4);
    }

    public static DeathbringerSlashEffectPacket decode(FriendlyByteBuf buffer) {
        return new DeathbringerSlashEffectPacket(
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        double x = this.x2, y = this.y2, z = this.z2;

        float r1 = ColorUtil.getRed(this.c1) / 255.0f, g1 = ColorUtil.getGreen(this.c1) / 255.0f, b1 = ColorUtil.getBlue(this.c1) / 255.0f;
        float r2 = ColorUtil.getRed(this.c2) / 255.0f, g2 = ColorUtil.getGreen(this.c2) / 255.0f, b2 = ColorUtil.getBlue(this.c2) / 255.0f;

        float roll = Mth.PI / 6 - world.random.nextFloat() * (Mth.PI * 7 / 6);
        float scale = 1.0f + world.random.nextFloat() * 0.2f;

        Vec3 horiz = new Vec3(x - this.x1, 0, z - this.z1);
        float yaw = (float) Mth.atan2(x - this.x1, z - this.z1);
        float pitch = (float) Mth.atan2(y - this.y1, horiz.length());
        float right = yaw + Mth.PI / 2, up = pitch + Mth.PI / 2;
        float sy = Mth.sin(yaw), cy = Mth.cos(yaw), sp = Mth.sin(pitch), cp = Mth.cos(pitch);
        float sr = Mth.sin(right), cr = Mth.cos(right), su = Mth.sin(up), cu = Mth.cos(up);
        float r = 0.5f;
        float xax = r * sr * cp, xay = 0, xaz = r * cr * cp;
        float yax = r * sy * cu, yay = r * su, yaz = r * cy * cu;
        float zax = r * sy * cp, zay = r * sp, zaz = r * cy * cp;
        float cro = Mth.cos(roll), sro = Mth.sin(roll);
        float nxax = xax * cro - yax * sro;
        float nxay = xay * cro - yay * sro;
        float nxaz = xaz * cro - yaz * sro;
        yax = xax * sro + yax * cro;
        yay = xay * sro + yay * cro;
        yaz = xaz * sro + yaz * cro;
        xax = nxax;
        xay = nxay;
        xaz = nxaz;
        for (float i = 0; i < 6; i++) {
            float c1 = (i + 0.5f) / 6;
            float angle = -75 + c1 * 150;
            float sa = Mth.sin(Mth.DEG_TO_RAD * angle), ca = Mth.cos(Mth.DEG_TO_RAD * angle);
            float dx = sa * xax + ca * zax, dy = sa * xay + ca * zay, dz = sa * xaz + ca * zaz;
            Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                    .randomVelocity(0.025f)
                    .addVelocity(dx * 0.25f, dy * 0.25f, dz * 0.25f)
                    .setColor(33.0f / 255, 26.0f / 255, 23.0f / 255, 0.125f, 10.0f / 255, 10.0f / 255, 12.0f / 255, 0)
                    .randomOffset(0.1f)
                    .setScale(0.375f, 0.125f)
                    .repeat(world, x - sy * cp + dx, y - sp + dy, z - cy * cp + dz, 4);
        }

        SlashParticleData.create(EidolonParticles.GLOWING_SLASH_PARTICLE.get())
                .lookat(this.x1, this.y1, this.z1, x, y, z)
                .color(r1, g1, b1, r2, g2, b2)
                .radius(0.9f * scale)
                .angle(250)
                .width(scale)
                .roll(roll)
                .lifetime(11)
                .spawn(world, x, y, z, 0, 0, 0);

        SlashParticleData.create(EidolonParticles.GLOWING_SLASH_PARTICLE.get())
                .lookat(this.x1, this.y1, this.z1, x, y, z)
                .color(r1, g1, b1, r2, g2, b2)
                .radius(0.8f * scale)
                .angle(250)
                .width(0.75f * scale)
                .roll(roll)
                .lifetime(11)
                .spawn(world, x, y, z, 0, 0, 0);

        SlashParticleData.create(EidolonParticles.GLOWING_SLASH_PARTICLE.get())
                .lookat(this.x1, this.y1, this.z1, x, y, z)
                .color(r1, g1, b1, r2, g2, b2)
                .radius(0.7f * scale)
                .angle(250)
                .width(0.5f * scale)
                .roll(roll)
                .lifetime(13)
                .spawn(world, x, y, z, 0, 0, 0);

        r1 = ColorUtil.getRed(this.c3) / 255.0f;
        g1 = ColorUtil.getGreen(this.c3) / 255.0f;
        b1 = ColorUtil.getBlue(this.c3) / 255.0f;
        r2 = ColorUtil.getRed(this.c4) / 255.0f;
        g2 = ColorUtil.getGreen(this.c4) / 255.0f;
        b2 = ColorUtil.getBlue(this.c4) / 255.0f;

        SlashParticleData.create(EidolonParticles.GLOWING_SLASH_PARTICLE.get())
                .lookat(this.x1, this.y1, this.z1, x, y, z)
                .color(r1, g1, b1, r2, g2, b2)
                .radius(0.8f * scale)
                .angle(210)
                .width(0.625f * scale)
                .highlight(0.75f)
                .alpha(0.75f, 0)
                .roll(roll)
                .lifetime(8)
                .spawn(world, x, y, z, 0, 0, 0);

        SlashParticleData.create(EidolonParticles.GLOWING_SLASH_PARTICLE.get())
                .lookat(this.x1, this.y1, this.z1, x, y, z)
                .color(r1, g1, b1, r2, g2, b2)
                .radius(0.9f * scale)
                .angle(210)
                .width(0.25f * scale)
                .highlight(0.625f)
                .roll(roll)
                .lifetime(10)
                .spawn(world, x, y, z, 0, 0, 0);

    }

    public @NotNull Type<DeathbringerSlashEffectPacket> type() {
        return TYPE;
    }
}
