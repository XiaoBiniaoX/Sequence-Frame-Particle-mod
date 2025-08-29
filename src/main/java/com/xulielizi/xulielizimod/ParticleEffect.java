package com.xulielizi.xulielizimod;

/**
 * 用于描述粒子效果属性（帧序列图像、fps、持续时间一类）
 */
public class ParticleEffect {
    private final String imagePath;
    private final int delay;
    private final int fps;
    private final int duration;

    public ParticleEffect(String imagePath, int delay, int fps, int duration) {
        this.imagePath = imagePath;
        this.delay = delay;
        this.fps = fps;
        this.duration = duration;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getDelay() {
        return delay;
    }

    public int getFps() {
        return fps;
    }

    public int getDuration() {
        return duration;
    }
}
