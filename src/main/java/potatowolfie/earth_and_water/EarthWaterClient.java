package potatowolfie.earth_and_water;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.client.*;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.particle.ShockwaveParticle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EarthWaterClient implements ClientModInitializer {

    public static final EntityModelLayer SPIKED_SHIELD = new EntityModelLayer(
            Identifier.of("earth-and-water", "spiked_shield"), "main"
    );

    private static final Vector3f POSITIVE_X = new Vector3f(1.0F, 0.0F, 0.0F);
    private static final Vector3f POSITIVE_Y = new Vector3f(0.0F, 1.0F, 0.0F);
    private static final Vector3f POSITIVE_Z = new Vector3f(0.0F, 0.0F, 1.0F);

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OXYGEN_BUBBLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_SPAWNER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POINTED_DARK_DRIPSTONE, RenderLayer.getCutout());
        ParticleFactoryRegistry.getInstance().register(EarthWater.LIGHT_UP, EndRodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EarthWater.SHOCK_WAVE, ShockwaveParticle.Factory::new);

        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.EARTH_CHARGE, EarthChargeProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.EARTH_CHARGE, EarthChargeProjectileRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.WATER_CHARGE, WaterChargeProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.WATER_CHARGE, WaterChargeProjectileRenderer::new);

        ModelPredicateProviderRegistry.register(
                ModItems.SPIKED_SHIELD,
                Identifier.of("minecraft", "blocking"),
                (stack, world, entity, seed) -> entity instanceof LivingEntity && entity.isUsingItem() &&
                        entity.getActiveItem() == stack ? 1.0F : 0.0F);

        ModelPredicateProviderRegistry.register(
                ModItems.BATTLE_AXE,
                Identifier.of("minecraft", "pulling"),
                (stack, world, entity, seed) -> entity instanceof LivingEntity && entity.isUsingItem() &&
                        entity.getActiveItem() == stack ? 1.0F : 0.0F);

        EntityModelLayerRegistry.registerModelLayer(SPIKED_SHIELD, SpikedShieldEntityModel::getTexturedModelData);

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SPIKED_SHIELD, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            SpikedShieldEntityModel model = new SpikedShieldEntityModel(client.getEntityModelLoader().getModelPart(SPIKED_SHIELD));

            matrices.push();

            applyDisplayTransform(matrices, mode.name().toLowerCase());

            boolean hasPattern = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA) != null;

            SpriteIdentifier baseSprite = new SpriteIdentifier(
                    TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE,
                    Identifier.of("earth-and-water", "entity/spiked_shield_base")
            );
            VertexConsumer baseConsumer = baseSprite.getSprite().getTextureSpecificVertexConsumer(
                    vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(baseSprite.getAtlasId()))
            );

            model.getHandle().render(matrices, baseConsumer, light, overlay);
            model.getPlate().render(matrices, baseConsumer, light, overlay);

            if (hasPattern) {
                NbtComponent blockEntityData = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
                if (blockEntityData != null) {
                    NbtCompound nbt = blockEntityData.copyNbt();
                    List<Pair<RegistryKey<BannerPattern>, DyeColor>> patterns = getPatternsFromNbt(nbt);

                    for (int i = 0; i < patterns.size() && i < 17; ++i) {
                        Pair<RegistryKey<BannerPattern>, DyeColor> pair = patterns.get(i);
                        Identifier patternTexture = Identifier.of("minecraft", "entity/shield/" + pair.getLeft().getRegistry());
                        SpriteIdentifier patternSprite = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, patternTexture);

                        VertexConsumer patternConsumer = patternSprite.getSprite().getTextureSpecificVertexConsumer(
                                vertexConsumers.getBuffer(RenderLayer.getEntityNoOutline(patternSprite.getAtlasId()))
                        );

                        int color = pair.getRight().getFireworkColor();
                        model.getPlate().render(matrices, patternConsumer, light, overlay, color);
                    }
                }
            }

            matrices.pop();
        });
    }

    private void applyDisplayTransform(MatrixStack matrices, String lowerCase) {
    }

    private static final Map<String, RegistryKey<BannerPattern>> PATTERN_ID_MAP = new HashMap<>();

    static {
        PATTERN_ID_MAP.put("b", BannerPatterns.BASE);
        PATTERN_ID_MAP.put("sbl", BannerPatterns.SQUARE_BOTTOM_LEFT);
        PATTERN_ID_MAP.put("sbr", BannerPatterns.SQUARE_BOTTOM_RIGHT);
        PATTERN_ID_MAP.put("stl", BannerPatterns.SQUARE_TOP_LEFT);
        PATTERN_ID_MAP.put("str", BannerPatterns.SQUARE_TOP_RIGHT);
        PATTERN_ID_MAP.put("sb", BannerPatterns.STRIPE_BOTTOM);
        PATTERN_ID_MAP.put("st", BannerPatterns.STRIPE_TOP);
        PATTERN_ID_MAP.put("sl", BannerPatterns.STRIPE_LEFT);
        PATTERN_ID_MAP.put("sr", BannerPatterns.STRIPE_RIGHT);
        PATTERN_ID_MAP.put("sce", BannerPatterns.STRIPE_CENTER);
        PATTERN_ID_MAP.put("sm", BannerPatterns.STRIPE_MIDDLE);
        PATTERN_ID_MAP.put("sdr", BannerPatterns.STRIPE_DOWNRIGHT);
        PATTERN_ID_MAP.put("sdl", BannerPatterns.STRIPE_DOWNLEFT);
        PATTERN_ID_MAP.put("ss", BannerPatterns.SMALL_STRIPES);
        PATTERN_ID_MAP.put("c", BannerPatterns.CROSS);
        PATTERN_ID_MAP.put("sc", BannerPatterns.STRAIGHT_CROSS);
        PATTERN_ID_MAP.put("tb", BannerPatterns.TRIANGLE_BOTTOM);
        PATTERN_ID_MAP.put("tt", BannerPatterns.TRIANGLE_TOP);
        PATTERN_ID_MAP.put("tsb", BannerPatterns.TRIANGLES_BOTTOM);
        PATTERN_ID_MAP.put("tst", BannerPatterns.TRIANGLES_TOP);
        PATTERN_ID_MAP.put("dl", BannerPatterns.DIAGONAL_LEFT);
        PATTERN_ID_MAP.put("dur", BannerPatterns.DIAGONAL_UP_RIGHT);
        PATTERN_ID_MAP.put("dul", BannerPatterns.DIAGONAL_UP_LEFT);
        PATTERN_ID_MAP.put("dr", BannerPatterns.DIAGONAL_RIGHT);
        PATTERN_ID_MAP.put("ci", BannerPatterns.CIRCLE);
        PATTERN_ID_MAP.put("r", BannerPatterns.RHOMBUS);
        PATTERN_ID_MAP.put("hv", BannerPatterns.HALF_VERTICAL);
        PATTERN_ID_MAP.put("hh", BannerPatterns.HALF_HORIZONTAL);
        PATTERN_ID_MAP.put("hvr", BannerPatterns.HALF_VERTICAL_RIGHT);
        PATTERN_ID_MAP.put("hhb", BannerPatterns.HALF_HORIZONTAL_BOTTOM);
        PATTERN_ID_MAP.put("bo", BannerPatterns.BORDER);
        PATTERN_ID_MAP.put("cb", BannerPatterns.CURLY_BORDER);
        PATTERN_ID_MAP.put("g", BannerPatterns.GRADIENT);
        PATTERN_ID_MAP.put("gu", BannerPatterns.GRADIENT_UP);
        PATTERN_ID_MAP.put("br", BannerPatterns.BRICKS);
        PATTERN_ID_MAP.put("gl", BannerPatterns.GLOBE);
        PATTERN_ID_MAP.put("cr", BannerPatterns.CREEPER);
        PATTERN_ID_MAP.put("s", BannerPatterns.SKULL);
        PATTERN_ID_MAP.put("f", BannerPatterns.FLOWER);
        PATTERN_ID_MAP.put("m", BannerPatterns.MOJANG);
        PATTERN_ID_MAP.put("p", BannerPatterns.PIGLIN);
        PATTERN_ID_MAP.put("fl", BannerPatterns.FLOW);
        PATTERN_ID_MAP.put("gust", BannerPatterns.GUSTER);
    }

    private static List<Pair<RegistryKey<BannerPattern>, DyeColor>> getPatternsFromNbt(NbtCompound nbt) {
        List<Pair<RegistryKey<BannerPattern>, DyeColor>> patterns = new ArrayList<>();

        int baseColorId = nbt.getInt("Base");
        DyeColor baseColor = DyeColor.byId(baseColorId);
        patterns.add(new Pair<>(BannerPatterns.BASE, baseColor));

        NbtList patternList = nbt.getList("Patterns", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < patternList.size(); i++) {
            NbtCompound patternTag = patternList.getCompound(i);
            String patternId = patternTag.getString("Pattern");
            int colorId = patternTag.getInt("Color");

            RegistryKey<BannerPattern> patternKey = PATTERN_ID_MAP.get(patternId);
            DyeColor color = DyeColor.byId(colorId);
            if (patternKey != null && color != null) {
                patterns.add(new Pair<>(patternKey, color));
            }
        }

        return patterns;
    }
}