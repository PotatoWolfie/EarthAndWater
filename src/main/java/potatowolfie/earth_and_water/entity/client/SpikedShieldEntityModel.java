package potatowolfie.earth_and_water.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
// Made with Blockbench 4.12.4

public class SpikedShieldEntityModel extends net.minecraft.client.render.entity.model.ShieldEntityModel {
	private static final String PLATE = "plate";
	private static final String HANDLE = "handle";
	public static final EntityModelLayer SPIKED_SHIELD_MODEL_LAYER =
			new EntityModelLayer(Identifier.of(EarthWater.MOD_ID, "spiked_shield"), "main");
	private final ModelPart root;
	private final ModelPart plate;
	private final ModelPart handle;

	public SpikedShieldEntityModel(ModelPart root) {
		super(root);
		this.root = root;
		this.plate = root.getChild(PLATE);
		this.handle = root.getChild(HANDLE);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(PLATE, ModelPartBuilder.create()
						.uv(0, 0).cuboid(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F, new Dilation(0.0F))
						.uv(0, 20).cuboid(-3.0F, -9.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(0, 20).cuboid(-9.0F, -9.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(0, 20).cuboid(-9.0F, -4.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(0, 20).cuboid(-3.0F, -4.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(6, 20).cuboid(-9.0F, 2.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(6, 20).cuboid(-3.0F, 2.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(6, 20).cuboid(-3.0F, 7.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(6, 20).cuboid(-9.0F, 7.0F, -5.0F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(0, 25).cuboid(-10.0F, -9.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
						.uv(2, 25).cuboid(-4.0F, -9.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
						.uv(0, 25).cuboid(-10.0F, -4.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
						.uv(0, 25).cuboid(-4.0F, -4.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
						.uv(0, 25).cuboid(-10.0F, 2.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
						.uv(0, 25).cuboid(-4.0F, 2.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
						.uv(0, 25).cuboid(-10.0F, 7.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
						.uv(0, 25).cuboid(-4.0F, 7.0F, -2.01F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)),
				ModelTransform.NONE);

		modelPartData.addChild(HANDLE, ModelPartBuilder.create()
						.uv(26, 0).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, new Dilation(0.0F)),
				ModelTransform.NONE);

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPlate() {
		return this.plate;
	}

	@Override
	public ModelPart getHandle() {
		return this.handle;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.root.render(matrices, vertices, light, overlay, color);
	}
}