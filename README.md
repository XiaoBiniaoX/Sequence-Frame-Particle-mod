*序列帧粒子播放器*
*Sequential Frame Particle Player*


模组功能简介 / Mod Introduction

本模组允许在Minecraft世界中播放自定义序列帧动画作为粒子效果，支持多种运动模式和屏幕显示。
支持通过指令和API两种方式调用，兼容脚本类模组。

This mod allows playing custom sequence frame animations as particle effects in Minecraft world, 
supporting multiple motion modes and screen display. 
Can be invoked through both commands and API,  compatible with script mods.

新增功能 / New Features

屏幕粒子显示 Screen Particle Display
• 新增 /bin see 指令 - 在玩家屏幕上播放序列帧动画
• 自动适应屏幕尺寸，居中显示
• 类似南瓜头覆盖效果，沉浸式体验

智能循环逻辑 Smart Loop Logic
• loop=true: 按时间停止 - 播放指定时长后停止
• loop=false: 按帧数停止 - 播放到最后一帧即停止

精确控制 Precise Control  
• 新增 /bin stopscreen 指令 - 专门停止屏幕粒子
• 独立的屏幕粒子管理，不影响世界中的粒子

指令系统 / Command System


模式A 匀速运动 │ /bin A <目标> <坐标> <速度> <fps> <大小> <规格> <时间> <图像> 
ModeA Uniform  │ /bin A <targets> <pos> <vx,vy,vz> <fps> <size> <unit> <time> <image> 
---
模式B 加速运动  │ /bin B <目标> <坐标> <初速> <加速度> <fps> <大小> <规格> <时间> <图像> 
ModeB Accelerated │ /bin B <targets> <pos> <初速> <加速度> <fps> <size> <unit> <time> <image> 
---
模式C 静止粒子  │ /bin C <目标> <坐标> <fps> <大小> <规格> <时间> <图像> 
ModeC Static   │ /bin C <targets> <pos> <fps> <size> <unit> <time> <image> 
---
屏幕粒子模式   │ /bin see <目标> <fps> <规格> <时间> <图像> [循环] [亮度] 
Screen Mode    │ /bin see <targets> <fps> <unit> <time> <image> [loop] [brightness] 
---
停止世界粒子   │ /bin stop <目标> [半径] 
Stop World     │ /bin stop <targets> [radius] 
---
停止屏幕粒子   │ /bin stopscreen <目标> 
Stop Screen    │ /bin stopscreen <targets> 
---

---
API 接口 / API Interface
---
*粒子生成 API / Particle Spawning API:*

---
ParticleAPI.spawnParticleA(viewer, x, y, z, velocityX, velocityY, velocityZ, fps, size, unit, time, image, loop, brightness)
// 参数: 观看者, x坐标, y坐标, z坐标, x速度, y速度, z速度, 帧率, 大小, 序列帧规格, 持续时间, 图像, 是否循环, 亮度

ParticleAPI.spawnParticleB(viewer, x, y, z, initialVelocityX, initialVelocityY, initialVelocityZ, accelerationX, accelerationY, accelerationZ, fps, size, unit, time, image, loop, brightness)
// 参数: 观看者, x坐标, y坐标, z坐标, 初速x, 初速y, 初速z, 加速度x, 加速度y, 加速度z, 帧率, 大小, 序列帧规格, 持续时间, 图像, 是否循环, 亮度

ParticleAPI.spawnParticleC(viewer, x, y, z, fps, size, unit, time, image, loop, brightness)
// 参数: 观看者, x坐标, y坐标, z坐标, 帧率, 大小, 序列帧规格, 持续时间, 图像, 是否循环, 亮度

ParticleAPI.spawnScreenParticle(viewer, fps, unit, time, image, loop, brightness)
// 参数: 观看者, 帧率, 序列帧规格, 持续时间, 图像, 是否循环, 亮度
---

*粒子控制 API / Particle Control API:*

---
ParticleAPI.stopParticles(viewer, centerX, centerY, centerZ, radius)
// 参数: 观看者, 中心x坐标, 中心y坐标, 中心z坐标, 半径

ParticleAPI.stopScreenParticles(viewer)
// 参数: 观看者
---

*实体检测 API / Entity Detection API:*

---
ParticleAPI.getEntitiesNearParticle(x, y, z, radius, viewer)
// 参数: x坐标, y坐标, z坐标, 半径, 观看者

ParticleAPI.forEachEntityNearParticle(x, y, z, radius, viewer, action)
// 参数: x坐标, y坐标, z坐标, 半径, 观看者, 操作函数

ParticleAPI.isEntityNearParticle(x, y, z, radius, viewer, targetEntity)
// 参数: x坐标, y坐标, z坐标, 半径, 观看者, 目标实体
---

*使用示例 / Usage Example*
--- JS ES5 ---
// 脚本模组中使用 / Using in script mods
var ParticleAPI = Java.type("com.xulielizi.xulielizimod.ParticleAPI");
var serverPlayer = player.getMCEntity();

// 在屏幕上播放火焰动画 / Play fire animation on screen
ParticleAPI.spawnScreenParticle(serverPlayer, 15, 4, 100, "fire.png", false, 0);

// 在世界中生成带重力粒子 / Generate gravity-affected particle in world  
ParticleAPI.spawnParticleB(serverPlayer, x, y, z, 0, 0.1, 0, 0, -0.02, 0, 15, 40, 4, 100, "fire.png", false, 0);

--- JS ES5 ---

*注意事项 / Important Notes*

• 图像路径: assets/xulielizimod/textures/particle/
• 序列帧格式: n×n 正方形，优先级: 从左到右 > 从上到下
• 时间单位: tick (20 tick = 1秒)
• 亮度范围: 0-15 (0=正常, 15=过曝)

Image path: assets/xulielizimod/textures/particle/
Sequence format: n×n square, priority: left to right > top to bottom  
Time unit: tick (20 tick = 1 second)
Brightness range: 0-15 (0=normal, 15=overexposed)
