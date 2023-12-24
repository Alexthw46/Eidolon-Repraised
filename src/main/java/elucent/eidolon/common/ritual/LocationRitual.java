package elucent.eidolon.common.ritual;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.network.CrystallizeEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class LocationRitual extends Ritual {

    TagKey<Structure> structureTagKey;

    public static final ResourceLocation SYMBOL = new ResourceLocation(Eidolon.MODID, "particle/summon_ritual");


    public LocationRitual(TagKey<Structure> structureTagKey) {
        super(SYMBOL, ColorUtil.packColor(255, 121, 94, 255));
        this.structureTagKey = structureTagKey;
    }

    @Override
    public Ritual cloneRitual() {
        return new LocationRitual(structureTagKey);
    }

    @Override
    public RitualResult start(Level level, BlockPos pos) {
        if ((level instanceof ServerLevel world)) {
            Networking.sendToTracking(world, pos, new CrystallizeEffectPacket(pos));
            BlockPos structure = world.findNearestMapStructure(structureTagKey, pos, 100, true);
            if (structure != null) {
                ItemStack mapStack = MapItem.create(world, structure.getX(), structure.getZ(), (byte) 2, true, true);
                MapItem.renderBiomePreviewMap(world, mapStack);
                MapItemSavedData.addTargetDecoration(mapStack, structure, "+", MapDecoration.Type.RED_X);
                mapStack.setHoverName(Component.literal("Magic Map"));
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, mapStack));
            }
        }
        return RitualResult.TERMINATE;
    }
}
