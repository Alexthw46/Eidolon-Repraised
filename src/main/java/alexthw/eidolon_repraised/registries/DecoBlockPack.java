package alexthw.eidolon_repraised.registries;

import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import static alexthw.eidolon_repraised.registries.Registry.*;

public class DecoBlockPack {
    final DeferredRegister<Block> mainBlock;
    public final String baseBlockName;
    final BlockBehaviour.Properties props;
    DeferredHolder<Block, Block> full, slab, stair;
    @Nullable DeferredHolder<Block, Block> wall = null, pressure_plate = null;

    public DecoBlockPack(DeferredRegister<Block> blocks, String baseBlockName, BlockBehaviour.Properties props) {
        this.mainBlock = blocks;
        this.baseBlockName = baseBlockName;
        this.props = props;
        full = Registry.addBlock(baseBlockName, () -> new Block(props));
        slab = Registry.addBlock(baseBlockName + "_slab", () -> new SlabBlock(props));
        stair = Registry.addBlock(baseBlockName + "_stairs", () -> new StairBlock(full.get().defaultBlockState(), props));
    }

    public DecoBlockPack addWall() {
        wall = Registry.addBlock(baseBlockName + "_wall", () -> new WallBlock(props));
        return this;
    }

    public DecoBlockPack addPressurePlate() {
        pressure_plate = Registry.addBlock(baseBlockName + "_pressure_plate", () -> new PressurePlateBlock(BlockSetType.DARK_OAK, props));
        return this;
    }

    public Block getBlock() {
        return full.get();
    }

    public Block getSlab() {
        return slab.get();
    }

    public Block getStairs() {
        return stair.get();
    }

    public Block getWall() {
        return wall == null ? null : wall.get();
    }

    public @Nullable PressurePlateBlock getPressurePlate() {
        return pressure_plate == null ? null : (PressurePlateBlock) pressure_plate.get();
    }

    public static class WoodDecoBlock extends DecoBlockPack {

        final WoodType woodType;
        String woodName;
        @Nullable DeferredHolder<Block, Block> hSign = null, hwSign = null, wSign = null, sSign = null, door = null, trapdoor = null, fence = null, fence_gate = null, button = null;

        public WoodDecoBlock(DeferredRegister<Block> blocks, String basename, WoodType type, BlockBehaviour.Properties props) {
            super(blocks, basename, props);
            this.woodName = type.name().split(":")[1]; //strip the namespace
            this.woodType = type;
        }

        public WoodDecoBlock addSign() {
            sSign = BLOCKS.register(woodName + "_standing_sign", () -> new StandingSignBlock(this.woodType, props));
            wSign = BLOCKS.register(woodName + "_wall_sign", () -> new WallSignBlock(this.woodType, props));
            hSign = BLOCKS.register(woodName + "_hanging_sign", () -> new CeilingHangingSignBlock(this.woodType, props));
            hwSign = BLOCKS.register(woodName + "_hanging_wall_sign", () -> new WallHangingSignBlock(this.woodType, props));
            ITEMS.register(woodName + "_sign", () -> new SignItem(itemProps(), sSign.get(), wSign.get()));
            ITEMS.register(woodName + "_hanging_sign", () -> new HangingSignItem(hSign.get(), hwSign.get(), itemProps()));

            return this;
        }

        public WoodDecoBlock addButton() {
            button = Registry.addBlock(woodName + "_button", () -> new ButtonBlock(BlockSetType.DARK_OAK, 30, props));
            return this;
        }

        public WoodDecoBlock addFence() {
            fence = Registry.addBlock(baseBlockName + "_fence", () -> new FenceBlock(props));
            fence_gate = Registry.addBlock(baseBlockName + "_fence_gate", () -> new FenceGateBlock(WoodType.DARK_OAK, props));
            return this;
        }

        public WoodDecoBlock addPressurePlate() {
            pressure_plate = Registry.addBlock(woodName + "_pressure_plate", () -> new PressurePlateBlock(BlockSetType.DARK_OAK, props));
            return this;
        }

        public WoodDecoBlock addDoors() {
            door = Registry.addBlock(woodName + "_door", () -> new DoorBlock(BlockSetType.DARK_OAK, props));
            trapdoor = Registry.addBlock(woodName + "_trapdoor", () -> new TrapDoorBlock(BlockSetType.DARK_OAK, props));
            return this;
        }

        public Block getFence() {
            return fence == null ? null : fence.get();
        }

        public Block getFenceGate() {
            return fence_gate == null ? null : fence_gate.get();
        }

        public @Nullable ButtonBlock getButton() {
            return button == null ? null : (ButtonBlock) button.get();
        }

        public @Nullable DoorBlock getDoor() {
            return door == null ? null : (DoorBlock) door.get();
        }

        public @Nullable TrapDoorBlock getTrapdoor() {
            return trapdoor == null ? null : (TrapDoorBlock) trapdoor.get();
        }

        public @Nullable StandingSignBlock getStandingSign() {
            return sSign == null ? null : (StandingSignBlock) sSign.get();
        }

        public @Nullable WallSignBlock getWallSign() {
            return wSign == null ? null : (WallSignBlock) wSign.get();
        }

        public @Nullable CeilingHangingSignBlock getHangingSign() {
            return hSign == null ? null : (CeilingHangingSignBlock) hSign.get();
        }

        public @Nullable WallHangingSignBlock getHangingWallSign() {
            return hwSign == null ? null : (WallHangingSignBlock) hwSign.get();
        }

    }

}