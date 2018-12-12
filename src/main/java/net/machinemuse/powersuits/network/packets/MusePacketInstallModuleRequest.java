package net.machinemuse.powersuits.network.packets;

import io.netty.buffer.ByteBuf;
import net.machinemuse.numina.module.IPowerModule;
import net.machinemuse.numina.network.MuseByteBufferUtils;
import net.machinemuse.numina.utils.MuseLogger;
import net.machinemuse.numina.utils.energy.ElectricItemUtils;
import net.machinemuse.numina.utils.item.MuseItemUtils;
import net.machinemuse.numina.utils.nbt.MuseNBTUtils;
import net.machinemuse.powersuits.common.ModuleManager;
import net.machinemuse.powersuits.common.config.MPSConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet for requesting to purchase an upgrade. Player-to-server. Server decides whether it is a valid upgrade or not and replies with an associated
 * inventoryrefresh packet.
 *
 * Author: MachineMuse (Claire Semple)
 * Created: 10:16 AM, 01/05/13
 *
 * Ported to Java by lehjr on 11/14/16.
 */
public class MusePacketInstallModuleRequest implements IMessage {
    EntityPlayer player;
    int itemSlot;
    String moduleName;

    public MusePacketInstallModuleRequest(EntityPlayer player, int itemSlot, String moduleName) {
        this.player = player;
        this.itemSlot = itemSlot;
        this.moduleName = moduleName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.itemSlot = buf.readInt();
        this.moduleName = MuseByteBufferUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(itemSlot);
        MuseByteBufferUtils.writeUTF8String(buf, moduleName);
    }

    public static class Handler implements IMessageHandler<MusePacketInstallModuleRequest, IMessage> {
        @Override
        public IMessage onMessage(MusePacketInstallModuleRequest message, MessageContext ctx) {
            if (ctx.side != Side.SERVER) {
                MuseLogger.logError("Module Install Packet sent to wrong side: " + ctx.side);
                return null;
            }

            int itemSlot= message.itemSlot;
            String moduleName = message.moduleName;
            final EntityPlayerMP player = ctx.getServerHandler().player;

            ItemStack stack = player.inventory.getStackInSlot(itemSlot);
            if (moduleName != null) {
                InventoryPlayer inventory = player.inventory;
                IPowerModule moduleType = ModuleManager.INSTANCE.getModule(moduleName);
                if (moduleType == null || !moduleType.isAllowed()) {
                    player.sendMessage(new TextComponentString("Server has disallowed this module. Sorry!"));
                    return null;
                }
                NonNullList<ItemStack> cost = ModuleManager.INSTANCE.getInstallCost(moduleName);
                if ((!ModuleManager.INSTANCE.itemHasModule(stack, moduleName) && MuseItemUtils.hasInInventory(cost, player.inventory)) || player.capabilities.isCreativeMode) {
                    MuseNBTUtils.removeMuseValuesTag(stack);
                    ModuleManager.INSTANCE.itemAddModule(stack, moduleType);
                    for (ItemStack stackInCost : cost) {
                        ElectricItemUtils.givePlayerEnergy(player, MPSConfig.INSTANCE.rfValueOfComponent(stackInCost));
                    }

                    if (!player.capabilities.isCreativeMode) {
                        MuseItemUtils.deleteFromInventory(cost, inventory);
                    }

                    // use builtin handler
                    player.inventoryContainer.detectAndSendChanges();
                }
            }
            return null;
        }
    }
}
