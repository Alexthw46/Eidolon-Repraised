package elucent.eidolon.common.entity;

import elucent.eidolon.datagen.EidBiomeTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class SlimySlugEntity extends PathfinderMob implements IForgeEntity {
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.PUMPKIN_SEEDS);
    float yRotTrail = 0.0f;
    public float squishAmount = 1.0f;
    public static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(SlimySlugEntity.class, EntityDataSerializers.INT);

    public SlimySlugEntity(EntityType<SlimySlugEntity> type, Level worldIn) {
        super(type, worldIn);
        registerGoals();
        yRotTrail = this.getYRot();
        getEntityData().set(TYPE, 0);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        Holder<Biome> holder = pLevel.getBiome(this.blockPosition());
        if (holder.is(EidBiomeTagProvider.BROWN_SLUG_TAG)) {
            this.setVariant(2);
        } else if (holder.is(EidBiomeTagProvider.BANANA_SLUG_TAG)) {
            this.setVariant(1);
        } else {
            this.setVariant(0);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private void setVariant(int type) {
        getEntityData().set(TYPE, type);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(TYPE, 0);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (e) -> ((Player) e).getGameProfile().getId().equals(UUID.fromString("0ca54301-6170-4c44-b3e0-b8afa6b81ed2"))));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1F)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.ATTACK_DAMAGE, 999.0D)
                .build();
    }

    public void tick() {
        super.tick();
        yRotTrail = Mth.rotLerp(yRotTrail, getYRot(), 0.2f);
    }

}
