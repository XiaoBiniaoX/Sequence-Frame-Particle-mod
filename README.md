序列帧粒子播放器 Sequential Frame Particle Player
模组功能简介 / Mod Introduction

本模组允许在Minecraft世界中播放自定义序列帧动画作为粒子效果，支持多种运动模式和屏幕显示。 支持通过指令和API两种方式调用，兼容脚本类模组。

This mod allows playing custom sequence frame animations as particle effects in Minecraft world, supporting multiple motion modes and screen display. Can be invoked through both commands and API, compatible with script mods.

*________________________________________________________________________________*

-主要特性 / Key Features-

* 支持四种粒子运动模式 * / * Supports four particle motion modes *
* 可自定义序列帧动画播放 * / * Customizable sequence frame animation playback *
* 支持屏幕显示粒子效果 * / * Supports screen display particle effects *
* 内置伤害和击退系统 * / * Built-in damage and knockback system *
* 伤害区域设置 * / * Flexible damage area settings *
* 多种伤害类型支持 * / * Multiple damage type support *

*________________________________________________________________________________*

-指令系统 / Command System-

基础指令格式 / Basic command format:
/bin <模式> <目标> <参数...>

可用模式 / Available modes:
* A模式 - 匀速直线运动 * / * Mode A - Uniform linear motion *
* B模式 - 匀加速曲线运动 * / * Mode B - Uniformly accelerated curved motion *
* C模式 - 静止粒子效果 * / * Mode C - Static particle effect *
* D模式 - 固定方向粒子 * / * Mode D - Fixed direction particles *
* see - 屏幕粒子效果 * / * see - Screen particle effect *
* stop - 停止世界粒子 * / * stop - Stop world particles *
* stopscreen - 停止屏幕粒子 * / * stopscreen - Stop screen particles *

指令示例 / Command examples:

/bin A @p 0 0 0 0.1 0 0 15 40 4 100 image.png true 5 10 0.5 0.5 0.5 magic 500 3*3*3
/bin C @a ~ ~1 ~ 20 50 4 200 effect.png false 0 5.0 1.0 0.5 1.0 player 1000 2*2*2
/bin see @p 15 4 100 screen_effect.png true 10

*________________________________________________________________________________*

-API调用 / API Calls-

在脚本中使用 / Usage in scripts:

*// 导入API类 * / *// Import API class *
var ParticleAPI = Java.type("com.xulielizi.xulielizimod.ParticleAPI");

*// 获取服务器玩家对象 * / *// Get server player object *
var serverPlayer = player.getMCEntity();

*// 调用不同模式 * / *// Call different modes *

*模式A - 匀速运动 * / *Mode A - Uniform motion*
ParticleAPI.spawnParticleA(serverPlayer, x, y, z, vx, vy, vz, fps, size, unit, time, image, loop, brightness, damage, kbx, kby, kbz, damageType, cooldown, hitbox);

*模式B - 加速运动 * / *Mode B - Accelerated motion*
ParticleAPI.spawnParticleB(serverPlayer, x, y, z, vx, vy, vz, ax, ay, az, fps, size, unit, time, image, loop, brightness, damage, kbx, kby, kbz, damageType, cooldown, hitbox);

*模式C - 静止粒子 * / *Mode C - Static particles*
ParticleAPI.spawnParticleC(serverPlayer, x, y, z, fps, size, unit, time, image, loop, brightness, damage, kbx, kby, kbz, damageType, cooldown, hitbox);

*模式D - 固定方向 * / *Mode D - Fixed direction*
ParticleAPI.spawnParticleD(serverPlayer, x, y, z, vx, vy, vz, ax, ay, az, fps, size, unit, time, image, loop, brightness, damage, kbx, kby, kbz, damageType, rx, ry, rz, cooldown, hitbox);

*屏幕粒子 * / *Screen particles*
ParticleAPI.spawnScreenParticle(serverPlayer, fps, unit, time, image, loop, brightness);

*停止粒子 * / *Stop particles*
ParticleAPI.stopParticles(serverPlayer, centerX, centerY, centerZ, radius);
ParticleAPI.stopScreenParticles(serverPlayer);

*________________________________________________________________________________*

