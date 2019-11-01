package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableLabel;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.google.common.collect.BiMap;
import net.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.machinemuse.powersuits.client.render.modelspec.DefaultModelSpec;
import net.machinemuse.powersuits.common.config.CosmeticPresetSaveLoad;
import net.machinemuse.powersuits.common.config.MPSConfig;
import net.machinemuse.powersuits.item.armor.ItemPowerArmor;
import net.machinemuse.powersuits.item.tool.ItemPowerFist;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.CosmeticInfoPacket;
import net.machinemuse.powersuits.network.packets.CosmeticPresetPacket;
import net.machinemuse.powersuits.network.packets.CosmeticPresetUpdatePacket;
import net.machinemuse.powersuits.utils.nbt.MPSNBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class LoadSaveResetSubFrame implements IGuiFrame {
    EntityPlayer player;
    public ItemSelectionFrame itemSelector;
    public DrawableRect border;
    protected ClickableButton loadButton;
    protected ClickableButton saveButton;
    protected ClickableButton resetButton;
    ColourPickerFrame colourpicker;
    ScrollableLabel saveAsLabel;
    final boolean usingCosmeticPresets;
    final boolean allowCosmeticPresetCreation;

    final double originalBottom;
    final double originalTop;
    final double originalHeight;
    final double newHeight;
    protected PartManipContainer partframe;
    protected CosmeticPresetContainer cosmeticFrame;
    protected boolean isEditing;
//    protected Map<Integer, String> lastCosmeticPresets;

    GuiTextField presetNameInputBox;

    public LoadSaveResetSubFrame(ColourPickerFrame colourpicker, EntityPlayer player, Rect borderRef, Colour insideColour, Colour borderColour, ItemSelectionFrame itemSelector, boolean usingCosmeticPresetsIn, boolean allowCosmeticPresetCreationIn, PartManipContainer partframe, CosmeticPresetContainer cosmeticFrame) {
        this.player = player;
        this.border = new DrawableRect(borderRef, insideColour, borderColour);
        this.originalTop = border.top();
        this.originalHeight = border.height();
        this.originalBottom = border.bottom();
        this.newHeight = (Math.abs(colourpicker.getBorder().top() - originalBottom));
        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();
        this.itemSelector = itemSelector;
        this.colourpicker = colourpicker;
        this.saveAsLabel = new ScrollableLabel(I18n.format("gui.powersuits.saveAs"),  new RelativeRect(border.left(), colourpicker.getBorder().top() + 20, border.right(), colourpicker.getBorder().top() + 30)).setEnabled(false);
        presetNameInputBox = new GuiTextField(0, Renderer.getFontRenderer(), (int) (border.left()) + 2, (int) (saveAsLabel.bottom() + 10), (int) border.width() - 4, 20);

        this.loadButton = new ClickableButton(
                I18n.format("gui.powersuits.load"),
                new Point2D(border.left() + sizex * 2.5 / 12.0, border.bottom() - sizey / 2.0), true);
        this.saveButton = new ClickableButton(
                I18n.format("gui.powersuits.save"),
                new Point2D(border.right() - sizex * 2.5 / 12.0, border.bottom() - sizey / 2.0), true);

        this.resetButton = new ClickableButton(
                I18n.format("gui.powersuits.reset"),
                new Point2D(border.left() + sizex / 2.0, border.bottom() - sizey / 2.0), true);

        textInputOff();
        presetNameInputBox.setMaxStringLength((int) border.width());
        presetNameInputBox.setText("");

        this.usingCosmeticPresets = usingCosmeticPresetsIn;
        this.allowCosmeticPresetCreation = allowCosmeticPresetCreationIn;

        this.partframe = partframe;
        this.cosmeticFrame = cosmeticFrame;
        this.isEditing = false;

        if (usingCosmeticPresets) {
            if (allowCosmeticPresetCreation)
                cosmeticPresetCreator ();
            else
                cosmeticPresetsNormal();
        } else
            setLegacyMode();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.border.setTargetDimensions(left, top, right, bottom);
    }

    /**
     * settings for the classic style cosmetic configuration
     */
    void setLegacyMode() {
        saveButton.disableAndHide();
        loadButton.disableAndHide();
        showPartManipFrame();
        colourPickerSetOpen();
    }

    /**
     * settings for cosmetic preset mode (normal user)
     */
    void cosmeticPresetsNormal() {
        saveButton.disableAndHide();
        loadButton.disableAndHide();
        colourpickerSetClosed();
        textInputOff();
        showPresetFrame();
    }

    /**
     * settings for cosmetic preset mode (creator)
     */
    void cosmeticPresetCreator () {
        if (isEditing) {
            loadButton.enableAndShow();
            loadButton.setLable(I18n.format("gui.powersuits.cancel"));
            saveButton.enableAndShow();
            saveButton.setLable(I18n.format("gui.powersuits.save"));
            showPartManipFrame();
            // save as dialog
            if (presetNameInputBox.getVisible()) {
                colourpickerSetClosed();
            } else {
                colourPickerSetOpen();
            }
        } else {
            textInputOff();
            showPresetFrame();
            colourpickerSetClosed();
            loadButton.disableAndHide();
            saveButton.enableAndShow();
            saveButton.setLable(I18n.format("gui.powersuits.new"));
        }
    }

    void colourPickerSetOpen() {
        this.border.setTop(originalTop).setHeight(originalHeight);
        colourpicker.frameOn();
        saveAsLabel.setEnabled(false);
    }

    void colourpickerSetClosed() {
        this.border.setTop(colourpicker.getBorder().top()).setHeight(newHeight);
        colourpicker.frameOff();
    }

    void textInputOn () {
        presetNameInputBox.setEnabled(true);
        presetNameInputBox.setVisible(true);
        presetNameInputBox.setFocused(true);
        saveAsLabel.setEnabled(true);
    }

    void textInputOff() {
        presetNameInputBox.setEnabled(false);
        presetNameInputBox.setVisible(false);
        presetNameInputBox.setFocused(false);
        saveAsLabel.setEnabled(false);
    }

    void showPresetFrame() {
        cosmeticFrame.frameOn();
        partframe.frameOff();
    }

    void showPartManipFrame() {
        cosmeticFrame.frameOff();
        partframe.frameOn();
    }

    /**
     * Get's the equipment slot the item is for.
     */
    EntityEquipmentSlot getEquipmentSlot() {
        ItemStack selectedItem = getSelectedItem().getItem();
        if (selectedItem != null && selectedItem.getItem() instanceof ItemPowerArmor) {
            return ((ItemPowerArmor) selectedItem.getItem()).armorType;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        ItemStack heldItem = player.getHeldItemOffhand();

        if (!heldItem.isEmpty() && Objects.equals(selectedItem, heldItem) /*ItemUtils.stackEqualExact(selectedItem, heldItem)*/) {
            return EntityEquipmentSlot.OFFHAND;
        }
        return EntityEquipmentSlot.MAINHAND;
    }

    void checkAndFixItem(ClickableItem clickie) {
        if (clickie != null) {
            ItemStack itemStack = clickie.getItem();
            NBTTagCompound itemNBT = NBTUtils.getMuseItemTag(itemStack);
            if (itemNBT.hasKey(MPALIbConstants.TAG_RENDER,Constants.NBT.TAG_COMPOUND)) {
                BiMap<String, NBTTagCompound> presetMap = MPSConfig.INSTANCE.getCosmeticPresets(itemStack);
                if (presetMap.containsValue(itemNBT.getCompoundTag(MPALIbConstants.TAG_RENDER))) {
                    String name = presetMap.inverse().get(itemNBT.getCompoundTag(MPALIbConstants.TAG_RENDER));
                    MPSPackets.sendToServer(new CosmeticPresetPacket(clickie.inventorySlot, name));
                } else
                    MPSPackets.sendToServer(new CosmeticPresetPacket(clickie.inventorySlot, "Default"));
            }
        }
    }

    /**
     * This is just for resetting the cosmetic tag to a preset and is called when either
     * switching to a new tab or when exiting the GUI altogether
     */
    public void onGuiClosed() {
//        System.out.println("creator gui closed and was editing: " + isEditing);
        if (allowCosmeticPresetCreation && isEditing) {
            for (ClickableItem clickie : itemSelector.itemButtons) {
                checkAndFixItem(clickie);
            }
        }
    }

    @Override
    public void update(double mousex, double mousey) {
        if (usingCosmeticPresets ||
                (!MPSConfig.INSTANCE.allowPowerFistCustomization() &&
                        itemSelector.getSelectedItem() != null && getSelectedItem().getItem().getItem() instanceof ItemPowerFist)) {
            // normal preset user
            if (allowCosmeticPresetCreation)
                cosmeticPresetCreator();
            else
                cosmeticPresetsNormal();
        } else
            setLegacyMode();
    }

    NBTTagCompound getDefaultPreset(@Nonnull ItemStack itemStack) {
        return MPSConfig.INSTANCE.getPresetNBTFor(itemStack, "Default");
    }

    public boolean isValidItem(ClickableItem clickie, EntityEquipmentSlot slot) {
        if (clickie != null) {
            if (clickie.getItem().getItem() instanceof ItemPowerArmor)
                return clickie.getItem().getItem().isValidArmor(clickie.getItem(), slot, Minecraft.getMinecraft().player);
            else if (clickie.getItem().getItem() instanceof ItemPowerFist && slot.getSlotType().equals(EntityEquipmentSlot.Type.HAND))
                return true;
        }
        return false;
    }

    public ClickableItem getSelectedItem() {
        return this.itemSelector.getSelectedItem();
    }

    @Override
    public void onMouseDown(double x, double y, int button) {
        if (itemSelector.getSelectedItem() == null || itemSelector.getSelectedItem().getItem().isEmpty())
            return;

        if (usingCosmeticPresets ||
                (!MPSConfig.INSTANCE.allowPowerFistCustomization() &&
                        getSelectedItem() != null && getSelectedItem().getItem().getItem() instanceof ItemPowerFist)) {
            if (allowCosmeticPresetCreation) {
                if (isEditing) {
                    // todo: insert check for new item selected and save tag for previous item selected

//                    if (itemSelector.getLastItemSlot() != -1 && itemSelector.selectedItemStack != itemSelector.getLastItemSlot()) {
//
//                        System.out.println("previous item index: " + itemSelector.getSelectedItemSlot());
//                        System.out.println("current item index: " + itemSelector.getSelectedItemSlot());
//
//                        System.out.println("this is where we would save the cosmetic preset tag for the previous item:");
//                    }

                    if (saveButton.hitBox(x, y)) {
                        // save as dialog is open
                        if (presetNameInputBox.getVisible()) {
                            String name = presetNameInputBox.getText();
                            ItemStack itemStack = getSelectedItem().getItem();

                            boolean save = CosmeticPresetSaveLoad.savePreset(name, itemStack);
                            if (save) {
                                if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                                    // get the render tag for the item
                                    NBTTagCompound nbt = MPSNBTUtils.getMuseRenderTag(itemStack).copy();
                                    MPSPackets.sendToServer(new CosmeticPresetUpdatePacket(itemStack.getItem().getRegistryName(), name, nbt));
                                }
                            }
                        } else {
                            // enabling here opens save dialog in update loop
                            textInputOn();
                        }

                        // reset tag to cosmetic copy of cosmetic preset default as legacy tag for editing.
                    } else if (resetButton.hitBox(x, y)) {
                        if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                            NBTTagCompound nbt = getDefaultPreset(itemSelector.getSelectedItem().getItem());
                            MPSPackets.sendToServer(new CosmeticInfoPacket(itemSelector.getSelectedItem().inventorySlot, MPALIbConstants.TAG_RENDER, nbt));
                        }
                        // cancel creation
                    } else if (loadButton.hitBox(x, y)) {
                        if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                            MPSPackets.sendToServer(new CosmeticPresetPacket(this.getSelectedItem().inventorySlot, "Default"));
                        }
                        isEditing = false;
                    }
                } else {
                    if (saveButton.hitBox(x, y)) {
                        if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                            isEditing = true;
                            NBTTagCompound nbt = MPSNBTUtils.getMuseRenderTag(getSelectedItem().getItem(), getEquipmentSlot());
                            MPSPackets.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, MPALIbConstants.TAG_RENDER, nbt));
                        }
                    } else if (resetButton.hitBox(x, y)) {
                        if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                            MPSPackets.sendToServer(new CosmeticPresetPacket(this.getSelectedItem().inventorySlot, "Default"));
                        }
                    }
                }
            } else {
                if (resetButton.hitBox(x, y)) {
                    if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                        MPSPackets.sendToServer(new CosmeticPresetPacket(this.getSelectedItem().inventorySlot, "Default"));
                    }
                }
            }
            // legacy mode
        } else {
            if (resetButton.hitBox(x, y)) {
                if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                    NBTTagCompound nbt = DefaultModelSpec.makeModelPrefs(itemSelector.getSelectedItem().getItem());
                    MPSPackets.sendToServer(new CosmeticInfoPacket(itemSelector.getSelectedItem().inventorySlot, MPALIbConstants.TAG_RENDER, nbt));
                }
            }
        }
    }

    @Override
    public void onMouseUp(double x, double y, int button) {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        border.draw();
        loadButton.render(mouseX, mouseY, partialTicks);
        saveButton.render(mouseX, mouseY, partialTicks);
        resetButton.render(mouseX, mouseY, partialTicks);
        saveAsLabel.render(mouseX, mouseY, partialTicks);
        presetNameInputBox.drawTextBox();
    }

    private static boolean isValidCharacterForName(char typedChar, int keyCode) {
        if (keyCode == 14 || // backspace;
                keyCode == 12 || // - ; 147 is _; 52 is .
                keyCode == 147 || //
                Character.isDigit(typedChar) ||
                Character.isLetter(typedChar ) ||
                Character.isSpaceChar(typedChar))
            return true;
        return false;
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (this.presetNameInputBox.getVisible() && isValidCharacterForName(typedChar, keyCode)) {
            this.presetNameInputBox.textboxKeyTyped(typedChar, keyCode);
        }
    }
}