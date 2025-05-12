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
            double dx = entity.getX() - pos.x;
            double dy = entity.getY() - pos.y;
            double dz = entity.getZ() - pos.z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist == 0) continue;

            double strength = 1.0 - (dist / radius);

            Vec3d direction = new Vec3d(dx, dy, dz).normalize();
            double knockbackAmount = (entity == directHit) ? directHitKnockback : areaKnockback;
            Vec3d knockback = direction.multiply(strength * knockbackAmount);

            if (entity == directHit) {
                entity.setVelocity(knockback.x, knockback.y + 0.1, knockback.z);
            } else {
                entity.addVelocity(knockback.x, knockback.y + 0.05, knockback.z);
            }

            entity.velocityDirty = true;

            DamageSource source = world.getDamageSources().explosion(null);
            entity.damage(source, damage * (float)strength);

            if (entity instanceof PlayerEntity playerEntity) {
                playerEntity.velocityModified = true;
            }
        }
    }
}