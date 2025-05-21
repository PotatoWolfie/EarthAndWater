package potatowolfie.earth_and_water.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class BoreEntityRenderer extends MobEntityRenderer<BoreEntity, BoreEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/bore.png");

    public BoreEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BoreEntityModel(context.getPart(ModEntityModelLayers.BORE)), 0.3f);
    }

    @Override
    public Identifier getTexture(BoreEntity entity) {
        return TEXTURE;
    }
}