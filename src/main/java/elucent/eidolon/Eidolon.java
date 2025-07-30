package elucent.eidolon;

import com.google.common.collect.ImmutableSet;
import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.common.item.AthameItem;
import elucent.eidolon.common.tile.*;
import elucent.eidolon.compat.CompatHandler;
import elucent.eidolon.event.Events;
import elucent.eidolon.gui.*;
import elucent.eidolon.mixin.BlockEntityTypeAccessor;
import elucent.eidolon.network.Networking;
import elucent.eidolon.proxy.ClientProxy;
import elucent.eidolon.proxy.ISidedProxy;
import elucent.eidolon.proxy.ServerProxy;
import elucent.eidolon.registries.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Mod(Eidolon.MODID)
public class Eidolon {
    public static ISidedProxy proxy;

    public static final String MODID = "eidolon";
    public static final Logger LOG = LogManager.getLogger("Eidolon Repraised");

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath("eidolon", path);
    }

    public static boolean trueMobType = false;

    public static boolean isValidUndead(LivingEntity e) {
        trueMobType = true;
        boolean type = e.getType().getTags().toList().contains(EntityTypeTags.UNDEAD);
        trueMobType = false;
        return type;
    }

    public Eidolon(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::sendImc);
        modEventBus.addListener(this::spawnPlacements);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.register(new Registry());
        Registry.init(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new Events());

        CompatHandler.initialize();

        if (FMLEnvironment.dist.isClient()) {
            //noinspection Convert2Lambda, avoids classloading
            proxy = new Supplier<ISidedProxy>() {
                @Override
                public ISidedProxy get() {
                    return new ClientProxy();
                }
            }.get();
        } else {
            proxy = new ServerProxy();
        }
        proxy.init(modEventBus);

    }

    public void setup(final FMLCommonSetupEvent event) {
        Networking.init();
        event.enqueueWork(() -> {
            Spells.init();
            RitualRegistry.init();
            IncenseRegistry.init();
            EidolonRecipes.ritualRecipeTypes.addAll(List.of(EidolonRecipes.CRAFTING_RITUAL_TYPE.get(), EidolonRecipes.SUMMON_RITUAL_TYPE.get(), EidolonRecipes.COMMAND_RITUAL_TYPE.get(), EidolonRecipes.LOCATION_RITUAL_TYPE.get(), EidolonRecipes.RITUAL_TYPE.get()));
            EidolonPotions.addBrewingRecipes();
            AltarEntries.init();
            Researches.init();
            Runes.init();
            AthameItem.initHarvestables();
            Raid.RaiderType.create("eidolon:necromancer", EidolonEntities.NECROMANCER.get(), new int[]{0, 0, 0, 0, 0, 1, 0, 1});
            addBlocksToTile(BlockEntityType.SIGN, Registry.ILLWOOD_PLANKS.getStandingSign(), Registry.ILLWOOD_PLANKS.getWallSign(), Registry.POLISHED_PLANKS.getStandingSign(), Registry.POLISHED_PLANKS.getWallSign());
            addBlocksToTile(BlockEntityType.HANGING_SIGN, Registry.ILLWOOD_PLANKS.getHangingSign(), Registry.ILLWOOD_PLANKS.getHangingWallSign(), Registry.POLISHED_PLANKS.getHangingSign(), Registry.POLISHED_PLANKS.getHangingWallSign());
        });
    }

    public static void addBlocksToTile(BlockEntityType<?> bet, Block... blocksToAdd) {
        Set<Block> oldSet = ((BlockEntityTypeAccessor) bet).getValidBlocks();
        if (oldSet instanceof ImmutableSet<Block>) {
            Set<Block> newSet = new HashSet<>();
            Collections.addAll(newSet, blocksToAdd);
            newSet.addAll(oldSet);
            ((BlockEntityTypeAccessor) bet).setValidBlocks(newSet);
        } else {
            Collections.addAll(oldSet, blocksToAdd);
        }
    }


//    public void spawnPlacements(final SpawnPlacementRegisterEvent event) {
//        event.register(EidolonEntities.ZOMBIE_BRUTE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
//        event.register(EidolonEntities.WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
//        event.register(EidolonEntities.GIANT_SKEL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                (pType, pLevel, pSpawnType, pPos, pRandom) -> (pLevel.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(pType, pLevel, pSpawnType, pPos, pRandom)), SpawnPlacementRegisterEvent.Operation.AND);
//        event.register(EidolonEntities.RAVEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
//        event.register(EidolonEntities.SLIMY_SLUG.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                (e, w, t, pos, rand) -> true, SpawnPlacementRegisterEvent.Operation.AND);
//    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(Registry.HAND_TILE_ENTITY.get(), (trd) -> new HandTileRenderer());
        BlockEntityRenderers.register(Registry.BRAZIER_TILE_ENTITY.get(), (trd) -> new BrazierTileRenderer());
        BlockEntityRenderers.register(Registry.NECROTIC_FOCUS_TILE_ENTITY.get(), (trd) -> new NecroticFocusTileRenderer());
        BlockEntityRenderers.register(Registry.CRUCIBLE_TILE_ENTITY.get(), (trd) -> new CrucibleTileRenderer());
        BlockEntityRenderers.register(Registry.SOUL_ENCHANTER_TILE_ENTITY.get(), (trd) -> new SoulEnchanterTileRenderer());
        BlockEntityRenderers.register(Registry.GOBLET_TILE_ENTITY.get(), (trd) -> new GobletTileRenderer());
        BlockEntityRenderers.register(Registry.CENSER_TILE_ENTITY.get(), (trd) -> new CenserRenderer());

        event.enqueueWork(() -> {
            MenuScreens.register(Registry.WORKTABLE_CONTAINER.get(), WorktableScreen::new);
            MenuScreens.register(Registry.SOUL_ENCHANTER_CONTAINER.get(), SoulEnchanterScreen::new);
            MenuScreens.register(Registry.WOODEN_STAND_CONTAINER.get(), WoodenBrewingStandScreen::new);
            MenuScreens.register(Registry.RESEARCH_TABLE_CONTAINER.get(), ResearchTableScreen::new);
            MenuScreens.register(Registry.SCRIPTORIUM_CONTAINER.get(), ScriptoriumScreen::new);

            ClientRegistry.initCurios();

            Sheets.addWoodType(Registry.ILLWOOD);
            Sheets.addWoodType(Registry.POLISHED);
        });
        NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
            InteractionResult result = Events.rightClickLectern(e.getEntity(), e.getLevel(), e.getHitVec());
            if (result.consumesAction()) {
                e.setCanceled(true);
                e.setCancellationResult(result);
            }
        });
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> Networking.sendTo(e.getEntity(), new Networking.initCodexPacket()));
    }

//    @OnlyIn(Dist.CLIENT)
//    public static void registerOverlays(RegisterGuiOverlaysEvent evt) {
//        evt.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "hearts", new EidolonOverlays.EidolonHearts());
//        evt.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "mana_bar", new EidolonOverlays.EidolonManaBar());
//        evt.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "raven_charge", new EidolonOverlays.EidolonRavenCharge());
//    }

    public void sendImc(InterModEnqueueEvent evt) {
        InterModComms.sendTo("consecration", "holy_material", () -> "silver");
    }
}
