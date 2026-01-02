package alexthw.eidolon_repraised.client;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.capability.IMana;
import alexthw.eidolon_repraised.api.capability.ISoul;
import alexthw.eidolon_repraised.common.item.IManaRelatedItem;
import alexthw.eidolon_repraised.common.item.IWingsItem;
import alexthw.eidolon_repraised.event.ClientEvents;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.EidolonPotions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EidolonOverlays {
    protected static final ResourceLocation ICONS_TEXTURE = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/gui/icons.png");
    protected static final ResourceLocation MANA_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/gui/mana_bar.png");
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static class EidolonManaBar implements LayeredDraw.Layer {
        int xPos() {
            String origin = ClientConfig.MANA_BAR_POSITION.get();
            if (origin.equals(ClientConfig.Positions.BOTTOM_LEFT)
                    || origin.equals(ClientConfig.Positions.LEFT)
                    || origin.equals(ClientConfig.Positions.TOP_LEFT))
                return -1;
            if (origin.equals(ClientConfig.Positions.BOTTOM_RIGHT)
                    || origin.equals(ClientConfig.Positions.RIGHT)
                    || origin.equals(ClientConfig.Positions.TOP_RIGHT))
                return 1;
            return 0;
        }

        int yPos() {
            String origin = ClientConfig.MANA_BAR_POSITION.get();
            if (origin.equals(ClientConfig.Positions.TOP)
                    || origin.equals(ClientConfig.Positions.TOP_LEFT))
                return -1;
            if (origin.equals(ClientConfig.Positions.BOTTOM_LEFT)
                    || origin.equals(ClientConfig.Positions.BOTTOM_RIGHT))
                return 1;
            return 0;
        }

        boolean horiz() {
            String orient = ClientConfig.MANA_BAR_ORIENTATION.get();
            String origin = ClientConfig.MANA_BAR_POSITION.get();
            if (orient.equals(ClientConfig.Orientations.HORIZONTAL)) return true;
            else if (orient.equals(ClientConfig.Orientations.VERTICAL)) return false;
            else return !origin.equals(ClientConfig.Positions.LEFT)
                        && !origin.equals(ClientConfig.Positions.RIGHT);
        }

        @Override
        public void render(GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            var mStack = guiGraphics.pose();
            if (player == null) return;
            int xp = xPos(), yp = yPos();
            boolean isHoriz = horiz();

            var width = guiGraphics.guiWidth();
            var height = guiGraphics.guiHeight();

            int w = isHoriz ? 120 : 28, h = isHoriz ? 28 : 120;

            int ox = width / 2 - w / 2;
            int oy = height / 2 - h / 2;
            if (isHoriz) {
                if (yp == -1) oy = 4;
                else if (yp == 1) oy = height + 4 - h;
                if (xp == -1) ox = 8;
                else if (xp == 1) ox = width - 4 - w;
            } else {
                if (yp == -1) oy = -8;
                else if (yp == 1) oy = height - 20 - h;
                if (xp == -1) ox = 4;
                else if (xp == 1) ox = width + 4 - w;
            }

            final int barlength = 114;
            float magic = 0, maxMagic = 0;
            try {
                IMana soul = player.getCapability(EidolonCapabilities.MANA_CAPABILITY);
                if (soul == null) return;
                magic = soul.getMagic();
                maxMagic = soul.getMaxMagic();
            } catch (Exception e) {
                //
            }
            if (maxMagic == 0) return;
            if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IManaRelatedItem)
                    && !(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof IManaRelatedItem))
                return;

            int length = Mth.ceil(barlength * magic / maxMagic);

            int iconU = 48, iconV = 48;

            mStack.pushPose();
            mStack.translate(0, 0, 0.01);
            if (isHoriz) {
                ox -= 4;
                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 2, length == 0 ? 6 : 38, 6, 20);
                if (xp > 0) {
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox - 23, oy - 2, 0, 64, 24, 24);
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox - 18, oy + 4, iconU, iconV, 12, 12);
                }
                ox += 6;

                int firstSegment = Math.min(8, length);
                length -= firstSegment;
                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 8, 38, firstSegment, 20);
                ox += firstSegment;
                if (firstSegment < 8) {
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 8 + firstSegment, 6, 8 - firstSegment, 20);
                    ox += 8 - firstSegment;
                }

                for (int i = 0; i < 6; i++) {
                    int segment = Math.min(16, length);
                    length -= segment;
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 16, 38, segment, 20);
                    ox += segment;
                    if (segment < 16) {
                        guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 16 + segment, 6, 16 - segment, 20);
                        ox += 16 - segment;
                    }
                }

                int lastSegment = Math.min(8, length);
                length -= lastSegment;
                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 32, 38, lastSegment, 20);
                ox += lastSegment;
                if (lastSegment < 8) {
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 32 + lastSegment, 6, 8 - lastSegment, 20);
                    ox += 8 - lastSegment;
                }

                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 40, Mth.ceil(barlength * magic / maxMagic) == barlength ? 6 : 38, 7, 20);
                if (xp <= 0) {
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox + 5, oy - 2, 32, 64, 24, 24);
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox + 12, oy + 4, iconU, iconV, 12, 12);
                }
            } else {
                oy += 16;
                oy += barlength;
                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, length == 0 ? 54 : 86, 40, 20, 6);
                if (yp < 0) {
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox - 2, oy + 5, 32, 96, 24, 24);
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox + 4, oy + 12, iconU, iconV, 12, 12);
                }

                int firstSegment = Math.min(8, length);
                length -= firstSegment;
                oy -= firstSegment;
                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 86, 32, 20, firstSegment);
                if (firstSegment < 8) {
                    oy -= 8 - firstSegment;
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 54, 32 + firstSegment, 20, 8 - firstSegment);
                }

                for (int i = 0; i < 6; i++) {
                    int segment = Math.min(16, length);
                    length -= segment;
                    oy -= segment;
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 86, 16, 20, segment);
                    if (segment < 16) {
                        oy -= 16 - segment;
                        guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 54, 16 + segment, 20, 16 - segment);
                    }
                }

                int lastSegment = Math.min(8, length);
                length -= lastSegment;
                oy -= lastSegment;
                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 86, 8, 20, lastSegment);
                if (lastSegment < 8) {
                    oy -= 8 - lastSegment;
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, 54, 8 + lastSegment, 20, 8 - lastSegment);
                }

                oy -= 6;
                guiGraphics.blit(MANA_BAR_TEXTURE, ox, oy, Mth.ceil(barlength * magic / maxMagic) == barlength ? 54 : 86, 2, 20, 6);
                if (yp >= 0) {
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox - 2, oy - 23, 0, 96, 24, 24);
                    guiGraphics.blit(MANA_BAR_TEXTURE, ox + 4, oy - 18, iconU, iconV, 12, 12);
                }
            }

            mStack.popPose();
        }
    }

    public static class EidolonHearts implements LayeredDraw.Layer {
        float lastEtherealHealth = 0;
        long healthBlinkTime = 0;
        long lastHealthTime = 0;

        @Override
        public void render(GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
            PoseStack mStack = guiGraphics.pose();
            LocalPlayer player = minecraft.player;

            if (!EidolonOverlays.minecraft.gameMode.canHurtPlayer() || player == null) return;
            mStack.pushPose();
            mStack.translate(0, 0, 0.01);

            int health = Mth.ceil(player.getHealth());
            float absorb = Mth.ceil(player.getAbsorptionAmount());
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = (float) attrMaxHealth.getValue();

            float etherealHealth = 0, etherealMax = 0;
            ISoul cap = player.getCapability(EidolonCapabilities.SOUL_HEART_CAPABILITY);
            if (cap != null) {
                etherealHealth = cap.getEtherealHealth();
                etherealMax = cap.getMaxEtherealHealth();
            }

            int ticks = (int) deltaTracker.getGameTimeDeltaTicks();
            boolean highlight = healthBlinkTime > (long) ticks && (healthBlinkTime - (long) ticks) / 3L % 2L == 1L;

            if (etherealHealth < this.lastEtherealHealth && player.invulnerableTime > 0) {
                this.lastHealthTime = Util.getMillis();
                this.healthBlinkTime = ticks + 20;
            } else if (etherealHealth > this.lastEtherealHealth) {
                this.lastHealthTime = Util.getMillis();
                this.healthBlinkTime = ticks + 10;
            }
            if (Util.getMillis() - this.lastHealthTime > 1000L) {
                lastEtherealHealth = etherealHealth;
                lastHealthTime = Util.getMillis();
            }

            lastEtherealHealth = etherealHealth;

            float f = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), (float) health);
            int regen = -1;
            if (player.hasEffect(MobEffects.REGENERATION)) regen = ticks % Mth.ceil(f + 5.0F);

            Random rand = new Random();
            rand.setSeed(ticks * 312871L);

            int absorptionHearts = Mth.ceil(absorb / 2.0f) - 1;
            int hearts = Mth.ceil(healthMax / 2.0f) - 1;
            int ethHearts = Mth.ceil(etherealMax / 2.0f);
            int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int totalHealthRows = Mth.ceil((healthMax + absorb + etherealMax) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);
            int extraHealthRows = totalHealthRows - healthRows;
            int extraRowHeight = Mth.clamp(10 - (healthRows - 2), 3, 10);

            int left = guiGraphics.guiWidth() / 2 - 91;
            int top = guiGraphics.guiHeight() - minecraft.gui.leftHeight + healthRows * rowHeight;
            if (rowHeight != 10) top += 10 - rowHeight;

            minecraft.gui.leftHeight += extraHealthRows * extraRowHeight;

            for (int i = absorptionHearts + hearts + ethHearts; i > absorptionHearts + hearts; --i) {
                int row = (i + 1) / 10;
                int heart = (i + 1) % 10;
                int x = left + heart * 8;
                int y = top - extraRowHeight * Math.max(0, row - healthRows + 1) - rowHeight * Math.min(row, healthRows - 1);
                guiGraphics.blit(ICONS_TEXTURE, x, y, highlight ? 9 : 0, 18, 9, 9, 32,32);
            }
            for (int i = absorptionHearts + hearts + ethHearts; i > absorptionHearts + hearts; --i) {
                int row = (i + 1) / 10;
                int heart = (i + 1) % 10;
                int x = left + heart * 8;
                int y = top - extraRowHeight * Math.max(0, row - healthRows + 1) - rowHeight * Math.min(row, healthRows - 1);
                int i2 = i - (Mth.ceil((healthMax + absorb) / 2.0f) - 1);
                if (i2 * 2 + 1 < etherealHealth)
                    guiGraphics.blit(ICONS_TEXTURE, x, y, 0, 9, 9, 9, 32,32);
                else if (i2 * 2 + 1 == etherealHealth)
                    guiGraphics.blit(ICONS_TEXTURE, x, y, 9, 9, 9, 9, 32,32);
            }
            for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                int row = i / 10;
                int heart = i % 10;
                int x = left + heart * 8;
                int y = top - row * rowHeight;

                if (health <= 4) y += rand.nextInt(2);
                if (i == regen) y -= 2;

                RenderSystem.enableBlend();
                if (player.hasEffect(EidolonPotions.CHILLED_EFFECT) && i <= Mth.ceil(healthMax / 2.0f) - 1) {
                    if (i * 2 + 1 < health)
                        guiGraphics.blit(ICONS_TEXTURE, x, y, 0, 0, 9, 9, 32,32);
                    else if (i * 2 + 1 == health)
                        guiGraphics.blit(ICONS_TEXTURE, x, y, 9, 0, 9, 9, 32,32);
                }
                RenderSystem.disableBlend();
            }
            mStack.popPose();
        }
    }

    public static class EidolonRavenCharge implements LayeredDraw.Layer {

        private static final ResourceLocation JUMP_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_background");
        private static final ResourceLocation JUMP_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_progress");

        @Override
        public void render(GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
            PoseStack mStack = guiGraphics.pose();
            LocalPlayer player = minecraft.player;
            var font = minecraft.font;

            var screenWidth = guiGraphics.guiWidth();
            var screenHeight = guiGraphics.guiHeight();

            if (player == null || player.isCreative() || player.isSpectator() || player.onGround()) return;
            var wingsCap = player.getCapability(EidolonCapabilities.WINGS_CAPABILITY);
            if (wingsCap != null) {
                ItemStack wings = wingsCap.getWingsItem(player);
                if (!(wings.getItem() instanceof IWingsItem wing)) return;

                int remainingFlaps = wingsCap.getWingCharges(player);

                //TODO render an icon
                //renders the number of remaining flaps
                String s = "" + remainingFlaps;
                int i1 = (screenWidth - font.width(s)) / 2;
                int j1 = screenHeight - 46;
                guiGraphics.drawString(font, s, i1 + 1, j1, 0, false);
                guiGraphics.drawString(font, s, i1 - 1, j1, 0, false);
                guiGraphics.drawString(font, s, i1, j1 + 1, 0, false);
                guiGraphics.drawString(font, s, i1, j1 - 1, 0, false);
                guiGraphics.drawString(font, s, i1, j1, 6505166, false);

                if (ClientEvents.jumpTicks >= 1) {
                    RenderSystem.enableBlend();
                    var x = screenWidth / 2 - 91;
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                    minecraft.getProfiler().push("ravenJumpBar");
                    float f = (ClientEvents.jumpTicks - 5 + Minecraft.getInstance().getFrameTimeNs() / 50_000_000.0f) / 15.0f;
                    int j = Mth.clamp((int) (f * 183.0F), 0, 182);
                    int k = guiGraphics.guiHeight() - 32 + 3;
                    guiGraphics.blitSprite(JUMP_BAR_BACKGROUND_SPRITE, x, k, 182, 5);
                    if (j > 0) {
                        guiGraphics.blitSprite(JUMP_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, x, k, j, 5);
                    }


                    minecraft.getProfiler().pop();

                    RenderSystem.disableBlend();
                    minecraft.getProfiler().pop();
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }
}
