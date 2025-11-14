package alexthw.eidolon_repraised.api.deity;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Events related to a player's reputation with a deity.
 * These events are cancellable, preventing the reputation change or stage unlock/lock.
 */
public abstract class ReputationEvent extends Event implements ICancellableEvent {

    public Deity deity;
    public Deity.Stage stage;
    public Player player;

    /**
     * Called when a player's reputation with a deity is about to change, called before Progression#tryProgress.
     * If cancelled, the reputation change will not occur.
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

    /**
     * Called when a player unlocks a new stage with a deity.
     * If cancelled, the stage will not be unlocked and the reputation remains capped.
     */
    public static class Unlock extends ReputationEvent {
        public Unlock(Deity deity, Player player, Deity.Stage stage) {
            this.player = player;
            this.deity = deity;
            this.stage = stage;
        }
    }

    /**
     * Called when a player reach a threshold with a deity and the progression is to be locked until certain conditions are met.
     * Locking is also used to grant the player access to new abilities to advance to the next stage.
     * If cancelled, the stage will not be locked and the reputation remains uncapped. This may allow the player to progress further than intended and lose access to the abilities granted by the lock. Don't be careless when cancelling this event.
     */
    public static class Lock extends ReputationEvent {
        public Lock(Deity deity, Player player ,Deity.Stage stage) {
            this.player = player;
            this.deity = deity;
            this.stage = stage;
        }
    }

}
