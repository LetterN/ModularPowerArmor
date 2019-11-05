package com.github.lehjr.modularpowerarmor.client.gui.clickable;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.clickable.IClickable;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.string.StringUtils;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.network.MPSPackets;
import com.github.lehjr.modularpowerarmor.network.packets.ToggleRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Ported to Java by lehjr on 10/19/16.
 */
public class ClickableKeybinding extends ClickableButton {
    public boolean toggleval = false;
    public boolean displayOnHUD;
    protected List<ClickableModule> boundModules = new ArrayList<>();
    boolean toggled = false;
    KeyBinding keybind;


    public ClickableKeybinding(KeyBinding keybind, Point2D position, boolean free, Boolean displayOnHUD) {
        super(ClickableKeybinding.parseName(keybind), position, true);
        this.displayOnHUD = (displayOnHUD != null) ? displayOnHUD : false;
        this.keybind = keybind;
    }

    static String parseName(KeyBinding keybind) {
        if (keybind.getKeyCode() < 0) {
            return "Mouse" + (keybind.getKeyCode() + 100);
        } else {
            return Keyboard.getKeyName(keybind.getKeyCode());
        }
    }

    public void doToggleTick() {
        doToggleIf(keybind.isPressed());
    }

    public void doToggleIf(boolean value) {
        if (value && !toggled) {
            toggleModules();
            KeybindManager.writeOutKeybinds();
        }
        toggled = value;
    }

    public void toggleModules() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        for (ClickableModule module : boundModules) {
            String valstring = (toggleval) ? " on" : " off";
            if ((FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT) && MPSConfig.INSTANCE.toggleModuleSpam())) {
                player.sendMessage(new TextComponentString("Toggled " + module.getModule().getDataName() + valstring));
            }
            ModuleManager.INSTANCE.toggleModuleForPlayer(player, module.getModule().getDataName(), toggleval);
            MPSPackets.sendToServer(new ToggleRequestPacket(module.getModule().getDataName(), toggleval));
        }
        toggleval = !toggleval;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        for (ClickableModule module : boundModules) {
            Renderer.drawLineBetween(this, module, Colour.LIGHTBLUE);
            GL11.glPushMatrix();
            GL11.glScaled(0.5, 0.5, 0.5);
            if (displayOnHUD) {
                Renderer.drawString(StringUtils.wrapFormatTags("HUD", StringUtils.FormatCodes.BrightGreen), this.position.getX() * 2 + 6, this.position.getY() * 2 + 6);
            } else {
                Renderer.drawString(StringUtils.wrapFormatTags("x", StringUtils.FormatCodes.Red), this.position.getX() * 2 + 6, this.position.getY() * 2 + 6);
            }
            GL11.glPopMatrix();
        }
    }

    public KeyBinding getKeyBinding() {
        return keybind;
    }

    public List<ClickableModule> getBoundModules() {
        return boundModules;
    }

    public void bindModule(ClickableModule module) {
        if (!boundModules.contains(module)) {
            boundModules.add(module);
        }
    }

    public void unbindModule(ClickableModule module) {
        boundModules.remove(module);
    }

    public void unbindFarModules() {
        Iterator<ClickableModule> iterator = boundModules.iterator();
        ClickableModule module;
        while (iterator.hasNext()) {
            module = iterator.next();
            int maxDistance = getTargetDistance() * 2;
            double distanceSq = module.getPosition().distanceSq(this.getPosition());
            if (distanceSq > maxDistance * maxDistance) {
                iterator.remove();
            }
        }
    }

    public int getTargetDistance() {
        return (boundModules.size() > 6) ? (16 + (boundModules.size() - 6) * 3) : 16;
    }

    public void attractBoundModules(IClickable exception) {
        for (ClickableModule module : boundModules) {
            if (!module.equals(exception)) {
                Point2D euclideanDistance = module.getPosition().minus(this.getPosition());
                Point2D directionVector = euclideanDistance.normalize();
                Point2D tangentTarget = directionVector.times(getTargetDistance()).plus(this.getPosition());
                Point2D midpointTangent = module.getPosition().midpoint(tangentTarget);
                module.move(midpointTangent.getX(), midpointTangent.getY());
            }
        }
    }

    public boolean equals(ClickableKeybinding other) {
        return other.keybind.getKeyCode() == this.keybind.getKeyCode();
    }

    public void toggleHUDState() {
        displayOnHUD = !displayOnHUD;
    }
}
