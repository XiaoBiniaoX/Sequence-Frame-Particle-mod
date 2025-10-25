package com.xulielizi.xulielizimod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(XulieliziMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SequenceSpawnPacket.class,
                SequenceSpawnPacket::encode, SequenceSpawnPacket::decode, SequenceSpawnPacket::handle);
        CHANNEL.registerMessage(id++, StopParticlesPacket.class,
                StopParticlesPacket::encode, StopParticlesPacket::decode, StopParticlesPacket::handle);
        CHANNEL.registerMessage(id++, ScreenParticlePacket.class,
                ScreenParticlePacket::encode, ScreenParticlePacket::decode, ScreenParticlePacket::handle);
        CHANNEL.registerMessage(id++, StopScreenParticlesPacket.class,
                StopScreenParticlesPacket::encode, StopScreenParticlesPacket::decode, StopScreenParticlesPacket::handle);
    }
}