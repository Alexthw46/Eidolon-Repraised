package alexthw.eidolon_repraised.api.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class SpellCastEvent extends Event {

    public Spell spell;
    public Level world;
    public BlockPos pos;
    public Player player;
    public SignSequence signs;

    public static class Pre extends SpellCastEvent implements ICancellableEvent {
        public Pre(Spell spell, Level world, BlockPos pos, Player player, SignSequence signs) {
            this.spell = spell;
            this.world = world;
            this.pos = pos;
            this.player = player;
            this.signs = signs;
        }
    }

    public static class Post extends SpellCastEvent {
        public Post(Spell spell, Level world, BlockPos pos, Player player, SignSequence signs) {
            this.spell = spell;
            this.world = world;
            this.pos = pos;
            this.player = player;
            this.signs = signs;
        }
    }

}
