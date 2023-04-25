package elucent.eidolon;

import com.mojang.authlib.GameProfile;
import elucent.eidolon.capability.*;
import elucent.eidolon.entity.ZombieBruteEntity;
import elucent.eidolon.entity.ai.PriestBarterGoal;
import elucent.eidolon.entity.ai.WitchBarterGoal;
import elucent.eidolon.event.StuckInBlockEvent;
import elucent.eidolon.item.*;
import elucent.eidolon.network.*;
import elucent.eidolon.registries.Potions;
import elucent.eidolon.ritual.Ritual;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.tile.GobletTileEntity;
import elucent.eidolon.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

import java.util.Comparator;
import java.util.List;

public class Events {
    @SubscribeEvent
    public void attachWorldCaps(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject() instanceof Level) event.addCapability(new ResourceLocation(Eidolon.MODID, "reputation"), new IReputation.Provider());
    }

    @SubscribeEvent
    public void attachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(Eidolon.MODID, "knowledge"), new IKnowledge.Provider());
            event.addCapability(new ResourceLocation(Eidolon.MODID, "player_data"), new IPlayerData.Provider());
        }
        if (event.getObject() instanceof LivingEntity) event.addCapability(new ResourceLocation(Eidolon.MODID, "soul"), new ISoul.Provider());
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        Capability<IKnowledge> KNOWLEDGE = IKnowledge.INSTANCE;
        Capability<ISoul> SOUL = ISoul.INSTANCE;
        Capability<IPlayerData> PDATA = IPlayerData.INSTANCE;
        event.getOriginal().reviveCaps();
        event.getEntity().getCapability(KNOWLEDGE).ifPresent((k) -> {
            event.getOriginal().getCapability(KNOWLEDGE).ifPresent((o) -> {
                ((INBTSerializable<CompoundTag>) k).deserializeNBT(((INBTSerializable<CompoundTag>) o).serializeNBT());
            });
        });
        event.getEntity().getCapability(SOUL).ifPresent((k) -> {
            event.getOriginal().getCapability(SOUL).ifPresent((o) -> {
                ((INBTSerializable<CompoundTag>) k).deserializeNBT(((INBTSerializable<CompoundTag>) o).serializeNBT());
            });
        });
        event.getEntity().getCapability(PDATA).ifPresent((k) -> {
            event.getOriginal().getCapability(PDATA).ifPresent((o) -> {
                ((INBTSerializable<CompoundTag>) k).deserializeNBT(((INBTSerializable<CompoundTag>) o).serializeNBT());
            });
        });
        event.getOriginal().invalidateCaps();
        if (!event.getEntity().level.isClientSide) {
            Networking.sendTo(event.getEntity(), new KnowledgeUpdatePacket(event.getEntity(), false));
            Networking.sendTo(event.getEntity(), new SoulUpdatePacket(event.getEntity()));
        }
    }
    
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        //KnowledgeCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onTarget(LivingSetAttackTargetEvent event) {
        if (EntityUtil.isEnthralledBy(event.getEntity(), event.getTarget()))
            ((Mob) event.getEntity()).setTarget(null);
    }

    @SubscribeEvent
    public void onTarget(LivingTickEvent event) {
        Level level = event.getEntity().getLevel();
        LivingEntity e = event.getEntity();
        if (e.hasEffect(Potions.UNDEATH_EFFECT.get()) && level.isDay() && !level.isClientSide) {
            float f = 0;
            BlockPos blockpos = e.getVehicle() instanceof Boat ? (new BlockPos(e.getX(), (double) Math.round(e.getY()), e.getZ())).above() : new BlockPos(e.getX(), (double) Math.round(e.getY()), e.getZ());
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
            if (goblets.size() > 0) {
                GobletTileEntity goblet = goblets.stream().min(Comparator.comparingDouble((g) -> g.getBlockPos().distSqr(pos))).get();
                goblet.setEntityType(entity.getType());
            }
        }

        if (entity instanceof Witch || entity instanceof Villager) {
            if (entity.getMainHandItem().getItem() instanceof CodexItem)
                event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), entity.getMainHandItem().copy()));
        }

        if (EntityUtil.isEnthralled(entity)) {
            event.getDrops().clear();
            return;
        }
        
        if (entity instanceof ZombieBruteEntity z && entity.hasEffect(MobEffects.WITHER) && !entity.level.isClientSide) {
            for (ItemEntity item : event.getDrops()) if (item.getItem().is(Registry.ZOMBIE_HEART.get())) {
                item.setItem(new ItemStack(Registry.WITHERED_HEART.get(), item.getItem().getCount()));
            }
        }

        if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity source) {
            ItemStack held = source.getMainHandItem();
            if (!entity.level.isClientSide && (held.getItem() instanceof ReaperScytheItem || event.getSource() == Registry.RITUAL_DAMAGE)
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
                    GameProfile gameprofile = ((Player)entity).getGameProfile();
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

    /*
    @SubscribeEvent
    public void registerSpawns(BiomeLoadingEvent ev) {
        ResourceKey<Biome> key = ResourceKey.<Biome>create(ForgeRegistries.Keys.BIOMES, ev.getName());
        if (BiomeDictionary.hasType(key, BiomeDictionary.Type.OVERWORLD) && ev.getCategory() != Biome.BiomeCategory.MUSHROOM) {
            ev.getSpawns().addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(Entities.WRAITH.get(), Config.WRAITH_SPAWN_WEIGHT.get(), 1, 2));
            ev.getSpawns().addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(Entities.ZOMBIE_BRUTE.get(), Config.ZOMBIE_BRUTE_SPAWN_WEIGHT.get(), 1, 2));
        }
        if (BiomeDictionary.hasType(key, BiomeDictionary.Type.OVERWORLD) && BiomeDictionary.hasType(key, BiomeDictionary.Type.FOREST)) {
            ev.getSpawns().addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(Entities.RAVEN.get(), Config.RAVEN_SPAWN_WEIGHT.get(), 2, 5));
        }
        if (key.equals(Biomes.OLD_GROWTH_PINE_TAIGA) || key.equals(Biomes.OLD_GROWTH_SPRUCE_TAIGA) || key.equals(Biomes.FLOWER_FOREST))
            ev.getSpawns().addSpawn(MobCategory.AMBIENT,
                new MobSpawnSettings.SpawnerData(Entities.SLIMY_SLUG.get(), Config.ABOVEGROUND_SLUG_WEIGHT.get(), 2, 5));
        if (key.equals(Biomes.LUSH_CAVES))
            ev.getSpawns().addSpawn(MobCategory.AMBIENT,
                new MobSpawnSettings.SpawnerData(Entities.SLIMY_SLUG.get(), Config.UNDERGROUND_SLUG_WEIGHT.get(), 2, 5));
    }

     */

    @SubscribeEvent
    public void registerCustomAI(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity && !event.getLevel().isClientSide) {
            if (event.getEntity() instanceof Player) {
                Networking.sendTo((Player) event.getEntity(), new KnowledgeUpdatePacket((Player) event.getEntity(), false));
                Networking.sendTo((Player) event.getEntity(), new SoulUpdatePacket((Player) event.getEntity()));
            }
            if (event.getEntity() instanceof Witch) {
                ((Witch) event.getEntity()).goalSelector.addGoal(1, new WitchBarterGoal(
                        (Witch) event.getEntity(),
                        (stack) -> stack.getItem() == Registry.CODEX.get(),
                        (stack) -> CodexItem.withSign(stack, Signs.WICKED_SIGN)
                ));
            }
            if (event.getEntity() instanceof Villager) {
                ((Villager)event.getEntity()).goalSelector.addGoal(1, new PriestBarterGoal(
                    (Villager)event.getEntity(),
                    (stack) -> stack.getItem() == Registry.CODEX.get(),
                    (stack) -> CodexItem.withSign(stack, Signs.SACRED_SIGN)
                ));
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) event.player.getCapability(IPlayerData.INSTANCE).ifPresent((d) -> {
            if (!d.getWingsItem(event.player).isEmpty()) {
                if (event.player.isCrouching() && event.player.getDeltaMovement().y < -0.1) {
                    d.startFlying(event.player);
                    event.player.setDeltaMovement(event.player.getDeltaMovement().x, -0.1, event.player.getDeltaMovement().z);
                }
                if (d.isFlying(event.player)) event.player.resetFallDistance();

                if (d.isDashing(event.player)) d.doDashTick(event.player);

                if (event.player.isOnGround()) {
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
        if (event.getEntity().hasEffect(Potions.UNDEATH_EFFECT.get())) {
            if (!event.getItem().is(Registry.ZOMBIE_FOOD_TAG))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPotionApplicable(Added event) {
        if (event.getEntity().hasEffect(MobEffects.HUNGER) && event.getEffectInstance().getEffect() == Potions.UNDEATH_EFFECT.get()) {
            event.getEntity().removeEffect(MobEffects.HUNGER);
        }
    }

    @SubscribeEvent
    public void onPotionApplicable(Applicable event) {
        if (event.getEntity().hasEffect(Potions.UNDEATH_EFFECT.get()) && event.getEffectInstance().getEffect() == MobEffects.HUNGER) {
            event.setResult(Result.DENY);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if ((event.getSource().getMsgId() == DamageSource.WITHER.getMsgId() || event.getSource().isMagic())) {
            if (event.getSource().getEntity() instanceof LivingEntity
                && ((LivingEntity) event.getSource().getEntity()).getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WarlockRobesItem) {
                event.setAmount(event.getAmount() * 1.5f);
                if (event.getSource().getMsgId() == DamageSource.WITHER.getMsgId())
                    ((LivingEntity) event.getSource().getEntity()).heal(event.getAmount() / 2);
            }
            if (event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WarlockRobesItem)
                event.setAmount(event.getAmount() / 2);
        }

        event.getEntity().getCapability(ISoul.INSTANCE).ifPresent(s -> {
            if (s.hasEtherealHealth()) {
                s.hurtEtherealHealth(event.getAmount(), ISoul.getPersistentHealth(event.getEntity()));
                event.setAmount(0);
                Networking.sendToTracking(event.getEntity().level, event.getEntity().getOnPos(), new SoulUpdatePacket((Player) event.getEntity()));
            }
        });
    }

    @SubscribeEvent
    public void onGetSpeedFactor(StuckInBlockEvent event) {
        if (event.getStuckMultiplier().length() < 1.0f && event.getEntity() instanceof LivingEntity && ((LivingEntity)event.getEntity()).getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WarlockRobesItem) {
            Vec3 diff = new Vec3(1, 1, 1).subtract(event.getStuckMultiplier()).scale(0.5);
            event.setStuckMultiplier(new Vec3(1, 1, 1).subtract(diff));
        }
    }
}
