package com.xulielizi.xulielizimod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.xulielizi.xulielizimod.XulieliziMod;

import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(modid = XulieliziMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientScreenRenderer {
    private static final CopyOnWriteArrayList<ScreenParticleEntry> SCREEN_PARTICLES = new CopyOnWriteArrayList<>();

    public static void addScreenParticle(int fps, int unit, int time, String imageName, boolean loop, int brightness) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation rl = new ResourceLocation(XulieliziMod.MODID, "textures/particle/" + imageName);
        
        try {
            if (!mc.getResourceManager().getResource(rl).isPresent()) {
                if (mc.player != null) {
                    mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("彬找不到你要的文件qwq " + imageName), true);
                }
                return;
            }
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("彬找不到你要的文件qwq " + imageName), true);
            }
            return;
        }
        
        SCREEN_PARTICLES.add(new ScreenParticleEntry(fps, unit, time, rl, loop, brightness));
        System.out.println("添加屏幕粒子: " + imageName + ", 时间: " + time + " tick, 循环: " + loop + ", 总数: " + SCREEN_PARTICLES.size());
    }

    public static void stopScreenParticles() {
        System.out.println("停止所有屏幕粒子，原数量: " + SCREEN_PARTICLES.size());
        SCREEN_PARTICLES.clear();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (SCREEN_PARTICLES.isEmpty()) return;
        
        for (ScreenParticleEntry particle : SCREEN_PARTICLES) {
            particle.age++;
            
            if (particle.age < particle.delayTicks) continue;
            
            int activeTime = particle.age - particle.delayTicks;
            
            // 修复循环逻辑
            if (particle.loop) {
                // loop = true: 播放到指定时间后停止
                if (activeTime >= particle.lifeTicks) {
                    particle.shouldRemove = true;
                    continue;
                }
            } else {
                // loop = false: 播放到最后一帧后停止
                if (particle.currentFrame >= particle.totalFrames - 1) {
                    particle.shouldRemove = true;
                    continue;
                }
            }
            
            // 更新帧动画
            particle.frameAcc += (particle.fps / 20.0f);
            if (particle.frameAcc >= particle.totalFrames) {
                if (particle.loop) {
                    particle.frameAcc = 0; // 循环模式下重置到第一帧
                } else {
                    particle.frameAcc = particle.totalFrames - 1; // 非循环模式下停留在最后一帧
                }
            }
            
            particle.currentFrame = (int) Math.floor(particle.frameAcc);
        }
        
        int removed = 0;
        for (ScreenParticleEntry particle : SCREEN_PARTICLES) {
            if (particle.shouldRemove) {
                removed++;
            }
        }
        if (removed > 0) {
            System.out.println("移除 " + removed + " 个完成的屏幕粒子");
            SCREEN_PARTICLES.removeIf(particle -> particle.shouldRemove);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent event) {
        if (SCREEN_PARTICLES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // 在 1.20.1 Forge 47.4.6 中，直接创建新的 PoseStack
        com.mojang.blaze3d.vertex.PoseStack poseStack = new com.mojang.blaze3d.vertex.PoseStack();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        poseStack.pushPose();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        for (ScreenParticleEntry particle : SCREEN_PARTICLES) {
            if (particle.age < particle.delayTicks) continue;
            if (particle.currentFrame >= particle.totalFrames) continue;
            
            float brightness = 1.0f + (particle.brightness / 15.0f);
            RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);

            // 计算适合屏幕的大小（保持正方形）
            int displaySize = Math.min(screenWidth, screenHeight) * 3 / 4;
            int x = (screenWidth - displaySize) / 2;
            int y = (screenHeight - displaySize) / 2;
            
            int row = particle.currentFrame / particle.unit;
            int col = particle.currentFrame % particle.unit;
            
            float u0 = (float) col / particle.unit;
            float v0 = (float) row / particle.unit;
            float u1 = u0 + 1.0f / particle.unit;
            float v1 = v0 + 1.0f / particle.unit;

            RenderSystem.setShaderTexture(0, particle.rl);
            
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            
            // 直接使用坐标渲染，不依赖矩阵变换
            buffer.vertex(x, y + displaySize, 0).uv(u0, v1).endVertex();
            buffer.vertex(x + displaySize, y + displaySize, 0).uv(u1, v1).endVertex();
            buffer.vertex(x + displaySize, y, 0).uv(u1, v0).endVertex();
            buffer.vertex(x, y, 0).uv(u0, v0).endVertex();
            
            tesselator.end();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        poseStack.popPose();
    }

    private static class ScreenParticleEntry {
        final int fps, unit, lifeTicks, delayTicks, totalFrames, brightness;
        final ResourceLocation rl;
        final boolean loop;
        final float frameDuration;
        int age = 0;
        float frameAcc = 0f;
        int currentFrame = 0;
        boolean shouldRemove = false;
        
        ScreenParticleEntry(int fps, int unit, int lifeTicks, ResourceLocation rl, boolean loop, int brightness) {
            this.fps = fps;
            this.unit = unit;
            this.lifeTicks = lifeTicks;
            this.delayTicks = 0;
            this.rl = rl;
            this.loop = loop;
            this.brightness = brightness;
            this.totalFrames = unit * unit;
            this.frameDuration = 20.0f / fps;
        }
    }
}