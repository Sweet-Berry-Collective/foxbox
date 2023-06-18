package dev.sweetberry.foxbox.client;

import dev.sweetberry.foxbox.FoxBoxSeatEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class FoxBoxSeatEntityRenderer extends EmptyEntityRenderer<FoxBoxSeatEntity> {
	public FoxBoxSeatEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRender(FoxBoxSeatEntity entity, Frustum frustum, double x, double y, double z) {
		return false;
	}
}
