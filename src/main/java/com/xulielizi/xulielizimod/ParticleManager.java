package com.xulielizi.xulielizimod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ParticleManager {
    public static void spawnA(ServerPlayer viewer, double x, double y, double z,
                             double vx, double vy, double vz, int fps, int size,
                             int unit, int time, String imagePath, boolean loop, int brightness,
                             float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType, int cooldown, String hitbox) {
        SequenceSpawnPacket pkt = new SequenceSpawnPacket(
                'A', x, y, z, vx, vy, vz, 0, 0, 0, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, 0, 0, 0, cooldown, hitbox
        );
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }

    public static void spawnB(ServerPlayer viewer, double x, double y, double z,
                             double vtx, double vty, double vtz, double ax, double ay, double az,
                             int fps, int size, int unit, int time, String imagePath, boolean loop, int brightness,
                             float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType, int cooldown, String hitbox) {
        SequenceSpawnPacket pkt = new SequenceSpawnPacket(
                'B', x, y, z, vtx, vty, vtz, ax, ay, az, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, 0, 0, 0, cooldown, hitbox
        );
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }

    public static void spawnC(ServerPlayer viewer, double x, double y, double z,
                             int fps, int size, int unit, int time, String imagePath, boolean loop, int brightness,
                             float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType, int cooldown, String hitbox) {
        SequenceSpawnPacket pkt = new SequenceSpawnPacket(
                'C', x, y, z, 0, 0, 0, 0, 0, 0, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, 0, 0, 0, cooldown, hitbox
        );
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }

    public static void spawnD(ServerPlayer viewer, double x, double y, double z,
                             double vtx, double vty, double vtz, double ax, double ay, double az,
                             int fps, int size, int unit, int time, String imagePath, boolean loop, int brightness,
                             float damage, float knockbackX, float knockbackY, float knockbackZ, String damageType,
                             float rotationX, float rotationY, float rotationZ, int cooldown, String hitbox) {
        SequenceSpawnPacket pkt = new SequenceSpawnPacket(
                'D', x, y, z, vtx, vty, vtz, ax, ay, az, fps, size, unit, time, imagePath, loop, brightness,
                damage, knockbackX, knockbackY, knockbackZ, damageType, rotationX, rotationY, rotationZ, cooldown, hitbox
        );
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }

    public static void spawnScreen(ServerPlayer viewer, int fps, int unit, int time,
                                  String imagePath, boolean loop, int brightness) {
        ScreenParticlePacket pkt = new ScreenParticlePacket(fps, unit, time, imagePath, loop, brightness);
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }

    public static void stopScreenParticles(ServerPlayer viewer) {
        StopScreenParticlesPacket pkt = new StopScreenParticlesPacket();
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }
}