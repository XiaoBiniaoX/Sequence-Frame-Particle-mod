# Sequence-Frame-Particle-mod
序列帧粒子播放器 是一个 Forge 1.20.1 模组，允许玩家通过指令播放自定义的序列帧动画，类似粒子特效的形式。

Sequential Frame Particle Player is a Forge 1.20.1 mod that allows players to play custom sequential frame animations through commands, similar to particle effects.

指令方面(command)
模式 A - 匀速运动(V)
/bin A <targets> <pos> <vx> <vy> <vz> <fps> <size> <unit> <time> <image> [loop] [brightness]

模式 B - 匀加速运动 (Vt-a) 
/bin B <targets> <pos> <vtx> <vty> <vtz> <ax> <ay> <az> <fps> <size> <unit> <time> <image> [loop] [brightness]

模式 C - 静止(Immobility)
/bin C <targets> <pos> <fps> <size> <unit> <time> <image> [loop] [brightness]

停止播放(stop)
/bin stop <targets> [radius]

以下是API：
粒子API（particle spawn or stop API）
#A 
ParticleAPI.spawnParticleA(
    serverPlayer,    目标玩家能看到 (ServerPlayer)
    x, y, z,         生成坐标 (double)
    vx, vy, vz,      速度向量 (double)
    fps,             帧率 (int)
    size,            粒子大小 (int)
    unit,            序列帧规格 (int)
    time,            持续时间 tick (int)
    imagePath,       图像文件名.png (String)
    loop,            是否循环 (boolean)
    brightness       亮度 0-15 (int)
);

#B
ParticleAPI.spawnParticleB(
    serverPlayer,    目标玩家能看到 (ServerPlayer)
    x, y, z,         生成坐标 (double)
    vtx, vty, vtz,   初速度向量 (double)
    ax, ay, az,      加速度向量 (double)
    fps,             帧率 (int)
    size,            粒子大小 (int)
    unit,            序列帧规格 (int)
    time,            持续时间 tick (int)
    imagePath,       图像文件名.png (String)
    loop,            是否循环 (boolean)
    brightness       亮度 0-15 (int)
);

#C
ParticleAPI.spawnParticleC(
    serverPlayer,    目标玩家能看到 (ServerPlayer)
    x, y, z,         生成坐标 (double)
    fps,             帧率 (int)
    size,            粒子大小 (int)
    unit,            序列帧规格 (int)
    time,            持续时间 tick (int)
    imagePath,       图像文件名.png (String)
    loop,            是否循环 (boolean)
    brightness       亮度 0-15 (int)
);

#停止stop
ParticleAPI.stopParticles(
    serverPlayer,    目标玩家 (ServerPlayer)
    centerX, centerY, center z,  停止中心坐标 (double)
    radius           停止半径 (double)
);

#其他API (Other API)
// 获取粒子附近的生物列表
List<LivingEntity> entities = ParticleAPI.getEntitiesNearParticle(
    x, y, z,         // 粒子坐标 (double)
    radius,          // 检测半径 (double)
    serverPlayer     // 玩家 (ServerPlayer)
);

// 对粒子附近的每个生物执行操作
ParticleAPI.forEachEntityNearParticle(
    x, y, z,         // 粒子坐标 (double)
    radius,          // 检测半径 (double)
    serverPlayer,    // 玩家 (ServerPlayer)
    entity -> {      // 对每个生物执行的操作
        // 你的代码
    }
);

// 检查特定生物是否在粒子附近
boolean isNear = ParticleAPI.isEntityNearParticle(
    x, y, z,         // 粒子坐标 (double)
    radius,          // 检测半径 (double)
    serverPlayer,    // 玩家 (ServerPlayer)
    targetEntity     // 目标生物 (Entity)
);
