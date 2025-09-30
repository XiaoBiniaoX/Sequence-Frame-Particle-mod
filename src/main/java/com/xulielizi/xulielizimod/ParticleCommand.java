package com.xulielizi.xulielizimod;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;

public class ParticleCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var image = Commands.argument("image", StringArgumentType.string()).executes(ctx -> {
            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
            Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
            double vx = DoubleArgumentType.getDouble(ctx, "vx");
            double vy = DoubleArgumentType.getDouble(ctx, "vy");
            double vz = DoubleArgumentType.getDouble(ctx, "vz");
            int delay = IntegerArgumentType.getInteger(ctx, "delay_ms");
            int fps = IntegerArgumentType.getInteger(ctx, "fps");
            int time = IntegerArgumentType.getInteger(ctx, "time_ticks");
            int unit = IntegerArgumentType.getInteger(ctx, "unit");
            int size = IntegerArgumentType.getInteger(ctx, "size");
            String imageName = StringArgumentType.getString(ctx, "image");

            for (ServerPlayer sp : targets) {
                SequenceSpawnPacket pkt = new SequenceSpawnPacket(
                        pos.x, pos.y, pos.z, vx, vy, vz,
                        delay, fps, time, unit, size, imageName
                );
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), pkt);
            }
            ctx.getSource().sendSuccess(() -> Component.literal("粒子已生成喵"), true);
            return 1;
        });

        var size = Commands.argument("size", IntegerArgumentType.integer(1)).then(image);
        var unit = Commands.argument("unit", IntegerArgumentType.integer(1)).then(size);
        var time = Commands.argument("time_ticks", IntegerArgumentType.integer(1)).then(unit);
        var fps = Commands.argument("fps", IntegerArgumentType.integer(1)).then(time);
        var delay = Commands.argument("delay_ms", IntegerArgumentType.integer(0)).then(fps);
        var vz = Commands.argument("vz", DoubleArgumentType.doubleArg()).then(delay);
        var vy = Commands.argument("vy", DoubleArgumentType.doubleArg()).then(vz);
        var vx = Commands.argument("vx", DoubleArgumentType.doubleArg()).then(vy);
        var pos = Commands.argument("pos", Vec3Argument.vec3()).then(vx);
        var targets = Commands.argument("targets", EntityArgument.players()).then(pos);
        return Commands.literal("bin").then(targets);
    }
}