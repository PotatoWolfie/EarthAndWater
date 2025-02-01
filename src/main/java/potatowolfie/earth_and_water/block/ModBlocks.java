package potatowolfie.earth_and_water.block;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.custom.ChiseledDripstoneBricksBlock;
import potatowolfie.earth_and_water.block.custom.OxygenBlock;
import potatowolfie.earth_and_water.block.custom.OxygenBubbleBlock;

import static net.minecraft.block.Blocks.*;

public class ModBlocks {
    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)
                    .strength(6f).requiresTool()));

    public static final Block DRIPSTONE_PILLAR = registerBlock("dripstone_pillar",
            new PillarBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.DRIPSTONE_BLOCK)
                    .strength(4f).requiresTool()));
    public static final Block DARK_PRISMARINE_PILLAR = registerBlock("dark_prismarine_pillar",
            new PillarBlock(AbstractBlock.Settings.copy(DARK_PRISMARINE)));
    public static final Block PRISMARINE_PILLAR = registerBlock("prismarine_pillar",
            new PillarBlock(AbstractBlock.Settings.copy(PRISMARINE)));

    public static final Block DRIPSTONE_BRICKS = registerBlock("dripstone_bricks",
            new Block(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_BRICK_STAIRS = registerBlock("dripstone_brick_stairs",
            new StairsBlock(ModBlocks.DRIPSTONE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_BRICK_SLAB = registerBlock("dripstone_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_BRICK_WALL = registerBlock("dripstone_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));

    public static final Block POLISHED_DRIPSTONE = registerBlock("polished_dripstone",
            new Block(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block POLISHED_DRIPSTONE_STAIRS = registerBlock("polished_dripstone_stairs",
            new StairsBlock(ModBlocks.POLISHED_DRIPSTONE.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block POLISHED_DRIPSTONE_SLAB = registerBlock("polished_dripstone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block POLISHED_DRIPSTONE_WALL = registerBlock("polished_dripstone_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));

    public static final Block DRIPSTONE_STAIRS = registerBlock("dripstone_stairs",
            new StairsBlock(Blocks.DRIPSTONE_BLOCK.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_SLAB = registerBlock("dripstone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_WALL = registerBlock("dripstone_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));

    public static final Block CHISELED_DRIPSTONE_BRICKS = registerBlock("chiseled_dripstone_bricks",
            new ChiseledDripstoneBricksBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sounds(BlockSoundGroup.DRIPSTONE_BLOCK)
                    .requiresTool()
                    .strength(1.5F, 1.0F)
                    .luminance(state -> state.get(ChiseledDripstoneBricksBlock.POWERED) ? 12 : 0)));

    public static final Block ANDESITE_BRICKS = registerBlock("andesite_bricks",
            new Block(AbstractBlock.Settings.copy(ANDESITE)));
    public static final Block ANDESITE_BRICK_STAIRS = registerBlock("andesite_brick_stairs",
            new StairsBlock(ModBlocks.ANDESITE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(ANDESITE)));
    public static final Block ANDESITE_BRICK_SLAB = registerBlock("andesite_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(ANDESITE)));
    public static final Block ANDESITE_BRICK_WALL = registerBlock("andesite_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(ANDESITE)));

    public static final Block DIORITE_BRICKS = registerBlock("diorite_bricks",
            new Block(AbstractBlock.Settings.copy(DIORITE)));
    public static final Block DIORITE_BRICK_STAIRS = registerBlock("diorite_brick_stairs",
            new StairsBlock(ModBlocks.DIORITE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(DIORITE)));
    public static final Block DIORITE_BRICK_SLAB = registerBlock("diorite_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DIORITE)));
    public static final Block DIORITE_BRICK_WALL = registerBlock("diorite_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(DIORITE)));


    public static final Block GRANITE_BRICKS = registerBlock("granite_bricks",
            new Block(AbstractBlock.Settings.copy(GRANITE)));
    public static final Block GRANITE_BRICK_STAIRS = registerBlock("granite_brick_stairs",
            new StairsBlock(ModBlocks.GRANITE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(GRANITE)));
    public static final Block GRANITE_BRICK_SLAB = registerBlock("granite_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(GRANITE)));
    public static final Block GRANITE_BRICK_WALL = registerBlock("granite_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(GRANITE)));

    public static final Block CHISELED_PRISMARINE_BRICKS = registerBlock("chiseled_prismarine_bricks",
            new Block(AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block PRISMARINE_TILES = registerBlock("prismarine_tiles",
            new Block(AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block PRISMARINE_TILE_STAIRS = registerBlock("prismarine_tile_stairs",
            new StairsBlock(ModBlocks.PRISMARINE_TILES.getDefaultState(),
                    AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block PRISMARINE_TILE_SLAB = registerBlock("prismarine_tile_slab",
            new SlabBlock(AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block PRISMARINE_TILE_WALL = registerBlock("prismarine_tile_wall",
            new WallBlock(AbstractBlock.Settings.copy(PRISMARINE)));

    public static final Block OXYGEN_BLOCK = registerBlock("oxygen_block",
            new OxygenBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .luminance(state -> 3)
                    .strength(0.5F)
                    .allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune())
                    .postProcess(Blocks::always)
                    .emissiveLighting(Blocks::always)));

    public static final Block OXYGEN_BUBBLE = registerBlock("oxygen_bubble",
            new OxygenBubbleBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.WATER_BLUE)
                    .replaceable()
                    .noCollision()
                    .dropsNothing()
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .liquid()
                    .sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)));

    public static final Block REINFORCED_SPAWNER = registerBlock("reinforced_spawner",
            new Block(AbstractBlock.Settings.create()
                    .strength(5.0f, 1200.0f)
                    .requiresTool()
                    .nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(EarthWater.MOD_ID, name), block);
    }
    private static void customBuildingBlocks(FabricItemGroupEntries entries) {
        entries.add(STEEL_BLOCK);
        entries.add(DRIPSTONE_BRICKS);
        entries.add(CHISELED_DRIPSTONE_BRICKS);
        entries.add(DRIPSTONE_BRICK_STAIRS);
        entries.add(DRIPSTONE_BRICK_SLAB);
        entries.add(DRIPSTONE_BRICK_WALL);
        entries.add(POLISHED_DRIPSTONE);
        entries.add(POLISHED_DRIPSTONE_STAIRS);
        entries.add(POLISHED_DRIPSTONE_SLAB);
        entries.add(POLISHED_DRIPSTONE_WALL);
        entries.add(DRIPSTONE_PILLAR);
        entries.add(ANDESITE_BRICKS);
        entries.add(ANDESITE_BRICK_STAIRS);
        entries.add(ANDESITE_BRICK_SLAB);
        entries.add(ANDESITE_BRICK_WALL);
        entries.add(DIORITE_BRICKS);
        entries.add(DIORITE_BRICK_STAIRS);
        entries.add(DIORITE_BRICK_SLAB);
        entries.add(DIORITE_BRICK_WALL);
        entries.add(GRANITE_BRICKS);
        entries.add(GRANITE_BRICK_STAIRS);
        entries.add(GRANITE_BRICK_SLAB);
        entries.add(GRANITE_BRICK_WALL);
        entries.add(PRISMARINE_TILES);
        entries.add(PRISMARINE_PILLAR);
        entries.add(PRISMARINE_TILE_STAIRS);
        entries.add(PRISMARINE_TILE_SLAB);
        entries.add(PRISMARINE_TILE_WALL);
        entries.add(CHISELED_PRISMARINE_BRICKS);
        entries.add(DARK_PRISMARINE_PILLAR);
    }

    private static void customNaturalBlocks(FabricItemGroupEntries entries) {
        entries.add(OXYGEN_BLOCK);
    }

    private static void customSpawnEggs(FabricItemGroupEntries entries) {
        entries.add(REINFORCED_SPAWNER);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(EarthWater.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks () {
        EarthWater.LOGGER.info("Registering Mod Blocks for " + EarthWater.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(ModBlocks::customBuildingBlocks);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModBlocks::customNaturalBlocks);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModBlocks::customSpawnEggs);
    }
}
