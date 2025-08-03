package alexthw.eidolon_repraised.capability;

import alexthw.eidolon_repraised.api.research.Research;
import alexthw.eidolon_repraised.api.spells.Rune;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.Researches;
import alexthw.eidolon_repraised.registries.Runes;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class KnowledgeCommand {
    public static class SignArgument implements ArgumentType<Sign> {
        private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType((obj) -> Component.translatable("argument.eidolon_repraised.sign.unknown", obj));

        public static Sign getSign(final CommandContext<?> context, final String name) {
            return context.getArgument(name, Sign.class);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
            for (Sign s : Signs.getSigns())
                if (s.getRegistryName().toString().startsWith(builder.getRemainingLowerCase()))
                    builder.suggest(s.getRegistryName().toString());
            return builder.buildFuture();
        }

        @Override
        public Sign parse(StringReader reader) throws CommandSyntaxException {
            ResourceLocation rl = ResourceLocation.read(reader);
            Sign s = Signs.find(rl);
            if (s == null) throw UNKNOWN.create(rl.toString());
            return s;
        }

        public static SignArgument signs() {
            return new SignArgument();
        }
    }

    public static class ResearchArgument implements ArgumentType<Research> {
        private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType((obj) -> Component.translatable("argument.eidolon_repraised.research.unknown", obj));

        public static ResearchArgument researches() {
            return new ResearchArgument();
        }

        public static Research getResearch(final CommandContext<?> context, final String name) {
            return context.getArgument(name, Research.class);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
            for (Research r : Researches.getResearches())
                if (r.getRegistryName().toString().startsWith(builder.getRemainingLowerCase()))
                    builder.suggest(r.getRegistryName().toString());
            return builder.buildFuture();
        }

        @Override
        public Research parse(StringReader reader) throws CommandSyntaxException {
            ResourceLocation rl = ResourceLocation.read(reader);
            Research r = Researches.find(rl);
            if (r == null) throw UNKNOWN.create(rl.toString());
            return r;
        }
    }

    public static class RuneArgument implements ArgumentType<Rune> {
        private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType((obj) -> Component.translatable("argument.eidolon_repraised.rune.unknown", obj));

        public static RuneArgument runes() {
            return new RuneArgument();
        }

        public static Rune getRune(final CommandContext<?> context, final String name) {
            return context.getArgument(name, Rune.class);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
            for (Rune r : Runes.getRunes())
                if (r.getRegistryName().toString().startsWith(builder.getRemainingLowerCase()))
                    builder.suggest(r.getRegistryName().toString());
            return builder.buildFuture();
        }

        @Override
        public Rune parse(StringReader reader) throws CommandSyntaxException {
            ResourceLocation rl = ResourceLocation.read(reader);
            Rune r = Runes.find(rl);
            if (r == null) throw UNKNOWN.create(rl.toString());
            return r;
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("knowledge").requires((player) -> player.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("reset").
                                then(Commands.literal("signs").executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null) KnowledgeUtil.resetSigns(player);
                                })))
                                .then(Commands.literal("facts").executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null) KnowledgeUtil.resetFacts(player);
                                })))
                                .then(Commands.literal("research").executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null) KnowledgeUtil.resetResearch(player);
                                })))
                                .then(Commands.literal("runes").executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null) KnowledgeUtil.resetRunes(player);
                                }))))
                        .then(Commands.literal("grant")
                                .then(Commands.literal("sign").then(Commands.argument("sign", new SignArgument()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null) KnowledgeUtil.grantSign(player, SignArgument.getSign(ctx, "sign"));
                                }))))
                                .then(Commands.literal("fact").then(Commands.argument("fact", ResourceLocationArgument.id()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null)
                                        KnowledgeUtil.grantFact(player, ResourceLocationArgument.getId(ctx, "fact"));
                                }))))
                                .then(Commands.literal("research").then(Commands.argument("research", new ResearchArgument()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null)
                                        KnowledgeUtil.grantResearch(player, ResearchArgument.getResearch(ctx, "research"));
                                }))))
                                .then(Commands.literal("rune").then(Commands.argument("rune", new RuneArgument()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null) KnowledgeUtil.grantRune(player, RuneArgument.getRune(ctx, "rune"));
                                })))))
                        .then(Commands.literal("remove")
                                .then(Commands.literal("sign").then(Commands.argument("sign", new SignArgument()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null)
                                        KnowledgeUtil.removeSign(player, SignArgument.getSign(ctx, "sign"));
                                }))))
                                .then(Commands.literal("fact").then(Commands.argument("fact", ResourceLocationArgument.id()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null)
                                        KnowledgeUtil.removeFact(player, ResourceLocationArgument.getId(ctx, "fact"));
                                }))))
                                .then(Commands.literal("research").then(Commands.argument("research", new ResearchArgument()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null)
                                        KnowledgeUtil.removeResearch(player, ResearchArgument.getResearch(ctx, "research").getRegistryName());
                                }))))
                                .then(Commands.literal("rune").then(Commands.argument("rune", new RuneArgument()).executes((ctx) -> apply(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), (player, sources) -> {
                                    Object k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
                                    if (k != null)
                                        KnowledgeUtil.removeRune(player, RuneArgument.getRune(ctx, "rune"));
                                }))))

                        )
                )
        );
    }

    private static int apply(CommandSourceStack sources, Collection<? extends Player> players, BiConsumer<Player, CommandSourceStack> action) {
        for (Player player : players) {
            action.accept(player, sources);
        }

        if (players.size() == 1) {
            sources.sendSuccess(() -> Component.translatable("commands.eidolon_repraised.knowledge.success.single", players.iterator().next().getDisplayName()), true);
        } else {
            sources.sendSuccess(() -> Component.translatable("commands.eidolon_repraised.knowledge.success.multiple", players.size()), true);
        }

        return players.size();
    }
}
