package com.xulielizi.xulielizimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopParticlesPacket {
    public final double x, y, z, radius;

    public StopParticlesPacket(double x, double y, double z, double radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public static void encode(StopParticlesPacket p, FriendlyByteBuf buf) {
        buf.writeDouble(p.x);
        buf.writeDouble(p.y);
        buf.writeDouble(p.z);
        buf.writeDouble(p.radius);
    }

    public static StopParticlesPacket decode(FriendlyByteBuf buf) {
        return new StopParticlesPacket(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );
    }

    public static void handle(StopParticlesPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> 
                () -> ClientSequenceRenderer.stopParticles(pkt.x, pkt.y, pkt.z, pkt.radius)
        ));
        ctx.get().setPacketHandled(true);
    }
}