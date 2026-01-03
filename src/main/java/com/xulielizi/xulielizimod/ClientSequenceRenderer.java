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
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = XulieliziMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientSequenceRenderer {
    private static final CopyOnWriteArrayList<ParticleEntry> PARTICLES = new CopyOnWriteArrayList<>();

    public static void add(char mode, double x, double y, double z, double vx, double vy, double vz,
                           double ax, double ay, double az, int fps, int size, int unit, int time,
                           String imageName, boolean loop, int brightness,
                           float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType,
                           float rotationX, float rotationY, float rotationZ, int cooldown, String hitbox,
                           float scaleChange) {
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
                Math.max(1, time), rl, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType,
                rotationX, rotationY, rotationZ, cooldown, hitbox, scaleChange));
    }

    public static List<DamageHandler.ParticleDamageInfo> getParticlesForDamageCheck() {
        List<DamageHandler.ParticleDamageInfo> damageParticles = new ArrayList<>();
        for (ParticleEntry particle : PARTICLES) {
            if (particle.age >= particle.delayTicks && particle.damage > 0 && (particle.mode == 'C' || particle.mode == 'D')) {
                damageParticles.add(new DamageHandler.ParticleDamageInfo(
                        particle.x, particle.y, particle.z,
                        particle.damage, particle.knockbackX, particle.knockbackY, particle.knockbackZ,
                        particle.damageType, particle.size, particle.cooldown, particle.hitbox
                ));
            }
        }
        return damageParticles;
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

            if (particle.loop) {
                if (activeTime >= particle.lifeTicks) {
                    particle.shouldRemove = true;
                    continue;
                }
            } else {
                if (particle.currentFrame >= particle.totalFrames - 1) {
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
                case 'B', 'D', 'E' -> {
                    particle.x += particle.vx;
                    particle.y += particle.vy;
                    particle.z += particle.vz;
                    particle.vx += particle.ax;
                    particle.vy += particle.ay;
                    particle.vz += particle.az;
                }
            }

            // E模式：更新大小缩放
            if (particle.mode == 'E') {
                particle.currentScale += particle.scaleChange;
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
            if (particle.mode == 'E') {
                s *= particle.currentScale;
            }

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

            if (particle.mode == 'D' || particle.mode == 'E') {
                // D和E模式：应用自定义旋转，然后渲染双面
                poseStack.mulPose(Axis.XP.rotationDegrees(particle.rotationX));
                poseStack.mulPose(Axis.YP.rotationDegrees(particle.rotationY));
                poseStack.mulPose(Axis.ZP.rotationDegrees(particle.rotationZ));

                // 渲染正面
                renderParticleQuad(poseStack, particle, u0, v0, u1, v1, s, false);

                // 旋转180度渲染背面
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                renderParticleQuad(poseStack, particle, u0, v0, u1, v1, s, true);
            } else {
                // 其他模式：始终面向玩家
                poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                renderParticleQuad(poseStack, particle, u0, v0, u1, v1, s, false);
            }

            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void renderParticleQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, ParticleEntry particle,
                                           float u0, float v0, float u1, float v1, float scale, boolean isBackFace) {
        RenderSystem.setShaderTexture(0, particle.rl);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        var matrix = poseStack.last().pose();

        if (isBackFace) {
            // 背面顶点顺序反转，确保双面都可见
            buffer.vertex(matrix, -0.5F * scale, -0.5F * scale, 0.0F).uv(u1, v1).endVertex();
            buffer.vertex(matrix, 0.5F * scale, -0.5F * scale, 0.0F).uv(u0, v1).endVertex();
            buffer.vertex(matrix, 0.5F * scale, 0.5F * scale, 0.0F).uv(u0, v0).endVertex();
            buffer.vertex(matrix, -0.5F * scale, 0.5F * scale, 0.0F).uv(u1, v0).endVertex();
        } else {
            // 正面正常渲染
            buffer.vertex(matrix, -0.5F * scale, -0.5F * scale, 0.0F).uv(u0, v1).endVertex();
            buffer.vertex(matrix, 0.5F * scale, -0.5F * scale, 0.0F).uv(u1, v1).endVertex();
            buffer.vertex(matrix, 0.5F * scale, 0.5F * scale, 0.0F).uv(u1, v0).endVertex();
            buffer.vertex(matrix, -0.5F * scale, 0.5F * scale, 0.0F).uv(u0, v0).endVertex();
        }

        tesselator.end();
    }

    private static class ParticleEntry {
        final char mode;
        double x, y, z, vx, vy, vz, ax, ay, az;
        final int fps, size, unit, lifeTicks, delayTicks, totalFrames, brightness, cooldown;
        final ResourceLocation rl;
        final boolean loop;
        final float frameDuration;
        final float damage, knockbackX, knockbackY, knockbackZ;
        final String damageType;
        final float rotationX, rotationY, rotationZ;
        final String hitbox;
        final float scaleChange;
        int age = 0;
        float frameAcc = 0f;
        int currentFrame = 0;
        boolean shouldRemove = false;
        float currentScale = 1.0f;

        ParticleEntry(char mode, double x, double y, double z, double vx, double vy, double vz,
                      double ax, double ay, double az, int fps, int size, int unit, int lifeTicks,
                      ResourceLocation rl, boolean loop, int brightness,
                      float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType,
                      float rotationX, float rotationY, float rotationZ, int cooldown, String hitbox,
                      float scaleChange) {
            this.mode = mode;
            this.x = x; this.y = y; this.z = z;
            this.vx = vx; this.vy = vy; this.vz = vz;
            this.ax = ax; this.ay = ay; this.az = az;
            this.fps = fps; this.size = size; this.unit = unit;
            this.lifeTicks = lifeTicks; this.delayTicks = 0;
            this.rl = rl; this.loop = loop; this.brightness = brightness;
            this.damage = damage; this.knockbackX = knockbackX; this.knockbackY = knockbackY; this.knockbackZ = knockbackZ;
            this.damageType = damageType;
            this.rotationX = rotationX; this.rotationY = rotationY; this.rotationZ = rotationZ;
            this.cooldown = cooldown;
            this.hitbox = hitbox != null ? hitbox : "1*1*1";
            this.scaleChange = scaleChange;
            this.totalFrames = unit * unit;
            this.frameDuration = 20.0f / fps;
        }
    }
}