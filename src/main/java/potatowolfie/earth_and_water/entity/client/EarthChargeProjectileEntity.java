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
    private static final float BASE_DAMAGE = 14.0F;
    private static final float KNOCKBACK_MULTIPLIER = 0.2F;

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

    public EarthChargeProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.EARTH_CHARGE, world);
        this.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0.0f, 1.0f, 1.0f);
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

    private void applyDirectHitDamage(Entity hitEntity) {
        if (hitEntity instanceof LivingEntity livingEntity && !this.getWorld().isClient()) {
            livingEntity.damage(this.getWorld().getDamageSources().generic(), BASE_DAMAGE);

            Vec3d knockbackDir = hitEntity.getPos().subtract(this.getPos()).normalize();
            if (knockbackDir.lengthSquared() < 0.001) {
                knockbackDir = new Vec3d(0, 1, 0);
            }
            double directKnockback = KNOCKBACK_MULTIPLIER * 3.5;
            hitEntity.addVelocity(
                    knockbackDir.x * directKnockback,
                    knockbackDir.y * directKnockback + 0.2,
                    knockbackDir.z * directKnockback
            );
            hitEntity.velocityModified = true;
        }
    }

    private void applyAreaDamageExcluding(Entity excludedEntity) {
        World world = this.getWorld();
        Vec3d pos = this.getPos();

        if (!world.isClient()) {
            if (world instanceof ServerWorld serverWorld) {
                float areaEffectDamage = BASE_DAMAGE * 2.1F;

                ExplosionUtil.createSilentExplosion(
                        serverWorld,
                        pos,
                        DAMAGE_RADIUS,
                        areaEffectDamage,
                        KNOCKBACK_MULTIPLIER * 1.75,
                        KNOCKBACK_MULTIPLIER * 3.5,
                        excludedEntity
                );
            }
        }
    }

    private void createExplosionEffects() {
        World world = this.getWorld();
        Vec3d pos = this.getPos();

        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND,
                SoundCategory.BLOCKS, 1.0F, 0.8F);

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
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
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        applyDirectHitDamage(hitEntity);
        applyAreaDamageExcluding(hitEntity);

        if (!this.getWorld().isClient()) {
            createExplosionEffects();
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        switch (result.getSide()) {
            case SOUTH -> groundedOffset = new Vector2f(215f, 180f);
            case NORTH -> groundedOffset = new Vector2f(215f, 0f);
            case EAST -> groundedOffset = new Vector2f(215f, -90f);
            case WEST -> groundedOffset = new Vector2f(215f, 90f);
            case DOWN -> groundedOffset = new Vector2f(115f, 180f);
            case UP -> groundedOffset = new Vector2f(285f, 180f);
        }

        applyAreaDamageExcluding(null);

        if (!this.getWorld().isClient()) {
            createExplosionEffects();
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