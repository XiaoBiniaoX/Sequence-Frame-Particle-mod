package com.xulielizi.xulielizimod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.Consumer;

public class ParticleAPI {
    
    public static void spawnParticleA(ServerPlayer viewer, double x, double y, double z,
                                     double vx, double vy, double vz, int fps, int size,
                                     int unit, int time, String imagePath, boolean loop, int brightness,
                                     float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType, int cooldown, String hitbox) {
        ParticleManager.spawnA(viewer, x, y, z, vx, vy, vz, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, cooldown, hitbox);
    }

    public static void spawnParticleB(ServerPlayer viewer, double x, double y, double z,
                                     double vtx, double vty, double vtz, double ax, double ay, double az,
                                     int fps, int size, int unit, int time, String imagePath, boolean loop, int brightness,
                                     float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType, int cooldown, String hitbox) {
        ParticleManager.spawnB(viewer, x, y, z, vtx, vty, vtz, ax, ay, az, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, cooldown, hitbox);
    }

    public static void spawnParticleC(ServerPlayer viewer, double x, double y, double z,
                                     int fps, int size, int unit, int time, String imagePath, boolean loop, int brightness,
                                     float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType, int cooldown, String hitbox) {
        ParticleManager.spawnC(viewer, x, y, z, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, cooldown, hitbox);
    }

    public static void spawnParticleD(ServerPlayer viewer, double x, double y, double z,
                                     double vtx, double vty, double vtz, double ax, double ay, double az,
                                     int fps, int size, int unit, int time, String imagePath, boolean loop, int brightness,
                                     float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType,
                                     float rotationX, float rotationY, float rotationZ, int cooldown, String hitbox) {
        ParticleManager.spawnD(viewer, x, y, z, vtx, vty, vtz, ax, ay, az, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, rotationX, rotationY, rotationZ, cooldown, hitbox);
    }

    public static void spawnScreenParticle(ServerPlayer viewer, int fps, int unit, int time, 
                                         String imagePath, boolean loop, int brightness) {
        ParticleManager.spawnScreen(viewer, fps, unit, time, imagePath, loop, brightness);
    }

    public static void stopParticles(ServerPlayer viewer, double centerX, double centerY, double centerZ, double radius) {
        StopParticlesPacket pkt = new StopParticlesPacket(centerX, centerY, centerZ, radius);
        NetworkHandler.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }

    public static void stopScreenParticles(ServerPlayer viewer) {
        ParticleManager.stopScreenParticles(viewer);
    }

    public static List<LivingEntity> getEntitiesNearParticle(double x, double y, double z, double radius, ServerPlayer viewer) {
        Vec3 center = new Vec3(x, y, z);
        AABB area = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        return viewer.level().getEntitiesOfClass(LivingEntity.class, area);
    }

    public static void forEachEntityNearParticle(double x, double y, double z, double radius, 
                                                ServerPlayer viewer, Consumer<LivingEntity> action) {
        List<LivingEntity> entities = getEntitiesNearParticle(x, y, z, radius, viewer);
        for (LivingEntity entity : entities) {
            action.accept(entity);
        }
    }

    public static boolean isEntityNearParticle(double x, double y, double z, double radius, 
                                              ServerPlayer viewer, Entity targetEntity) {
        Vec3 center = new Vec3(x, y, z);
        AABB area = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        return targetEntity.getBoundingBox().intersects(area);
    }
}