-参数说明 / Parameter Description-

通用参数 / Common parameters:
* serverPlayer - 观看者(ServerPlayer对象) * / * serverPlayer - Viewer (ServerPlayer object) *
* x,y,z - 生成坐标 * / * x,y,z - Spawn coordinates *
* fps - 帧率(1-60) * / * fps - Frame rate (1-60) *
* size - 粒子大小(1-100) * / * size - Particle size (1-100) *
* unit - 序列帧网格尺寸 * / * unit - Sequence frame grid size *
* time - 持续时间(tick) * / * time - Duration (ticks) *
* image - 图片文件名 * / * image - Image file name *
* loop - 是否循环 * / * loop - Whether to loop *
* brightness - 亮度(0-15) * / * brightness - Brightness (0-15) *

运动参数 / Motion parameters:
* vx,vy,vz - 速度向量 * / * vx,vy,vz - Velocity vector *
* ax,ay,az - 加速度向量 * / * ax,ay,az - Acceleration vector *
* rx,ry,rz - 旋转角度(仅D模式) * / * rx,ry,rz - Rotation angles (D mode only) *

伤害参数 / Damage parameters:
* damage - 伤害值(可为负值治疗) * / * damage - Damage value (negative for healing) *
* kbx,kby,kbz - 击退方向 * / * kbx,kby,kbz - Knockback direction *
* damageType - 伤害类型 * / * damageType - Damage type *
* cooldown - 伤害冷却(毫秒) * / * cooldown - Damage cooldown (ms) *
* hitbox - 伤害区域(宽*高*深) * / * hitbox - Damage area (width*height*depth) *

伤害类型 / Damage types:
* player - 玩家攻击 * / * player - Player attack *
* magic - 魔法伤害 * / * magic - Magic damage *
* fire - 火焰伤害 * / * fire - Fire damage *
* explosion - 爆炸伤害 * / * explosion - Explosion damage *
* fall - 摔落伤害 * / * fall - Fall damage *
* drown - 溺水伤害 * / * drown - Drowning damage *
* wither - 凋零伤害 * / * wither - Wither damage *

*________________________________________________________________________________*

-选择器参数 / Selector Parameters-

目标选择器 / Target selectors:
* @p - 最近玩家 * / * @p - Nearest player *
* @a - 所有玩家 * / * @a - All players *
* @r - 随机玩家 * / * @r - Random player *
* @e - 所有实体 * / * @e - All entities *
* @s - 自己 * / * @s - Self *

坐标参数 / Coordinate parameters:
* ~ ~ ~ - 相对坐标 * / * ~ ~ ~ - Relative coordinates *
* ^ ^ ^ - 局部坐标 * / * ^ ^ ^ - Local coordinates *
* x y z - 绝对坐标 * / * x y z - Absolute coordinates *

*________________________________________________________________________________*

-使用示例 / Usage Examples-

*简单粒子效果 * / *Simple particle effect*
/bin C @p ~ ~1 ~ 15 30 4 100 fire.png false 0

*带伤害的区域效果 * / *Area effect with damage*
/bin C @a 0 64 0 10 50 2 300 explosion.png true 5 8.0 1.0 0.5 1.0 explosion 1000 5*5*5

*治疗区域 * / *Healing area*
/bin C @a ~ ~ ~ 20 40 4 200 heal.png true 8 -3.0 0 0 0 magic 2000 4*4*4

*运动粒子轨迹 * / *Moving particle trail*
/bin A @p 0 70 0 0.2 0 0 20 25 4 150 trail.png false 0

*屏幕提示效果 * / *Screen hint effect*
/bin see @p 12 4 80 hint.png false 0

*________________________________________________________________________________*

-注意事项 / Notes-

* 图片文件应放置在: assets/xulielizimod/textures/particle/ * / * Image files should be placed in: assets/xulielizimod/textures/particle/ *
* 序列帧图片应为网格布局 * / * Sequence frame images should be grid layout *
* 只有C和D模式支持伤害系统 * / * Only modes C and D support damage system *
* 伤害会对玩家和生物都生效 * / * Damage affects both players and mobs *
* 冷却时间防止连续伤害 * / * Cooldown prevents continuous damage *
