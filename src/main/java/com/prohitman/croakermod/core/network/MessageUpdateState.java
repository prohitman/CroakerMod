package com.prohitman.croakermod.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateState implements IMessage<MessageUpdateState> {
    public int id;

    public MessageUpdateState() {

    }

    public MessageUpdateState(int id) {
        this.id = id;
    }

    @Override
    public void encode(MessageUpdateState pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.id);
    }

    @Override
    public MessageUpdateState decode(FriendlyByteBuf buf) {
        return new MessageUpdateState(buf.readInt());
    }

    @Override
    public void handle(MessageUpdateState message, Supplier<NetworkEvent.Context> ctx) {
        // DEBUG
        System.out.println("Message received");
        // Know it will be on the server so make it thread-safe
        final ServerPlayer thePlayer = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(message, ctx));

        });
        ctx.get().setPacketHandled(true);// no response message
    }
}
