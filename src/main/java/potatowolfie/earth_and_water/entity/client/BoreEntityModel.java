package potatowolfie.earth_and_water.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.12.4

public class BoreEntityModel extends EntityModel<BoreEntity> {
	private final ModelPart head;
	private final ModelPart rods_top;
	private final ModelPart rod1;
	private final ModelPart rod2;
	private final ModelPart rod3;
	private final ModelPart rod4;
	private final ModelPart rods_bottom;
	private final ModelPart rod_bottom1;
	private final ModelPart rod_bottom2;

	public BoreEntityModel(ModelPart root) {
		this.head = root.getChild("head");
		this.rods_top = root.getChild("rods_top");
		this.rod1 = this.rods_top.getChild("rod1");
		this.rod2 = this.rods_top.getChild("rod2");
		this.rod3 = this.rods_top.getChild("rod3");
		this.rod4 = this.rods_top.getChild("rod4");
		this.rods_bottom = root.getChild("rods_bottom");
		this.rod_bottom1 = this.rods_bottom.getChild("rod_bottom1");
		this.rod_bottom2 = this.rods_bottom.getChild("rod_bottom2");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData head = modelPartData.addChild("head",
				ModelPartBuilder.create().uv(0, 0)
						.cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)),
				ModelTransform.pivot(0.0F, 4.0F, 0.0F));

		head.addChild("cube_r1",
				ModelPartBuilder.create().uv(0, 9)
						.cuboid(0.0F, -6.0F, -3.5F, 0.0F, 6.0F, 7.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, -8.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		head.addChild("cube_r2",
				ModelPartBuilder.create().uv(0, 9)
						.cuboid(0.0F, -6.0F, -3.5F, 0.0F, 6.0F, 7.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, -8.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData rods_top = modelPartData.addChild("rods_top",
				ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.0F, 0.0F));

		rods_top.addChild("rod1",
				ModelPartBuilder.create().uv(14, 16)
						.cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
				ModelTransform.pivot(-5.0F, 0.0F, -5.0F));

		rods_top.addChild("rod2",
				ModelPartBuilder.create().uv(14, 16)
						.cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
				ModelTransform.pivot(5.0F, 0.0F, -5.0F));

		rods_top.addChild("rod3",
				ModelPartBuilder.create().uv(14, 16)
						.cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
				ModelTransform.pivot(5.0F, 0.0F, 5.0F));

		rods_top.addChild("rod4",
				ModelPartBuilder.create().uv(14, 16)
						.cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
				ModelTransform.pivot(-5.0F, 0.0F, 5.0F));

		ModelPartData rods_bottom = modelPartData.addChild("rods_bottom",
				ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 11.0F, 0.0F));

		rods_bottom.addChild("rod_bottom1",
				ModelPartBuilder.create().uv(0, 0)
						.cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F))
						.uv(0, 22).mirrored()
						.cuboid(-2.0F, 6.0F, 0.0F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)).mirrored(false),
				ModelTransform.of(2.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		rods_bottom.addChild("rod_bottom2",
				ModelPartBuilder.create().uv(0, 0)
						.cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F))
						.uv(0, 22)
						.cuboid(-1.0F, 6.0F, 0.0F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)),
				ModelTransform.of(-2.5F, 0.0F, 0.0F, -0.0078F, -0.0231F, -0.1285F));

		return TexturedModelData.of(modelData, 32, 32); // Make sure your texture file is 32x32 or adjust accordingly
	}

	@Override
	public void setAngles(BoreEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		// This mob has no AI or animation, so this method is intentionally left empty
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		head.render(matrices, vertices, light, overlay, color);
		rods_top.render(matrices, vertices, light, overlay, color);
		rods_bottom.render(matrices, vertices, light, overlay, color);
	}



}