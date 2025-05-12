package potatowolfie.earth_and_water.entity.client;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.effect.ModEffects;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.ModItems;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import potatowolfie.earth_and_water.util.ExplosionUtil;

public class WaterChargeProjectileEntity extends PersistentProjectileEntity {
    public Vector2f groundedOffset;

    boolean isStuck = false;
    private Entity attachedEntity = null;
    private BlockPos attachedBlock = null;
    private Direction attachedFace = null;
    private Vec3d exactHitPosition = null;

    private boolean isPaused = false;
    private boolean wasInSinglePlayer = false;
    private Vec3d pausedVelocity = null;
    private int disconnectTimer = 0;
    private static final int MULTIPLAYER_DESPAWN_TIME = 200;

    private Vec3d attachmentPosition = null;
    private Vec3d attachmentOffset = null;
    private float initialEntityYaw = 0;

    private Vec3d initialDirection = null;

    private static final float DIRECT_DAMAGE = 4.5F;
    private static final float INDIRECT_KNOCKBACK_RADIUS = 5.0F;
    private static final float DIRECT_OXYGEN_RADIUS = 3.0F;
    private static final float KNOCKBACK_STRENGTH = 0.5F;
    private static final int BUBBLE_EFFECT_DURATION = 60;
    private static final float WATER_BREATHING_DURATION = 2.5F;

    private static final float EXPLOSION_DAMAGE_FACTOR = 1.5F;
    private static final float MAX_EXPLOSION_DAMAGE = 4.5F;
    private static final float EXPLOSION_KNOCKBACK_MULTIPLIER = 1.2F;

    private int bubbleEffectTimer = 0;
    private boolean isPerformingBubbleEffect = false;
    private boolean isDirectHit = false;
    private final List<BlockPos> temporaryOxygenBlocks = new ArrayList<>();

