/*
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.powermodule.tool;// package andrew.powersuits.modules;
//
// import cpw.mods.fml.common.registry.GameRegistry;
// import cpw.mods.fml.common.network.NetworkRegistry;
// import cpw.mods.fml.common.Loader;
// import net.machinemuse.numina.item.IModularItem;
// import net.machinemuse.numina.module.IRightClickModule;
// import net.machinemuse.powersuits.item.ItemComponent;
// import net.machinemuse.powersuits.powermodule.PowerModuleBase;
// import net.machinemuse.powersuits.utils.MuseCommonStrings;
// import net.machinemuse.numina.utils.item.ItemUtils;
// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.entity.player.EntityPlayerMP;
// import net.minecraft.item.ItemStack;
//
// import net.minecraft.nbt.NBTTagCompound;
// import net.minecraft.world.World;
// import li.cil.oc.OpenComputers;
// import li.cil.oc.api.API;
// import li.cil.oc.common.tileentity.ServerRack;
// import li.cil.oc.server.component.Server;
// import li.cil.oc.api.network.Node;
// //import li.cil.oc.api.internal.Server;
// //import li.cil.oc.common.item.Terminal;
// import li.cil.oc.api.machine.Machine;
// import li.cil.oc.common.component.Terminal;
// import li.cil.oc.Settings;
// import li.cil.oc.server.PacketSender;
// import li.cil.oc.common.GuiType;
// import li.cil.oc.client.GuiHandler;
// import li.cil.oc.common.Tier;
// //import scala.Enumeration.Value;
// import com.typesafe.config.Config;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.UUID;
// import java.lang.Math;
// import java.lang.reflect.Field;
// import java.lang.NoSuchFieldException;
// import java.io.File;
//
// /**
//  * Created by User: Andrew2448
//  * 8:42 PM 6/17/13
//  */
// public class OCTerminalModule extends PowerModuleBase implements IRightClickModule {
// public static final String MODULE_OC_TERMINAL = "OC Terminal";
// private ItemStack terminal;
//
// public OCTerminalModule(((EnumModuleTarget moduleTarget))) {
//         super(validItems);
//         addInstallCost(ItemUtils.copyAndResize(ItemComponent.controlCircuit, 4));
//         terminal = API.items.get("terminal").createItemStack(1);
//         addInstallCost(terminal);
// }
//
// @Override
// public String getCategory() {
//         return MuseCommonStrings.TOOL;
// }
//
// @Override
// public String getDataName() {
//         return MODULE_OC_TERMINAL;
// }
//
// @Override
// public void onRightClick(EntityPlayer player, World world, ItemStack stack) {
// /*        if (!player.isSneaking() && stack.hasTagCompound()) {
//       String key = stack.getTagCompound().getString("oc:key");
//       String server = stack.getTagCompound().getString("oc:server");
//
//       if (key != null && !key.isEmpty() && server != null && !server.isEmpty()) {
//           if (world.isRemote) {
//    //		    player.openGui( li.cil.oc.OpenComputers$.MODULE$, GuiType.Terminal().id(), world, 0, 0, 0);
//         player.openGui( Loader.instance().getIndexedModList().get("OpenComputers").getMod() , GuiType.Terminal().id(), world, 0, 0, 0);
//     }
//     player.swingItem();
//       }
//    }*/
// }
//
// @Override
// public void onItemUse(ItemStack stack, EntityPlayer player, World world, int getX, int getY, int z, int side, float hitX, float hitY, float hitZ) {
// /*        if (world.getTileEntity(getX, getY, z) instanceof ServerRack) {
//       ServerRack rack = (ServerRack) world.getTileEntity(getX, getY, z);
//       if (side == rack.facing().ordinal()) {
//           double l = 2 / 16.0;
//     double h = 14 / 16.0;
//     int slot = ((int) (((1 - hitY) - l) / (h - l) * 4));
//     if (slot >= 0 && slot <= 3 && rack.getStackInSlot(slot) != null) {
//         if (! world.isRemote) {
//             Server server = ((Server) rack.server(slot));
//       Terminal term = rack.terminals()[slot];
//       Machine machine = server.machine();
//       String key = UUID.randomUUID().toString();
//
//                         if (! stack.hasTagCompound()) {
//                             stack.setTagCompound(new NBTTagCompound());
//                         } else {
//                             term.keys().$minus$eq(stack.getTagCompound().getString("oc:key"));
//                         }
//
//                         int maxSize = Settings.get().terminalsPerTier()[(Math.min(2, server.tier()))];
//
//                         while (term.keys().size() >= maxSize) {
//                             term.keys().remove(0);
//                         }
//
//       term.keys().$plus$eq(key);
//
//                         term.connect(machine.node());
//
//                         PacketSender.sendServerState(rack, slot, scala.Option.apply((EntityPlayerMP) player));
//       stack.getTagCompound().setString("oc:key", key);
//                         stack.getTagCompound().setString("oc:server", machine.node().address());
//                         player.inventory.markDirty();
//         }
//     }
//       }
//    }*/
// }
//
// @Override
// public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int getX, int getY, int z, int side, float hitX, float hitY, float hitZ) {
//         if (world.getTileEntity(getX, getY, z) instanceof ServerRack) {
//                 ServerRack rack = (ServerRack) world.getTileEntity(getX, getY, z);
//                 if (side == rack.facing().ordinal()) {
//                         double l = 2 / 16.0;
//                         double h = 14 / 16.0;
//                         int slot = ((int) (((1 - hitY) - l) / (h - l) * 4));
//                         if (slot >= 0 && slot <= 3 && rack.getStackInSlot(slot) != null) {
//                                 if (!world.isRemote) {
//                                         Server server = ((Server) rack.server(slot));
//                                         Terminal term = rack.terminals()[slot];
//                                         Machine machine = server.machine();
//                                         String key = UUID.randomUUID().toString();
//
//                                         if (!stack.hasTagCompound()) {
//                                                 stack.setTagCompound(new NBTTagCompound());
//                                         } else {
//                                                 term.keys().$minus$eq(stack.getTagCompound().getString("oc:key"));
//                                         }
//
//                                         int maxSize = Settings.get().terminalsPerTier()[(Math.min(2, server.tier()))];
//
//                                         while (term.keys().size() >= maxSize) {
//                                                 term.keys().remove(0);
//                                         }
//
//                                         term.keys().$plus$eq(key);
//
//                                         term.connect(machine.node());
//
//                                         PacketSender.sendServerState(rack, slot, scala.Option.apply((EntityPlayerMP) player));
//                                         stack.getTagCompound().setString("oc:key", key);
//                                         stack.getTagCompound().setString("oc:server", machine.node().address());
//                                         player.inventory.markDirty();
//                                 }
//                         }
//                 }
//         }
//         if (!player.isSneaking() && stack.hasTagCompound()) {
//                 String key = stack.getTagCompound().getString("oc:key");
//                 String server = stack.getTagCompound().getString("oc:server");
//
//                 if (key != null && !key.isEmpty() && server != null && !server.isEmpty()) {
//                         if (world.isRemote) {
// //                  player.openGui( li.cil.oc.OpenComputers$.MODULE$, GuiType.Terminal().id(), world, 0, 0, 0);
//                                 player.openGui( Loader.instance().getIndexedModList().get("OpenComputers").getMod(), GuiType.Terminal().id(), world, 0, 0, 0);
//                         }
//                         player.swingItem();
//                 }
//         }
//         return false;
// }
//
// public float minF(float a, float b) {
//         return a < b ? a : b;
// }
//
// @Override
// public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int par4) {
// }
// }
