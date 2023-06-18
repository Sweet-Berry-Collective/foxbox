package dev.sweetberry.foxbox.client;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class ConfettiParticle extends SpriteBillboardParticle {
	public final int texture_type;
	public final float vel_add;

	public ConfettiParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f, g, h, i);
		var random = clientWorld.getRandom();
		velocityY = random.nextFloat()*2+2;
		velocityX = (random.nextFloat()-0.5)*0.5;
		velocityZ = (random.nextFloat()-0.5)*0.5;
		texture_type = clientWorld.getRandom().range(0, 14);
		scale = random.nextFloat();
		vel_add = random.nextFloat()*0.5f;
	}

	@Override
	public void tick() {
		velocityY -= 1;
		super.tick();
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float tickDelta) {
		return scale * MathHelper.clamp(((float)age + tickDelta) / (float)maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void setSpriteForAge(SpriteProvider spriteProvider) {
		if (!this.dead) {
			this.setSprite(spriteProvider.getSprite(texture_type, 14));
		}
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld world, double d, double e, double f, double g, double h, double i) {
			ConfettiParticle particle = new ConfettiParticle(world, d, e, f, g, h, i);
			particle.setSprite(spriteProvider);
			return particle;
		}
	}
}
