package com.github.lehjr.modularpowerarmor.client.control;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.config.ConfigHelper;
import com.github.lehjr.mpalib.control.KeyBindingHelper;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public enum KeybindManager {
    INSTANCE;

    private static KeyBindingHelper keyBindingHelper = new KeyBindingHelper();
    // only stores keybindings relevant to us!!
    protected final Set<ClickableKeybinding> keybindings = new HashSet();

    public Set<ClickableKeybinding> getKeybindings() {
        return keybindings;
    }

    public void remove(ClickableKeybinding keybinding) {
        keybindings.remove(keybinding);
        writeOutKeybinds();
    }

    public KeyBinding addKeybinding(String keybindDescription, InputMappings.Input keyCode, Point2D position) {
        KeyBinding kb = new KeyBinding(keybindDescription, keyCode.getKeyCode(), KeybindKeyHandler.mpa);
        boolean free = !keyBindingHelper.keyBindingHasKey(keyCode);
        keybindings.add(new ClickableKeybinding(kb, position, free, false));
        return kb;
    }

    public String parseName(KeyBinding keybind) {
        if (keybind.getKey().getKeyCode() < 0) {
            return "Mouse" + (keybind.getKey().getKeyCode() + 100);
        } else {
            return keybind.getKey().getTranslationKey();
        }
    }

    public void writeOutKeybinds() {
        try {
            File file = new File(ConfigHelper.setupConfigFile("modularpowerarmor-keybinds.cfg", MPAConstants.MOD_ID).getAbsolutePath());
            if (!file.exists()) {
                Files.createDirectories(file.toPath().getParent());
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file, false);

            PlayerEntity player = Minecraft.getInstance().player;
            NonNullList modulesToWrite = NonNullList.create();

            for (EquipmentSlotType slot: EquipmentSlotType.values()) {
                if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                    player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                            iItemHandler -> {
                                if (iItemHandler instanceof IModularItem) {
                                    modulesToWrite.addAll(((IModularItem) iItemHandler).getInstalledModules());
                                }
                            });
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (ClickableKeybinding keybinding : keybindings) {
                stringBuilder.append(keybinding.getKeyBinding().getKey().getKeyCode())
                        .append(":")
                        .append(keybinding.getPosition().getX())
                        .append(':')
                        .append(keybinding.getPosition().getY())
                        .append(':')
                        .append(keybinding.displayOnHUD)
                        .append(':')
                        .append(keybinding.toggleval)
                        .append('\n');

                for (ClickableModule module : keybinding.getBoundModules()) {
                    stringBuilder.append(module.getModule().getItem().getRegistryName().getPath())
                            .append('~')
                            .append(module.getPosition().getX())
                            .append('~')
                            .append(module.getPosition().getY())
                            .append('\n');
                }
            }

            String out = stringBuilder.toString();

            fileWriter.write(out);
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
            MPALibLogger.logger.error("Problem writing out keyconfig :(");
            e.printStackTrace();
        }
    }

    public void readInKeybinds() {
        try {
            File file = new File(ConfigHelper.setupConfigFile("modularpowerarmor-keybinds.cfg", MPAConstants.MOD_ID).getAbsolutePath());
            if (!file.exists()) {
                MPALibLogger.logger.error("No modular power armor keybind file found.");
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ClickableKeybinding workingKeybinding = null;
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains(":")) {
                    String[] exploded = line.split(":");
                    int id = Integer.parseInt(exploded[0]);
                    if (!keyBindingHelper.keyBindingHasKey(id)) {
                        Point2D position = new Point2D(Float.parseFloat(exploded[1]), Float.parseFloat(exploded[2]));
                        boolean free = !keyBindingHelper.keyBindingHasKey(id);
                        boolean displayOnHUD = false;
                        boolean toggleval = false;
                        if (exploded.length > 3) {
                            displayOnHUD = Boolean.parseBoolean(exploded[3]);
                        }
                        if (exploded.length > 4) {
                            toggleval = Boolean.parseBoolean(exploded[4]);
                        }

                        workingKeybinding = new ClickableKeybinding(
                                new KeyBinding(KeyBindingHelper.getInputByCode(id).getTranslationKey(), id, KeybindKeyHandler.mpa), position, free, displayOnHUD);
                        workingKeybinding.toggleval = toggleval;
                        keybindings.add(workingKeybinding);
                    } else {
                        workingKeybinding = null;
                    }

                } else if (line.contains("~") && workingKeybinding != null) {
                    String[] exploded = line.split("~");
                    Point2D position = new Point2D(Float.parseFloat(exploded[1]), Float.parseFloat(exploded[2]));
                    ItemStack module = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MPAConstants.MOD_ID, exploded[0])));
                    if (!module.isEmpty()) {
                        ClickableModule cmodule = new ClickableModule(module, position, -1, EnumModuleCategory.NONE);
                        workingKeybinding.bindModule(cmodule);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            MPALibLogger.logger.error("Problem reading in keyconfig :(");
            e.printStackTrace();
        }
    }
}