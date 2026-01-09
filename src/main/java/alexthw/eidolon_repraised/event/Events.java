package alexthw.eidolon_repraised.event;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.capability.ISoul;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.capability.KnowledgeCommand;
import alexthw.eidolon_repraised.capability.ReputationCommand;
import alexthw.eidolon_repraised.common.entity.ZombieBruteEntity;
import alexthw.eidolon_repraised.common.entity.ai.FollowOwnerGoal;
import alexthw.eidolon_repraised.common.entity.ai.PriestBarterGoal;
import alexthw.eidolon_repraised.common.entity.ai.ThrallTargetGoal;
import alexthw.eidolon_repraised.common.entity.ai.WitchBarterGoal;
import alexthw.eidolon_repraised.common.item.BonelordArmorItem;
import alexthw.eidolon_repraised.common.item.CleavingAxeItem;
import alexthw.eidolon_repraised.common.item.CodexItem;
import alexthw.eidolon_repraised.common.item.ReaperScytheItem;
import alexthw.eidolon_repraised.common.item.SummoningStaffItem;
import alexthw.eidolon_repraised.common.item.WarlockRobesItem;
import alexthw.eidolon_repraised.common.spell.ThrallSpell;
import alexthw.eidolon_repraised.common.tile.GobletTileEntity;
import alexthw.eidolon_repraised.network.CrystallizeEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.OpenCodexPacket;
import alexthw.eidolon_repraised.network.SoulUpdatePacket;
import alexthw.eidolon_repraised.network.WingsDataUpdatePacket;
import alexthw.eidolon_repraised.registries.EidolonAttributes;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.EidolonPotions;
import alexthw.eidolon_repraised.registries.Registry;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static alexthw.eidolon_repraised.util.EntityUtil.THRALL_KEY;

