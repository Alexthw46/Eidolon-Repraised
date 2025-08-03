package alexthw.eidolon_repraised.client.particle;

import alexthw.eidolon_repraised.api.spells.Rune;
import alexthw.eidolon_repraised.network.GenericParticlePacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.Runes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Random;

public class Particles {

    // variant of ParticleBuilder#Spawn that takes all parameters at once without using the builder
    public static void spawnParticle(Level world, ParticleOptions type, double x, double y, double z,
                                     double maxXSpeed, double maxYSpeed, double maxZSpeed, double maxXDist, double maxYDist, double maxZDist
    ) {
        double yaw = Math.random() * Math.PI * 2, pitch = Math.random() * Math.PI - Math.PI / 2,
                xSpeed = Math.random() * maxXSpeed, ySpeed = Math.random() * maxYSpeed, zSpeed = Math.random() * maxZSpeed;
        double vx = Math.sin(yaw) * Math.cos(pitch) * xSpeed;
        double vy = Math.sin(pitch) * ySpeed;
        double vz = Math.cos(yaw) * Math.cos(pitch) * zSpeed;
        double yaw2 = Math.random() * Math.PI * 2, pitch2 = Math.random() * Math.PI - Math.PI / 2,
                xDist = Math.random() * maxXDist, yDist = Math.random() * maxYDist, zDist = Math.random() * maxZDist;
        double dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
        double dy = Math.sin(pitch2) * yDist;
        double dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;
        world.addParticle(type, x + dx, y + dy, z + dz, vx, vy, vz);
    }


    public static class ParticleBuilder {
        static final Random random = new Random();

        final ParticleType<?> type;
        final GenericParticleData data;
        double vx = 0, vy = 0, vz = 0;
        double dx = 0, dy = 0, dz = 0;
        double maxXSpeed = 0, maxYSpeed = 0, maxZSpeed = 0;
        double maxXDist = 0, maxYDist = 0, maxZDist = 0;

        protected ParticleBuilder(ParticleType<?> type) {
            this.type = type;
            this.data = new GenericParticleData(type);
        }

        public ParticleBuilder setColor(float r, float g, float b) {
            setColor(r, g, b, data.a1, r, g, b, data.a2);
            return this;
        }

        public ParticleBuilder setColor(float r, float g, float b, float a) {
            setColor(r, g, b, a, r, g, b, a);
            return this;
        }

        public ParticleBuilder setColor(float r, float g, float b, float a1, float a2) {
            setColor(r, g, b, a1, r, g, b, a2);
            return this;
        }

        public ParticleBuilder setColor(float r1, float g1, float b1, float r2, float g2, float b2) {
            setColor(r1, g1, b1, data.a1, r2, g2, b2, data.a2);
            return this;
        }

        public ParticleBuilder setColor(float r1, float g1, float b1, float r2, float g2, float b2, float a) {
            setColor(r1, g1, b1, a, r2, g2, b2, a);
            return this;
        }

        public ParticleBuilder setColor(float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
            data.r1 = r1;
            data.g1 = g1;
            data.b1 = b1;
            data.a1 = a1;
            data.r2 = r2;
            data.g2 = g2;
            data.b2 = b2;
            data.a2 = a2;
            return this;
        }

        public ParticleBuilder setAlpha(float a) {
            setAlpha(a, a);
            return this;
        }

        public ParticleBuilder setAlpha(float a1, float a2) {
            data.a1 = a1;
            data.a2 = a2;
            return this;
        }

        public ParticleBuilder setScale(float scale) {
            setScale(scale, scale);
            return this;
        }

        public ParticleBuilder setScale(float scale1, float scale2) {
            data.scale1 = scale1;
            data.scale2 = scale2;
            return this;
        }

        public ParticleBuilder enableGravity() {
            data.gravity = true;
            return this;
        }

        public ParticleBuilder disableGravity() {
            data.gravity = false;
            return this;
        }

        public ParticleBuilder setSpin(float angularVelocity) {
            data.spin = angularVelocity;
            return this;
        }

        public ParticleBuilder setLifetime(int lifetime) {
            data.lifetime = lifetime;
            return this;
        }

        public ParticleBuilder randomVelocity(double maxSpeed) {
            randomVelocity(maxSpeed, maxSpeed, maxSpeed);
            return this;
        }

        public ParticleBuilder randomVelocity(double maxHSpeed, double maxVSpeed) {
            randomVelocity(maxHSpeed, maxVSpeed, maxHSpeed);
            return this;
        }

