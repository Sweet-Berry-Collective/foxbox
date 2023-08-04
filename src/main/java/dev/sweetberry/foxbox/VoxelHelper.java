package dev.sweetberry.foxbox;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public final class VoxelHelper {
	public static VoxelShape rotate(VoxelShape shape, Direction dir) {
		var shapes = new ArrayList<VoxelShape>();

		shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapes.add(switch (dir) {
			case WEST -> VoxelShapes.cuboid(z1, y1, x1, z2, y2, x2);
			case SOUTH -> VoxelShapes.cuboid(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
			case EAST -> VoxelShapes.cuboid(1 - z2, y1, 1 - x2, 1 - z1, y2, 1 - x1);
			default -> VoxelShapes.cuboid(x1, y1, z1, x2, y2, z2);
		}));

		return VoxelShapes.union(VoxelShapes.empty(), shapes.toArray(new VoxelShape[]{}));
	}

	public static VoxelShape upwards(VoxelShape shape, float amount) {
		var shapes = new ArrayList<VoxelShape>();

		shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapes.add(VoxelShapes.cuboid(x1, y1+(amount/16), z1, x2, y2+(amount/16), z2)));

		return VoxelShapes.union(VoxelShapes.empty(), shapes.toArray(new VoxelShape[]{}));
	}

	public static VoxelShape forwards(VoxelShape shape, float amount) {
		var shapes = new ArrayList<VoxelShape>();

		shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapes.add(VoxelShapes.cuboid(x1, y1, z1-(amount/16), x2, y2, z2-(amount/16))));

		return VoxelShapes.union(VoxelShapes.empty(), shapes.toArray(new VoxelShape[]{}));
	}

	public static VoxelShape[] generateShapes(VoxelShape rotator, @Nullable VoxelShape base) {
		var shapes = new VoxelShape[6];

		var values = Direction.values();
		for (int i = 0; i < values.length; i++) {
			var value = values[i];
			var rotated = rotate(rotator, value.getOpposite());
			shapes[i] = base == null ? rotated : VoxelShapes.union(base, rotated);
		}

		return shapes;
	}
}
