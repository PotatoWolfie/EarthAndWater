package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class SpearItem extends SwordItem {
    public SpearItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player && player.isSprinting() && target.isBlocking()) {
            if (target instanceof PlayerEntity targetPlayer) {
                ItemStack activeItem = targetPlayer.getActiveItem();
                if (activeItem.getItem() instanceof ShieldItem) {
                    targetPlayer.disableShield();
                }
            }
        }
        return true;
    }
}