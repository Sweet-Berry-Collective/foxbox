package dev.sweetberry.foxbox;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ConfigEnvironment;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.impl.config.Json5Serializer;

public final class FoxBoxConfig extends WrappedConfig {
	public static final ConfigEnvironment env = new ConfigEnvironment(
		QuiltLoader.getConfigDir(),
		Json5Serializer.INSTANCE
	);
	public static final FoxBoxConfig instance = Config.create(
		env,
		"foxbox",
		"foxbox",
		FoxBoxConfig.class,
		builder -> builder.format("json5")
	);

	public final TbhConfig tbh = new TbhConfig();

	public static void poke() {}

	public static float tbh_volume(boolean global) {
		if (global)
			return instance.tbh.tbh_global_volume;
		return instance.tbh.tbh_local_volume;
	}

	public static final class TbhConfig implements Section {
		@Comment("Volume for TBH when you activate it")
		public final float tbh_local_volume = 1;
		@Comment("Volume for TBH when others it")
		public final float tbh_global_volume = 1;
		@Comment("Requires you to hold a TBH to yippee")
		public final boolean yippee_needs_tbh = true;
	}
}
