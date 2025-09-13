package elucent.eidolon.api.deity;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Events related to a player's reputation with a deity.
 * These events are cancellable, preventing the reputation change or stage unlock/lock.
 */
@Cancelable
public abstract class ReputationEvent extends Event {

    public Deity deity;
    public Deity.Stage stage;
    public Player player;

    /**
     * Called when a player's reputation with a deity is about to change, called before Progression#tryProgress.
     */
    public static class Change extends ReputationEvent {

        public final double oldRep;
        public final double newRep;

        public Change(Deity deity, Player player, double oldRep, double newRep) {
            this.player = player;
            this.deity = deity;
            this.oldRep = oldRep;
            this.newRep = newRep;
        }
    }

    public static class Unlock extends ReputationEvent {
        public Unlock(Deity deity, Player player, Deity.Stage stage) {
            this.player = player;
            this.deity = deity;
            this.stage = stage;
        }
    }

    public static class Lock extends ReputationEvent {
        public Lock(Deity deity, Player player, Deity.Stage stage) {
            this.player = player;
            this.deity = deity;
            this.stage = stage;
        }
    }

}
