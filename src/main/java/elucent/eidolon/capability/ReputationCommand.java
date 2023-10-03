package elucent.eidolon.capability;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.common.deity.Deities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ReputationCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("devotion")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("get")
                                .then(Commands.argument("deity", new DeityArgument())
                                        .executes(
                                                ctx -> applyGet(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> sources.getLevel().getCapability(IReputation.INSTANCE).ifPresent((k) -> {
                                                    var devotion = k.getReputation(player, DeityArgument.getDeity(ctx, "deity").getId());
                                                    ctx.getSource().sendSuccess(Component.literal(player.getName().getString() + " : " + devotion), false);
                                                }))
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .requires(cs -> cs.hasPermission(2))
                                .then(Commands.argument("deity", new DeityArgument())
                                        .then(Commands.argument("qt", DoubleArgumentType.doubleArg(0, 100))
                                                .executes(
                                                        ctx -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> sources.getLevel().getCapability(IReputation.INSTANCE).ifPresent((k) -> k.setReputation(player, DeityArgument.getDeity(ctx, "deity").getId(), DoubleArgumentType.getDouble(ctx, "qt"))))
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int apply(CommandSourceStack sources, Collection<? extends Player> players, BiConsumer<Player, CommandSourceStack> action) {
        for (Player player : players) {
            action.accept(player, sources);
        }
        if (players.size() == 1) {
            sources.sendSuccess(Component.translatable("commands.eidolon.reputation.success.single", players.iterator().next().getDisplayName()), true);
        } else {
            sources.sendSuccess(Component.translatable("commands.eidolon.reputation.success.multiple", players.size()), true);
        }
        return players.size();
    }

    private static int applyGet(CommandSourceStack sources, Collection<? extends Player> players, BiConsumer<Player, CommandSourceStack> action) {
        for (Player player : players) {
            action.accept(player, sources);
        }
        return players.size();
    }

    public static class DeityArgument implements ArgumentType<Deity> {
        private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType((obj) -> Component.translatable("argument.eidolon.deity.unknown", obj));

        public static Deity getDeity(final CommandContext<?> context, final String name) {
            return context.getArgument(name, Deity.class);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
            for (Deity s : Deities.getDeities())
                if (s.getId().toString().startsWith(builder.getRemainingLowerCase()))
                    builder.suggest(s.getId().toString());
            return builder.buildFuture();
        }

        @Override
        public Deity parse(StringReader reader) throws CommandSyntaxException {
            ResourceLocation rl = ResourceLocation.read(reader);
            Deity s = Deities.find(rl);
            if (s == null) throw UNKNOWN.create(rl.toString());
            return s;
        }

        public static ReputationCommand.DeityArgument deities() {
            return new ReputationCommand.DeityArgument();
        }
    }
}