    public WaterChargeProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoClip(false);
        this.setDamage(0);
    }

    public WaterChargeProjectileEntity(World world, PlayerEntity player) {
        super(ModEntities.WATER_CHARGE, player, world, new ItemStack(ModItems.WATER_CHARGE), null);

        this.setPosition(player.getX(), player.getEyeY() - 0.3, player.getZ());

        this.setPitch(player.getPitch());
        this.setYaw(player.getYaw());

        float pitch = player.getPitch() * 0.017453292F;
        float yaw = player.getYaw() * 0.017453292F;
        float x = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
        float y = -MathHelper.sin(pitch);
        float z = MathHelper.cos(yaw) * MathHelper.cos(pitch);
        initialDirection = new Vec3d(x, y, z).normalize();

        float speed = 1.5F;
        this.setVelocity(x * speed, y * speed, z * speed);

        this.setNoClip(false);
        this.setDamage(0);
    }

    public WaterChargeProjectileEntity(World world, double x, double y, double z, Vec3d vec3d) {
        super(ModEntities.WATER_CHARGE, world);
        this.setPosition(x, y - 0.2, z);
        if (vec3d != null) {
            this.setVelocity(vec3d);
            initialDirection = vec3d.normalize();
        }
        this.setNoClip(false);
        this.setDamage(0);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.WATER_CHARGE);
    }

    public float getRenderingRotation() {
        if (!isStuck && initialDirection != null) {
            return (float) Math.toDegrees(Math.atan2(initialDirection.x, initialDirection.z));
        }
        return 0.0f;
    }

    public boolean isGrounded() {
        return inGround;
    }


    private boolean isReallyInWater() {
        BlockPos pos = this.getBlockPos();
        World world = this.getWorld();

        if (world.isWater(pos)) {
            return true;
        }

        for (Direction dir : Direction.values()) {
            if (world.isWater(pos.offset(dir))) {
                return true;
            }
        }

        return false;
    }


    private void restoreOxygen(LivingEntity entity) {

        int durationTicks = (int)(WATER_BREATHING_DURATION * 20);
        entity.addStatusEffect(new StatusEffectInstance(ModEffects.BREATH_GIVING, durationTicks, 1));

        if (!getWorld().isClient() && entity instanceof PlayerEntity) {
            ServerWorld serverWorld = (ServerWorld) getWorld();
            serverWorld.spawnParticles(
                    ParticleTypes.BUBBLE,
                    entity.getX(),
                    entity.getY() + entity.getHeight() * 0.5,
                    entity.getZ(),
                    10,
                    0.3, 0.3, 0.3,
                    0.1
            );
        }
    }

    private void spawnTemporaryOxygenBlocks(Vec3d center, float radius) {
        if (getWorld().isClient()) return;

        ServerWorld serverWorld = (ServerWorld) getWorld();
        BlockPos centerPos = new BlockPos((int)center.x, (int)center.y, (int)center.z);

        int radiusInt = (int)Math.ceil(radius);

        clearTemporaryOxygenBlocks();

        for (int x = -radiusInt; x <= radiusInt; x++) {
            for (int y = -radiusInt; y <= radiusInt; y++) {
                for (int z = -radiusInt; z <= radiusInt; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance <= radius) {
                        BlockPos pos = centerPos.add(x, y, z);

                        if (serverWorld.isWater(pos) && serverWorld.getBlockState(pos).isAir() ||
                                serverWorld.getFluidState(pos).isOf(net.minecraft.fluid.Fluids.WATER)) {
                            temporaryOxygenBlocks.add(pos);

                            serverWorld.setBlockState(pos, ModBlocks.OXYGEN_BUBBLE.getDefaultState());
                        }
                    }
                }
            }
        }
    }


    private void clearTemporaryOxygenBlocks() {
        if (getWorld().isClient() || temporaryOxygenBlocks.isEmpty()) return;

        ServerWorld serverWorld = (ServerWorld) getWorld();

        for (BlockPos pos : temporaryOxygenBlocks) {
            if (serverWorld.getBlockState(pos).isOf(ModBlocks.OXYGEN_BUBBLE)) {
                serverWorld.removeBlock(pos, false);
            }
        }

        temporaryOxygenBlocks.clear();
    }


    private void createWaterShockwave() {
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
                    13.0f,
                    2,
                    2.5,
                    null
            );

            if (world.isWater(this.getBlockPos())) {
                float shockwaveScale = 0.62f;

                Vec3d shockwavePos = exactHitPosition != null ? exactHitPosition : this.getPos();
                shockwavePos = shockwavePos.add(0, 0.01, 0);
                serverWorld.spawnParticles(
                        EarthWater.SHOCK_WAVE,
                        shockwavePos.x, shockwavePos.y, shockwavePos.z,
                        1, 0, 0, 0, shockwaveScale
                );

                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE_COLUMN_UP,
                        pos.x, pos.y, pos.z,
                        15, 0.7, 0.7, 0.7, 0.2
                );

                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE,
                        pos.x, pos.y, pos.z,
                        25, 1.5, 1.0, 1.5, 0.1
                );

                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE_POP,
                        pos.x, pos.y + 0.5, pos.z,
                        10, 1.2, 0.8, 1.2, 0.05
                );

                world.getServer().execute(() -> {
                    serverWorld.spawnParticles(
                            ParticleTypes.BUBBLE,
                            pos.x, pos.y, pos.z,
                            15, 2.0, 1.2, 2.0, 0.05
                    );
                });
            } else {
                Vec3d shockwavePos = exactHitPosition != null ? exactHitPosition : this.getPos();
                shockwavePos = shockwavePos.add(0, 0.01, 0);
                serverWorld.spawnParticles(
                        EarthWater.SHOCK_WAVE,
                        shockwavePos.x, shockwavePos.y, shockwavePos.z,
                        1, 0, 0, 0, 0
                );
            }

            float effectRadius = INDIRECT_KNOCKBACK_RADIUS * 1.2f;

            Box affectBox = new Box(
                    pos.x - effectRadius - 0.5,
                    pos.y - effectRadius - 0.5,
                    pos.z - effectRadius - 0.5,
                    pos.x + effectRadius + 0.5,
                    pos.y + effectRadius + 0.5,
                    pos.z + effectRadius + 0.5
            );

            List<Entity> nearbyEntities = world.getEntitiesByClass(Entity.class, affectBox, entity ->
                    entity != this && entity != this.getOwner());

            for (Entity entity : nearbyEntities) {
                double distance = entity.getPos().distanceTo(pos);

                if (distance <= effectRadius) {
                    float distanceFactor = (float)(1.0 - distance / effectRadius);
                    float explosionFactor = (float)Math.pow(distanceFactor, 2.5) * EXPLOSION_KNOCKBACK_MULTIPLIER;

                    Vec3d knockbackDir = entity.getPos().subtract(pos).normalize();
                    float knockbackStrength = KNOCKBACK_STRENGTH * explosionFactor;

                    double upwardForce = 0.2 + (0.3 * explosionFactor);

                    entity.addVelocity(
                            knockbackDir.x * knockbackStrength,
                            knockbackDir.y * knockbackStrength + upwardForce,
                            knockbackDir.z * knockbackStrength
                    );
                    entity.velocityModified = true;

                    if (entity instanceof LivingEntity livingEntity) {
                        float damage = distanceFactor * EXPLOSION_DAMAGE_FACTOR * MAX_EXPLOSION_DAMAGE;
                        if (damage > 0.5f) {
                            livingEntity.damage(world.getDamageSources().explosion(this, this.getOwner()), damage);
                            restoreOxygen(livingEntity);
                        }
                    }
                }
            }
        }

        isPerformingBubbleEffect = true;
        bubbleEffectTimer = BUBBLE_EFFECT_DURATION;
    }

    private void createDirectHitEffect(Entity hitEntity) {
        World world = this.getWorld();
        Vec3d pos = hitEntity.getPos();

        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK,
                SoundCategory.NEUTRAL, 0.8F, 1.2F);
        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ENTITY_GENERIC_SPLASH,
                SoundCategory.BLOCKS, 1.0F, 1.2F);

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            ExplosionUtil.createSilentExplosion(
                    serverWorld,
                    this.getPos(),
                    3.0f,
                    13.0f,
                    2,
                    2.5,
                    null
            );

            float entityWidth = hitEntity.getWidth();
            float entityHeight = hitEntity.getHeight();
            float entitySize = (entityWidth + entityHeight) / 2.0f;
            float shockwaveScale = Math.max(1.0f, entitySize);

            Vec3d shockwavePos = exactHitPosition != null ? exactHitPosition : this.getPos();
            shockwavePos = shockwavePos.add(0, 0.01, 0);
            serverWorld.spawnParticles(
                    EarthWater.SHOCK_WAVE,
                    shockwavePos.x, shockwavePos.y, shockwavePos.z,
                    1, 0, 0, 0, shockwaveScale
            );

            if (hitEntity instanceof LivingEntity livingEntity) {
                livingEntity.damage(world.getDamageSources().explosion(this, this.getOwner()), DIRECT_DAMAGE);
                int durationTicks = (int)(WATER_BREATHING_DURATION * 20);
                livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.BREATH_GIVING, durationTicks, 1));
            }
        }

        isPerformingBubbleEffect = true;
        isDirectHit = true;
        bubbleEffectTimer = BUBBLE_EFFECT_DURATION;
        attachedEntity = hitEntity;
    }

    private void spawnBubbleParticles() {
        World world = this.getWorld();

        if (!world.isClient()) {
            return;
        }

        if (isDirectHit && attachedEntity != null) {
            Vec3d entityPos = attachedEntity.getPos();
            float entityHeight = attachedEntity.getHeight();
            float entityWidth = attachedEntity.getWidth();

            int spiralLayers = 3;
            float timeMultiplier = 0.1f;

            for (int layer = 0; layer < spiralLayers; layer++) {
                double spiralRadius = entityWidth * (1.2 + layer * 0.4);
                double baseHeight = layer * entityHeight / (spiralLayers + 1);
                float rotationDirection = (layer % 2 == 0) ? 1.0f : -1.0f;
                double baseAngle = this.age * timeMultiplier * rotationDirection;
                int spiralsPerLayer = 2;
                for (int spiral = 0; spiral < spiralsPerLayer; spiral++) {
                    double spiralOffset = spiral * (Math.PI * 2.0 / spiralsPerLayer);

                    int particlesPerSpiral = 10;
                    for (int i = 0; i < particlesPerSpiral; i++) {
                        double progress = (double)i / particlesPerSpiral;
                        double angle = baseAngle + spiralOffset + progress * Math.PI * 2.0;
                        double posX = entityPos.x + Math.sin(angle) * spiralRadius;
                        double posY = entityPos.y + baseHeight + progress * entityHeight * 0.8;
                        double posZ = entityPos.z + Math.cos(angle) * spiralRadius;
                        double jitter = 0.05;
                        posX += (this.random.nextDouble() - 0.5) * jitter;
                        posY += (this.random.nextDouble() - 0.5) * jitter;
                        posZ += (this.random.nextDouble() - 0.5) * jitter;
                        double velX = Math.cos(angle) * 0.1 * rotationDirection;
                        double velY = 0.05 + (layer * 0.02);
                        double velZ = -Math.sin(angle) * 0.1 * rotationDirection;

                        if (i % 2 == 0) {
                            world.addParticle(
                                    ParticleTypes.BUBBLE,
                                    posX, posY, posZ,
                                    velX, velY, velZ);
                        }

                        if (this.random.nextInt(15) == 0) {
                            world.addParticle(
                                    ParticleTypes.BUBBLE_POP,
                                    posX, posY + 0.2, posZ,
                                    velX * 1.5, velY * 1.5, velZ * 1.5);
                        }
                    }
                }
            }
        } else {
            Vec3d pos = exactHitPosition != null ? exactHitPosition : this.getPos();
            float explosionProgress = 1.0f - ((float)bubbleEffectTimer / BUBBLE_EFFECT_DURATION);

            if (bubbleEffectTimer > BUBBLE_EFFECT_DURATION * 0.3) {
                for (int i = 0; i < 2; i++) {
                    double randX = (this.random.nextDouble() - 0.5) * explosionProgress * 6.0;
                    double randY = (this.random.nextDouble() - 0.5) * explosionProgress * 6.0;
                    double randZ = (this.random.nextDouble() - 0.5) * explosionProgress * 6.0;

                    if (this.random.nextInt(2) == 0) {
                        world.addParticle(
                                ParticleTypes.BUBBLE_POP,
                                pos.x + randX * 0.5,
                                pos.y + randY * 0.5,
                                pos.z + randZ * 0.5,
                                randX * 0.1, 0.3, randZ * 0.1
                        );
                    }
                }
            }

            int spiralLayers = 2;
            double maxRadius = 1.0 + (double)(BUBBLE_EFFECT_DURATION - bubbleEffectTimer)
                    / BUBBLE_EFFECT_DURATION * INDIRECT_KNOCKBACK_RADIUS * 0.4;

            for (int layer = 0; layer < spiralLayers; layer++) {
                double layerRadius = maxRadius * (0.4 + 0.6 * ((double)layer / spiralLayers));
                double baseHeight = -1.0 + layer * 0.7;
                double heightRange = 2.0 + layer * 0.3;

                double baseAngle = this.age * (0.05 + layer * 0.02) * (layer % 2 == 0 ? 1 : -1);

                int spiralsPerLayer = 1 + layer;
                for (int spiral = 0; spiral < spiralsPerLayer; spiral++) {
                    double spiralOffset = spiral * (Math.PI * 2.0 / spiralsPerLayer);

                    int pointsPerSpiral = 7;
                    for (int i = 0; i < pointsPerSpiral; i++) {
                        double progress = (double)i / pointsPerSpiral;

                        double angle = baseAngle + spiralOffset + progress * Math.PI * 4.0;

                        double currentRadius = layerRadius * (0.3 + 0.7 * progress);

                        double offsetX = Math.sin(angle) * currentRadius;
                        double offsetY = baseHeight + progress * heightRange;
                        double offsetZ = Math.cos(angle) * currentRadius;

                        double spiralTightness = 0.12;
                        double upwardSpeed = 0.20;
                        double velX = Math.cos(angle) * spiralTightness * (layer % 2 == 0 ? -1 : 1);
                        double velY = upwardSpeed;
                        double velZ = -Math.sin(angle) * spiralTightness * (layer % 2 == 0 ? -1 : 1);

                        double rand = 0.03 * (layer + 1);
                        double randX = (this.random.nextDouble() - 0.5) * rand;
                        double randY = (this.random.nextDouble() - 0.5) * rand;
                        double randZ = (this.random.nextDouble() - 0.5) * rand;

                        if (i % 2 == 0) {
                            world.addParticle(
                                    ParticleTypes.BUBBLE,
                                    pos.x + offsetX + randX,
                                    pos.y + offsetY + randY,
                                    pos.z + offsetZ + randZ,
                                    velX, velY, velZ);
                        }

                        if (this.random.nextInt(20) == 0) {
                            world.addParticle(
                                    ParticleTypes.BUBBLE_POP,
                                    pos.x + offsetX + randX,
                                    pos.y + offsetY + randY + 0.2,
                                    pos.z + offsetZ + randZ,
                                    velX * 1.5, velY * 1.5, velZ * 1.5);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();

        if (entity instanceof ItemEntity || !entity.isAlive()) {
            return;
        }

        if (!isReallyInWater()) {
            spawnAsItem();
            return;
        }

        createDirectHitEffect(entity);

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.2F);

        Vec3d hitPos = entityHitResult.getPos();
        exactHitPosition = hitPos;
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.2F);
        if (!isReallyInWater()) {
            spawnAsItem();
            return;
        }

        exactHitPosition = result.getPos();
        createWaterShockwave();
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
    }

    public PersistentProjectileEntity.PickupPermission getPickupType() {
        if (isStuck && isReallyInWater()) {
            return PersistentProjectileEntity.PickupPermission.DISALLOWED;
        } else {
            return PersistentProjectileEntity.PickupPermission.ALLOWED;
        }
    }

    @Override
    public void tick() {
        if (isPerformingBubbleEffect) {
            bubbleEffectTimer--;
            spawnBubbleParticles();

            if (bubbleEffectTimer <= 0) {
                isPerformingBubbleEffect = false;
                this.discard();
                return;
            }
        }

        if (!isStuck) {
            super.tick();

            if (this.isTouchingWater() && this.getWorld().isClient()) {
                Vec3d velocity = this.getVelocity();
                double speed = velocity.length();

                if (speed > 0.1 && this.age % 2 == 0) {
                    Vec3d normalized = velocity.normalize();
                    Vec3d bubblePos = this.getPos().subtract(normalized.multiply(0.3));

                    double spreadFactor = 0.05;

                    for (int i = 0; i < 2; i++) {
                        this.getWorld().addParticle(
                                ParticleTypes.BUBBLE,
                                bubblePos.x + (this.random.nextDouble() - 0.5) * spreadFactor,
                                bubblePos.y + (this.random.nextDouble() - 0.5) * spreadFactor,
                                bubblePos.z + (this.random.nextDouble() - 0.5) * spreadFactor,
                                (this.random.nextDouble() - 0.5) * 0.02,
                                0.05,
                                (this.random.nextDouble() - 0.5) * 0.02
                        );
                    }
                }
            }

            if (this.isTouchingWater()) {
                Vec3d currentVelocity = this.getVelocity();
                this.setVelocity(currentVelocity.multiply(1.2));
            }

            Vec3d velocity = this.getVelocity();
            double length = velocity.length();
            if (length < 1.0) {
                this.setVelocity(velocity.normalize().multiply(1.0));
            }

            if (initialDirection == null && length > 0.1) {
                initialDirection = velocity.normalize();
            }
        }
    }

    private Vec3d getVec3d() {
        Vec3d entityPos = attachedEntity.getPos();

        float currentYaw = attachedEntity.getYaw() * 0.017453292F;
        float initialYawRadians = initialEntityYaw * 0.017453292F;
        float yawDifference = currentYaw - initialYawRadians;

        double offsetX = attachmentOffset.x;
        double offsetY = attachmentOffset.y;
        double offsetZ = attachmentOffset.z;

        double rotatedX = offsetX * Math.cos(yawDifference) + offsetZ * Math.sin(yawDifference);
        double rotatedZ = offsetZ * Math.cos(yawDifference) - offsetX * Math.sin(yawDifference);

        Vec3d targetPos = entityPos.add(rotatedX, offsetY, rotatedZ);
        Vec3d currentPos = new Vec3d(this.getX(), this.getY(), this.getZ());
        Vec3d newPos = currentPos.lerp(targetPos, 0.6);
        return newPos;
    }

    private void spawnAsItem() {
        if (!this.getWorld().isClient()) {
            ItemEntity itemEntity = new ItemEntity(
                    this.getWorld(),
                    this.getX(), this.getY(), this.getZ(),
                    new ItemStack(ModItems.WATER_CHARGE)
            );
            this.getWorld().spawnEntity(itemEntity);

            if (attachedEntity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
                livingEntity.setStuckArrowCount(Math.max(0, livingEntity.getStuckArrowCount() - 1));
            }

            this.discard();
        }
    }

    public boolean isStuck() {
        return isStuck;
    }

    @Override
    protected float getDragInWater() {
        return 0.99F;
    }

    @Override
    public void setDamage(double damage) {
        super.setDamage(0.0);
    }

    @Override
    public double getDamage() {
        return 0.0;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        clearTemporaryOxygenBlocks();
        super.remove(reason);
    }

    public static void registerDispenserBehavior() {
        DispenserBehavior behavior = new DispenserBehavior() {
            @Override
            public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos pos = pointer.pos();
                Direction direction = pointer.state().get(Properties.FACING);

                double x = pos.getX() + 0.5 + direction.getOffsetX() * 0.5;
                double y = pos.getY() + 0.5 + direction.getOffsetY() * 0.5;
                double z = pos.getZ() + 0.5 + direction.getOffsetZ() * 0.5;

                float speed = 1.5f;
                Vec3d velocity = new Vec3d(
                        direction.getOffsetX() * speed,
                        direction.getOffsetY() * speed,
                        direction.getOffsetZ() * speed
                );

                WaterChargeProjectileEntity projectile = new WaterChargeProjectileEntity(
                        world, x, y, z, velocity
                );

                world.spawnEntity(projectile);

                stack.decrement(1);
                return stack;
            }
        };

        DispenserBlock.registerBehavior(ModItems.WATER_CHARGE, behavior);
    }

    @Override
    public void kill() {
        if (!isPerformingBubbleEffect && !getWorld().isClient()) {
            Vec3d pos = this.getPos();
            World world = this.getWorld();

            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.spawnParticles(
                    ParticleTypes.BUBBLE_COLUMN_UP,
                    pos.x, pos.y, pos.z,
                    20, 1.0, 1.0, 1.0, 0.2
            );
        }
        super.kill();
    }

    @Override
    public void onRemoved() {
        clearTemporaryOxygenBlocks();
        super.onRemoved();
    }
}