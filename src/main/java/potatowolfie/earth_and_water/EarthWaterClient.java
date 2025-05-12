package potatowolfie.earth_and_water;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.client.*;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.particle.ShockwaveParticle;


public class EarthWaterClient implements ClientModInitializer {
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
                (stack, world, entity, seed) -> {
                    return entity instanceof LivingEntity && ((LivingEntity) entity).isUsingItem() &&
                            ((LivingEntity) entity).getActiveItem() == stack ? 1.0F : 0.0F;
                });

        ModelPredicateProviderRegistry.register(
                ModItems.BATTLE_AXE,
                Identifier.of("minecraft", "pulling"),
                (stack, world, entity, seed) -> {
                    return entity instanceof LivingEntity && ((LivingEntity) entity).isUsingItem() &&
                            ((LivingEntity) entity).getActiveItem() == stack ? 1.0F : 0.0F;
                });

    }

}
