package elucent.eidolon.registries;

import elucent.eidolon.common.tile.SignBlockEntityCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static elucent.eidolon.registries.Registry.*;

public class DecoBlockPack {
    final DeferredRegister<Block> mainBlock;
    public final String baseBlockName;
    final BlockBehaviour.Properties props;
    RegistryObject<Block> full, slab, stair;
    @Nullable RegistryObject<Block> wall = null, pressure_plate = null;

    public DecoBlockPack(DeferredRegister<Block> blocks, String baseBlockName, BlockBehaviour.Properties props) {
        this.mainBlock = blocks;
        this.baseBlockName = baseBlockName;
        this.props = props;
        full = Registry.addBlock(baseBlockName, () -> new Block(props));
        slab = Registry.addBlock(baseBlockName + "_slab", () -> new SlabBlock(props));
        stair = Registry.addBlock(baseBlockName + "_stairs", () -> new StairBlock(() -> full.get().defaultBlockState(), props));
    }

    public DecoBlockPack addWall() {
        wall = Registry.addBlock(baseBlockName + "_wall", () -> new WallBlock(props));
        return this;
    }

    public DecoBlockPack addPressurePlate(PressurePlateBlock.Sensitivity sensitivity) {
        pressure_plate = Registry.addBlock(baseBlockName + "_pressure_plate", () -> new PressurePlateBlock(sensitivity, props));
        return this;
    }

    public DecoBlockPack addPressurePlate() {
        return addPressurePlate(PressurePlateBlock.Sensitivity.MOBS);
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
        public final String woodName;

        RegistryObject<Block> fence, fence_gate, button;
        @Nullable RegistryObject<Block> wSign = null, sSign = null, door = null, trapdoor = null;

        public WoodDecoBlock(DeferredRegister<Block> blocks, String basename, WoodType type, BlockBehaviour.Properties props) {
            super(blocks, basename, props);
            this.woodName = type.name().split(":")[1]; //strip the namespace
            this.woodType = type;
            addButton();
            addPressurePlate();
            addFence();
        }

        public WoodDecoBlock addSign() {
            sSign = BLOCKS.register(woodName + "_standing_sign", () -> new StandingSignBlock(props, this.woodType) {
                @Override
                public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
                    return new SignBlockEntityCopy(pPos, pState);
                }
            });
            wSign = BLOCKS.register(woodName + "_wall_sign", () -> new WallSignBlock(props, this.woodType) {
                @Override
                public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
                    return new SignBlockEntityCopy(pPos, pState);
                }
            });
            ITEMS.register(woodName + "_sign", () -> new SignItem(itemProps(), sSign.get(), wSign.get()));
            return this;
        }

        public WoodDecoBlock addButton() {
            button = Registry.addBlock(woodName + "_button", () -> new WoodButtonBlock(props));
            return this;
        }

        public WoodDecoBlock addFence() {
            fence = Registry.addBlock(baseBlockName + "_fence", () -> new FenceBlock(props));
            fence_gate = Registry.addBlock(baseBlockName + "_fence_gate", () -> new FenceGateBlock(props));
            return this;
        }

        @Override
        public WoodDecoBlock addPressurePlate() {
            pressure_plate = Registry.addBlock(woodName + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, props));
            return this;
        }

        public WoodDecoBlock addDoors() {
            door = Registry.addBlock(woodName + "_door", () -> new DoorBlock(props));
            trapdoor = Registry.addBlock(woodName + "_trapdoor", () -> new TrapDoorBlock(props));
            return this;
        }

        public Block getFence() {
            return fence.get();
        }

        public Block getFenceGate() {
            return fence_gate.get();
        }

        public ButtonBlock getButton() {
            return (ButtonBlock) button.get();
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
    }

}