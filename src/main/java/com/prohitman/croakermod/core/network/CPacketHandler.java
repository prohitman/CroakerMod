package com.prohitman.croakermod.core.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int id = 0;
    public static SimpleChannel HANDLER;

    public static void init(){
        HANDLER = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("croakermod", "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
    }

    public static <MSG> void register(Class<MSG> classIn, IMessage<MSG> message) {
        HANDLER.registerMessage(id++, classIn, message::encode, message::decode, message::handle);
    }
}
