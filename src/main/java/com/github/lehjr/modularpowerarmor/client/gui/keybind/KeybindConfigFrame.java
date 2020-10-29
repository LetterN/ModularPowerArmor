package com.github.lehjr.modularpowerarmor.client.gui.keybind;

import com.github.lehjr.modularpowerarmor.client.control.KeybindKeyHandler;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.mpalib.util.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.util.client.gui.clickable.IClickable;
import com.github.lehjr.mpalib.util.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.util.client.gui.geometry.GradientAndArcCalculator;
import com.github.lehjr.mpalib.util.client.gui.geometry.IRect;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.control.KeyBindingHelper;
import com.github.lehjr.mpalib.util.client.render.MPALibRenderer;
import com.github.lehjr.mpalib.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

import static com.github.lehjr.mpalib.util.math.MathUtils.clampFloat;

public class KeybindConfigFrame implements IGuiFrame {
    private static KeyBindingHelper keyBindingHelper = new KeyBindingHelper();
    protected Set<ClickableModule> modules;
    protected IClickable selectedClickie;
    protected ClickableKeybinding closestKeybind;
    protected PlayerEntity player;
    protected boolean selecting;
    protected ClickableButton newKeybindButton;
    protected ClickableButton trashKeybindButton;
    protected long takenTime;
    KeybindManager keybindManager = KeybindManager.INSTANCE;

    Rect rect;

