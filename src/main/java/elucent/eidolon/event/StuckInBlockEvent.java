package elucent.eidolon.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class StuckInBlockEvent extends net.minecraftforge.eventbus.api.Event {
    final Entity entity;
    BlockState state;
    Vec3 mul;

    public StuckInBlockEvent(Entity entity, BlockState state, Vec3 factor) {
        this.entity = entity;
        this.mul = factor;
    }

    public Entity getEntity() {
        return entity;
    }

    public Vec3 getStuckMultiplier() {
        return mul;
    }

    public void setStuckMultiplier(Vec3 factor) {
        mul = factor;
    }
}
