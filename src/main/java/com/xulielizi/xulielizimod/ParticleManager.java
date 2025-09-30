package com.xulielizi.xulielizimod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ParticleManager {
    public static void spawn(ServerPlayer viewer,
                             double x, double y, double z,
                             double vx, double vy, double vz,
                             int delay, int fps, int time,
                             int unit, int size, String imagePath) {
        SequenceSpawnPacket pkt = new SequenceSpawnPacket(
                x, y, z, vx, vy, vz, delay, fps, time, unit, size, imagePath
        );
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), pkt);
    }
}