    public KeybindConfigFrame(Rect backgroundRect, PlayerEntity player) {
        modules = new HashSet();
        for (ClickableKeybinding kb : keybindManager.getKeybindings()) {
            modules.addAll(kb.getBoundModules());
        }
        this.rect = backgroundRect;
        this.player = player;
        Point2D center = rect.center();
        newKeybindButton = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.newKeybind"), center.plus(new Point2D(0, -8)), true);
        trashKeybindButton = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.trashKeybind"), center.plus(new Point2D(0, 8)), true);
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        newKeybindButton.setPosition(rect.center().plus(new Point2D(0, -8)));
        trashKeybindButton.setPosition(rect.center().plus(new Point2D(0, 8)));
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.rect.containsPoint(x, y)) {
            if (button == 0) {
                if (selectedClickie == null) {
                    for (ClickableModule module : modules) {
                        if (module.hitBox(x, y)) {
                            selectedClickie = module;
                            return true;
                        }
                    }
                    for (ClickableKeybinding keybind : keybindManager.getKeybindings()) {
                        if (keybind.hitBox(x, y)) {
                            selectedClickie = keybind;
                            return true;
                        }
                    }
                }
                if (newKeybindButton.hitBox(x, y)) {
                    selecting = true;
                }
            } else if (button == 1) {
                for (ClickableKeybinding keybind : keybindManager.getKeybindings()) {
                    if (keybind.hitBox(x, y)) {
                        keybind.toggleHUDState();
                        return true;
                    }
                }
            } else if (button > 2) {
                int key = button - 100;

                if (keyBindingHelper.keyBindingHasKey(key)) {
                    takenTime = System.currentTimeMillis();
                }
                if (!keyBindingHelper.keyBindingHasKey(key)) {
                    addKeybind(key, true);
                } else if (MPASettings.allowConfictingKeyBinds()) {
                    addKeybind(key, false);
                }
                selecting = false;
            }
            return true;
        }
        return false;
    }

    public void refreshModules() {
        NonNullList<ItemStack> installedModules = NonNullList.create();

        for (EquipmentSlotType slot: EquipmentSlotType.values()) {
            switch (slot.getSlotType()) {
                case HAND:
                    player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                            iModeChanging -> {
                                if (iModeChanging instanceof IModeChangingItem)
                                    installedModules.addAll(((IModularItem) iModeChanging).getInstalledModulesOfType(IToggleableModule.class));
                            });
                    break;

                case ARMOR:
                    if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                        player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                                iModularItem -> {
                                    if (iModularItem instanceof IModularItem)
                                        installedModules.addAll(((IModularItem) iModularItem).getInstalledModulesOfType(IToggleableModule.class));
                                });
                    }
            }
        }

        List<Point2D> points = GradientAndArcCalculator.pointsInLine(
                installedModules.size(),
                new Point2D(rect.finalLeft() + 10, rect.finalTop() + 10),
                new Point2D(rect.finalLeft() + 10, rect.finalBottom() - 10));
        Iterator<Point2D> pointIterator = points.iterator();
        for (ItemStack module : installedModules) {
            if (!alreadyAdded(module)) {
                ClickableModule clickie = new ClickableModule(module, pointIterator.next(), -1, EnumModuleCategory.NONE);
                modules.add(clickie);
            }
        }
    }

    public boolean alreadyAdded(@Nonnull ItemStack module) {
        if (module.isEmpty())
            return false;

        for (ClickableModule clickie : modules) {
            if (ItemStack.areItemsEqual(clickie.getModule(),module)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (button == 0) {
            if (selectedClickie != null && closestKeybind != null && selectedClickie instanceof ClickableModule) {
                closestKeybind.bindModule((ClickableModule) selectedClickie);
            } else if (selectedClickie != null && selectedClickie instanceof ClickableKeybinding && trashKeybindButton.hitBox((float) x, (float) y)) {
                KeyBinding binding = ((ClickableKeybinding) selectedClickie).getKeyBinding();
                keyBindingHelper.removeKey(binding);
//                KeyBinding.HASH.removeObject(binding.getKeyCode());
                keyBindingHelper.removeKey(binding);
                keybindManager.remove((ClickableKeybinding) selectedClickie);
            }
            selectedClickie = null;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        if (selecting) {
            return;
        }
        refreshModules();
        this.closestKeybind = null;
        double closestDistance = Double.MAX_VALUE;
        if (this.selectedClickie != null) {
            this.selectedClickie.setPosition(new Point2D(mousex, mousey));
            if (this.selectedClickie instanceof ClickableModule) {
                ClickableModule selectedModule = ((ClickableModule) this.selectedClickie);
                for (ClickableKeybinding keybind : keybindManager.getKeybindings()) {
                    double distance = keybind.getPosition().minus(selectedModule.getPosition()).distance();
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        if (closestDistance < 32) {
                            this.closestKeybind = keybind;
                        }
                    }
                }
            }
        }
        for (ClickableKeybinding keybind : keybindManager.getKeybindings()) {
            if (keybind != selectedClickie) {
                keybind.unbindFarModules();
            }
            keybind.attractBoundModules(selectedClickie);
        }
        for (IClickable module : modules) {
            if (module != selectedClickie) {
                repelOtherModules(module);
            }
        }
        for (IClickable keybind : keybindManager.getKeybindings()) {
            if (keybind != selectedClickie) {
                repelOtherModules(keybind);
            }
        }
        for (IClickable module : modules) {
            clampClickiePosition(module);
        }
        for (IClickable keybind : keybindManager.getKeybindings()) {
            clampClickiePosition(keybind);
        }
    }

    private void clampClickiePosition(IClickable clickie) {
        Point2D position = clickie.getPosition();
        position.setX(clampDouble(position.getX(), rect.finalLeft(), rect.finalRight()));
        position.setY(clampDouble(position.getY(), rect.finalTop(), rect.finalBottom()));
    }

    private double clampDouble(double x, double lower, double upper) {
        if (x < lower) {
            return lower;
        } else if (x > upper) {
            return upper;
        } else {
            return x;
        }
    }

    private void repelOtherModules(IClickable module) {
        Point2D modulePosition = module.getPosition();
        for (ClickableModule otherModule : modules) {
            if (otherModule != selectedClickie && otherModule != module && otherModule.getPosition().distanceTo(modulePosition) < 16) {
                Point2D euclideanDistance = otherModule.getPosition().minus(module.getPosition());
                Point2D directionVector = euclideanDistance.normalize();
                Point2D tangentTarget = directionVector.times(16).plus(module.getPosition());
                Point2D midpointTangent = otherModule.getPosition().midpoint(tangentTarget);
                if (midpointTangent.distanceTo(module.getPosition()) > 2) {
                    otherModule.setPosition(midpointTangent.copy());
                }
                 Point2D away = directionVector.times(0).plus(modulePosition);
                 module.setPosition(away.copy());
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // FIXME!!
        float zLevel = Minecraft.getInstance().currentScreen.getBlitOffset();

        Point2D center = rect.center();

        if (selecting) {
            MPALibRenderer.drawCenteredString(matrixStack, I18n.format("gui.modularpowerarmor.pressKey"), center.getX(), center.getY());
            return;
        }
        newKeybindButton.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
        trashKeybindButton.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);

        MPALibRenderer.drawCenteredString(matrixStack, I18n.format("gui.modularpowerarmor.keybindInstructions1"), center.getX(), center.getY() + 40);
        MPALibRenderer.drawCenteredString(matrixStack, I18n.format("gui.modularpowerarmor.keybindInstructions2"), center.getX(), center.getY() + 50);
        MPALibRenderer.drawCenteredString(matrixStack, I18n.format("gui.modularpowerarmor.keybindInstructions3"), center.getX(), center.getY() + 60);
        MPALibRenderer.drawCenteredString(matrixStack, I18n.format("gui.modularpowerarmor.keybindInstructions4"), center.getX(), center.getY() + 70);
        if (takenTime + 1000 > System.currentTimeMillis()) {
            Point2D pos = newKeybindButton.getPosition().plus(new Point2D(0, -20));
            MPALibRenderer.drawCenteredString(matrixStack, I18n.format("gui.modularpowerarmor.keybindTaken"), pos.getX(), pos.getY());
        }
        for (ClickableModule module : modules) {
            module.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
        }
        for (ClickableKeybinding keybind : keybindManager.getKeybindings()) {
            keybind.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
        }
        if (selectedClickie != null && closestKeybind != null) {
            MPALibRenderer.drawLineBetween(selectedClickie, closestKeybind, Colour.YELLOW, zLevel);
        }
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        for (ClickableModule module : modules) {
            if (module.hitBox(x, y)) {
                if (doAdditionalInfo()) {
                    return module.getToolTip();
                }
                return Collections.singletonList(module.getLocalizedName());
            }
        }
        return null;
    }

    public static boolean doAdditionalInfo() {
        return false; //InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT);
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

        int key = p_keyPressed_1_; // no idea which one to use here!!

        if (selecting) {
//                if (KeyBinding.HASH.containsItem(key)) {
            if (keyBindingHelper.keyBindingHasKey(key)) {
                takenTime = System.currentTimeMillis();
            }
//                if (!KeyBinding.HASH.containsItem(key)) {
            if (!keyBindingHelper.keyBindingHasKey(key)) {
                addKeybind(key, true);
            } else if (MPASettings.allowConfictingKeyBinds()) {
                addKeybind(key, false);
            }
            selecting = false;
        }

        return true; // no idea what to return here!!!
    }

    private void addKeybind(int key, boolean free) {
        addKeybind(KeyBindingHelper.getInputByCode(key), free);
    }

    private void addKeybind(InputMappings.Input key, boolean free) {
        String name;
        try {
            name = key.getTranslationKey();
        } catch (Exception e) {
            name = "???";
        }
        KeyBinding keybind = new KeyBinding(name, key.getKeyCode(), KeybindKeyHandler.mpa);
        ClickableKeybinding clickie = new ClickableKeybinding(keybind, newKeybindButton.getPosition().plus(new Point2D(0, -20)), free, false);
        keybindManager.getKeybindings().add(clickie);
    }

    @Override
    public IRect getBorder() {
        return rect;
    }

    boolean enabled = true;
    @Override
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    boolean visible = true;
    @Override
    public void setVisible(boolean b) {
        visible = b;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public IRect setLeft(double v) {
        return rect.setLeft(v);
    }

    @Override
    public IRect setRight(double v) {
        return rect.setRight(v);
    }

    @Override
    public IRect setTop(double v) {
        return rect.setTop(v);
    }

    @Override
    public IRect setBottom(double v) {
        return rect.setBottom(v);
    }

    @Override
    public IRect setWidth(double v) {
        return rect.setWidth(v);
    }

    @Override
    public IRect setHeight(double v) {
        return rect.setHeight(v);
    }
}
