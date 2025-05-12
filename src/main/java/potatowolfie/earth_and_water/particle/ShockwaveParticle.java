package potatowolfie.earth_and_water.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShockwaveParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final float initialScale;
    private final float targetScale;
    private final float initialAlpha;

    protected ShockwaveParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ,
                                SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.spriteProvider = spriteProvider;
        this.maxAge = 10;
        this.scale = 1.5f;
        this.initialScale = 1.5f;

        this.targetScale = (float)Math.max(1.5f, velocityX * 3.0);

        this.alpha = 0.7f;
        this.initialAlpha = 0.7f;

        this.setColor(0.8f, 0.8f, 0.8f);

        this.setSpriteForAge(spriteProvider);
        this.collidesWithWorld = false;

        this.setAlpha(0.7f);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        float progress = (float)this.age / (float)this.maxAge;

        this.scale = initialScale + (targetScale - initialScale) * progress;

        if (progress > 0.5f) {
            float fadeProgress = (progress - 0.5f) * 2.0f;
            this.alpha = initialAlpha * (1.0f - fadeProgress);
        }

        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        float x = (float)(this.prevPosX + (this.x - this.prevPosX) * (double)tickDelta - cameraPos.getX());
        float y = (float)(this.prevPosY + (this.y - this.prevPosY) * (double)tickDelta - cameraPos.getY());
        float z = (float)(this.prevPosZ + (this.z - this.prevPosZ) * (double)tickDelta - cameraPos.getZ());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(x, y, z);

        matrixStack.multiply(new Quaternionf().rotationY((float)Math.toRadians(90)));

        float size = this.scale;

        Vector3f[] corners = new Vector3f[]{
                new Vector3f(-size, 0, -size),
                new Vector3f(-size, 0, size),
                new Vector3f(size, 0, size),
                new Vector3f(size, 0, -size)
        };

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();

        int light = this.getBrightness(tickDelta);

        vertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), corners[0].x, corners[0].y, corners[0].z)
                .texture(maxU, maxV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light);

        vertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), corners[1].x, corners[1].y, corners[1].z)
                .texture(maxU, minV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light);

        vertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), corners[2].x, corners[2].y, corners[2].z)
                .texture(minU, minV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light);

        vertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), corners[3].x, corners[3].y, corners[3].z)
                .texture(minU, maxV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new ShockwaveParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}