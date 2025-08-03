package alexthw.eidolon_repraised.common.entity;

import alexthw.eidolon_repraised.api.spells.Rune;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.api.spells.SignSequence;
import alexthw.eidolon_repraised.api.spells.Spell;
import alexthw.eidolon_repraised.client.particle.RuneParticleData;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.SpellCastPacket;
import alexthw.eidolon_repraised.registries.*;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChantCasterEntity extends Entity {
    public static final EntityDataAccessor<CompoundTag> RUNES = SynchedEntityData.defineId(ChantCasterEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public static final EntityDataAccessor<CompoundTag> SIGNS = SynchedEntityData.defineId(ChantCasterEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(ChantCasterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Optional<UUID>> CASTER_ID = SynchedEntityData.defineId(ChantCasterEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Boolean> SUCCEEDED = SynchedEntityData.defineId(ChantCasterEntity.class, EntityDataSerializers.BOOLEAN);
    public int timer = 0, deathTimer = 0;
    public Vec3 look;

    @Nullable Player caster;

    public ChantCasterEntity(Level world, Player caster, List<Sign> runes, Vec3 look) {
        super(EidolonEntities.CHANT_CASTER.get(), world);
        this.look = look;
        this.caster = caster;
        //setRunesTag(runes);
        setChantTag(runes);
        getEntityData().set(CASTER_ID, Optional.of(caster.getUUID()));
    }

    public ChantCasterEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public static void createChanter(Player player, Level world, List<Sign> runes) {
        for (Sign rune : runes) if (!KnowledgeUtil.knowsSign(player, rune)) return;
        Vec3 placement = player.position().add(0, player.getBbHeight() * 2 / 3, 0).add(player.getLookAngle().scale(0.5f));
        ChantCasterEntity entity = new ChantCasterEntity(world, player, runes, player.getLookAngle());
        entity.setPos(placement.x, placement.y, placement.z);
        world.addFreshEntity(entity);
    }

    protected CompoundTag getNoRunesTag() {
        CompoundTag emptyRunes = new CompoundTag();
        emptyRunes.put("runes", new ListTag());
        return emptyRunes;
    }

    protected List<Rune> loadRunesTag() {
        List<Rune> runes = new ArrayList<>();
        ListTag runesTag = getEntityData().get(RUNES).getList("runes", Tag.TAG_STRING);
        for (int i = 0; i < runesTag.size(); i++) {
            Rune r = Runes.find(ResourceLocation.parse(runesTag.getString(i)));
            if (r != null) runes.add(r);
        }
        return runes;
    }

    protected void setRunesTag(List<Rune> runes) {
        ListTag runesList = new ListTag();
        CompoundTag runesTag = new CompoundTag();
        for (Rune r : runes) runesList.add(StringTag.valueOf(r.getRegistryName().toString()));
        runesTag.put("runes", runesList);
        getEntityData().set(RUNES, runesTag);
    }

    protected List<Sign> loadChantTag() {
        List<Sign> runes = new ArrayList<>();
        ListTag runesTag = getEntityData().get(RUNES).getList("runes", Tag.TAG_STRING);
        for (int i = 0; i < runesTag.size(); i++) {
            Sign r = Signs.find(ResourceLocation.tryParse(runesTag.getString(i)));
            if (r != null) runes.add(r);
        }
        return runes;
    }

    protected void setChantTag(List<Sign> signs) {
        ListTag signList = new ListTag();
        CompoundTag signTag = new CompoundTag();
        for (Sign r : signs) signList.add(StringTag.valueOf(r.getRegistryName().toString()));
        signTag.put("runes", signList);
        getEntityData().set(RUNES, signTag);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(RUNES, getNoRunesTag());
        builder.define(SIGNS, new SignSequence().serializeNbt());
        builder.define(INDEX, 0);
        builder.define(CASTER_ID, Optional.empty());
        builder.define(SUCCEEDED, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (deathTimer > 0) {
            deathTimer--;
            if (deathTimer <= 0) remove(RemovalReason.KILLED);
            return;
        }
        if (timer > 0) {
            timer--;
            if (timer <= 0) {
                CompoundTag signData = getEntityData().get(SIGNS);
                Optional<UUID> optuuid = getEntityData().get(CASTER_ID);
                if (!level().isClientSide && optuuid.isPresent()) {
                    SignSequence seq = SignSequence.deserializeNbt(signData);
                    Spell spell = Spells.find(seq, level());
                    Player player = level().getPlayerByUUID(optuuid.get());
                    if (spell != null && player != null && spell.canCast(level(), blockPosition(), player, seq)) {
                        spell.cast(level(), blockPosition(), player, seq);
                        Networking.sendToNearbyClient(level(), blockPosition(), new SpellCastPacket(player, blockPosition(), spell, seq));
                        getEntityData().set(SUCCEEDED, true);
                    } else {
                        level().playSound(null, blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
                        getEntityData().set(SUCCEEDED, false);
                    }
                }
                deathTimer = 20;
                return;
            }
        }

        //updates cached caster
        cacheCaster();
        double castSpeed;
        if (caster == null) {
            castSpeed = 1.0;
        } else {
            AttributeInstance attribute = caster.getAttribute(EidolonAttributes.CHANTING_SPEED);
            castSpeed = attribute != null ? attribute.getValue() : 1.0;
        }

        if (timer <= 0 && tickCount % Mth.floor(5 / castSpeed) == 0) {
            List<Sign> runes = loadChantTag();
            SignSequence seq = SignSequence.deserializeNbt(getEntityData().get(SIGNS));
            Vector3f initColor = seq.getAverageColor();

            int index = getEntityData().get(INDEX);
            if (index >= runes.size()) return;
            Sign sign = runes.get(index);
            seq.addRight(sign);

            Vector3f afterColor = seq.getAverageColor();
            double x = getX() + 0.1 * random.nextGaussian(),
                    y = getY() + 0.1 * random.nextGaussian(),
                    z = getZ() + 0.1 * random.nextGaussian();
            for (int i = 0; i < 2; i++) {
                level().addParticle(new RuneParticleData(
                        Runes.find(ResourceLocation.tryParse("eidolon_repraised:sin")),
                        initColor.x(), initColor.y(), initColor.z(),
                        afterColor.x(), afterColor.y(), afterColor.z()
                ), x, y, z, look.x * 0.03, look.y * 0.03, look.z * 0.03);
            }
            level().playSound(null, blockPosition(), EidolonSounds.CHANT_WORD.get(), SoundSource.NEUTRAL, 0.7f, random.nextFloat() * 0.375f + 0.625f);
            if (index + 1 >= runes.size()) {
                Spell match = Spells.find(seq, level());
                timer = match != null ? match.getDelay() : 10;
            }
            if (!level().isClientSide) {
                getEntityData().set(INDEX, index + 1);
                getEntityData().set(SIGNS, seq.serializeNbt());
            }
        }

        if (caster != null) {
            double rad = Math.toRadians(caster.yHeadRot);
            this.setPos(caster.getEyePosition().add(-Math.sin(rad) / 2, -0.75, Math.cos(rad) / 2));
            this.look = caster.getLookAngle();
        }
    }

    private void cacheCaster() {
        if (caster == null) {
            Optional<UUID> optuuid = getEntityData().get(CASTER_ID);
            if (optuuid.isPresent()) {
                Player e = level().getPlayerByUUID(optuuid.get());
                if (e != null) caster = e;
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        getEntityData().set(RUNES, compound.getCompound("runes_tag"));
        getEntityData().set(SIGNS, compound.getCompound("signs_tag"));
        getEntityData().set(INDEX, compound.getInt("index"));
        getEntityData().set(CASTER_ID, Optional.of(compound.getUUID("caster_id")));
        look = new Vec3(compound.getDouble("lookX"), compound.getDouble("lookY"), compound.getDouble("lookZ"));
        timer = compound.getInt("timer");
        deathTimer = compound.getInt("deathTimer");
        getEntityData().set(SUCCEEDED, compound.getBoolean("succeeded"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("runes_tag", getEntityData().get(RUNES));
        compound.put("signs_tag", getEntityData().get(SIGNS));
        compound.putInt("index", getEntityData().get(INDEX));
        compound.putInt("timer", timer);
        if (getEntityData().get(CASTER_ID).isPresent())
            compound.putUUID("caster_id", getEntityData().get(CASTER_ID).get());
        compound.putDouble("lookX", look.x);
        compound.putDouble("lookY", look.y);
        compound.putDouble("lookZ", look.z);
        compound.putInt("deathTimer", deathTimer);
        compound.putBoolean("succeeded", getEntityData().get(SUCCEEDED));
    }

//    @Override
//    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
//        return NetworkHooks.getEntitySpawningPacket(this);
//    }
//
//    @Override
//    public void writeSpawnData(FriendlyByteBuf buffer) {
//        buffer.writeDouble(look.x);
//        buffer.writeDouble(look.y);
//        buffer.writeDouble(look.z);
//    }
//
//    @Override
//    public void readSpawnData(FriendlyByteBuf buf) {
//        look = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
//    }
}
