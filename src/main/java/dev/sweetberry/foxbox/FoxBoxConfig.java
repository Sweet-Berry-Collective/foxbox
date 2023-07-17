package dev.sweetberry.foxbox;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public final class FoxBoxConfig extends ReflectiveConfig {
	public static final FoxBoxConfig instance = QuiltConfig.create("foxbox", "foxbox", FoxBoxConfig.class);

	public final TbhConfig tbh = new TbhConfig();

	public static void poke() {}

	public static float tbh_volume(boolean global) {
		if (global)
			return instance.tbh.tbh_global_volume.value();
		return instance.tbh.tbh_local_volume.value();
	}

	public static final class TbhConfig extends Section {
		@Comment("Volume for TBH when you activate it")
		public final TrackedValue<Float> tbh_local_volume = value(1.0f);
		@Comment("Volume for TBH when others it")
		public final TrackedValue<Float> tbh_global_volume = value(1.0f);
		@Comment("Requires you to hold a TBH to yippee")
		public final TrackedValue<Boolean> yippee_needs_tbh = value(true);
	}
}
