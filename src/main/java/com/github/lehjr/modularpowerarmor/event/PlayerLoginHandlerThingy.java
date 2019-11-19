package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.ConfigPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 8:01 PM, 12/05/13
 * <p>
 * Ported to Java by lehjr on 10/24/16.
 */
public final class PlayerLoginHandlerThingy {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        boolean isUsingBuiltInServer = FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer();

        // dedidated server or multiplayer game
        if (!isUsingBuiltInServer || (isUsingBuiltInServer && FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount() > 1)) {
            // sync config settings between client and server
            MPAPackets.sendTo(new ConfigPacket(), (EntityPlayerMP) player);
        } else {
//            MPASettings.loadCustomInstallCosts();
        }
        if (player.world.isRemote) {
            KeybindManager.readInKeybinds();
        }
    }
}