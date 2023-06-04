package elucent.eidolon.old;

/*
public class WorldGen {
    static DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, Eidolon.MODID);
    static List<StructureFeature<?>> STRUCTURE_LIST = new ArrayList<>();
    static Map<ResourceLocation, StructureFeatureConfiguration> STRUCTURE_SETTINGS = new HashMap<>();

    static <C extends FeatureConfiguration> RegistryObject<StructureFeature<C>> addStructure(String name, StructureFeature<C> structure, GenerationStep.Decoration stage, StructureFeatureConfiguration settings) {
        StructureFeature.STRUCTURES_REGISTRY.put(Eidolon.MODID + ":" + name, structure);
        StructureFeature.STEP.put(structure, stage);
        STRUCTURE_LIST.add(structure);
        STRUCTURE_SETTINGS.put(new ResourceLocation(Eidolon.MODID, name), settings);
        if (stage != GenerationStep.Decoration.UNDERGROUND_STRUCTURES) {
            StructureFeature.NOISE_AFFECTING_FEATURES = ImmutableList.<StructureFeature<?>>builder().addAll(StructureFeature.NOISE_AFFECTING_FEATURES).add(structure).build();
        }

        return STRUCTURES.register(name, () -> structure);
    }

    public static StructurePieceType LAB_PIECE, STRAY_TOWER_PIECE;

    public static RegistryObject<StructureFeature<NoneFeatureConfiguration>> LAB_STRUCTURE = addStructure("lab",
        new LabStructure(NoneFeatureConfiguration.CODEC),
        GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
        new StructureFeatureConfiguration(7, 5, 1337));

    public static RegistryObject<StructureFeature<NoneFeatureConfiguration>> STRAY_TOWER_STRUCTURE = addStructure("stray_tower",
        new StrayTowerStructure(NoneFeatureConfiguration.CODEC),
        GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
        new StructureFeatureConfiguration(16, 8, 1341));

    public static RegistryObject<StructureFeature<NoneFeatureConfiguration>> CATACOMB_STRUCTURE = addStructure("catacomb",
        new CatacombStructure(NoneFeatureConfiguration.CODEC),
        GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
        new StructureFeatureConfiguration(11, 7, 1347));

    public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> LAB_FEATURE, STRAY_TOWER_FEATURE, CATACOMB_FEATURE;

    public static void preInit() {
        STRUCTURES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void init() {

        LAB_PIECE = register((ctx, tag) -> new RandomlyRotatedPiece(LAB_PIECE, tag, ctx.structureManager()), "lab");
        LAB_FEATURE = register(LAB_STRUCTURE.get().configured(NoneFeatureConfiguration.INSTANCE), "lab");

        STRAY_TOWER_PIECE = register((ctx, tag) -> new RandomlyRotatedPiece(STRAY_TOWER_PIECE, tag, ctx.structureManager()), "stray_tower");
        STRAY_TOWER_FEATURE = register(STRAY_TOWER_STRUCTURE.get().configured(NoneFeatureConfiguration.INSTANCE), "stray_tower");

        CatacombPieces.CORRIDOR_CENTER = register((ctx, tag) -> new CatacombPieces.CorridorCenter(ctx.structureManager(), tag), CatacombPieces.CORRIDOR_CENTER_ID.getPath());
        CatacombPieces.CORRIDOR_DOOR = register((ctx, tag) -> new CatacombPieces.CorridorDoor(ctx.structureManager(), tag), CatacombPieces.CORRIDOR_DOOR_ID.getPath());
        CatacombPieces.SMALL_ROOM = register((ctx, tag) -> new CatacombPieces.SmallRoom(ctx.structureManager(), tag), CatacombPieces.SMALL_ROOM_ID.getPath());
        CatacombPieces.SHRINE = register((ctx, tag) -> new CatacombPieces.Shrine(ctx.structureManager(), tag), CatacombPieces.SHRINE_ID.getPath());
        CatacombPieces.TRAP = register((ctx, tag) -> new CatacombPieces.Trap(ctx.structureManager(), tag), CatacombPieces.TRAP_ID.getPath());
        CatacombPieces.SKULL = register((ctx, tag) -> new CatacombPieces.Skull(ctx.structureManager(), tag), CatacombPieces.SKULL_ID.getPath());
        CatacombPieces.SPAWNER = register((ctx, tag) -> new CatacombPieces.Spawner(ctx.structureManager(), tag), CatacombPieces.SPAWNER_ID.getPath());
        CatacombPieces.COFFIN = register((ctx, tag) -> new CatacombPieces.Coffin(ctx.structureManager(), tag), CatacombPieces.COFFIN_ID.getPath());
        CatacombPieces.MEDIUM_ROOM = register((ctx, tag) -> new CatacombPieces.MediumRoom(ctx.structureManager(), tag), CatacombPieces.MEDIUM_ROOM_ID.getPath());
        CatacombPieces.GRAVEYARD = register((ctx, tag) -> new CatacombPieces.Graveyard(ctx.structureManager(), tag), CatacombPieces.GRAVEYARD_ID.getPath());
        CatacombPieces.TURNAROUND = register((ctx, tag) -> new CatacombPieces.Turnaround(ctx.structureManager(), tag), CatacombPieces.TURNAROUND_ID.getPath());
        CatacombPieces.LAB = register((ctx, tag) -> new CatacombPieces.Lab(ctx.structureManager(), tag), CatacombPieces.LAB_ID.getPath());
        CATACOMB_FEATURE = register(CATACOMB_STRUCTURE.get().configured(NoneFeatureConfiguration.INSTANCE), "catacomb");

        for (StructureFeature<?> s : STRUCTURE_LIST) {
            StructureSettings.DEFAULTS = // Default structures
                ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
                    .putAll(StructureSettings.DEFAULTS)
                    .put(s, STRUCTURE_SETTINGS.get(s.getRegistryName()))
                    .build();

            NoiseGeneratorSettings overworld = BuiltinRegistries.NOISE_GENERATOR_SETTINGS.get(NoiseGeneratorSettings.OVERWORLD);
            overworld.structureSettings().structureConfig.put(s, STRUCTURE_SETTINGS.get(s.getRegistryName()));
        }
    }
    
    public void onWorldLoad() {
        if (event.getWorld() instanceof ServerLevel sl) {
            ChunkGenerator chunkGenerator = sl.getChunkSource().getGenerator();

            if (chunkGenerator instanceof FlatLevelSource && sl.dimensionType().equals(DimensionType.OVERWORLD_LOCATION)) {
                return;
            }

            StructureSettings worldStructureConfig = chunkGenerator.getSettings();
            HashMap<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> structureMap = new HashMap<>();
            
            for(Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : sl.registryAccess().ownedRegistryOrThrow(net.minecraft.core.Registry.BIOME_REGISTRY).entrySet()) {
                Biome.BiomeCategory biomeCategory = biomeEntry.getValue().getBiomeCategory();
                if (Config.LAB_ENABLED.get()) 
    	        	associateBiomeToConfiguredStructure(structureMap, LAB_FEATURE, biomeEntry.getKey());
                if ((biomeCategory == BiomeCategory.ICY || biomeCategory == BiomeCategory.TAIGA) && Config.STRAY_TOWER_ENABLED.get())
    	        	associateBiomeToConfiguredStructure(structureMap, STRAY_TOWER_FEATURE, biomeEntry.getKey());
                if (Config.CATACOMB_ENABLED.get()) 
    	        	associateBiomeToConfiguredStructure(structureMap, CATACOMB_FEATURE, biomeEntry.getKey());
            }
            
            ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> tempStructureToMultiMap = ImmutableMap.builder();
            worldStructureConfig.configuredStructures.entrySet().stream().filter(entry -> !structureMap.containsKey(entry.getKey())).forEach(tempStructureToMultiMap::put);
            structureMap.forEach((key, value) -> tempStructureToMultiMap.put(key, ImmutableMultimap.copyOf(value)));
            worldStructureConfig.configuredStructures = tempStructureToMultiMap.build();

            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(worldStructureConfig.structureConfig());
            tempMap.putIfAbsent(STRAY_TOWER_STRUCTURE.get(), STRUCTURE_SETTINGS.get(STRAY_TOWER_STRUCTURE.getId()));
            tempMap.putIfAbsent(LAB_STRUCTURE.get(), STRUCTURE_SETTINGS.get(LAB_STRUCTURE.getId()));
            tempMap.putIfAbsent(CATACOMB_STRUCTURE.get(), STRUCTURE_SETTINGS.get(CATACOMB_STRUCTURE.getId()));
            worldStructureConfig.structureConfig = tempMap;
        }
    }


}

 */
