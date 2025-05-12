package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import potatowolfie.earth_and_water.effect.ModEffects;

import java.util.List;

public class WhipItem extends SwordItem {

    public WhipItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(net.minecraft.item.ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isSubmergedIn(FluidTags.WATER)) {
            target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 40, 1));
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.earth-and-water.reinforced_spawner.tooltipempty"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.whip.tooltip1"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.whip.tooltip2"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
