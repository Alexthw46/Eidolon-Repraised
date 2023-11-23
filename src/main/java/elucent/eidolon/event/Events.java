package elucent.eidolon.event;

import com.mojang.authlib.GameProfile;
import elucent.eidolon.Config;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.capability.*;
import elucent.eidolon.common.entity.ZombieBruteEntity;
import elucent.eidolon.common.entity.ai.FollowOwnerGoal;
import elucent.eidolon.common.entity.ai.PriestBarterGoal;
import elucent.eidolon.common.entity.ai.WitchBarterGoal;
import elucent.eidolon.common.item.*;
import elucent.eidolon.common.tile.GobletTileEntity;
import elucent.eidolon.network.*;
import elucent.eidolon.registries.EidolonAttributes;
import elucent.eidolon.registries.EidolonPotions;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent.Added;
import net.minecraftforge.event.entity.living.MobEffectEvent.Applicable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static elucent.eidolon.util.EntityUtil.THRALL_KEY;

public class Events {
    @SubscribeEvent
    public void attachWorldCaps(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject() != null)
            event.addCapability(new ResourceLocation(Eidolon.MODID, "reputation"), new IReputation.Provider());
    }

    @SubscribeEvent
    public void attachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(Eidolon.MODID, "knowledge"), new IKnowledge.Provider());
            event.addCapability(new ResourceLocation(Eidolon.MODID, "player_data"), new IPlayerData.Provider());
            event.addCapability(new ResourceLocation(Eidolon.MODID, "soul"), new ISoul.Provider());
        }
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        Capability<IKnowledge> KNOWLEDGE = IKnowledge.INSTANCE;
        Capability<ISoul> SOUL = ISoul.INSTANCE;
        Capability<IPlayerData> PDATA = IPlayerData.INSTANCE;
        event.getOriginal().reviveCaps();
        event.getEntity().getCapability(KNOWLEDGE).ifPresent(k -> event.getOriginal().getCapability(KNOWLEDGE).ifPresent(o -> ((INBTSerializable<CompoundTag>) k).deserializeNBT(((INBTSerializable<CompoundTag>) o).serializeNBT())));
        event.getEntity().getCapability(SOUL).ifPresent(k -> event.getOriginal().getCapability(SOUL).ifPresent(o -> ((INBTSerializable<CompoundTag>) k).deserializeNBT(((INBTSerializable<CompoundTag>) o).serializeNBT())));
        event.getEntity().getCapability(PDATA).ifPresent(k -> event.getOriginal().getCapability(PDATA).ifPresent(o -> ((INBTSerializable<CompoundTag>) k).deserializeNBT(((INBTSerializable<CompoundTag>) o).serializeNBT())));
        event.getOriginal().invalidateCaps();
        if (!event.getEntity().level.isClientSide) {
            Networking.sendTo(event.getEntity(), new KnowledgeUpdatePacket(event.getEntity(), false));
            Networking.sendTo(event.getEntity(), new SoulUpdatePacket(event.getEntity()));
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        KnowledgeCommand.register(event.getDispatcher());
        ReputationCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() == null || !EntityUtil.isEnthralled(event.getEntity())) return;
        UUID master = event.getEntity().getPersistentData().getUUID(THRALL_KEY);
        LivingEntity newTarget = null;
        if (EntityUtil.isEnthralledBy(event.getEntity(), event.getOriginalTarget())) {
            LivingEntity lastHurt = event.getOriginalTarget().getLastHurtMob();
            LivingEntity lastHurtBy = event.getOriginalTarget().getLastHurtByMob();
            newTarget = handleEnthralledTargeting(lastHurt, lastHurtBy, event.getEntity());
        } else if (event.getEntity().level().getPlayerByUUID(master) instanceof ServerPlayer player) {
            LivingEntity lastHurt = player.getLastHurtMob();
            LivingEntity lastHurtBy = player.getLastHurtByMob();
            newTarget = handleEnthralledTargeting(lastHurt, lastHurtBy, event.getEntity());
        }
        if (!(event.getEntity() instanceof HoglinBase && newTarget == null)) event.setNewTarget(newTarget);
    }

    private @Nullable LivingEntity handleEnthralledTargeting(LivingEntity lastHurt, LivingEntity lastHurtBy, LivingEntity thrall) {
        if (lastHurtBy != null && lastHurtBy != thrall && !(EntityUtil.isEnthralled(lastHurtBy) && EntityUtil.sameMaster(thrall, lastHurtBy))) {
            return lastHurtBy;
        } else if (lastHurt != null && lastHurt != thrall && !(EntityUtil.isEnthralled(lastHurt) && EntityUtil.sameMaster(thrall, lastHurt))) {
            return lastHurt;
        } else return null;
    }

    @SubscribeEvent
    public void onTick(LivingTickEvent event) {
        Level level = event.getEntity().level();
        LivingEntity e = event.getEntity();
        if (e.hasEffect(EidolonPotions.UNDEATH_EFFECT.get()) && level.isDay() && !level.isClientSide) {
            float f = e.getLightLevelDependentMagicValue();
            BlockPos blockpos = e.getVehicle() instanceof Boat ? BlockPos.containing(e.getX(), (double) Math.round(e.getY()), e.getZ()).above() : BlockPos.containing(e.getX(), (double) Math.round(e.getY()), e.getZ());
            if (f > 0.5F && e.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && level.canSeeSky(blockpos)) {
                e.setSecondsOnFire(8);
            }
        }
        boolean hasBoneArmor = false;
        for (ItemStack s : e.getArmorSlots()) {
            if (s.getItem() instanceof BonelordArmorItem) hasBoneArmor = true;
        }
        if (hasBoneArmor && event.getEntity().getHealth() >= event.getEntity().getMaxHealth() * 0.999 && event.getEntity().tickCount % 80 == 0)
            event.getEntity().getCapability(ISoul.INSTANCE).ifPresent(s -> s.healEtherealHealth(1, ISoul.getPersistentHealth(event.getEntity())));
    }

    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            Level world = entity.level;
            BlockPos pos = entity.blockPosition();
            List<GobletTileEntity> goblets = Ritual.getTilesWithinAABB(GobletTileEntity.class, world, new AABB(pos.offset(-2, -2, -2), pos.offset(3, 3, 3)));
            if (!goblets.isEmpty()) {
                GobletTileEntity goblet = goblets.stream().min(Comparator.comparingDouble(g -> g.getBlockPos().distSqr(pos))).get();
                goblet.setEntityType(entity.getType());
            }
        }

        //Drop candies on Halloween
        if (event.getSource().getEntity() instanceof Player && !entity.level.isClientSide()) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //if it's Halloween period, add one of the two candies to the loot table with 10% chance
            if ((month == 10 && day >= 28 || month == 11 && day <= 2) && entity.level.random.nextInt(10) == 0) {
                if (entity instanceof Zombie) {
                    event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Registry.RED_CANDY.get())));
                } else if (entity instanceof AbstractSkeleton) {
                    event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Registry.GRAPE_CANDY.get())));
                }
            }
        }

        if (entity instanceof Witch || entity instanceof Villager) {
            if (entity.getMainHandItem().getItem() instanceof CodexItem)
                event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), entity.getMainHandItem().copy()));
        }

        //TODO Replace with GLM
        if (entity instanceof ZombieBruteEntity && (entity.hasEffect(MobEffects.WITHER) || event.getSource().is(DamageTypes.WITHER)) && !entity.level.isClientSide) {
            for (ItemEntity item : event.getDrops())
                if (item.getItem().is(Registry.ZOMBIE_HEART.get())) {
                    item.setItem(new ItemStack(Registry.WITHERED_HEART.get(), item.getItem().getCount()));
                }
        }

        if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity source) {
            ItemStack held = source.getMainHandItem();
            if (!entity.level.isClientSide && (held.getItem() instanceof ReaperScytheItem || event.getSource().is(Registry.RITUAL_DAMAGE.key))
                && entity.isInvertedHealAndHarm()) {
                if (!(entity instanceof Player)) event.getDrops().clear();
                int looting = ForgeHooks.getLootingLevel(entity, source, event.getSource());
                ItemEntity drop = new ItemEntity(source.level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(Registry.SOUL_SHARD.get(), source.level.random.nextInt(2 + looting)));
                drop.setDefaultPickUpDelay();
                event.getDrops().add(drop);
                Networking.sendToTracking(entity.level, entity.blockPosition(), new CrystallizeEffectPacket(entity.blockPosition()));
            }
            if (!entity.level.isClientSide && held.getItem() instanceof CleavingAxeItem) {
                int looting = ForgeHooks.getLootingLevel(entity, source, event.getSource());
                ItemStack head = ItemStack.EMPTY;
                if (entity instanceof WitherSkeleton) head = new ItemStack(Items.WITHER_SKELETON_SKULL);
                else if (entity instanceof Skeleton) head = new ItemStack(Items.SKELETON_SKULL);
                else if (entity instanceof Zombie) head = new ItemStack(Items.ZOMBIE_HEAD);
                else if (entity instanceof Creeper) head = new ItemStack(Items.CREEPER_HEAD);
                else if (entity instanceof EnderDragon) head = new ItemStack(Items.DRAGON_HEAD);
                else if (entity instanceof Player) {
                    head = new ItemStack(Items.PLAYER_HEAD);
                    GameProfile gameprofile = ((Player) entity).getGameProfile();
                    head.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
                }
                if (!head.isEmpty()) {
                    boolean doDrop = false;
                    if (entity.level.random.nextInt(20) == 0) doDrop = true;
                    else for (int i = 0; i < looting; i++) {
                        if (entity.level.random.nextInt(40) == 0) {
                            doDrop = true;
                            break;
                        }
                    }
                    for (ItemEntity e : event.getDrops()) {
                        if (e.getItem().is(head.getItem())) doDrop = false; // No duplicate heads.
                    }
                    if (doDrop) {
                        ItemEntity drop = new ItemEntity(source.level, entity.getX(), entity.getY(), entity.getZ(), head);
                        drop.setDefaultPickUpDelay();
                        event.getDrops().add(drop);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void registerCustomAI(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity && !event.getLevel().isClientSide) {
            if (event.getEntity() instanceof Player player) {
                Networking.sendTo(player, new KnowledgeUpdatePacket(player, false));
                Networking.sendTo(player, new SoulUpdatePacket(player));
            }
            if (event.getEntity() instanceof Witch witch) {
                witch.goalSelector.addGoal(1, new WitchBarterGoal(
                        witch,
                        stack -> stack.getItem() == Registry.CODEX.get(),
                        stack -> CodexItem.withSign(stack, Signs.WICKED_SIGN)
                ));
            }
            if (event.getEntity() instanceof Villager villager) {
                villager.goalSelector.addGoal(1, new PriestBarterGoal(
                        villager,
                        stack -> stack.getItem() == Registry.CODEX.get(),
                        stack -> CodexItem.withSign(stack, Signs.SACRED_SIGN)
                ));
            }
            if (event.getEntity() instanceof PathfinderMob mob && Eidolon.getTrueMobType(mob) == MobType.UNDEAD && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof FlyingPathNavigation)) {
                mob.goalSelector.addGoal(1, new AvoidEntityGoal<>(mob, LivingEntity.class, 6.0F, 1.0D, 1.2D, living -> !EntityUtil.isEnthralled(mob) && living.hasEffect(EidolonPotions.LIGHT_BLESSED.get())));
                try {
                    mob.goalSelector.addGoal(2, new FollowOwnerGoal(mob, 1.5F, 3.0F, 1.2F));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) event.player.getCapability(IPlayerData.INSTANCE).ifPresent(d -> {
            if (!d.getWingsItem(event.player).isEmpty()) {
                if (event.player.isCrouching() && event.player.getDeltaMovement().y < -0.1) {
                    d.startFlying(event.player);
                    event.player.setDeltaMovement(event.player.getDeltaMovement().x, -0.1, event.player.getDeltaMovement().z);
                }
                if (d.isFlying(event.player)) event.player.resetFallDistance();

                if (d.isDashing(event.player)) d.doDashTick(event.player);

                if (event.player.onGround()) {
                    d.rechargeWings(event.player);
                    d.stopFlying(event.player);
                }
                if (!event.player.level.isClientSide) {
                    Networking.sendToTracking(event.player.level, event.player.blockPosition(), new WingsDataUpdatePacket(event.player));
                }
            }
        });
    }

    @SubscribeEvent
    public void onApplyPotion(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().getEffect() == MobEffects.MOVEMENT_SLOWDOWN && event.getEntity().getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WarlockRobesItem) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onLivingUse(LivingEntityUseItemEvent event) {
        if (event.getEntity().hasEffect(EidolonPotions.UNDEATH_EFFECT.get())) {
            if (!event.getItem().is(Registry.ZOMBIE_FOOD_TAG))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @Deprecated
    public void onPotionApplicable(Added event) {
        if (event.getEntity().hasEffect(MobEffects.HUNGER) && event.getEffectInstance().getEffect() == EidolonPotions.UNDEATH_EFFECT.get()) {
            event.getEntity().removeEffect(MobEffects.HUNGER);
        }
    }

    @SubscribeEvent
    public void onPotionApplicable(Applicable event) {
        if (event.getEntity().hasEffect(EidolonPotions.UNDEATH_EFFECT.get()) && event.getEffectInstance().getEffect() == MobEffects.HUNGER) {
            event.setResult(Result.DENY);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {

        boolean isMagic = event.getSource().is(Registry.FORGE_MAGIC);
        boolean isWither = event.getSource().getMsgId().equals(event.getEntity().damageSources().wither().getMsgId()); //TODO .is(Registry.FORGE_WITHER);

        if (isMagic && event.getSource().getEntity() instanceof LivingEntity living) {
            AttributeInstance attribute = living.getAttribute(EidolonAttributes.MAGIC_POWER.get());
            if (attribute != null) {
                event.setAmount(event.getAmount() * (float) attribute.getValue());
            }
        }

        if (isWither) {
            if (event.getSource().getEntity() instanceof LivingEntity living
                && living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WarlockRobesItem) {
                event.setAmount(event.getAmount() * 1.5f);
                living.heal(event.getAmount() / 2);
            }
        }

        if ((isMagic || isWither) && event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WarlockRobesItem)
            event.setAmount(event.getAmount() / 2);

        event.getEntity().getCapability(ISoul.INSTANCE).ifPresent(s -> {
            if (s.hasEtherealHealth()) {
                float reduced = s.hurtEtherealHealth(event.getAmount(), ISoul.getPersistentHealth(event.getEntity()));
                event.setAmount(reduced);
                Networking.sendToTracking(event.getEntity().level, event.getEntity().getOnPos(), new SoulUpdatePacket((Player) event.getEntity()));
            }
        });
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity source) {
            if (EntityUtil.isEnthralledBy(event.getEntity(), source)) {
                if (source.getMainHandItem().getItem() instanceof SummoningStaffItem summoningStaffItem) {
                    CompoundTag eTag = event.getEntity().serializeNBT();
                    event.getEntity().remove(Entity.RemovalReason.KILLED);
                    summoningStaffItem.addCharge(source.getMainHandItem(), eTag);
                    event.setCanceled(true);
                }
            } else if (EntityUtil.isEnthralledBy(source, event.getEntity())) {
                event.setCanceled(true);
            } else if (EntityUtil.isEnthralled(event.getEntity()) && EntityUtil.isEnthralled(source)) {
                if (EntityUtil.sameMaster(event.getEntity(), source)) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AttributeInstance attr = player.getAttribute(EidolonAttributes.MAX_SOUL_HEARTS.get());
            if (attr != null && attr.getModifier(etherealHealthUUID) == null)
                attr.addPermanentModifier(new AttributeModifier(etherealHealthUUID, "eidolon:configured_max_ethereal", Config.MAX_ETHEREAL_HEALTH.get(), AttributeModifier.Operation.ADDITION));
        }
    }

    UUID etherealHealthUUID = UUID.fromString("e7d7b2d0-4b8a-11eb-ae93-0242ac130002");

}
