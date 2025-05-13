package potatowolfie.earth_and_water;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.client.*;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.particle.ShockwaveParticle;

public class EarthWaterClient implements ClientModInitializer {

    private static SpikedShieldEntityModel SPIKED_SHIELD_MODEL;
    private static final Identifier SPIKED_SHIELD_BASE = Identifier.of(EarthWater.MOD_ID, "textures/entity/spiked_shield_base.png");
    private static final Identifier SPIKED_SHIELD_BASE_NO_PATTERN = Identifier.of(EarthWater.MOD_ID, "textures/entity/spiked_shield_base_nopattern.png");

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
        EntityModelLayerRegistry.registerModelLayer(
                SpikedShieldEntityModel.SPIKED_SHIELD_MODEL_LAYER,
                SpikedShieldEntityModel::getTexturedModelData
        );

        ModelPredicateProviderRegistry.register(
                ModItems.SPIKED_SHIELD,
                Identifier.of("minecraft", "blocking"),
                (stack, world, entity, seed) -> entity instanceof LivingEntity && entity.isUsingItem() &&
                        entity.getActiveItem() == stack ? 1.0F : 0.0F);

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SPIKED_SHIELD, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            if (SPIKED_SHIELD_MODEL == null) {
                SPIKED_SHIELD_MODEL = new SpikedShieldEntityModel(MinecraftClient.getInstance().getEntityModelLoader()
                        .getModelPart(SpikedShieldEntityModel.SPIKED_SHIELD_MODEL_LAYER));
            }

            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);

            BannerPatternsComponent patterns = stack.get(DataComponentTypes.BANNER_PATTERNS);
            DyeColor baseColor = stack.get(DataComponentTypes.BASE_COLOR);
            boolean hasPatterns = patterns != null && !patterns.layers().isEmpty();
            Identifier baseTexture = hasPatterns ? SPIKED_SHIELD_BASE : SPIKED_SHIELD_BASE_NO_PATTERN;

            SPIKED_SHIELD_MODEL.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(baseTexture)),
                    light, overlay, 0xFFFFFFFF);

            if (hasPatterns) {
                renderBannerPatterns(
                        matrices,
                        vertexConsumers,
                        light,
                        overlay,
                        patterns,
                        baseColor
                );
            }

            matrices.pop();
        });

        ModelPredicateProviderRegistry.register(
                ModItems.BATTLE_AXE,
                Identifier.of("minecraft", "pulling"),
                (stack, world, entity, seed) -> entity instanceof LivingEntity && entity.isUsingItem() &&
                        entity.getActiveItem() == stack ? 1.0F : 0.0F);
    }

    private static void renderBannerPatterns(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BannerPatternsComponent patterns,
            DyeColor baseColor) {

        if (SPIKED_SHIELD_MODEL == null) {
            SPIKED_SHIELD_MODEL = new SpikedShieldEntityModel(MinecraftClient.getInstance().getEntityModelLoader()
                    .getModelPart(SpikedShieldEntityModel.SPIKED_SHIELD_MODEL_LAYER));
        }

        if (patterns == null || patterns.layers().isEmpty()) {
            return;
        }

        if (baseColor != null) {
            int baseColorValue = baseColor.getSignColor();
            SPIKED_SHIELD_MODEL.getPlate().render(
                    matrices,
                    vertexConsumers.getBuffer(RenderLayer.getEntitySolid(SPIKED_SHIELD_BASE)),
                    light,
                    overlay,
                    baseColorValue
            );
        }

        for (int i = 0; i < patterns.layers().size(); i++) {
            var layer = patterns.layers().get(i);
            RegistryEntry<BannerPattern> patternEntry = layer.pattern();
            DyeColor patternColor = layer.color();

            if (patternEntry != null && patternColor != null) {
                String patternId = String.valueOf(patternEntry.value().assetId());
                Identifier patternTexture = Identifier.of("minecraft", "entity/banner/" + patternId);

                int colorValue = patternColor.getSignColor();

                SPIKED_SHIELD_MODEL.getPlate().render(
                        matrices,
                        vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(patternTexture)),
                        light,
                        overlay,
                        colorValue
                );
            }
        }
    }
}