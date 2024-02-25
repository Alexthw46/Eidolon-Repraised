package elucent.eidolon.util;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface TargetMode {
    @Nullable Predicate<Entity> eidolon$getMode();
    void eidolon$setMode(final Predicate<Entity> targetMode);
}
