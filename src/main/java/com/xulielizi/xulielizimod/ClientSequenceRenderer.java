package com.xulielizi.xulielizimod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
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
    private static final CopyOnWriteArrayList<Entry> ENTRIES = new CopyOnWriteArrayList<>();

    public static void add(double x, double y, double z, double vx, double vy, double vz,
                           int delayMs, int fps, int timeTicks, int unit, int size, String imageName) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation rl = new ResourceLocation(XulieliziMod.MODID, "textures/particle/" + imageName);
        
        // 检查资源是否存在
        try {
            boolean exists = mc.getResourceManager().getResource(rl).isPresent();
            if (!exists) {
                if (mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("彬未找到此图像: " + imageName), true);
                }
                return;
            }
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal("彬未找到此图像: " + imageName), true);
            }
            return;
        }
        
        int delayTicks = Math.max(0, (int)Math.round(delayMs / 50.0));
        ENTRIES.add(new Entry(x, y, z, vx, vy, vz, delayTicks, Math.max(1, fps),
                Math.max(1, timeTicks), Math.max(1, unit), Math.max(1, size), rl));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (ENTRIES.isEmpty()) return;
        
        for (Entry it : ENTRIES) {
            it.age++;
            if (it.age >= it.delayTicks) {
                // 帧数计算
                it.frameAcc += (it.fps / 20.0f); // 20 ticks per second
                if (it.frameAcc >= it.totalFrames) {
                    it.frameAcc = 0; // 重置到第一帧搞循环
                }
            }
            it.x += it.vx;
            it.y += it.vy;
            it.z += it.vz;
        }
        ENTRIES.removeIf(it -> it.age >= it.lifeTicks);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (ENTRIES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        var poseStack = event.getPoseStack();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false); // 允许透明
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (Entry it : ENTRIES) {
            if (it.age < it.delayTicks) continue; // 跳过延迟期间的粒子
            
            float s = it.size * 0.0625f; // 将像素大小转换为方块大小
            int frame = (int) Math.floor(it.frameAcc) % it.totalFrames;
            int row = frame / it.unit;
            int col = frame % it.unit;
            
            float u0 = (float) col / it.unit;
            float v0 = (float) row / it.unit;
            float u1 = u0 + 1.0f / it.unit;
            float v1 = v0 + 1.0f / it.unit;

            double rx = it.x - cam.x;
            double ry = it.y - cam.y;
            double rz = it.z - cam.z;

            poseStack.pushPose();
            poseStack.translate(rx, ry, rz);
            
            // 面向相机
            poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.scale(s, s, s);

            RenderSystem.setShaderTexture(0, it.rl);
            
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
    }

    // 粒子条目
    private static class Entry {
        double x, y, z, vx, vy, vz;
        final int delayTicks, fps, lifeTicks, unit, size, totalFrames;
        final ResourceLocation rl;
        int age = 0;
        float frameAcc = 0f;
        
        Entry(double x, double y, double z, double vx, double vy, double vz,
              int delayTicks, int fps, int lifeTicks, int unit, int size, ResourceLocation rl) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.delayTicks = delayTicks;
            this.fps = fps;
            this.lifeTicks = lifeTicks;
            this.unit = unit;
            this.size = size;
            this.rl = rl;
            this.totalFrames = unit * unit;
        }
    }
}