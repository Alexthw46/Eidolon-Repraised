package elucent.eidolon.api.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;


public abstract class SpellCastEvent extends Event {

    public Spell spell;
    public Level world;
    public BlockPos pos;
    public Player player;
    public SignSequence signs;

    @Cancelable
    public static class Pre extends SpellCastEvent {
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
