package potatowolfie.earth_and_water.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_STEEL_TOOL = createTag("needs_steel_tool");
        public static final TagKey<Block> INCORRECT_FOR_STEEL_TOOL = createTag("incorrect_for_steel_tool");
        public static final TagKey<Block> NEEDS_PRISMARINE_TOOL = createTag("needs_prismarine_tool");
        public static final TagKey<Block> INCORRECT_FOR_PRISMARINE_TOOL = createTag("incorrect_for_prismarine_tool");
        public static final TagKey<Block> IS_REINFORCED_SPAWNER = createTag("is_reinforced_spawner");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(EarthWater.MOD_ID, name));
        }
    }

    public static class Item {
        public static final TagKey<net.minecraft.item.Item> BATTLE_AXE_ENCHANTABLE = createTag("battle_axe_enchantable");

        private static TagKey<net.minecraft.item.Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(EarthWater.MOD_ID, name));
        }
    }

}
