package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.config.MPAServerSettings;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sync settings between server and client
 */
public class ConfigPacket implements IMessage {
    public ConfigPacket() {

    }

    // read settings from packet
    @Override
    public void fromBytes(ByteBuf buf) {
        MPSServerSettings settings = new MPSServerSettings(buf);
        MPSConfig.INSTANCE.setServerSettings(settings);
    }

    // write values to packet to send to the client
    @Override
    public void toBytes(ByteBuf buf) {
        if (MPSConfig.INSTANCE.getServerSettings() == null)
            MPSConfig.INSTANCE.setServerSettings(new MPSServerSettings());
        MPSConfig.INSTANCE.getServerSettings().writeToBuffer(buf);
    }

    public static class Handler implements IMessageHandler<ConfigPacket, IMessage> {
        @Override
        public IMessage onMessage(ConfigPacket message, MessageContext ctx) {
            return null;
        }
    }
}