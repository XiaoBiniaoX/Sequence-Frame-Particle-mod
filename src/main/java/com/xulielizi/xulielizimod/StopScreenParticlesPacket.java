package com.xulielizi.xulielizimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopScreenParticlesPacket {

    public StopScreenParticlesPacket() {
    }

    public static void encode(StopScreenParticlesPacket p, FriendlyByteBuf buf) {
    }

    public static StopScreenParticlesPacket decode(FriendlyByteBuf buf) {
        return new StopScreenParticlesPacket();
    }

    public static void handle(StopScreenParticlesPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> 
                () -> com.xulielizi.xulielizimod.client.ClientScreenRenderer.stopScreenParticles()
        ));
        ctx.get().setPacketHandled(true);
    }
}