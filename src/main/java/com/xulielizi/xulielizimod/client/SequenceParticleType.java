package com.xulielizi.xulielizimod.client;

import com.xulielizi.xulielizimod.XulieliziMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = XulieliziMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SequenceParticleType {
    public static final SimpleParticleType SEQUENCE_PARTICLE = new SimpleParticleType(true);

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public net.minecraft.client.particle.Particle createParticle(SimpleParticleType type, ClientLevel world,
                                                                     double x, double y, double z,
                                                                     double vx, double vy, double vz) {
            return new SequenceParticle(world, x, y, z, vx, vy, vz, sprites);
        }
    }

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        // 正确的写法：注册时把 SpriteSet 交给 Provider
        event.registerSpriteSet(SEQUENCE_PARTICLE, Provider::new);
    }
}
