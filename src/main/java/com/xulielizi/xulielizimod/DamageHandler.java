package com.xulielizi.xulielizimod;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = XulieliziMod.MODID)
public class DamageHandler {
    private static final Map<UUID, Long> damageCooldown = new HashMap<>();
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        long currentTime = System.currentTimeMillis();
        
        for (var server : event.getServer().getAllLevels()) {
            ServerLevel serverLevel = (ServerLevel) server;
            for (var particle : getActiveParticles()) {
                checkParticleDamage(serverLevel, particle, currentTime);
            }
        }
        
        damageCooldown.entrySet().removeIf(entry -> currentTime - entry.getValue() > 1000);
    }
    
    private static List<ParticleDamageInfo> getActiveParticles() {
        List<ParticleDamageInfo> activeParticles = new ArrayList<>();
        
        try {
            var particles = ClientSequenceRenderer.getParticlesForDamageCheck();
            if (particles != null) {
                activeParticles.addAll(particles);
            }
        } catch (Exception e) {
        }
        
        return activeParticles;
    }
    
    private static void checkParticleDamage(ServerLevel level, ParticleDamageInfo particle, long currentTime) {
        if (particle == null) return;
        
        double width, height, depth;
        try {
            String[] dimensions = particle.hitbox.split("\\*");
            width = Double.parseDouble(dimensions[0]);
            height = Double.parseDouble(dimensions[1]);
            depth = Double.parseDouble(dimensions[2]);
        } catch (Exception e) {
            width = 1.0;
            height = 1.0;
            depth = 1.0;
        }
        
        AABB particleBox = new AABB(
            particle.x - width/2, particle.y - height/2, particle.z - depth/2,
            particle.x + width/2, particle.y + height/2, particle.z + depth/2
        );
        
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, particleBox);
        
        for (LivingEntity entity : entities) {
            UUID entityId = entity.getUUID();
            if (damageCooldown.containsKey(entityId) && currentTime - damageCooldown.get(entityId) < particle.cooldown) {
                continue;
            }
            
            if (particle.damage > 0) {
                DamageSource damageSource = createDamageSource(level, particle.damageType);
                entity.hurt(damageSource, particle.damage);
            }
            
            if (particle.knockbackX != 0 || particle.knockbackY != 0 || particle.knockbackZ != 0) {
                Vec3 knockback = new Vec3(particle.knockbackX, particle.knockbackY, particle.knockbackZ);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
            }
            
            damageCooldown.put(entityId, currentTime);
        }
    }
    
    private static DamageSource createDamageSource(ServerLevel level, String damageType) {
        if (damageType == null || damageType.equals("player")) {
            return level.damageSources().playerAttack(level.getNearestPlayer(0, 0, 0, 10, false));
        }
        
        switch (damageType.toLowerCase()) {
            case "fall": return level.damageSources().fall();
            case "magic": return level.damageSources().magic();
            case "fire": return level.damageSources().onFire();
            case "explosion": return level.damageSources().explosion(null, null);
            case "drown": return level.damageSources().drown();
            case "wither": return level.damageSources().wither();
            default: return level.damageSources().playerAttack(level.getNearestPlayer(0, 0, 0, 10, false));
        }
    }
    
    public static class ParticleDamageInfo {
        public final double x, y, z;
        public final float damage, knockbackX, knockbackY, knockbackZ;
        public final String damageType;
        public final int size;
        public final int cooldown;
        public final String hitbox;
        
        public ParticleDamageInfo(double x, double y, double z, float damage, 
                                float knockbackX, float knockbackY, float knockbackZ, 
                                String damageType, int size, int cooldown, String hitbox) {
            this.x = x; this.y = y; this.z = z;
            this.damage = damage;
            this.knockbackX = knockbackX; this.knockbackY = knockbackY; this.knockbackZ = knockbackZ;
            this.damageType = damageType;
            this.size = size;
            this.cooldown = cooldown;
            this.hitbox = hitbox;
        }
    }
}