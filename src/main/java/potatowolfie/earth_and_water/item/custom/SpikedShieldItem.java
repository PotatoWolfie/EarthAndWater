package potatowolfie.earth_and_water.item.custom;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class SpikedShieldItem extends ShieldItem implements Equipment {
    public static final float MIN_DAMAGE_AMOUNT_TO_BREAK = 3.0F;
    private static final int BASE_DURABILITY = 336;

    public SpikedShieldItem(Item.Settings settings) {
        super(settings.maxDamage(BASE_DURABILITY).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        DyeColor dyeColor = stack.get(DataComponentTypes.BASE_COLOR);
        return dyeColor != null ? this.getTranslationKey() + "." + dyeColor.getName() : super.getTranslationKey(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(ItemTags.PLANKS) || super.canRepair(stack, ingredient);
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.OFFHAND;
    }
}