package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import potatowolfie.earth_and_water.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.STEEL_BLOCK,
                        ModBlocks.POLISHED_DRIPSTONE,
                        ModBlocks.POLISHED_DRIPSTONE_STAIRS,
                        ModBlocks.POLISHED_DRIPSTONE_SLAB,
                        ModBlocks.DRIPSTONE_PILLAR
                );

        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.POLISHED_DRIPSTONE_WALL,
                        ModBlocks.DRIPSTONE_WALL,
                        ModBlocks.ANDESITE_BRICK_WALL,
                        ModBlocks.DRIPSTONE_BRICK_WALL,
                        ModBlocks.DIORITE_BRICK_WALL,
                        ModBlocks.GRANITE_BRICK_WALL,
                        ModBlocks.PRISMARINE_TILE_WALL
                );
    }
}
