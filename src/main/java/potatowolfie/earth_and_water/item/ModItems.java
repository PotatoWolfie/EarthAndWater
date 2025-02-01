package potatowolfie.earth_and_water.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.item.custom.SpearItem;
import potatowolfie.earth_and_water.item.custom.SpikedShieldItem;
import potatowolfie.earth_and_water.item.custom.WhipItem;

public class ModItems {
    public static final Item BRINE_ROD = registerItem( "brine_rod", new Item(new Item.Settings()));
    public static final Item BORE_ROD = registerItem( "bore_rod", new Item(new Item.Settings()));
    public static final Item STEEL_INGOT = registerItem("steel_ingot", new Item(new Item.Settings()));
    public static final Item SPIKED_SHIELD_SMITHING_TEMPLATE = registerItem("spiked_shield_smithing_template", new Item(new Item.Settings()));
    public static final Item BLOCK_SMITHING_TEMPLATE = registerItem("block_smithing_template", new Item(new Item.Settings()));
    public static final Item GUARD_SMITHING_TEMPLATE = registerItem("guard_smithing_template", new Item(new Item.Settings()));
    public static final Item REINFORCED_KEY = registerItem("reinforced_key", new Item(new Item.Settings()));
    public static final Item WHIP = registerItem("whip", 
            new WhipItem(ToolMaterials.IRON, 400, -2.4f, new Item.Settings()));
    public static final Item SPEAR = registerItem("spear", 
            new SpearItem(ToolMaterials.IRON, 500, -3.0f, new Item.Settings()));
    public static final Item SPIKED_SHIELD = registerItem("spiked_shield", 
            new SpikedShieldItem(new Item.Settings().maxDamage(336)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(EarthWater.MOD_ID, name), item);
    }
    private static void customIngredients(FabricItemGroupEntries entries) {
        entries.add(BRINE_ROD);
        entries.add(BORE_ROD);
        entries.add(STEEL_INGOT);
        entries.add(SPIKED_SHIELD_SMITHING_TEMPLATE);
        entries.add(BLOCK_SMITHING_TEMPLATE);
        entries.add(GUARD_SMITHING_TEMPLATE);
        entries.add(REINFORCED_KEY);
    }

    private static void customCombat(FabricItemGroupEntries entries) {
        entries.add(WHIP);
        entries.add(SPEAR);
        entries.add(SPIKED_SHIELD);
    }

    public static void registerModItems() {
        EarthWater.LOGGER.info("Registering Mod Items for " + EarthWater.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::customIngredients);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::customCombat);
    }
}