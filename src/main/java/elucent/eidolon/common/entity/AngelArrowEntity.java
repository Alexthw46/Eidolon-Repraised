package elucent.eidolon.common.entity;

import elucent.eidolon.mixin.AbstractArrowMixin;
import elucent.eidolon.mixin.ProjectileMixin;
import elucent.eidolon.registries.EidolonEntities;
import elucent.eidolon.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class AngelArrowEntity extends AbstractArrow implements IEntityAdditionalSpawnData {
    public AbstractArrow internal = null;

    public Predicate<Entity> mode = target -> true;

    public AngelArrowEntity(EntityType<? extends AbstractArrow> type, Level worldIn) {
        super(type, worldIn);
    }

    public AngelArrowEntity(Level worldIn, LivingEntity shooter) {
        super(EidolonEntities.ANGEL_ARROW.get(), shooter, worldIn);
    }

    public void setArrow(AbstractArrow entity) {
        this.internal = entity;
        internal.copyPosition(this);
    }

    public static float lerpDegrees(float a, float b, float t) {
        float d1 = Math.abs(b - a), d2 = Math.abs((b - 360) - a), d3 = Math.abs((b + 360) - a);
        if (d2 > d3 && d2 > d1) {
            b -= 360;
            d1 = d2;
        }
        if (d3 > d2 && d3 > d1) {
            b += 360;
            d1 = d3;
        }
        return a + d1 * t;
    }

    @Override
    public void tick() {
        if (internal == null) {
            removeAfterChangingDimensions();
            return;
        }

        super.tick();
        internal.tick();
        internal.xo = xo;
        internal.yo = yo;
        internal.zo = zo;
        internal.xRotO = xRotO;
        internal.yRotO = yRotO;
        internal.copyPosition(this);
        internal.setDeltaMovement(getDeltaMovement());

        if (!inGround) {
            EntityUtil.moveTowardsTarget(this);
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return internal == null ? ItemStack.EMPTY :
                ((AbstractArrowMixin) internal).callGetPickupItem();
    }

    @Override
    public void onHitEntity(@NotNull EntityHitResult result) {
        ((ProjectileMixin) internal).callOnHit(result);
        if (internal.isRemoved()) remove(RemovalReason.DISCARDED);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("type", getRegistryName(internal.getType()).toString());
        nbt.put("data", internal.serializeNBT());
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        ResourceLocation rl = new ResourceLocation(nbt.getString("type"));
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(rl);
        if (type == null) removeAfterChangingDimensions();

        internal = (AbstractArrow) type.create(level);
        internal.deserializeNBT(nbt.getCompound("data"));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag extra = new CompoundTag();
        extra.putString("type", getRegistryName(internal.getType()).toString());
        extra.put("data", internal.serializeNBT());
        buffer.writeNbt(extra);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        CompoundTag extra = additionalData.readNbt();
        ResourceLocation rl = new ResourceLocation(extra.getString("type"));
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(rl);
        if (type == null) removeAfterChangingDimensions();

        internal = (AbstractArrow) type.create(level);
        internal.deserializeNBT(extra.getCompound("data"));
    }
}
