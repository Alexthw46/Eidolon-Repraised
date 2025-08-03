package alexthw.eidolon_repraised.common.item;

import alexthw.eidolon_repraised.api.research.Research;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.registries.Researches;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class ResearchNotesItem extends ItemBase {
    private static final Random random = new Random();

    public ResearchNotesItem(Properties builderIn) {
        super(builderIn.component(EidolonDataComponents.RESEARCH, ResearchNotesItem.ResearchData.EMPTY));
    }

    public record ResearchData(ResourceLocation research, int stepsDone, long seed) {

        public ResearchData(ResourceLocation research, int stepsDone) {
            this(research, stepsDone, random.nextLong());
        }

        public static final Codec<ResearchData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("research").forGetter(ResearchData::research),
                        Codec.INT.fieldOf("stepsDone").forGetter(ResearchData::stepsDone),
                        Codec.LONG.optionalFieldOf("seed", random.nextLong()).forGetter(ResearchData::seed)
                ).apply(instance, ResearchData::new)
        );
        public static final ResearchData EMPTY = new ResearchData(ResourceLocation.parse("eidolon_repraised:dummy"), 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext tooltipContext, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        ResearchData researchData = stack.get(EidolonDataComponents.RESEARCH);
        if (researchData == null) return;
        Research r = Researches.find(researchData.research());
        if (r == null) return;
        int done = researchData.stepsDone();
        StringBuilder stars = new StringBuilder();
        stars.append(ChatFormatting.GOLD);
        for (int i = 0; i < r.getStars(); i++) {
            if (i == done) stars.append(ChatFormatting.GRAY);
            if (i < done) stars.append("\u2605");
            else stars.append("\u2606");
        }
        tooltip.add(Component.literal(stars.toString()));
        boolean known = done >= r.getStars() || KnowledgeUtil.knowsResearch(Minecraft.getInstance().player, r.getRegistryName());
        String name = known ? r.getName() : "???";
        tooltip.add(Component.literal(String.valueOf(known ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY) + ChatFormatting.ITALIC + name));
    }
}
