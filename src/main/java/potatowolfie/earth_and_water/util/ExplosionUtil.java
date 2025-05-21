package potatowolfie.earth_and_water.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ExplosionUtil {

    public static void createSilentExplosion(ServerWorld world, Vec3d pos, float radius, float damage, double areaKnockback, double directHitKnockback, @Nullable Entity directHit) {
        double radiusSq = radius * radius;

        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(
                        pos.x - radius, pos.y - radius, pos.z - radius,
                        pos.x + radius, pos.y + radius, pos.z + radius
                ),
                entity -> entity.squaredDistanceTo(pos) <= radiusSq
        );

        for (LivingEntity entity : entities) {
            if (entity == directHit) {
                continue;
            }

            double dx = entity.getX() - pos.x;
            double dy = entity.getY() - pos.y;
            double dz = entity.getZ() - pos.z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist <= 0.001) dist = 0.001;
            double strength = Math.max(0.7, 1.0 - (dist / radius) * 0.3);

            Vec3d direction = new Vec3d(dx, dy, dz).normalize();
            Vec3d knockback = direction.multiply(strength * areaKnockback);

            entity.addVelocity(knockback.x, knockback.y + 0.05, knockback.z);
            entity.velocityDirty = true;

            DamageSource source = world.getDamageSources().explosion(null);
            entity.damage(source, damage * (float)strength);

            if (entity instanceof PlayerEntity playerEntity) {
                playerEntity.velocityModified = true;
            }
        }
    }
}