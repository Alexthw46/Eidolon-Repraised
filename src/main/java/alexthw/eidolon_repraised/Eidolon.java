package alexthw.eidolon_repraised;

import alexthw.eidolon_repraised.client.ClientConfig;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.client.EidolonOverlays;
import alexthw.eidolon_repraised.common.item.AthameItem;
import alexthw.eidolon_repraised.common.tile.*;
import alexthw.eidolon_repraised.compat.CompatHandler;
import alexthw.eidolon_repraised.event.Events;
import alexthw.eidolon_repraised.mixin.BlockEntityTypeAccessor;
import alexthw.eidolon_repraised.network.*;
import alexthw.eidolon_repraised.proxy.ClientProxy;
import alexthw.eidolon_repraised.proxy.ISidedProxy;
import alexthw.eidolon_repraised.proxy.ServerProxy;
import alexthw.eidolon_repraised.registries.*;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
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

    public static final String MODID = "eidolon_repraised";
    public static final Logger LOG = LogManager.getLogger("Eidolon Repraised");

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath("eidolon_repraised", path);
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
        //modEventBus.addListener(this::spawnPlacements);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.register(new Registry());
        Registry.init(modEventBus);
        modEventBus.addListener(Networking::register);
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
        event.enqueueWork(() -> {
            Spells.init();
            RitualRegistry.init();
            IncenseRegistry.init();
            EidolonRecipes.ritualRecipeTypes.addAll(List.of(EidolonRecipes.CRAFTING_RITUAL_TYPE.get(), EidolonRecipes.SUMMON_RITUAL_TYPE.get(), EidolonRecipes.COMMAND_RITUAL_TYPE.get(), EidolonRecipes.LOCATION_RITUAL_TYPE.get(), EidolonRecipes.RITUAL_TYPE.get()));
            Researches.init();
            Runes.init();
            AthameItem.initHarvestables();
            //Raid.RaiderType.create("eidolon_repraised:necromancer", EidolonEntities.NECROMANCER.get(), );
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
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) ->
        {
            if (!(e.getEntity() instanceof ServerPlayer player)) return;
            // Send all the data to the player when they log in
            Networking.sendToPlayerClient(new KnowledgeUpdatePacket(player, false), player);
            Networking.sendToPlayerClient(new SoulUpdatePacket(player), player);
            Networking.sendToPlayerClient(new WingsDataUpdatePacket(player), player);
            Networking.sendToPlayerClient(new InitCodexPacket(null), player);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerOverlays(RegisterGuiLayersEvent evt) {
        evt.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, prefix("hearts"), new EidolonOverlays.EidolonHearts());
        evt.registerBelow(VanillaGuiLayers.CHAT, prefix("mana_bar"), new EidolonOverlays.EidolonManaBar());
        evt.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR, prefix("raven_charge"), new EidolonOverlays.EidolonRavenCharge());
    }

    public void sendImc(InterModEnqueueEvent evt) {
        InterModComms.sendTo("consecration", "holy_material", () -> "silver");
    }
}
