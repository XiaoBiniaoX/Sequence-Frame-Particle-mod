package com.xulielizi.xulielizimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SequenceSpawnPacket {
    public final char mode;
    public final double x, y, z, vx, vy, vz, ax, ay, az;
    public final int fps, size, unit, time, brightness;
    public final String image;
    public final boolean loop;

    public SequenceSpawnPacket(char mode, double x, double y, double z, 
                              double vx, double vy, double vz, double ax, double ay, double az,
                              int fps, int size, int unit, int time, String image, boolean loop, int brightness) {
        this.mode = mode;
        this.x=x; this.y=y; this.z=z; 
        this.vx=vx; this.vy=vy; this.vz=vz;
        this.ax=ax; this.ay=ay; this.az=az;
        this.fps=fps; this.size=size; this.unit=unit; this.time=time; 
        this.image=image; this.loop=loop; this.brightness=brightness;
    }

    public static void encode(SequenceSpawnPacket p, FriendlyByteBuf buf) {
        buf.writeChar(p.mode);
        buf.writeDouble(p.x); buf.writeDouble(p.y); buf.writeDouble(p.z);
        buf.writeDouble(p.vx); buf.writeDouble(p.vy); buf.writeDouble(p.vz);
        buf.writeDouble(p.ax); buf.writeDouble(p.ay); buf.writeDouble(p.az);
        buf.writeInt(p.fps); buf.writeInt(p.size); buf.writeInt(p.unit); buf.writeInt(p.time);
        buf.writeUtf(p.image);
        buf.writeBoolean(p.loop);
        buf.writeInt(p.brightness);
    }

    public static SequenceSpawnPacket decode(FriendlyByteBuf buf) {
        return new SequenceSpawnPacket(
                buf.readChar(),
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readUtf(), buf.readBoolean(), buf.readInt()
        );
    }

    public static void handle(SequenceSpawnPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> 
                () -> ClientSequenceRenderer.add(
                        pkt.mode, pkt.x, pkt.y, pkt.z, pkt.vx, pkt.vy, pkt.vz,
                        pkt.ax, pkt.ay, pkt.az, pkt.fps, pkt.size, pkt.unit,
                        pkt.time, pkt.image, pkt.loop, pkt.brightness
                )
        ));
        ctx.get().setPacketHandled(true);
    }
}