        public ParticleBuilder randomVelocity(double maxXSpeed, double maxYSpeed, double maxZSpeed) {
            this.maxXSpeed = maxXSpeed;
            this.maxYSpeed = maxYSpeed;
            this.maxZSpeed = maxZSpeed;
            return this;
        }

        public ParticleBuilder addVelocity(double vx, double vy, double vz) {
            this.vx += vx;
            this.vy += vy;
            this.vz += vz;
            return this;
        }

        public ParticleBuilder setVelocity(double vx, double vy, double vz) {
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            return this;
        }

        public ParticleBuilder randomOffset(double maxDistance) {
            randomOffset(maxDistance, maxDistance, maxDistance);
            return this;
        }

        public ParticleBuilder randomOffset(double maxHDist, double maxVDist) {
            randomOffset(maxHDist, maxVDist, maxHDist);
            return this;
        }

        public ParticleBuilder randomOffset(double maxXDist, double maxYDist, double maxZDist) {
            this.maxXDist = maxXDist;
            this.maxYDist = maxYDist;
            this.maxZDist = maxZDist;
            return this;
        }

        public ParticleBuilder spawn(Level world, double x, double y, double z) {
            double yaw = random.nextFloat() * Math.PI * 2, pitch = random.nextFloat() * Math.PI - Math.PI / 2,
                    xSpeed = random.nextFloat() * maxXSpeed, ySpeed = random.nextFloat() * maxYSpeed, zSpeed = random.nextFloat() * maxZSpeed;
            this.vx += Math.sin(yaw) * Math.cos(pitch) * xSpeed;
            this.vy += Math.sin(pitch) * ySpeed;
            this.vz += Math.cos(yaw) * Math.cos(pitch) * zSpeed;
            double yaw2 = random.nextFloat() * Math.PI * 2, pitch2 = random.nextFloat() * Math.PI - Math.PI / 2,
                    xDist = random.nextFloat() * maxXDist, yDist = random.nextFloat() * maxYDist, zDist = random.nextFloat() * maxZDist;
            this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
            this.dy = Math.sin(pitch2) * yDist;
            this.dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;

            if (world instanceof ServerLevel level) {
                Networking.sendToNearbyClient(level, BlockPos.containing(x, y, z), new GenericParticlePacket(x + dx, y + dy, z + dz, vx, vy, vz, data));
            } else {
                world.addParticle(data, x + dx, y + dy, z + dz, vx, vy, vz);
            }
            return this;
        }

        public ParticleBuilder repeat(Level world, double x, double y, double z, int n) {
            for (int i = 0; i < n; i++) spawn(world, x, y, z);
            return this;
        }
    }

    public static ParticleBuilder create(ParticleType<?> type) {
        return new ParticleBuilder(type);
    }

    public static ParticleBuilder createRune(ResourceLocation id) {
        return new RuneParticleBuilder(EidolonParticles.RUNE_PARTICLE.get(), id);
    }

    public static class RuneParticleBuilder extends ParticleBuilder {
        Rune rune;

        public RuneParticleBuilder(ParticleType<?> type, ResourceLocation id) {
            super(type);
            rune = Runes.find(id);
        }

        @Override
        public ParticleBuilder spawn(Level world, double x, double y, double z) {
            var rData = new RuneParticleData(rune, data.r1, data.g1, data.b1, data.r2, data.b2, data.g2);
            double yaw = random.nextFloat() * Math.PI * 2, pitch = random.nextFloat() * Math.PI - Math.PI / 2,
                    xSpeed = random.nextFloat() * maxXSpeed, ySpeed = random.nextFloat() * maxYSpeed, zSpeed = random.nextFloat() * maxZSpeed;
            this.vx += Math.sin(yaw) * Math.cos(pitch) * xSpeed;
            this.vy += Math.sin(pitch) * ySpeed;
            this.vz += Math.cos(yaw) * Math.cos(pitch) * zSpeed;
            double yaw2 = random.nextFloat() * Math.PI * 2, pitch2 = random.nextFloat() * Math.PI - Math.PI / 2,
                    xDist = random.nextFloat() * maxXDist, yDist = random.nextFloat() * maxYDist, zDist = random.nextFloat() * maxZDist;
            this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
            this.dy = Math.sin(pitch2) * yDist;
            this.dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;

            if (world instanceof ServerLevel level) {
                Networking.sendToNearbyClient(level, BlockPos.containing(x, y, z), new GenericParticlePacket(x + dx, y + dy, z + dz, vx, vy, vz, rData));
            } else {
                world.addParticle(rData, x + dx, y + dy, z + dz, vx, vy, vz);
            }
            return this;
        }
    }


}
