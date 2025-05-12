package potatowolfie.earth_and_water.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potatowolfie.earth_and_water.effect.ModEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private static final Map<UUID, Integer> lastAirValues = new HashMap<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void captureAirValuesBeforeTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        UUID entityId = entity.getUuid();

        if (entity.hasStatusEffect(ModEffects.BREATH_GIVING)) {
            lastAirValues.put(entityId, entity.getAir());
        } else {
            lastAirValues.remove(entityId);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void restoreAirAfterTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        UUID entityId = entity.getUuid();

        if (entity.hasStatusEffect(ModEffects.BREATH_GIVING) && lastAirValues.containsKey(entityId)) {
            int lastAir = lastAirValues.get(entityId);
            int currentAir = entity.getAir();

            if (currentAir < lastAir) {
                entity.setAir(lastAir);
            }

            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                boolean isCreativeOrSpectator = player.isSpectator() || player.getAbilities().creativeMode;

                if (isCreativeOrSpectator) {
                    entity.setAir(Math.min(entity.getAir() + 1, entity.getMaxAir()));
                }
            }
        }
    }
}