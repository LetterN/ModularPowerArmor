package net.machinemuse.powersuits.network.packets;

import net.machinemuse.numina.capabilities.render.MuseRenderCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MusePacketCosmeticPreset {
    protected int itemSlot;
    protected String presetName;

    public MusePacketCosmeticPreset() {
    }

    public MusePacketCosmeticPreset(int itemSlot, String presetName) {
        this.itemSlot = itemSlot;
        this.presetName = presetName;
    }

    public static void encode(MusePacketCosmeticPreset msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.itemSlot);
        packetBuffer.writeString(msg.presetName);
    }

    public static MusePacketCosmeticPreset decode(PacketBuffer packetBuffer) {
        return new MusePacketCosmeticPreset(packetBuffer.readInt(), packetBuffer.readString(500));
    }

    public static void handle(MusePacketCosmeticPreset message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            int itemSlot = message.itemSlot;
            String presetName = message.presetName;

            player.inventory.getStackInSlot(itemSlot).getCapability(MuseRenderCapability.RENDER).ifPresent(render-> {
                        // fixme preset stuff again
//                    CompoundNBT itemTag = MuseNBTUtils.getMuseItemTag(stack);
//                    itemTag.remove(NuminaConstants.TAG_RENDER);
//                    itemTag.putString(NuminaConstants.TAG_COSMETIC_PRESET, presetName);
            });
        });
    }
}