package net.machinemuse.powersuits.client.control;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.control.KeyBindingHelper;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.ConfigHelper;
import net.machinemuse.powersuits.client.gui.clickable.ClickableKeybinding;
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

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public enum KeybindManager {
    INSTANCE;

    private static KeyBindingHelper keyBindingHelper = new KeyBindingHelper();
    // only stores keybindings relevant to us!!
    protected final Set<ClickableKeybinding> keybindings = new HashSet();

    public static Set<ClickableKeybinding> getKeybindings() {
        return INSTANCE.keybindings;
    }

    public static KeyBinding addKeybinding(String keybindDescription, InputMappings.Input keyCode, MusePoint2D position) {
        KeyBinding kb = new KeyBinding(keybindDescription, keyCode.getKeyCode(), KeybindKeyHandler.mps);
//        boolean free = !KeyBinding.HASH.containsItem(keycode);
        boolean free = !keyBindingHelper.keyBindingHasKey(keyCode);

        INSTANCE.keybindings.add(new ClickableKeybinding(kb, position, free, false));
        return kb;
    }

    public static String parseName(KeyBinding keybind) {
        if (keybind.getKey().getKeyCode() < 0) {
            return "Mouse" + (keybind.getKey().getKeyCode() + 100);
        } else {
            return keybind.getKey().getTranslationKey();
        }
    }

    public static void writeOutKeybinds() {
        BufferedWriter writer = null;
        try {
            File file = new File(ConfigHelper.getConfigFolder().getAbsolutePath(), "powersuits-keybinds.cfg");
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));

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

            for (ClickableKeybinding keybinding : INSTANCE.keybindings) {
                writer.write(keybinding.getKeyBinding().getKey().getKeyCode() + ":" + keybinding.getPosition().getX() + ':' + keybinding.getPosition().getY() + ':' + keybinding.displayOnHUD + ':' + keybinding.toggleval + '\n');
                for (ClickableModule module : keybinding.getBoundModules()) {
                    writer.write(module.getModule().getItem().getRegistryName().getPath() + '~' + module.getPosition().getX() + '~' + module.getPosition().getY() + '\n');
                }
            }
        } catch (Exception e) {
            MuseLogger.logger.error("Problem writing out keyconfig :(");
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Throwable ignored) {
            }
        }
    }

    public static void readInKeybinds() {
        try {
            File file = new File(ConfigHelper.getConfigFolder().getAbsolutePath(), "powersuits-keybinds.cfg");
            if (!file.exists()) {
                MuseLogger.logger.error("No powersuits keybind file found.");
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
                        MusePoint2D position = new MusePoint2D(Double.parseDouble(exploded[1]), Double.parseDouble(exploded[2]));
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
                                new KeyBinding(KeyBindingHelper.getInputByCode(id).getTranslationKey(), id, KeybindKeyHandler.mps), position, free, displayOnHUD);
                        workingKeybinding.toggleval = toggleval;
                        INSTANCE.keybindings.add(workingKeybinding);
                    } else {
                        workingKeybinding = null;
                    }

                } else if (line.contains("~") && workingKeybinding != null) {
                    String[] exploded = line.split("~");
                    MusePoint2D position = new MusePoint2D(Double.parseDouble(exploded[1]), Double.parseDouble(exploded[2]));
                    ItemStack module = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MPSConstants.MODID, exploded[0])));
                    if (!module.isEmpty()) {
                        ClickableModule cmodule = new ClickableModule(module, position, -1, EnumModuleCategory.NONE);
                        workingKeybinding.bindModule(cmodule);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            MuseLogger.logger.error("Problem reading in keyconfig :(");
            e.printStackTrace();
        }
    }
}