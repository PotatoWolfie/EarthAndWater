package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class BattleAxeItem extends AxeItem {
    private static final int DASH_COOLDOWN = 45;
    private static final int CREATIVE_DASH_COOLDOWN = 10;
    private static final float DASH_STRENGTH = 1.1383f;
    private static final float MIDAIR_DASH_STRENGTH = 0.65f;
    private static final float Y_DASH_STRENGTH = 0.8f;
    private static final float MIDAIR_Y_DASH_STRENGTH = 0.76f;
    private static final int DASH_DISTANCE = 4;
    private static final float DASH_DAMAGE = 5.0f;
    private static final int SHIELD_DISABLE_DURATION = 100;

    public BattleAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.earth-and-water.reinforced_spawner.tooltipempty"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.battle_axe.tooltip1"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.battle_axe.tooltip2"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isBlocking()) {
            boolean isCriticalHit = false;

            if (attacker instanceof PlayerEntity playerAttacker) {
                isCriticalHit = playerAttacker.getVelocity().y < 0
                        && !playerAttacker.isOnGround()
                        && !playerAttacker.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS)
                        && !playerAttacker.isTouchingWater()
                        && !playerAttacker.getAbilities().flying;
            }

            if (isCriticalHit) {
                target.getWorld().playSound(null,
                        target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ITEM_SHIELD_BREAK,
                        SoundCategory.PLAYERS,
                        0.8F,
                        0.8F + target.getWorld().getRandom().nextFloat() * 0.4F);

                if (target instanceof PlayerEntity playerTarget) {
                    ItemStack shieldStack = playerTarget.getActiveItem();
                    if (shieldStack.getItem() instanceof ShieldItem || shieldStack.getItem() instanceof SpikedShieldItem) {
                        playerTarget.getItemCooldownManager().set(shieldStack.getItem(), SHIELD_DISABLE_DURATION);
                        playerTarget.clearActiveItem();

                        playerTarget.getWorld().sendEntityStatus(playerTarget, (byte)30);
                    }
                }
            }
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.pass(itemStack);
        }

        if (!world.isClient) {
            Vec3d lookVec = player.getRotationVector();
            double horizontalLength = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
            if (horizontalLength > 0) {
                lookVec = new Vec3d(
                        lookVec.x / horizontalLength,
                        Math.max(0, lookVec.y * 0.3),
                        lookVec.z / horizontalLength
                );
            }

            boolean isInAir = !player.isOnGround();
            float currentDashStrength = isInAir ? MIDAIR_DASH_STRENGTH : DASH_STRENGTH;
            float currentYDashStrength = isInAir ? MIDAIR_Y_DASH_STRENGTH : Y_DASH_STRENGTH;

            Vec3d dashVec = new Vec3d(
                    lookVec.x * currentDashStrength,
                    lookVec.y * currentYDashStrength,
                    lookVec.z * currentDashStrength
            );

            player.setVelocity(dashVec);
            player.velocityModified = true;
            Vec3d playerPos = player.getPos();
            Vec3d dashEnd = playerPos.add(dashVec.multiply(DASH_DISTANCE));
            Box collisionBox = new Box(
                    Math.min(playerPos.x, dashEnd.x) - 1,
                    Math.min(playerPos.y, dashEnd.y) - 1,
                    Math.min(playerPos.z, dashEnd.z) - 1,
                    Math.max(playerPos.x, dashEnd.x) + 1,
                    Math.max(playerPos.y, dashEnd.y) + 1,
                    Math.max(playerPos.z, dashEnd.z) + 1
            );

            List<LivingEntity> entities = world.getEntitiesByClass(
                    LivingEntity.class,
                    collisionBox,
                    entity -> entity != player && !entity.isSpectator()
            );

            boolean hitAnyMob = false;

            for (LivingEntity entity : entities) {
                entity.damage(player.getDamageSources().playerAttack(player), DASH_DAMAGE);
                entity.takeKnockback(0.5, -lookVec.x, -lookVec.z);
                hitAnyMob = true;
            }

            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );

            int cooldown = player.getAbilities().creativeMode ? CREATIVE_DASH_COOLDOWN : DASH_COOLDOWN;
            player.getItemCooldownManager().set(this, cooldown);
        }

        return TypedActionResult.success(itemStack);
    }
}