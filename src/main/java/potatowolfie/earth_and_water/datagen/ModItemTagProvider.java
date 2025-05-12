package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import potatowolfie.earth_and_water.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(ModItems.STEEL_INGOT);

        getOrCreateTagBuilder(ItemTags.TRIM_TEMPLATES)
                .add(ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE)
                .add(ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE)
                .add(ModItems.SPIKED_SHIELD_UPGRADE_SMITHING_TEMPLATE);

        getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE)
                .add(ModItems.BATTLE_AXE)
                .add(ModItems.WHIP);

    }
}
