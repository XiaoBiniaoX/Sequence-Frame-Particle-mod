package com.xulielizi.xulielizimod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(modid = XulieliziMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientSequenceRenderer {
    private static final CopyOnWriteArrayList<ParticleEntry> PARTICLES = new CopyOnWriteArrayList<>();

    public static void add(char mode, double x, double y, double z, double vx, double vy, double vz,
                          double ax, double ay, double az, int fps, int size, int unit, int time,
                          String imageName, boolean loop, int brightness) {
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
        
        PARTICLES.add(new ParticleEntry(mode, x, y, z, vx, vy, vz, ax, ay, az, 
                Math.max(1, fps), Math.max(1, size), Math.max(1, unit), 
                Math.max(1, time), rl, loop, brightness));
    }

    public static void stopParticles(double centerX, double centerY, double centerZ, double radius) {
        if (radius <= 0) {
            PARTICLES.clear();
        } else {
            PARTICLES.removeIf(particle -> {
                double dx = particle.x - centerX;
                double dy = particle.y - centerY;
                double dz = particle.z - centerZ;
                return dx * dx + dy * dy + dz * dz <= radius * radius;
            });
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (PARTICLES.isEmpty()) return;
        
        for (ParticleEntry particle : PARTICLES) {
            particle.age++;
            
            if (particle.age < particle.delayTicks) continue;
            
            int activeTime = particle.age - particle.delayTicks;
            
            if (!particle.loop && particle.currentFrame >= particle.totalFrames - 1 && activeTime >= particle.frameDuration * particle.totalFrames) {
                particle.shouldRemove = true;
                continue;
            }
            
            if (activeTime >= particle.lifeTicks) {
                if (particle.loop) {
                    particle.age = particle.delayTicks;
                    particle.currentFrame = 0;
                    particle.frameAcc = 0;
                    activeTime = 0;
                } else {
                    particle.shouldRemove = true;
                    continue;
                }
            }
            
            particle.frameAcc += (particle.fps / 20.0f);
            if (particle.frameAcc >= particle.totalFrames) {
                if (particle.loop) {
                    particle.frameAcc = 0;
                } else {
                    particle.frameAcc = particle.totalFrames - 1;
                }
            }
            
            particle.currentFrame = (int) Math.floor(particle.frameAcc);
            
            switch (particle.mode) {
                case 'A' -> {
                    particle.x += particle.vx;
                    particle.y += particle.vy;
                    particle.z += particle.vz;
                }
                case 'B' -> {
                    double tickProgress = activeTime / 20.0;
                    particle.x += particle.vx + 0.5 * particle.ax * tickProgress * tickProgress;
                    particle.y += particle.vy + 0.5 * particle.ay * tickProgress * tickProgress;
                    particle.z += particle.vz + 0.5 * particle.az * tickProgress * tickProgress;
                    particle.vx += particle.ax * tickProgress;
                    particle.vy += particle.ay * tickProgress;
                    particle.vz += particle.az * tickProgress;
                }
            }
        }
        
        PARTICLES.removeIf(particle -> particle.shouldRemove);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (PARTICLES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        var poseStack = event.getPoseStack();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        for (ParticleEntry particle : PARTICLES) {
            if (particle.age < particle.delayTicks) continue;
            if (particle.currentFrame >= particle.totalFrames) continue;
            
            float brightness = 1.0f + (particle.brightness / 15.0f);
            RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);

            float s = particle.size * 0.0625f;
            int row = particle.currentFrame / particle.unit;
            int col = particle.currentFrame % particle.unit;
            
            float u0 = (float) col / particle.unit;
            float v0 = (float) row / particle.unit;
            float u1 = u0 + 1.0f / particle.unit;
            float v1 = v0 + 1.0f / particle.unit;

            double rx = particle.x - cam.x;
            double ry = particle.y - cam.y;
            double rz = particle.z - cam.z;

            poseStack.pushPose();
            poseStack.translate(rx, ry, rz);
            poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.scale(s, s, s);

            RenderSystem.setShaderTexture(0, particle.rl);
            
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            
            var matrix = poseStack.last().pose();
            buffer.vertex(matrix, -0.5F, -0.5F, 0.0F).uv(u0, v1).endVertex();
            buffer.vertex(matrix, 0.5F, -0.5F, 0.0F).uv(u1, v1).endVertex();
            buffer.vertex(matrix, 0.5F, 0.5F, 0.0F).uv(u1, v0).endVertex();
            buffer.vertex(matrix, -0.5F, 0.5F, 0.0F).uv(u0, v0).endVertex();
            
            tesselator.end();
            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static class ParticleEntry {
        final char mode;
        double x, y, z, vx, vy, vz, ax, ay, az;
        final int fps, size, unit, lifeTicks, delayTicks, totalFrames, brightness;
        final ResourceLocation rl;
        final boolean loop;
        final float frameDuration;
        int age = 0;
        float frameAcc = 0f;
        int currentFrame = 0;
        boolean shouldRemove = false;
        
        ParticleEntry(char mode, double x, double y, double z, double vx, double vy, double vz,
                     double ax, double ay, double az, int fps, int size, int unit, int lifeTicks,
                     ResourceLocation rl, boolean loop, int brightness) {
            this.mode = mode;
            this.x = x; this.y = y; this.z = z;
            this.vx = vx; this.vy = vy; this.vz = vz;
            this.ax = ax; this.ay = ay; this.az = az;
            this.fps = fps; this.size = size; this.unit = unit; 
            this.lifeTicks = lifeTicks; this.delayTicks = 0;
            this.rl = rl; this.loop = loop; this.brightness = brightness;
            this.totalFrames = unit * unit;
            this.frameDuration = 20.0f / fps;
        }
    }
}