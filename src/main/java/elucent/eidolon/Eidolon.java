package elucent.eidolon;

import elucent.eidolon.codex.CodexChapters;
import elucent.eidolon.gui.ResearchTableScreen;
import elucent.eidolon.gui.SoulEnchanterScreen;
import elucent.eidolon.gui.WoodenBrewingStandScreen;
import elucent.eidolon.gui.WorktableScreen;
import elucent.eidolon.item.AthameItem;
import elucent.eidolon.network.Networking;
import elucent.eidolon.proxy.ClientProxy;
import elucent.eidolon.proxy.ISidedProxy;
import elucent.eidolon.proxy.ServerProxy;
import elucent.eidolon.registries.Entities;
import elucent.eidolon.registries.Potions;
import elucent.eidolon.research.Researches;
import elucent.eidolon.ritual.RitualRegistry;
import elucent.eidolon.spell.AltarEntries;
import elucent.eidolon.spell.Runes;
import elucent.eidolon.tile.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(Eidolon.MODID)
public class Eidolon {
    public static final ISidedProxy proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static final String MODID = "eidolon";

    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(Registry.SHADOW_GEM.get(), 1);
        }
    };
    
    public static boolean trueMobType = false;
    
    public static MobType getTrueMobType(LivingEntity e) {
        trueMobType = true;
        MobType type = e.getMobType();
        trueMobType = false;
        return type;
    }

    public Eidolon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::sendImc);
        modEventBus.addListener(this::spawnPlacements);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.register(new Registry());
        Registry.init();
        proxy.init();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public void setup(final FMLCommonSetupEvent event) {
        Networking.init();
        event.enqueueWork(() -> {
            //CrucibleRegistry.init();
            RitualRegistry.init();
            Potions.addBrewingRecipes();
            AltarEntries.init();
            Researches.init();
            Runes.init();
            AthameItem.initHarvestables();
            CodexChapters.init();
        });
    }


    public void spawnPlacements(final SpawnPlacementRegisterEvent event) {
        event.register(Entities.ZOMBIE_BRUTE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(Entities.WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(Entities.RAVEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(Entities.SLIMY_SLUG.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (e, w, t, pos, rand) -> true, SpawnPlacementRegisterEvent.Operation.AND);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(Registry.HAND_TILE_ENTITY.get(), (trd) -> new HandTileRenderer());
        BlockEntityRenderers.register(Registry.BRAZIER_TILE_ENTITY.get(), (trd) -> new BrazierTileRenderer());
        BlockEntityRenderers.register(Registry.NECROTIC_FOCUS_TILE_ENTITY.get(), (trd) -> new NecroticFocusTileRenderer());
        BlockEntityRenderers.register(Registry.CRUCIBLE_TILE_ENTITY.get(), (trd) -> new CrucibleTileRenderer());
        BlockEntityRenderers.register(Registry.SOUL_ENCHANTER_TILE_ENTITY.get(), (trd) -> new SoulEnchanterTileRenderer());
        BlockEntityRenderers.register(Registry.GOBLET_TILE_ENTITY.get(), (trd) -> new GobletTileRenderer());
        /*
        BlockEntityRenderers.register(Registry.CISTERN_TILE_ENTITY.get(), (trd) -> new CisternTileRenderer());
        BlockEntityRenderers.register(Registry.PIPE_TILE_ENTITY.get(), (trd) -> new PipeTileRenderer());
        */

        event.enqueueWork(() -> {
            MenuScreens.register(Registry.WORKTABLE_CONTAINER.get(), WorktableScreen::new);
            MenuScreens.register(Registry.SOUL_ENCHANTER_CONTAINER.get(), SoulEnchanterScreen::new);
            MenuScreens.register(Registry.WOODEN_STAND_CONTAINER.get(), WoodenBrewingStandScreen::new);
            MenuScreens.register(Registry.RESEARCH_TABLE_CONTAINER.get(), ResearchTableScreen::new);

            ClientRegistry.initCurios();
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerOverlays(RegisterGuiOverlaysEvent evt) {
        evt.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "hearts", new ClientRegistry.EidolonHearts());
        evt.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "mana_bar", new ClientRegistry.EidolonManaBar());
    }

    public void sendImc(InterModEnqueueEvent evt) {
        InterModComms.sendTo("consecration", "holy_material", () -> "silver");
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }
}
