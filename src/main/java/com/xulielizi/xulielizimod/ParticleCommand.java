package com.xulielizi.xulielizimod;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
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
        return Commands.literal("bin")
                .then(Commands.literal("A").then(buildModeA()))
                .then(Commands.literal("B").then(buildModeB()))
                .then(Commands.literal("C").then(buildModeC()))
                .then(Commands.literal("stop").then(buildStopCommand()));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> buildModeA() {
        var image = Commands.argument("image", StringArgumentType.string())
                .executes(ctx -> executeModeA(ctx.getSource(), 
                        EntityArgument.getPlayers(ctx, "targets"),
                        Vec3Argument.getVec3(ctx, "pos"),
                        DoubleArgumentType.getDouble(ctx, "vx"),
                        DoubleArgumentType.getDouble(ctx, "vy"),
                        DoubleArgumentType.getDouble(ctx, "vz"),
                        IntegerArgumentType.getInteger(ctx, "fps"),
                        IntegerArgumentType.getInteger(ctx, "size"),
                        IntegerArgumentType.getInteger(ctx, "unit"),
                        IntegerArgumentType.getInteger(ctx, "time"),
                        StringArgumentType.getString(ctx, "image"),
                        true, 0
                ))
                .then(Commands.argument("loop", BoolArgumentType.bool())
                        .executes(ctx -> executeModeA(ctx.getSource(), 
                                EntityArgument.getPlayers(ctx, "targets"),
                                Vec3Argument.getVec3(ctx, "pos"),
                                DoubleArgumentType.getDouble(ctx, "vx"),
                                DoubleArgumentType.getDouble(ctx, "vy"),
                                DoubleArgumentType.getDouble(ctx, "vz"),
                                IntegerArgumentType.getInteger(ctx, "fps"),
                                IntegerArgumentType.getInteger(ctx, "size"),
                                IntegerArgumentType.getInteger(ctx, "unit"),
                                IntegerArgumentType.getInteger(ctx, "time"),
                                StringArgumentType.getString(ctx, "image"),
                                BoolArgumentType.getBool(ctx, "loop"), 0
                        ))
                        .then(Commands.argument("brightness", IntegerArgumentType.integer(0, 15))
                                .executes(ctx -> executeModeA(ctx.getSource(), 
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        Vec3Argument.getVec3(ctx, "pos"),
                                        DoubleArgumentType.getDouble(ctx, "vx"),
                                        DoubleArgumentType.getDouble(ctx, "vy"),
                                        DoubleArgumentType.getDouble(ctx, "vz"),
                                        IntegerArgumentType.getInteger(ctx, "fps"),
                                        IntegerArgumentType.getInteger(ctx, "size"),
                                        IntegerArgumentType.getInteger(ctx, "unit"),
                                        IntegerArgumentType.getInteger(ctx, "time"),
                                        StringArgumentType.getString(ctx, "image"),
                                        BoolArgumentType.getBool(ctx, "loop"),
                                        IntegerArgumentType.getInteger(ctx, "brightness")
                                ))
                        )
                );

        var time = Commands.argument("time", IntegerArgumentType.integer(1)).then(image);
        var unit = Commands.argument("unit", IntegerArgumentType.integer(1)).then(time);
        var size = Commands.argument("size", IntegerArgumentType.integer(1)).then(unit);
        var fps = Commands.argument("fps", IntegerArgumentType.integer(1)).then(size);
        var vz = Commands.argument("vz", DoubleArgumentType.doubleArg()).then(fps);
        var vy = Commands.argument("vy", DoubleArgumentType.doubleArg()).then(vz);
        var vx = Commands.argument("vx", DoubleArgumentType.doubleArg()).then(vy);
        var pos = Commands.argument("pos", Vec3Argument.vec3()).then(vx);
        var targets = Commands.argument("targets", EntityArgument.players()).then(pos);
        
        return targets;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> buildModeB() {
        var image = Commands.argument("image", StringArgumentType.string())
                .executes(ctx -> executeModeB(ctx.getSource(), 
                        EntityArgument.getPlayers(ctx, "targets"),
                        Vec3Argument.getVec3(ctx, "pos"),
                        DoubleArgumentType.getDouble(ctx, "vtx"),
                        DoubleArgumentType.getDouble(ctx, "vty"),
                        DoubleArgumentType.getDouble(ctx, "vtz"),
                        DoubleArgumentType.getDouble(ctx, "ax"),
                        DoubleArgumentType.getDouble(ctx, "ay"),
                        DoubleArgumentType.getDouble(ctx, "az"),
                        IntegerArgumentType.getInteger(ctx, "fps"),
                        IntegerArgumentType.getInteger(ctx, "size"),
                        IntegerArgumentType.getInteger(ctx, "unit"),
                        IntegerArgumentType.getInteger(ctx, "time"),
                        StringArgumentType.getString(ctx, "image"),
                        true, 0
                ))
                .then(Commands.argument("loop", BoolArgumentType.bool())
                        .executes(ctx -> executeModeB(ctx.getSource(), 
                                EntityArgument.getPlayers(ctx, "targets"),
                                Vec3Argument.getVec3(ctx, "pos"),
                                DoubleArgumentType.getDouble(ctx, "vtx"),
                                DoubleArgumentType.getDouble(ctx, "vty"),
                                DoubleArgumentType.getDouble(ctx, "vtz"),
                                DoubleArgumentType.getDouble(ctx, "ax"),
                                DoubleArgumentType.getDouble(ctx, "ay"),
                                DoubleArgumentType.getDouble(ctx, "az"),
                                IntegerArgumentType.getInteger(ctx, "fps"),
                                IntegerArgumentType.getInteger(ctx, "size"),
                                IntegerArgumentType.getInteger(ctx, "unit"),
                                IntegerArgumentType.getInteger(ctx, "time"),
                                StringArgumentType.getString(ctx, "image"),
                                BoolArgumentType.getBool(ctx, "loop"), 0
                        ))
                        .then(Commands.argument("brightness", IntegerArgumentType.integer(0, 15))
                                .executes(ctx -> executeModeB(ctx.getSource(), 
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        Vec3Argument.getVec3(ctx, "pos"),
                                        DoubleArgumentType.getDouble(ctx, "vtx"),
                                        DoubleArgumentType.getDouble(ctx, "vty"),
                                        DoubleArgumentType.getDouble(ctx, "vtz"),
                                        DoubleArgumentType.getDouble(ctx, "ax"),
                                        DoubleArgumentType.getDouble(ctx, "ay"),
                                        DoubleArgumentType.getDouble(ctx, "az"),
                                        IntegerArgumentType.getInteger(ctx, "fps"),
                                        IntegerArgumentType.getInteger(ctx, "size"),
                                        IntegerArgumentType.getInteger(ctx, "unit"),
                                        IntegerArgumentType.getInteger(ctx, "time"),
                                        StringArgumentType.getString(ctx, "image"),
                                        BoolArgumentType.getBool(ctx, "loop"),
                                        IntegerArgumentType.getInteger(ctx, "brightness")
                                ))
                        )
                );

        var time = Commands.argument("time", IntegerArgumentType.integer(1)).then(image);
        var unit = Commands.argument("unit", IntegerArgumentType.integer(1)).then(time);
        var size = Commands.argument("size", IntegerArgumentType.integer(1)).then(unit);
        var fps = Commands.argument("fps", IntegerArgumentType.integer(1)).then(size);
        var az = Commands.argument("az", DoubleArgumentType.doubleArg()).then(fps);
        var ay = Commands.argument("ay", DoubleArgumentType.doubleArg()).then(az);
        var ax = Commands.argument("ax", DoubleArgumentType.doubleArg()).then(ay);
        var vtz = Commands.argument("vtz", DoubleArgumentType.doubleArg()).then(ax);
        var vty = Commands.argument("vty", DoubleArgumentType.doubleArg()).then(vtz);
        var vtx = Commands.argument("vtx", DoubleArgumentType.doubleArg()).then(vty);
        var pos = Commands.argument("pos", Vec3Argument.vec3()).then(vtx);
        var targets = Commands.argument("targets", EntityArgument.players()).then(pos);
        
        return targets;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> buildModeC() {
        var image = Commands.argument("image", StringArgumentType.string())
                .executes(ctx -> executeModeC(ctx.getSource(), 
                        EntityArgument.getPlayers(ctx, "targets"),
                        Vec3Argument.getVec3(ctx, "pos"),
                        IntegerArgumentType.getInteger(ctx, "fps"),
                        IntegerArgumentType.getInteger(ctx, "size"),
                        IntegerArgumentType.getInteger(ctx, "unit"),
                        IntegerArgumentType.getInteger(ctx, "time"),
                        StringArgumentType.getString(ctx, "image"),
                        true, 0
                ))
                .then(Commands.argument("loop", BoolArgumentType.bool())
                        .executes(ctx -> executeModeC(ctx.getSource(), 
                                EntityArgument.getPlayers(ctx, "targets"),
                                Vec3Argument.getVec3(ctx, "pos"),
                                IntegerArgumentType.getInteger(ctx, "fps"),
                                IntegerArgumentType.getInteger(ctx, "size"),
                                IntegerArgumentType.getInteger(ctx, "unit"),
                                IntegerArgumentType.getInteger(ctx, "time"),
                                StringArgumentType.getString(ctx, "image"),
                                BoolArgumentType.getBool(ctx, "loop"), 0
                        ))
                        .then(Commands.argument("brightness", IntegerArgumentType.integer(0, 15))
                                .executes(ctx -> executeModeC(ctx.getSource(), 
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        Vec3Argument.getVec3(ctx, "pos"),
                                        IntegerArgumentType.getInteger(ctx, "fps"),
                                        IntegerArgumentType.getInteger(ctx, "size"),
                                        IntegerArgumentType.getInteger(ctx, "unit"),
                                        IntegerArgumentType.getInteger(ctx, "time"),
                                        StringArgumentType.getString(ctx, "image"),
                                        BoolArgumentType.getBool(ctx, "loop"),
                                        IntegerArgumentType.getInteger(ctx, "brightness")
                                ))
                        )
                );

        var time = Commands.argument("time", IntegerArgumentType.integer(1)).then(image);
        var unit = Commands.argument("unit", IntegerArgumentType.integer(1)).then(time);
        var size = Commands.argument("size", IntegerArgumentType.integer(1)).then(unit);
        var fps = Commands.argument("fps", IntegerArgumentType.integer(1)).then(size);
        var pos = Commands.argument("pos", Vec3Argument.vec3()).then(fps);
        var targets = Commands.argument("targets", EntityArgument.players()).then(pos);
        
        return targets;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> buildStopCommand() {
        var radius = Commands.argument("radius", DoubleArgumentType.doubleArg(0))
                .executes(ctx -> executeStop(ctx.getSource(),
                        EntityArgument.getPlayers(ctx, "targets"),
                        DoubleArgumentType.getDouble(ctx, "radius")
                ));

        var targets = Commands.argument("targets", EntityArgument.players()).then(radius);
        
        return targets.executes(ctx -> executeStop(ctx.getSource(),
                EntityArgument.getPlayers(ctx, "targets"),
                0.0
        ));
    }

    private static int executeModeA(CommandSourceStack source, Collection<ServerPlayer> targets, 
                                   Vec3 pos, double vx, double vy, double vz, int fps, int size, 
                                   int unit, int time, String image, boolean loop, int brightness) {
        for (ServerPlayer player : targets) {
            ParticleManager.spawnA(player, pos.x, pos.y, pos.z, vx, vy, vz, 
                    fps, size, unit, time, image, loop, brightness);
        }
        source.sendSuccess(() -> Component.literal("彬播放出来力"), true);
        return 1;
    }

    private static int executeModeB(CommandSourceStack source, Collection<ServerPlayer> targets,
                                   Vec3 pos, double vtx, double vty, double vtz, double ax, double ay, double az,
                                   int fps, int size, int unit, int time, String image, boolean loop, int brightness) {
        for (ServerPlayer player : targets) {
            ParticleManager.spawnB(player, pos.x, pos.y, pos.z, vtx, vty, vtz, ax, ay, az,
                    fps, size, unit, time, image, loop, brightness);
        }
        source.sendSuccess(() -> Component.literal("彬播放出来力"), true);
        return 1;
    }

    private static int executeModeC(CommandSourceStack source, Collection<ServerPlayer> targets,
                                   Vec3 pos, int fps, int size, int unit, int time, String image, boolean loop, int brightness) {
        for (ServerPlayer player : targets) {
            ParticleManager.spawnC(player, pos.x, pos.y, pos.z, fps, size, unit, time, image, loop, brightness);
        }
        source.sendSuccess(() -> Component.literal("彬播放出来力"), true);
        return 1;
    }

    private static int executeStop(CommandSourceStack source, Collection<ServerPlayer> targets, double radius) {
        Vec3 center = source.getPosition();
        for (ServerPlayer player : targets) {
            StopParticlesPacket pkt = new StopParticlesPacket(center.x, center.y, center.z, radius);
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), pkt);
        }
        source.sendSuccess(() -> Component.literal("彬停止力"), true);
        return 1;
    }
}