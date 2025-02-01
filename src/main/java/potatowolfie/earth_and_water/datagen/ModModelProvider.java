package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TexturedModel;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockStateModelGenerator.BlockTexturePool polisheddripstoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.POLISHED_DRIPSTONE);

        BlockStateModelGenerator.BlockTexturePool dripstonebricksTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.DRIPSTONE_BRICKS);
        dripstonebricksTexturePool.stairs(ModBlocks.DRIPSTONE_BRICK_STAIRS);
        dripstonebricksTexturePool.slab(ModBlocks.DRIPSTONE_BRICK_SLAB);
        dripstonebricksTexturePool.wall(ModBlocks.DRIPSTONE_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool dripstoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(Blocks.DRIPSTONE_BLOCK);
        dripstoneTexturePool.stairs(ModBlocks.DRIPSTONE_STAIRS);
        dripstoneTexturePool.slab(ModBlocks.DRIPSTONE_SLAB);
        dripstoneTexturePool.wall(ModBlocks.DRIPSTONE_WALL);

        BlockStateModelGenerator.BlockTexturePool andesitebrickTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.ANDESITE_BRICKS);
        andesitebrickTexturePool.stairs(ModBlocks.ANDESITE_BRICK_STAIRS);
        andesitebrickTexturePool.slab(ModBlocks.ANDESITE_BRICK_SLAB);
        andesitebrickTexturePool.wall(ModBlocks.ANDESITE_BRICK_WALL);

        polisheddripstoneTexturePool.stairs(ModBlocks.POLISHED_DRIPSTONE_STAIRS);
        polisheddripstoneTexturePool.slab(ModBlocks.POLISHED_DRIPSTONE_SLAB);
        polisheddripstoneTexturePool.wall(ModBlocks.POLISHED_DRIPSTONE_WALL);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STEEL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.OXYGEN_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CHISELED_PRISMARINE_BRICKS);

        BlockStateModelGenerator.BlockTexturePool dioritebricksTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.DIORITE_BRICKS);
        dioritebricksTexturePool.stairs(ModBlocks.DIORITE_BRICK_STAIRS);
        dioritebricksTexturePool.slab(ModBlocks.DIORITE_BRICK_SLAB);
        dioritebricksTexturePool.wall(ModBlocks.DIORITE_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool granitebricksTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.GRANITE_BRICKS);
        granitebricksTexturePool.stairs(ModBlocks.GRANITE_BRICK_STAIRS);
        granitebricksTexturePool.slab(ModBlocks.GRANITE_BRICK_SLAB);
        granitebricksTexturePool.wall(ModBlocks.GRANITE_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool prismarinetilesTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.PRISMARINE_TILES);
        prismarinetilesTexturePool.stairs(ModBlocks.PRISMARINE_TILE_STAIRS);
        prismarinetilesTexturePool.slab(ModBlocks.PRISMARINE_TILE_SLAB);
        prismarinetilesTexturePool.wall(ModBlocks.PRISMARINE_TILE_WALL);

        blockStateModelGenerator.registerAxisRotated(ModBlocks.DRIPSTONE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerAxisRotated(ModBlocks.DARK_PRISMARINE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerAxisRotated(ModBlocks.PRISMARINE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BORE_ROD, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRINE_ROD, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPIKED_SHIELD_SMITHING_TEMPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.REINFORCED_KEY, Models.GENERATED);
        itemModelGenerator.register(ModItems.WHIP, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPEAR, Models.GENERATED);

    }
}
