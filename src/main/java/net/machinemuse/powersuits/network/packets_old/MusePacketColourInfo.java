//package net.machinemuse.powersuits.network.packets_old;
//
//import io.netty.buffer.ByteBufInputStream;
//import net.machinemuse.numina.common.constants.NuminaNBTConstants;
//import net.machinemuse.numina.item.IModularItem;
//import net.machinemuse.numina.network.IMusePackager;
//import net.machinemuse.numina.network.MusePacket;
//import net.machinemuse.powersuits.utils.nbt.MPSNBTUtils;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//
//
//public class MusePacketColourInfo extends MusePacket {
//    EntityPlayer player;
//    int itemSlot;
//    int[] tagData;
//
//    public MusePacketColourInfo(EntityPlayer player, int itemSlot, int[] tagData) {
//        this.player = player;
//        this.itemSlot = itemSlot;
//        this.tagData = tagData;
//    }
//
//    public static MusePacketColourInfoPackager getPackagerInstance() {
//        return MusePacketColourInfoPackager.INSTANCE;
//    }
//
//    @Override
//    public IMusePackager packager() {
//        return getPackagerInstance();
//    }
//
//    @Override
//    public void write() {
//        writeInt(itemSlot);
//        writeIntArray(tagData);
//    }
//
//    @Override
//    public void handleServer(EntityPlayerMP player) {
//        ItemStack stack = player.inventory.getStackInSlot(itemSlot);
//        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
//            NBTTagCompound renderTag = MPSNBTUtils.getMuseRenderTag(stack);
//            renderTag.setIntArray(NuminaNBTConstants.TAG_COLOURS, tagData);
//        }
//    }
//
//    public enum MusePacketColourInfoPackager implements IMusePackager {
//        INSTANCE;
//
//        @Override
//        public MusePacket read(ByteBufInputStream datain, EntityPlayer player) {
//            int itemSlot = readInt(datain);
//            int[] tagData = readIntArray(datain);
//            return new MusePacketColourInfo(player, itemSlot, tagData);
//        }
//    }
//}