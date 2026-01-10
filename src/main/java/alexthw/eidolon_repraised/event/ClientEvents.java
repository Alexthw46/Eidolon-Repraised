package alexthw.eidolon_repraised.event;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.client.ClientConfig;
import alexthw.eidolon_repraised.codex.CodexChapters;
import alexthw.eidolon_repraised.common.item.IWingsItem;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.util.ClientInfo;
import alexthw.eidolon_repraised.util.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.SequencedMap;

import static alexthw.eidolon_repraised.registries.Registry.CHANT_SCROLL;
import static alexthw.eidolon_repraised.registries.Registry.WARLOCK_BOOTS;
import static alexthw.eidolon_repraised.registries.Registry.WARLOCK_CLOAK;
import static alexthw.eidolon_repraised.registries.Registry.WARLOCK_HAT;

@EventBusSubscriber(modid = Eidolon.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @OnlyIn(Dist.CLIENT)
    static MultiBufferSource.BufferSource DELAYED_RENDER = null;

    static RenderType[] renderTypes = new RenderType[]{
            RenderUtil.VAPOR_TRANSLUCENT,
            RenderUtil.DELAYED_PARTICLE,
            RenderUtil.GLOWING_PARTICLE,
            RenderUtil.GLOWING_BLOCK_PARTICLE,
            RenderUtil.GLOWING,
            RenderUtil.GLOWING_SPRITE
    };

    @OnlyIn(Dist.CLIENT)
    public static MultiBufferSource.BufferSource getDelayedRender() {
        if (DELAYED_RENDER == null) {
            SequencedMap<RenderType, ByteBufferBuilder> buffers = Util.make(new Object2ObjectLinkedOpenHashMap<>(), map -> {
                for (RenderType type : renderTypes) {
                    map.put(type, new ByteBufferBuilder(ModList.get().isLoaded("rubidium") ? 262144 : type.bufferSize()));
                }
            });
            DELAYED_RENDER = MultiBufferSource.immediateWithBuffers(buffers, new ByteBufferBuilder(ModList.get().isLoaded("rubidium") ? 262144 : 256));
        }
        return DELAYED_RENDER;
    }

    @OnlyIn(Dist.CLIENT)
    public static Matrix4f particleMVMatrix = null;

    @OnlyIn(Dist.CLIENT)
    public static void onRenderLast() {
        ClientInfo.renderTickEnd();
        if (ClientConfig.BETTER_LAYERING.get()) {
            Matrix4fStack viewStack = RenderSystem.getModelViewStack();
            viewStack.pushMatrix(); // this feels...cheaty
            viewStack.identity();
            if (particleMVMatrix != null) viewStack.mul(particleMVMatrix);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            getDelayedRender().endBatch(RenderUtil.DELAYED_PARTICLE);
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            getDelayedRender().endBatch(RenderUtil.GLOWING_PARTICLE);
            getDelayedRender().endBatch(RenderUtil.GLOWING_BLOCK_PARTICLE);
            getDelayedRender().endBatch(RenderUtil.GLOWING);
            getDelayedRender().endBatch(RenderUtil.GLOWING_SPRITE);
            viewStack.popMatrix();
            RenderSystem.applyModelViewMatrix();
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderStages(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY)
            ClientInfo.renderTickStart(event.getPartialTick().getGameTimeDeltaTicks());
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) onRenderLast();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)
            ClientInfo.clientTicks += event.getPartialTick().getGameTimeDeltaTicks();
    }

    public static int jumpTicks = 0;
    public static boolean wasJumping = false;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide()) return;
        Player p = event.getEntity();
        if (p instanceof LocalPlayer lp) {
            var wingsData = lp.getCapability(EidolonCapabilities.WINGS_CAPABILITY);
            if (wingsData == null) return;
            ItemStack wings = wingsData.getWingsItem(p);
            if (!wingsData.canFlap(p)) return;
            if (!(wings.getItem() instanceof IWingsItem)) return;
            if (lp.input.jumping && (!wasJumping || jumpTicks > 0)) {
                jumpTicks++;
                if (jumpTicks > 20) jumpTicks = 20;
            } else if (wasJumping && jumpTicks > 0) {
                if (jumpTicks >= 20 && !wingsData.isDashing(p)) {
                    wingsData.tryDash(p);
                } else wingsData.tryFlapWings(p);
                jumpTicks = 0;
            }
            if (p.onGround()) jumpTicks = 0;
            wasJumping = p.onGround() || lp.input.jumping;
        }
    }

    @SubscribeEvent
    public static void TooltipEvent(RenderTooltipEvent.Pre e) {
        CodexChapters.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        Integer necro = itemStack.get(EidolonDataComponents.NECROTIC);
        if (necro != null && necro > 0) {
            event.getToolTip().add(Component.translatable("eidolon_repraised.tooltip.necrotic").withStyle(ChatFormatting.DARK_BLUE));
        }

        Integer sacred = itemStack.get(EidolonDataComponents.CONSECRATED);
        if (sacred != null && sacred > 0) {
            event.getToolTip().add(Component.translatable("eidolon_repraised.tooltip.sacred").withStyle(ChatFormatting.GOLD));
        }
    }

    @SubscribeEvent
    public static void initItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                WARLOCK_HAT.get());

        event.register((stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                WARLOCK_CLOAK.get());

        event.register(
                (stack, color) -> color > 0 ? -1 :
                        colorFromArmor(stack),
                WARLOCK_BOOTS.get()
        );

        event.register((stack, color) -> {
                    if (color == 0 || !stack.has(EidolonDataComponents.SPELL)) return -1;
                    List<Sign> spell = stack.getOrDefault(EidolonDataComponents.SPELL, List.of());
                    if (!spell.isEmpty()) {
                        // Randomly pick a color from the spell's colors based on system time
                        int index = (int) ((ClientInfo.clientTicks / 80) % spell.size());
                        return spell.get(index).color();
                    }
                    return -1;
                },
                CHANT_SCROLL.get());
    }

    public static int colorFromArmor(ItemStack stack) {
        DyeColor color = stack.getOrDefault(DataComponents.BASE_COLOR, DyeColor.BLUE);
        if (color == DyeColor.BLUE) return FastColor.ABGR32.color(255, 100, 125, 250);
        return FastColor.ABGR32.opaque(color.getTextureDiffuseColor());
    }

}
