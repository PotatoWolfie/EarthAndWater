package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import potatowolfie.earth_and_water.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModLootTableGenerator extends FabricBlockLootTableProvider {
    public ModLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.STEEL_BLOCK);
        addDrop(ModBlocks.DRIPSTONE_PILLAR);
        addDrop(ModBlocks.POLISHED_DRIPSTONE);
        addDrop(ModBlocks.POLISHED_DRIPSTONE_STAIRS);
        addDrop(ModBlocks.POLISHED_DRIPSTONE_SLAB, slabDrops(ModBlocks.POLISHED_DRIPSTONE_SLAB));
        addDrop(ModBlocks.POLISHED_DRIPSTONE_WALL);
        addDrop(ModBlocks.DARK_PRISMARINE_PILLAR);
        addDrop(ModBlocks.CHISELED_DRIPSTONE_BRICKS);
        addDrop(ModBlocks.DIORITE_BRICKS);
        addDrop(ModBlocks.DIORITE_BRICK_STAIRS);
        addDrop(ModBlocks.DIORITE_BRICK_SLAB);
        addDrop(ModBlocks.DIORITE_BRICK_WALL);
        addDrop(ModBlocks.ANDESITE_BRICKS);
        addDrop(ModBlocks.ANDESITE_BRICK_STAIRS);
        addDrop(ModBlocks.ANDESITE_BRICK_SLAB);
        addDrop(ModBlocks.ANDESITE_BRICK_WALL);
        addDrop(ModBlocks.GRANITE_BRICKS);
        addDrop(ModBlocks.GRANITE_BRICK_STAIRS);
        addDrop(ModBlocks.GRANITE_BRICK_SLAB);
        addDrop(ModBlocks.GRANITE_BRICK_WALL);
        addDrop(ModBlocks.DRIPSTONE_STAIRS);
        addDrop(ModBlocks.DRIPSTONE_SLAB);
        addDrop(ModBlocks.DRIPSTONE_WALL);
        addDrop(ModBlocks.DRIPSTONE_BRICK_STAIRS);
        addDrop(ModBlocks.DRIPSTONE_BRICK_SLAB);
        addDrop(ModBlocks.DRIPSTONE_BRICK_WALL);
        addDrop(ModBlocks.DARK_DRIPSTONE_BLOCK);
        addDrop(ModBlocks.DARK_DRIPSTONE_STAIRS);
        addDrop(ModBlocks.DARK_DRIPSTONE_SLAB);
        addDrop(ModBlocks.DARK_DRIPSTONE_WALL);
        addDrop(ModBlocks.DARK_DRIPSTONE_BRICKS);
        addDrop(ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS);
        addDrop(ModBlocks.DARK_DRIPSTONE_BRICK_SLAB);
        addDrop(ModBlocks.DARK_DRIPSTONE_BRICK_WALL);
        addDrop(ModBlocks.POLISHED_DARK_DRIPSTONE);
        addDrop(ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS);
        addDrop(ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB);
        addDrop(ModBlocks.POLISHED_DARK_DRIPSTONE_WALL);
        addDrop(ModBlocks.CHISELED_PRISMARINE_BRICKS);
        addDrop(ModBlocks.DARK_DRIPSTONE_PILLAR);
        addDrop(ModBlocks.POINTED_DARK_DRIPSTONE);
        addDrop(ModBlocks.PRISMARINE_PILLAR);
        addDrop(ModBlocks.PRISMARINE_TILES);
        addDrop(ModBlocks.PRISMARINE_TILE_STAIRS);
        addDrop(ModBlocks.PRISMARINE_TILE_SLAB);
        addDrop(ModBlocks.PRISMARINE_TILE_WALL);
        addDrop(ModBlocks.OXYGEN_BLOCK);

    }
}
