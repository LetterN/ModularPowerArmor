package com.github.lehjr.modularpowerarmor.client.gui.clickable;

import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.ToggleRequestPacket;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.util.client.gui.clickable.IClickable;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.client.render.MPALibRenderer;
import com.github.lehjr.mpalib.util.math.Colour;
import com.github.lehjr.mpalib.util.string.StringUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

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

    static ITextComponent parseName(KeyBinding keybind) {
        if (keybind.getKey().getKeyCode() < 0) {
            return new TranslationTextComponent("Mouse" + (keybind.getKey().getKeyCode() + 100));
        } else {
            return new TranslationTextComponent(keybind.getKey().getTranslationKey());
        }
    }

    public void doToggleTick() {
        doToggleIf(keybind.isPressed());
    }

    public void doToggleIf(boolean value) {
        if (value && !toggled) {
            toggleModules();
            KeybindManager.INSTANCE.writeOutKeybinds();
        }
        toggled = value;
    }

    public void toggleModules() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        for (ClickableModule module : boundModules) {
            ResourceLocation registryName = module.getModule().getItem().getRegistryName();
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                player.inventory.getStackInSlot(i).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->{
                    if (handler instanceof IModularItem) {
                        ((IModularItem) handler).toggleModule(registryName, toggleval);
                    }
                });
            }
            MPALibPackets.CHANNEL_INSTANCE.sendToServer(new ToggleRequestPacket(registryName, toggleval));
        }
        toggleval = !toggleval;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, float zLevel) {
        super.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
        for (ClickableModule module : boundModules) {
            MPALibRenderer.drawLineBetween(this, module, Colour.LIGHT_BLUE, zLevel);
            RenderSystem.pushMatrix();
            RenderSystem.scaled(0.5, 0.5, 0.5);
            if (displayOnHUD) {
                MPALibRenderer.drawString(matrixStack, StringUtils.wrapFormatTags("HUD", StringUtils.FormatCodes.BrightGreen), this.position.getX() * 2 + 6, this.position.getY() * 2 + 6);
            } else {
                MPALibRenderer.drawString(matrixStack, StringUtils.wrapFormatTags("x", StringUtils.FormatCodes.Red), this.position.getX() * 2 + 6, this.position.getY() * 2 + 6);
            }
            RenderSystem.popMatrix();
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
                module.setPosition(midpointTangent.copy());
            }
        }
    }

    public boolean equals(ClickableKeybinding other) {
        return other.keybind.getKey().getKeyCode() == this.keybind.getKey().getKeyCode();
    }

    public void toggleHUDState() {
        displayOnHUD = !displayOnHUD;
    }
}