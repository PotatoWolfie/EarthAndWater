package potatowolfie.earth_and_water.entity.client;

import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.util.ExplosionUtil;

import java.util.List;

public class EarthChargeProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    public Vector2f groundedOffset;

    private static final float DAMAGE_RADIUS = 4.5F;
    private static final float BASE_DAMAGE = 7.0F;
    private static final float KNOCKBACK_MULTIPLIER = 0.0F;

    public EarthChargeProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false);
    }

    public EarthChargeProjectileEntity(World world, PlayerEntity player) {
        super(ModEntities.EARTH_CHARGE, player, world, new ItemStack(ModItems.EARTH_CHARGE), null);

        this.setPosition(player.getX(), player.getEyeY() - 0.3, player.getZ());

        this.setPitch(0);
        this.setYaw(player.getYaw());

        this.setVelocityClient(1.0F, 1.0f, 1.0f);

        this.setNoGravity(false);
    }

    public EarthChargeProjectileEntity(World world, double x, double y, double z, Vec3d vec3d) {
        super(ModEntities.EARTH_CHARGE, world);
        this.setPosition(x, y - 0.2, z);
        if (vec3d != null) {
            this.setVelocity(vec3d);
        }

        this.setNoGravity(false);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.EARTH_CHARGE);
    }

    public float getRenderingRotation() {
        return 0.0f;
    }

    public boolean isGrounded() {
        return inGround;
    }

    private void createEarthChargeImpact() {
        World world = this.getWorld();
        Vec3d pos = this.getPos();

        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND,
                SoundCategory.BLOCKS, 1.0F, 0.8F);

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            ExplosionUtil.createSilentExplosion(
                    serverWorld,
                    this.getPos(),
                    3.0f,
                    52.0f,
                    0.35,
                    0.7,
                    null
            );

            serverWorld.spawnParticles(
                    ParticleTypes.DUST_PLUME,
                    pos.x, pos.y + 0.05, pos.z,
                    15,
                    0.2, 0.05, 0.2,
                    0.1
            );

            for (int i = 0; i < 6; i++) {
                double angle = i * Math.PI / 3;
                double distance = 0.2;

                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;

                serverWorld.spawnParticles(
                        ParticleTypes.DUST_PLUME,
                        pos.x + offsetX, pos.y + 0.03, pos.z + offsetZ,
                        5,
                        0.05, 0.02, 0.05,
                        0.15
                );
            }



            for (int i = 0; i < 6; i++) {
                double angle = world.getRandom().nextDouble() * Math.PI * 2;
                double distance = 0.1 + world.getRandom().nextDouble() * 0.25;

                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;

                serverWorld.spawnParticles(
                        ParticleTypes.DUST_PLUME,
                        pos.x + offsetX * 0.3, pos.y + 0.05, pos.z + offsetZ * 0.3,
                        3,
                        0.03, 0.02, 0.03,
                        0.3
                );
            }

            Box damageBox = new Box(
                    pos.x - DAMAGE_RADIUS, pos.y - DAMAGE_RADIUS, pos.z - DAMAGE_RADIUS,
                    pos.x + DAMAGE_RADIUS, pos.y + DAMAGE_RADIUS, pos.z + DAMAGE_RADIUS
            );

            List<Entity> nearbyEntities = world.getEntitiesByClass(Entity.class, damageBox, entity ->
                    entity != this && entity != this.getOwner());

            for (Entity entity : nearbyEntities) {
                double distance = entity.getPos().distanceTo(pos);

                if (distance <= DAMAGE_RADIUS) {
                    float damage = BASE_DAMAGE * (4.8f - (float)(distance / DAMAGE_RADIUS));

                    entity.damage(world.getDamageSources().explosion(this, this.getOwner()), damage);

                    if (entity instanceof LivingEntity) {
                        Vec3d knockbackDir = entity.getPos().subtract(pos).normalize();
                        double knockbackStrength = Math.max(0, 1 - (distance / DAMAGE_RADIUS)) * KNOCKBACK_MULTIPLIER;
                        entity.addVelocity(knockbackDir.x * knockbackStrength,
                                0.2 * knockbackStrength,
                                knockbackDir.z * knockbackStrength);
                        entity.velocityModified = true;
                    }
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {

        createEarthChargeImpact();

        if (!this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {

        if(result.getSide() == Direction.SOUTH) {
            groundedOffset = new Vector2f(215f,180f);
        }
        if(result.getSide() == Direction.NORTH) {
            groundedOffset = new Vector2f(215f, 0f);
        }
        if(result.getSide() == Direction.EAST) {
            groundedOffset = new Vector2f(215f,-90f);
        }
        if(result.getSide() == Direction.WEST) {
            groundedOffset = new Vector2f(215f,90f);
        }
        if(result.getSide() == Direction.DOWN) {
            groundedOffset = new Vector2f(115f,180f);
        }
        if(result.getSide() == Direction.UP) {
            groundedOffset = new Vector2f(285f,180f);
        }

        createEarthChargeImpact();

        if (!this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient() && !this.isRemoved()) {
            if (this.isTouchingWater()) {
                Vec3d currentVelocity = this.getVelocity();
                this.setVelocity(currentVelocity.multiply(0.9));
            }
        }

        super.tick();

        if (!this.inGround) {
            Vec3d velocity = this.getVelocity();
            double length = velocity.length();

            if (length < 0.5 && (Math.abs(velocity.x) > 0.01 || Math.abs(velocity.z) > 0.01)) {
                double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                if (horizontalSpeed < 0.5) {
                    double factor = 0.5 / horizontalSpeed;
                    this.setVelocity(
                            velocity.x * factor,
                            velocity.y,
                            velocity.z * factor
                    );
                }
            }
        }

        if (this.getWorld().isClient() && !this.inGround) {
            this.getWorld().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0, 0);
        }
    }

    @Override
    public boolean isTouchingWater() {
        return this.getWorld().isWater(this.getBlockPos());
    }

    @Override
    protected float getDragInWater() {
        return 0.8F;
    }

    @Override
    protected double getGravity() {
        return 0.05F;
    }
}