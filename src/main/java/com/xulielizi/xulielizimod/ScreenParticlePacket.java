package com.xulielizi.xulielizimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScreenParticlePacket {
    public final int fps, unit, time, brightness;
    public final String image;
    public final boolean loop;

    public ScreenParticlePacket(int fps, int unit, int time, String image, boolean loop, int brightness) {
        this.fps = fps;
        this.unit = unit;
        this.time = time;
        this.image = image;
        this.loop = loop;
        this.brightness = brightness;
    }

    public static void encode(ScreenParticlePacket p, FriendlyByteBuf buf) {
        buf.writeInt(p.fps);
        buf.writeInt(p.unit);
        buf.writeInt(p.time);
        buf.writeUtf(p.image);
        buf.writeBoolean(p.loop);
        buf.writeInt(p.brightness);
    }

    public static ScreenParticlePacket decode(FriendlyByteBuf buf) {
        return new ScreenParticlePacket(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readUtf(),
                buf.readBoolean(),
                buf.readInt()
        );
    }

    public static void handle(ScreenParticlePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> 
                () -> com.xulielizi.xulielizimod.client.ClientScreenRenderer.addScreenParticle(
                        pkt.fps, pkt.unit, pkt.time, pkt.image, pkt.loop, pkt.brightness
                )
        ));
        ctx.get().setPacketHandled(true);
    }
}