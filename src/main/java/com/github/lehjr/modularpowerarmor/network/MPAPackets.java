package com.github.lehjr.modularpowerarmor.network;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.network.packets.*;
import com.github.lehjr.modularpowerarmor.network.packets.reworked_crafting_packets.CPlaceRecipePacket;
import com.github.lehjr.modularpowerarmor.network.packets.reworked_crafting_packets.SPlaceGhostRecipePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MPAPackets {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MPAConstants.MOD_ID, "data"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMPAPackets() {
        int i = 0;
        CHANNEL_INSTANCE.registerMessage(
                i++,
                ColourInfoPacket.class,
                ColourInfoPacket::encode,
                ColourInfoPacket::decode,
                ColourInfoPacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                CosmeticInfoPacket.class,
                CosmeticInfoPacket::encode,
                CosmeticInfoPacket::decode,
                CosmeticInfoPacket::handle);

//        CHANNEL_INSTANCE.registerMessage(
//                i++,
//                MusePacketCosmeticPreset.class,
//                MusePacketCosmeticPreset::encode,
//                MusePacketCosmeticPreset::decode,
//                MusePacketCosmeticPreset::handle);

//        CHANNEL_INSTANCE.registerMessage(
//                i++,
//                MusePacketCosmeticPresetUpdate.class,
//                MusePacketCosmeticPresetUpdate::encode,
//                MusePacketCosmeticPresetUpdate::decode,
//                MusePacketCosmeticPresetUpdate::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                CreativeInstallModuleRequestPacket.class,
                CreativeInstallModuleRequestPacket::encode,
                CreativeInstallModuleRequestPacket::decode,
                CreativeInstallModuleRequestPacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                MoveModuleFromSlotToSlotPacket.class,
                MoveModuleFromSlotToSlotPacket::encode,
                MoveModuleFromSlotToSlotPacket::decode,
                MoveModuleFromSlotToSlotPacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                CPlaceRecipePacket.class,
                CPlaceRecipePacket::encode,
                CPlaceRecipePacket::decode,
                CPlaceRecipePacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                SPlaceGhostRecipePacket.class,
                SPlaceGhostRecipePacket::encode,
                SPlaceGhostRecipePacket::decode,
                SPlaceGhostRecipePacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                OnClientLoginPacket.class,
                OnClientLoginPacket::encode,
                OnClientLoginPacket::decode,
                OnClientLoginPacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                ContainerGuiOpenPacket.class,
                ContainerGuiOpenPacket::encode,
                ContainerGuiOpenPacket::decode,
                ContainerGuiOpenPacket::handle);

    }

    public SimpleChannel getWrapper() {
        return CHANNEL_INSTANCE;
    }
}