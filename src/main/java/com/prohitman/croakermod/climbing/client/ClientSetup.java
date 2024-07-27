package com.prohitman.croakermod.climbing.client;

import net.minecraftforge.common.MinecraftForge;

public class ClientSetup {
	public static void run() {
		MinecraftForge.EVENT_BUS.register(ClientEventHandlers.class);
	}
}
