package com.xulielizi.xulielizimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SequenceSpawnPacket {
    public final double x, y, z, vx, vy, vz;
    public final int delayMs, fps, timeTicks, unit, size;
    public final String image;

    public SequenceSpawnPacket(double x, double y, double z, double vx, double vy, double vz,
                               int delayMs, int fps, int timeTicks, int unit, int size, String image) {
        this.x=x; this.y=y; this.z=z; this.vx=vx; this.vy=vy; this.vz=vz;
        this.delayMs=delayMs; this.fps=fps; this.timeTicks=timeTicks; this.unit=unit; this.size=size; this.image=image;
    }

    public static void encode(SequenceSpawnPacket p, FriendlyByteBuf buf) {
        buf.writeDouble(p.x).writeDouble(p.y).writeDouble(p.z);
        buf.writeDouble(p.vx).writeDouble(p.vy).writeDouble(p.vz);
        buf.writeInt(p.delayMs).writeInt(p.fps).writeInt(p.timeTicks).writeInt(p.unit).writeInt(p.size);
        buf.writeUtf(p.image);
    }

    public static SequenceSpawnPacket decode(FriendlyByteBuf buf) {
        return new SequenceSpawnPacket(
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readUtf()
        );
    }

    public static void handle(SequenceSpawnPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () ->
                () -> ClientSequenceRenderer.add(
                        pkt.x, pkt.y, pkt.z, pkt.vx, pkt.vy, pkt.vz,
                        pkt.delayMs, pkt.fps, pkt.timeTicks, pkt.unit, pkt.size, pkt.image
                )
        ));
        ctx.get().setPacketHandled(true);
    }
}
