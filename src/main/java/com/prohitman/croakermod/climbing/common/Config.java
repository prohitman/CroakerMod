package com.prohitman.croakermod.climbing.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	public static final ForgeConfigSpec COMMON;

	public static final ForgeConfigSpec.BooleanValue PREVENT_CLIMBING_IN_RAIN;

	public static final ForgeConfigSpec.BooleanValue PATH_FINDER_DEBUG_PREVIEW;

	public static final ForgeConfigSpec.IntValue CROAKER_SPAWN_WEIGHT;
	public static final ForgeConfigSpec.IntValue CROAKER_MIN_SPAWN_SIZE;
	public static final ForgeConfigSpec.IntValue CROAKER_MAX_SPAWN_SIZE;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		CROAKER_SPAWN_WEIGHT = builder.comment("Spawn weight of the croaker.")
				.defineInRange("croakerSpawnWeight", 15, 0, 50);

		CROAKER_MIN_SPAWN_SIZE = builder.comment("Minimum size of a croaker spawn group, should always be lower than max size. Requires restarting the game.")
				.worldRestart()
				.defineInRange("croakerMinSpawnSize", 1, 0, 20);

		CROAKER_MAX_SPAWN_SIZE = builder.comment("Maximum size of a croaker spawn group, should always be higher than max size. Requires restarting the game.")
				.worldRestart()
				.defineInRange("croakerMaxSpawnSize", 1, 0, 20);

		PREVENT_CLIMBING_IN_RAIN = builder.comment("Whether spiders should be unable to climb when exposed to rain")
				.define("prevent_climbing_in_rain", true);

		PATH_FINDER_DEBUG_PREVIEW = builder
				.worldRestart()
				.comment("Whether the path finder debug preview should be enabled.")
				.define("path_finder_debug_preview", false);

		COMMON = builder.build();
	}
}
