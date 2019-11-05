package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.modularpowerarmor.containers.providers.TinkerContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

/**
 * A packet for sending a containerGui open request from the client side.
 */
public class ContainerGuiOpenPacket {
    int guiID;
    public ContainerGuiOpenPacket(int guiIDIn) {
        this.guiID = guiIDIn;
    }

    public static void encode(ContainerGuiOpenPacket msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.guiID);
    }

    public static ContainerGuiOpenPacket decode(PacketBuffer packetBuffer) {
        return new ContainerGuiOpenPacket(packetBuffer.readInt());
    }

    public static void handle(ContainerGuiOpenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkHooks.openGui(ctx.get().getSender(), new TinkerContainerProvider(msg.guiID), (buffer) -> buffer.writeInt(msg.guiID));
        });
        ctx.get().setPacketHandled(true);
    }
}