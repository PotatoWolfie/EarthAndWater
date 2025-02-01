package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.FluidTags;

public class WhipItem extends SwordItem {
    public WhipItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(net.minecraft.item.ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isSubmergedIn(FluidTags.WATER)) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 120, 0)); // 6 seconds
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 120, 0)); // 6 seconds
        }
        return super.postHit(stack, target, attacker);
    }
}
