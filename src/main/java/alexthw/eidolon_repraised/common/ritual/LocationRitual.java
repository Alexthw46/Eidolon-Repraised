package alexthw.eidolon_repraised.common.ritual;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.network.CrystallizeEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class LocationRitual extends Ritual {

    TagKey<Structure> structureTagKey;

    public static final ResourceLocation SYMBOL = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "particle/summon_ritual");


    public LocationRitual(TagKey<Structure> structureTagKey) {
        super(SYMBOL, ColorUtil.packColor(255, 121, 94, 255));
        this.structureTagKey = structureTagKey;
    }

    @Override
    public Component getName() {
        return Component.translatable(Eidolon.MODID + ".ritual.location", structureTagKey.location().getPath());
    }

    @Override
    public Ritual cloneRitual() {
        return new LocationRitual(structureTagKey);
    }

    @Override
    public RitualResult start(Level level, BlockPos pos) {
        if ((level instanceof ServerLevel world)) {
            Networking.sendToNearbyClient(world, pos, new CrystallizeEffectPacket(pos));
            BlockPos structure = world.findNearestMapStructure(structureTagKey, pos, 100, true);
            if (structure != null) {
                ItemStack mapStack = MapItem.create(world, structure.getX(), structure.getZ(), (byte) 2, true, true);
                MapItem.renderBiomePreviewMap(world, mapStack);
                MapItemSavedData.addTargetDecoration(mapStack, structure, "+", MapDecorationTypes.RED_X);
                mapStack.set(DataComponents.CUSTOM_NAME, Component.literal("Magic Map"));
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, mapStack));
            }
        }
        return RitualResult.TERMINATE;
    }
}
