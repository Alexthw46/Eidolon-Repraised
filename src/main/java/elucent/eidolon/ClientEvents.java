package elucent.eidolon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import elucent.eidolon.capability.IPlayerData;
import elucent.eidolon.item.IWingsItem;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class ClientEvents {
    @OnlyIn(Dist.CLIENT)
    static MultiBufferSource.BufferSource DELAYED_RENDER = null;

    @OnlyIn(Dist.CLIENT)
    public static MultiBufferSource.BufferSource getDelayedRender() {
        if (DELAYED_RENDER == null) {
            Map<RenderType, BufferBuilder> buffers = new HashMap<>();
            for (RenderType type : new RenderType[]{
                RenderUtil.VAPOR_TRANSLUCENT,
                RenderUtil.DELAYED_PARTICLE,
                RenderUtil.GLOWING_PARTICLE,
                RenderUtil.GLOWING_BLOCK_PARTICLE,
                RenderUtil.GLOWING,
                RenderUtil.GLOWING_SPRITE}) {
                buffers.put(type, new BufferBuilder(type.bufferSize()));
            }
            DELAYED_RENDER = MultiBufferSource.immediateWithBuffers(buffers, new BufferBuilder(256));
        }
        return DELAYED_RENDER;
    }

    @OnlyIn(Dist.CLIENT)
    static float clientTicks = 0;
    
    @OnlyIn(Dist.CLIENT)
    public static Matrix4f particleMVMatrix = null;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderLast(RenderLevelLastEvent event) {
        if (ClientConfig.BETTER_LAYERING.get()) {
            PoseStack mStack = RenderSystem.getModelViewStack();

            RenderSystem.getModelViewStack().pushPose(); // this feels...cheaty
            RenderSystem.getModelViewStack().setIdentity();
            if (particleMVMatrix != null) RenderSystem.getModelViewStack().mulPoseMatrix(particleMVMatrix);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            getDelayedRender().endBatch(RenderUtil.DELAYED_PARTICLE);
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            getDelayedRender().endBatch(RenderUtil.GLOWING_PARTICLE);
            getDelayedRender().endBatch(RenderUtil.GLOWING_BLOCK_PARTICLE);
            RenderSystem.getModelViewStack().popPose();
            RenderSystem.applyModelViewMatrix();

            getDelayedRender().endBatch(RenderUtil.GLOWING_SPRITE);
            getDelayedRender().endBatch(RenderUtil.GLOWING);
        }
        clientTicks += event.getPartialTick();
    }

    @OnlyIn(Dist.CLIENT)
    public static float getClientTicks() {
        return clientTicks;
    }

    public static boolean isInGui = false;

    //TODO: find a way to detect if the player is in a gui
    @SubscribeEvent
    public void onRenderGuiStart(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id() == null) isInGui = true;
    }

    @SubscribeEvent
    public void onRenderGuiStart(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id() == null) isInGui = false;
    }
    
    public static int jumpTicks = 0;
    public static boolean wasJumping = false;
    
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.side != LogicalSide.CLIENT) return;
        Player p = event.player;
        if (p instanceof LocalPlayer lp) {
            p.getCapability(IPlayerData.INSTANCE).ifPresent((d) -> {
                ItemStack wings = d.getWingsItem(p);
                if (!d.canFlap(p)) return;
                if (!(wings.getItem() instanceof IWingsItem)) return;
                if (lp.input.jumping && (!wasJumping || jumpTicks > 0)) {
                    jumpTicks ++;
                    if (jumpTicks > 20) jumpTicks = 20;
                } else if (wasJumping && jumpTicks > 0) {
                    if (jumpTicks >= 20 && !d.isDashing(p)) {
                        d.tryDash(p);
                    } else d.tryFlapWings(p);
                    jumpTicks = 0;
                }
            });
            if (p.isOnGround()) jumpTicks = 0;
            wasJumping = p.isOnGround() || lp.input.jumping;
        }
    }
}
