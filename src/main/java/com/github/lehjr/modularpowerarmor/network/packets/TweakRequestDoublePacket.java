//package com.github.lehjr.modularpowerarmor.network.packets;
//
//import com.github.lehjr.mpalib.nbt.NBTUtils;
//import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//import net.minecraftforge.fml.relauncher.Side;
//
//public class TweakRequestDoublePacket implements IMessage {
//    int itemSlot;
//    String moduleName;
//    String tweakName;
//    double tweakValue;
//
//    public TweakRequestDoublePacket() {
//    }
//
//    public TweakRequestDoublePacket(int itemSlot, String moduleName, String tweakName, double tweakValue) {
//        this.itemSlot = itemSlot;
//        this.moduleName = moduleName;
//        this.tweakName = tweakName;
//        this.tweakValue = tweakValue;
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        this.itemSlot = buf.readInt();
//        this.moduleName = MuseByteBufferUtils.readUTF8String(buf);
//        this.tweakName = MuseByteBufferUtils.readUTF8String(buf);
//        this.tweakValue =buf.readDouble();
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        buf.writeInt(this.itemSlot);
//        MuseByteBufferUtils.writeUTF8String(buf, this.moduleName);
//        MuseByteBufferUtils.writeUTF8String(buf, this.tweakName);
//        buf.writeDouble(this.tweakValue);
//    }
//
//    public static class Handler implements IMessageHandler<TweakRequestDoublePacket, IMessage> {
//        @Override
//        public IMessage onMessage(TweakRequestDoublePacket message, MessageContext ctx) {
//            if (ctx.side == Side.SERVER) {
//                final EntityPlayerMP player = ctx.getServerHandler().player;
//                player.getServerWorld().addScheduledTask(() -> {
//                    int itemSlot = message.itemSlot;
//                    String moduleName = message.moduleName;
//                    String tweakName = message.tweakName;
//                    double tweakValue = message.tweakValue;
//                    if (moduleName != null && tweakName != null) {
//                        ItemStack stack = player.inventory.getStackInSlot(itemSlot);
//                        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
//
//                        if (itemTag != null && ModuleManager.INSTANCE.tagHasModule(itemTag, moduleName)) {
//                            NBTUtils.removeMuseValuesTag(stack);
//                            NBTTagCompound moduleTag = itemTag.getCompoundTag(moduleName);
//                            moduleTag.setDouble(tweakName, tweakValue);
//                        }
//                    }
//                });
//            }
//            return null;
//        }
//    }
//}