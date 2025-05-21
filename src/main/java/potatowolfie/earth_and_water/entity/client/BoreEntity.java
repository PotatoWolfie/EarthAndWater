package potatowolfie.earth_and_water.entity.client;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.EnumSet;

public class BoreEntity extends PathAwareEntity {

    public enum BoreState {
        IDLE,
        BURROWED,
        UNBURROWING,
        SHOOTING
    }

    private BoreState boreState = BoreState.IDLE;
    private int animationTick = 0;
    private boolean isDarkVariant = false;
    private int shootCooldown = 0;

    public BoreEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 8;
    }

    public static DefaultAttributeContainer.Builder createBoreAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0) // No walking animation
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new BoreShootGoal(this));
        this.goalSelector.add(2, new BoreUnburrowGoal(this));
        this.goalSelector.add(3, new BoreBurrowGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        animationTick++;

        if (boreState == BoreState.SHOOTING && shootCooldown > 0) {
            shootCooldown--;
        }
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public BoreState getBoreState() {
        return boreState;
    }

    public void setBoreState(BoreState newState) {
        if (this.boreState != newState) {
            this.boreState = newState;
            this.animationTick = 0;
        }
    }

    public void tryShootAtPlayer() {
        if (shootCooldown > 0) return;

        LivingEntity target = this.getTarget();
        if (target == null || !this.canSee(target)) return;

        Vec3d targetPos = target.getPos().add(0, target.getStandingEyeHeight() - 0.3, 0);
        Vec3d direction = targetPos.subtract(this.getPos()).normalize();

        EarthChargeProjectileEntity charge = new EarthChargeProjectileEntity(this.getWorld(), this);
        charge.setPosition(this.getX(), this.getEyeY(), this.getZ());
        charge.setVelocity(direction.x, direction.y, direction.z, 1.0f, 0.1f);
        this.getWorld().spawnEntity(charge);

        shootCooldown = 60;
    }

    public boolean isDarkVariant() {
        return isDarkVariant;
    }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 EntityData entityData, NbtCompound entityNbt) {
        if (this.random.nextFloat() < 0.00195f) {
            this.isDarkVariant = true;
        }
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean damaged = super.damage(source, amount);

        if (damaged && boreState == BoreState.BURROWED && source.getAttacker() instanceof PlayerEntity) {
            this.setBoreState(BoreState.UNBURROWING);
        }

        return damaged;
    }

    // ---- AI Goals as inner classes ----

    private static class BoreBurrowGoal extends Goal {
        private final BoreEntity bore;

        public BoreBurrowGoal(BoreEntity bore) {
            this.bore = bore;
        }

        @Override
        public boolean canStart() {
            return bore.getBoreState() == BoreState.IDLE && bore.getTarget() == null;
        }

        @Override
        public void start() {
            bore.setBoreState(BoreState.BURROWED);
        }

        @Override
        public boolean shouldContinue() {
            return bore.getBoreState() == BoreState.BURROWED;
        }
    }

    private static class BoreUnburrowGoal extends Goal {
        private final BoreEntity bore;

        public BoreUnburrowGoal(BoreEntity bore) {
            this.bore = bore;
        }

        @Override
        public boolean canStart() {
            if (bore.getBoreState() != BoreState.BURROWED) return false;
            PlayerEntity player = bore.getWorld().getClosestPlayer(bore, 15.0);
            return player != null && player.distanceTo(bore) > 10.0;
        }

        @Override
        public void start() {
            bore.setBoreState(BoreState.UNBURROWING);
        }

        @Override
        public boolean shouldContinue() {
            return bore.getBoreState() == BoreState.UNBURROWING;
        }
    }

    private static class BoreShootGoal extends Goal {
        private final BoreEntity bore;
        private int cooldown = 0;

        public BoreShootGoal(BoreEntity bore) {
            this.bore = bore;
            this.setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            return bore.getBoreState() == BoreState.UNBURROWING && bore.getTarget() != null;
        }

        @Override
        public void tick() {
            if (cooldown-- <= 0) {
                bore.tryShootAtPlayer();
                bore.setBoreState(BoreState.SHOOTING);
                cooldown = 60;
            }
        }

        @Override
        public boolean shouldContinue() {
            return bore.getTarget() != null;
        }
    }
}