public class Events {
    public static InteractionResult rightClickLectern(Player player, Level world, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (world.getBlockEntity(pos) instanceof LecternBlockEntity lectern && player instanceof ServerPlayer serverPlayer) {
            if (state.getValue(LecternBlock.HAS_BOOK))
                if (lectern.getBook().getItem() instanceof CodexItem) {
                    player.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
                    Networking.sendToPlayerClient(new OpenCodexPacket(), serverPlayer);
                    return InteractionResult.SUCCESS;
                }
        }
        return InteractionResult.PASS;
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        KnowledgeCommand.register(event.getDispatcher());
        ReputationCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onTarget(LivingChangeTargetEvent event) {
        if (!EntityUtil.isEnthralled(event.getEntity())) return;
        UUID master = event.getEntity().getPersistentData().getUUID(THRALL_KEY);
        LivingEntity newTarget = null;
        if (EntityUtil.isEnthralledBy(event.getEntity(), event.getNewAboutToBeSetTarget())) {
            LivingEntity lastHurt = event.getNewAboutToBeSetTarget().getLastHurtMob();
            LivingEntity lastHurtBy = event.getNewAboutToBeSetTarget().getLastHurtByMob();
            newTarget = handleEnthralledTargeting(lastHurt, lastHurtBy, event.getEntity());
        } else if (event.getEntity().level() instanceof ServerLevel server && server.getEntity(master) instanceof LivingEntity living) {
            LivingEntity lastHurt = living.getLastHurtMob();
            LivingEntity lastHurtBy = living.getLastHurtByMob();
            newTarget = handleEnthralledTargeting(lastHurt, lastHurtBy, event.getEntity());
        }
        if (!(event.getEntity() instanceof HoglinBase && newTarget == null)) event.setNewAboutToBeSetTarget(newTarget);
    }

    public static @Nullable LivingEntity handleEnthralledTargeting(@Nullable LivingEntity lastHurt, @Nullable LivingEntity lastHurtBy, LivingEntity thrall) {
        if (lastHurtBy != null && lastHurtBy != thrall && !thrall.isAlliedTo(lastHurtBy)) return lastHurtBy;
        if (lastHurt != null && lastHurt != thrall && !thrall.isAlliedTo(lastHurt)) return lastHurt;
        if (thrall.getLastHurtByMob() != null && !thrall.isAlliedTo(thrall.getLastHurtByMob()))
            return thrall.getLastHurtByMob();
        return null;
    }

    @SubscribeEvent
    public void onTick(EntityTickEvent.Pre event) {
        Level level = event.getEntity().level();
        if (!(event.getEntity() instanceof LivingEntity e)) return;
        if (e.hasEffect(EidolonPotions.UNDEATH_EFFECT) && level.isDay() && !level.isClientSide) {
            float f = e.getLightLevelDependentMagicValue();
            BlockPos blockpos = e.getVehicle() instanceof Boat ? BlockPos.containing(e.getX(), (double) Math.round(e.getY()), e.getZ()).above() : BlockPos.containing(e.getX(), (double) Math.round(e.getY()), e.getZ());
            if (f > 0.5F && e.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && level.canSeeSky(blockpos)) {
                e.setRemainingFireTicks(20 * 8);
            }
        }
        boolean hasBoneArmor = false;
        for (ItemStack s : e.getArmorSlots()) {
            if (s.getItem() instanceof BonelordArmorItem) {
                hasBoneArmor = true;
                break;
            }
        }
        if (hasBoneArmor && e.getHealth() >= e.getMaxHealth() * 0.999 && event.getEntity().tickCount % 120 == 0) {
            var s = e.getCapability(EidolonCapabilities.SOUL_HEART_CAPABILITY);
            if (s != null) {
                if (s.getEtherealHealth() < ISoul.getPersistentHealth(e)) // update ethereal health if it's lower than the persistent health
                    s.setMaxEtherealHealth(Math.max(Math.min(ISoul.getPersistentHealth(e), s.getMaxEtherealHealth()), 2 * Mth.floor((s.getEtherealHealth() + 2) / 2)));
                s.healEtherealHealth(1, ISoul.getPersistentHealth(e));
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            Level world = entity.level();
            BlockPos pos = entity.blockPosition();
            List<GobletTileEntity> goblets = Ritual.getTilesWithinAABB(GobletTileEntity.class, world, new AABB(pos.offset(-2, -2, -2).getBottomCenter(), pos.offset(3, 3, 3).getCenter()));
            if (!goblets.isEmpty()) {
                GobletTileEntity goblet = goblets.stream().min(Comparator.comparingDouble(g -> g.getBlockPos().distSqr(pos))).get();
                goblet.setEntityType(entity.getType());
            }
        }

        //Drop candies on Halloween
        if (event.getSource().getEntity() instanceof Player && !entity.level().isClientSide()) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //if it's Halloween period, add one of the two candies to the loot table with 10% chance
            if ((month == 10 && day >= 28 || month == 11 && day <= 2) && entity.level().random.nextInt(10) == 0) {
                if (entity instanceof Zombie) {
                    event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Registry.RED_CANDY.get())));
                } else if (entity instanceof AbstractSkeleton) {
                    event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Registry.GRAPE_CANDY.get())));
                }
            }
        }

        if (entity instanceof Witch || entity instanceof Villager) {
            if (entity.getMainHandItem().getItem() instanceof CodexItem)
                event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity.getMainHandItem().copy()));
        }

        //TODO Replace with GLM
        if (entity instanceof ZombieBruteEntity && (entity.hasEffect(MobEffects.WITHER) || event.getSource().is(DamageTypes.WITHER)) && !entity.level().isClientSide) {
            for (ItemEntity item : event.getDrops())
                if (item.getItem().is(Registry.ZOMBIE_HEART.get())) {
                    item.setItem(new ItemStack(Registry.WITHERED_HEART.get(), item.getItem().getCount()));
                }
        }

        if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity source) {
            ItemStack held = source.getMainHandItem();
            var lootingHolder = source.level().registryAccess().holder(Enchantments.LOOTING);

            if (!entity.level().isClientSide && (held.getItem() instanceof ReaperScytheItem || event.getSource().is(Registry.RITUAL_DAMAGE.key))
                    && Eidolon.isValidUndead(entity)) {
                if (!(entity instanceof Player))
                    event.getDrops().removeIf(i -> !(i.getItem().getItem() instanceof ArmorItem));
                int looting = lootingHolder.map(enchantmentReference -> EnchantmentHelper.getEnchantmentLevel(enchantmentReference, source)).orElse(0);
                if (source.hasEffect(EidolonPotions.SOUL_HARVEST)) looting += 2;
                ItemEntity drop = new ItemEntity(source.level(), entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(Registry.SOUL_SHARD.get(), source.level().random.nextInt(2 + looting)));
                drop.setDefaultPickUpDelay();
                event.getDrops().add(drop);
                Networking.sendToNearbyClient(entity.level(), entity.blockPosition(), new CrystallizeEffectPacket(entity.blockPosition()));
            }
            if (!entity.level().isClientSide && held.getItem() instanceof CleavingAxeItem) {
                int looting = lootingHolder.map(enchantmentReference -> EnchantmentHelper.getEnchantmentLevel(enchantmentReference, source)).orElse(0);
                beheading(event, source, entity, looting);
            }
        }
    }

    private static void beheading(LivingDropsEvent event, LivingEntity source, LivingEntity entity, int looting) {
        ItemStack head = ItemStack.EMPTY;
        if (entity instanceof WitherSkeleton) head = new ItemStack(Items.WITHER_SKELETON_SKULL);
        else if (entity instanceof Skeleton) head = new ItemStack(Items.SKELETON_SKULL);
        else if (entity instanceof Zombie) head = new ItemStack(Items.ZOMBIE_HEAD);
        else if (entity instanceof Creeper) head = new ItemStack(Items.CREEPER_HEAD);
        else if (entity instanceof EnderDragon) head = new ItemStack(Items.DRAGON_HEAD);
        else if (entity instanceof Player player) {
            head = new ItemStack(Items.PLAYER_HEAD);
            head.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
        }
        if (!head.isEmpty()) {
            boolean doDrop = false;
            if (entity.level().random.nextInt(20) == 0) doDrop = true;
            else for (int i = 0; i < looting; i++) {
                if (entity.level().random.nextInt(40) == 0) {
                    doDrop = true;
                    break;
                }
            }
            for (ItemEntity e : event.getDrops()) {
                if (e.getItem().is(head.getItem())) doDrop = false; // No duplicate heads.
            }
            if (doDrop) {
                ItemEntity drop = new ItemEntity(source.level(), entity.getX(), entity.getY(), entity.getZ(), head);
                drop.setDefaultPickUpDelay();
                event.getDrops().add(drop);
            }
        }
    }

    @SubscribeEvent
    public void registerCustomAI(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity && !event.getLevel().isClientSide) {
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
            if (event.getEntity() instanceof PathfinderMob mob && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof FlyingPathNavigation)) {
                mob.goalSelector.addGoal(1, new AvoidEntityGoal<>(mob, LivingEntity.class, 6.0F, 1.0D, 1.2D, living -> Eidolon.isValidUndead(mob) && !EntityUtil.isEnthralled(mob) && living.hasEffect(EidolonPotions.LIGHT_BLESSED)));
                if (mob.getType().is(ThrallSpell.ENTHRALL_WHITELIST) || mob.getType().is(EntityTypeTags.UNDEAD) && !mob.getType().is(ThrallSpell.ENTHRALL_BLACKLIST)) {
                    try {
                        mob.goalSelector.addGoal(2, new FollowOwnerGoal(mob, 1.5F, 3.0F, 1.2F));
                        mob.targetSelector.addGoal(1, new ThrallTargetGoal(mob));
                    } catch (IllegalArgumentException ignored) {

                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {

        var wings = event.getEntity().getCapability(EidolonCapabilities.WINGS_CAPABILITY);
        if (wings != null) {

            if (!wings.getWingsItem(event.getEntity()).isEmpty()) {
                if (event.getEntity().isCrouching() && event.getEntity().getDeltaMovement().y < -0.1) {
                    wings.startFlying(event.getEntity());
                    event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().x, -0.1, event.getEntity().getDeltaMovement().z);
                }
                if (wings.isFlying(event.getEntity())) event.getEntity().resetFallDistance();

                if (wings.isDashing(event.getEntity())) wings.doDashTick(event.getEntity());

                if (event.getEntity().onGround()) {
                    wings.rechargeWings(event.getEntity());
                    wings.stopFlying(event.getEntity());
                }
                if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                    Networking.sendToPlayerClient(new WingsDataUpdatePacket(event.getEntity()), serverPlayer);
                }
            }

        }
    }

    @SubscribeEvent
    public void onLivingUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity().hasEffect(EidolonPotions.UNDEATH_EFFECT)) {
            if (event.getItem().getFoodProperties(event.getEntity()) != null && !event.getItem().is(Registry.ZOMBIE_FOOD_TAG))
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
        if (event.getEffectInstance().getEffect() == MobEffects.MOVEMENT_SLOWDOWN && event.getEntity().getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WarlockRobesItem) {
            event.setResult(Applicable.Result.DO_NOT_APPLY);
        }
        if (event.getEntity().hasEffect(EidolonPotions.UNDEATH_EFFECT) && event.getEffectInstance().getEffect() == MobEffects.HUNGER) {
            event.setResult(Applicable.Result.DO_NOT_APPLY);
        }
        if (event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BonelordArmorItem && (event.getEffectInstance().getEffect() == MobEffects.WITHER || event.getEffectInstance().getEffect() == MobEffects.POISON)) {
            event.setResult(Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingDamageEvent.Pre event) {

        boolean isMagic = event.getSource().is(Tags.DamageTypes.IS_MAGIC);
        boolean isWither = event.getSource().getMsgId().equals(event.getEntity().damageSources().wither().getMsgId()); //TODO .is(Registry.FORGE_WITHER);

        if (isMagic && event.getSource().getEntity() instanceof LivingEntity living) {
            AttributeInstance attribute = living.getAttribute(EidolonAttributes.MAGIC_POWER);
            if (attribute != null) {
                event.setNewDamage(event.getNewDamage() * (float) attribute.getValue());
            }
        }

        if (isWither) {
            if (event.getSource().getEntity() instanceof LivingEntity living
                    && living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WarlockRobesItem) {
                event.setNewDamage(event.getNewDamage() * 1.5f);
                living.heal(event.getNewDamage() / 2);
            }
        }

        if ((isMagic || isWither) && event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WarlockRobesItem)
            event.setNewDamage(event.getNewDamage() / 2);


        var soul = event.getEntity().getCapability(EidolonCapabilities.SOUL_HEART_CAPABILITY);
        if (soul != null) {
            if (soul.hasEtherealHealth()) {
                float reduced = soul.hurtEtherealHealth(event.getNewDamage(), ISoul.getPersistentHealth(event.getEntity()));
                event.setNewDamage(reduced);
                if (event.getEntity() instanceof ServerPlayer)
                    Networking.sendToPlayerClient(new SoulUpdatePacket(event.getEntity()), (ServerPlayer) event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity source) {
            if (EntityUtil.isEnthralledBy(event.getEntity(), source)) {
                if (source.getMainHandItem().getItem() instanceof SummoningStaffItem summoningStaffItem) {
                    CompoundTag eTag = event.getEntity().serializeNBT(event.getEntity().registryAccess());
                    event.getEntity().remove(Entity.RemovalReason.KILLED);
                    summoningStaffItem.addCharge(source.getMainHandItem(), eTag);
                    event.setCanceled(true);
                }
            } else if (EntityUtil.isEnthralledBy(source, event.getEntity())) {
                event.setCanceled(true);
            } else if (EntityUtil.sameMaster(event.getEntity(), source)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onExpDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() != null && event.getAttackingPlayer().hasEffect(EidolonPotions.SOUL_HARVEST)) {
            event.setDroppedExperience(Mth.ceil(event.getDroppedExperience() * 1.25));
        }
    }

}
