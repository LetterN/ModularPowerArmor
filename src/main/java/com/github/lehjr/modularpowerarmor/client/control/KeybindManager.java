package com.github.lehjr.modularpowerarmor.client.control;

import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.control.KeyBindingHelper;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum KeybindManager {
    INSTANCE;
    private static KeyBindingHelper keyBindingHelper = new KeyBindingHelper();
    // only stores keybindings relevant to us!!
    protected final Set<ClickableKeybinding> keybindings = new HashSet();

    public static Set<ClickableKeybinding> getKeybindings() {
        return INSTANCE.keybindings;
    }

    public static KeyBinding addKeybinding(String keybindDescription, int keycode, Point2D position) {
        KeyBinding kb = new KeyBinding(keybindDescription, keycode, KeybindKeyHandler.mps);
//        boolean free = !KeyBinding.HASH.containsItem(keycode);
        boolean free = !keyBindingHelper.keyBindingHasKey(keycode);
        INSTANCE.keybindings.add(new ClickableKeybinding(kb, position, free, false));
        return kb;
    }

    public static String parseName(KeyBinding keybind) {
        if (keybind.getKeyCode() < 0) {
            return "Mouse" + (keybind.getKeyCode() + 100);
        } else {
            return Keyboard.getKeyName(keybind.getKeyCode());
        }
    }

    public static void writeOutKeybinds() {
        BufferedWriter writer = null;
        try {
            File file = new File(Loader.instance().getConfigDir() + "/machinemuse/", "modularpowerarmor-keybinds.cfg");
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            List<IPowerModule> modulesToWrite = ModuleManager.INSTANCE.getPlayerInstalledModules(Minecraft.getMinecraft().player);
            for (ClickableKeybinding keybinding : INSTANCE.keybindings) {
                writer.write(keybinding.getKeyBinding().getKeyCode() + ":" + keybinding.getPosition().getX() + ':' + keybinding.getPosition().getY() + ':' + keybinding.displayOnHUD + ':' + keybinding.toggleval + '\n');
                for (ClickableModule module : keybinding.getBoundModules()) {
                    writer.write(module.getModule().getDataName() + '~' + module.getPosition().getX() + '~' + module.getPosition().getY() + '\n');
                }
            }
        } catch (Exception e) {
            MPALibLogger.logError("Problem writing out keyconfig :(");
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
            File file = new File(Loader.instance().getConfigDir() + "/machinemuse/", "modularpowerarmor-keybinds.cfg");
            if (!file.exists()) {
                MPALibLogger.logError("No modularpowerarmor keybind file found.");
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
                        Point2D position = new Point2D(Double.parseDouble(exploded[1]), Double.parseDouble(exploded[2]));
                        boolean free = !keyBindingHelper.keyBindingHasKey(id);
                        boolean displayOnHUD = false;
                        boolean toggleval = false;
                        if (exploded.length > 3) {
                            displayOnHUD = Boolean.parseBoolean(exploded[3]);
                        }
                        if (exploded.length > 4) {
                            toggleval = Boolean.parseBoolean(exploded[4]);
                        }
                        workingKeybinding = new ClickableKeybinding(new KeyBinding(Keyboard.getKeyName(id), id, KeybindKeyHandler.mps), position, free, displayOnHUD);
                        workingKeybinding.toggleval = toggleval;
                        INSTANCE.keybindings.add(workingKeybinding);
                    } else {
                        workingKeybinding = null;
                    }

                } else if (line.contains("~") && workingKeybinding != null) {
                    String[] exploded = line.split("~");
                    Point2D position = new Point2D(Double.parseDouble(exploded[1]), Double.parseDouble(exploded[2]));
                    IPowerModule module = ModuleManager.INSTANCE.getModule(exploded[0]);
                    if (module != null) {
                        ClickableModule cmodule = new ClickableModule(module, position);
                        workingKeybinding.bindModule(cmodule);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            MPALibLogger.logError("Problem reading in keyconfig :(");
            e.printStackTrace();
        }
    }
}