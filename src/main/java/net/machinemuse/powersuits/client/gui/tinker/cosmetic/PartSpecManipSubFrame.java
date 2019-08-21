package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.client.gui.GuiIcons;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.geometry.MuseRect;
import net.machinemuse.numina.client.gui.geometry.MuseRelativeRect;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.render.RenderState;
import net.machinemuse.numina.client.render.modelspec.*;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.powersuits.client.gui.tinker.common.ItemSelectionFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 1:46 AM, 30/04/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
public class PartSpecManipSubFrame {
    public SpecBase model;
    public ColourPickerFrame colourframe;
    public ItemSelectionFrame itemSelector;
    public MuseRelativeRect border;
    public List<PartSpecBase> partSpecs;
    public boolean open;
    Minecraft minecraft;

    public PartSpecManipSubFrame(SpecBase model, ColourPickerFrame colourframe, ItemSelectionFrame itemSelector, MuseRelativeRect border) {
        this.model = model;
        this.colourframe = colourframe;
        this.itemSelector = itemSelector;
        this.border = border;
        this.partSpecs = this.getPartSpecs();
        this.open = true;
        minecraft = Minecraft.getInstance();
    }

    /**
     * get all valid parts of model for the equipment itemSlot
     * Don't bother converting to Java stream with filter, the results are several times slower
     */
    private List<PartSpecBase> getPartSpecs() {
        List<PartSpecBase> specsArray = new ArrayList<>();
        Iterator<PartSpecBase> specIt = model.getPartSpecs().iterator();
        PartSpecBase spec;
        while (specIt.hasNext()) {
            spec = specIt.next();
            if (isValidItem(getSelectedItem(), spec.getBinding().getSlot()))
                specsArray.add(spec);
        }
        return specsArray;
    }

    // FIXME
    public boolean isValidItem(ClickableItem clickie, EquipmentSlotType slot) {
//        if (clickie != null) {
//            if (clickie.getItemStack().getItem() instanceof ItemPowerArmor)
//                return clickie.getItemStack().getItem().canEquip(clickie.getItemStack(), slot, minecraft.player);
//            else if (clickie.getItemStack().getItem() instanceof ItemPowerFist && slot.getSlotType().equals(EquipmentSlotType.Group.HAND))
//                return true;
//        }
        return false;
    }

    public ClickableItem getSelectedItem() {
        return this.itemSelector.getSelectedItem();
    }

    /**
     * Get's the equipment itemSlot the item is for.
     */
//    public EquipmentSlotType getEquipmentSlot() {
//        ItemStack selectedItem = getSelectedItem().getItemStack();
//        if (!selectedItem.isEmpty() && selectedItem.getItem() instanceof ItemPowerArmor)
//            return ((ItemPowerArmor) selectedItem.getItem()).getEquipmentSlot();
//
//        PlayerEntity player = minecraft.player;
//        ItemStack heldItem = player.getHeldItemOffhand();
//
//        if (!heldItem.isEmpty() && ItemStack.areItemsEqual(selectedItem, heldItem))
//            return EquipmentSlotType.OFFHAND;
//        return EquipmentSlotType.MAINHAND;
//    }

    // FIXME: capabilities
    public CompoundNBT getRenderTag() {
        return new CompoundNBT();
//        return MPSModelHelper.getMuseRenderTag(this.getSelectedItem().getItemStack(), this.getEquipmentSlot());
    }

    public CompoundNBT getItemTag() {
        return new CompoundNBT();
//        return MuseNBTUtils.getMuseItemTag(this.getSelectedItem().getItemStack());
    }

    @Nullable
    public CompoundNBT getOrDontGetSpecTag(PartSpecBase partSpec) {
        // there can be many ModelPartSpecs
        if (partSpec instanceof ModelPartSpec) {
            String name = ModelRegistry.getInstance().makeName(partSpec);
            return this.getRenderTag().contains(name) ? this.getRenderTag().getCompound(name) : null;
        }
        // Only one TexturePartSpec is allowed at a time, so figure out if this one is enabled
        if (partSpec instanceof TexturePartSpec && this.getRenderTag().contains(NuminaConstants.NBT_TEXTURESPEC_TAG)) {
            CompoundNBT texSpecTag = this.getRenderTag().getCompound(NuminaConstants.NBT_TEXTURESPEC_TAG);
            if (partSpec.spec.getOwnName().equals(texSpecTag.getString(NuminaConstants.TAG_MODEL))) {
                return getRenderTag().getCompound(NuminaConstants.NBT_TEXTURESPEC_TAG);
            }
        }
        // if no match found
        return null;
    }

    public CompoundNBT getSpecTag(PartSpecBase partSpec) {
        CompoundNBT nbt = getOrDontGetSpecTag(partSpec);
        return nbt != null ? nbt : new CompoundNBT();
    }

    public CompoundNBT getOrMakeSpecTag(PartSpecBase partSpec) {
        String name;
        CompoundNBT nbt = getSpecTag(partSpec);
        if (nbt.isEmpty()) {
            if (partSpec instanceof ModelPartSpec) {
                name = ModelRegistry.getInstance().makeName(partSpec);
                ((ModelPartSpec) partSpec).multiSet(nbt, null, null);
            } else {
                name = NuminaConstants.NBT_TEXTURESPEC_TAG;
                partSpec.multiSet(nbt, null);
            }
            this.getRenderTag().put(name, nbt);
        }
        return nbt;
    }

    public void updateItems() {
        this.partSpecs = getPartSpecs();
        this.border.setHeight((partSpecs.size() > 0) ? (partSpecs.size() * 8 + 10) : 0);
    }

    public void drawPartial(double min, double max) {
        if (partSpecs.size() > 0) {
            MuseRenderer.drawString(model.getDisaplayName(), border.left() + 8, border.top());
            drawOpenArrow(min, max);
            if (open) {
                int y = (int) (border.top() + 8);
                for (PartSpecBase spec : partSpecs) {
                    drawSpecPartial(border.left(), y, spec, min, max);
                    y += 8;
                }
            }
        }
    }

    public void decrAbove(int index) {
        for (PartSpecBase spec : partSpecs) {
            String tagname = ModelRegistry.getInstance().makeName(spec);
            ClientPlayerEntity player = minecraft.player;
            CompoundNBT tagdata = getOrDontGetSpecTag(spec);

            if (tagdata != null) {
                int oldindex = spec.getColourIndex(tagdata);
                if (oldindex >= index && oldindex > 0) {
                    spec.setColourIndex(tagdata, oldindex - 1);
//                    if (player.world.isRemote)
//                        MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticInfo(getSelectedItem().inventorySlot, tagname, tagdata));
//

                }
            }
        }
    }

    public void drawSpecPartial(double x, double y, PartSpecBase partSpec, double ymino, double ymaxo) {
        CompoundNBT tag = this.getSpecTag(partSpec);
        int selcomp = tag.isEmpty() ? 0 : (partSpec instanceof ModelPartSpec && ((ModelPartSpec) partSpec).getGlow(tag) ? 2 : 1);
        int selcolour = partSpec.getColourIndex(tag);
        new GuiIcons.TransparentArmor(x, y, null, null, ymino, null, ymaxo);
        new GuiIcons.NormalArmor(x + 8, y, null, null, ymino, null, ymaxo);

        if (partSpec instanceof ModelPartSpec)
            new GuiIcons.GlowArmor(x + 16, y, null, null, ymino, null, ymaxo);

        new GuiIcons.SelectedArmorOverlay(x + selcomp * 8, y, null, null, ymino, null, ymaxo);

        double acc = (x + 28);
        for (int colour : colourframe.colours()) {
            new GuiIcons.ArmourColourPatch(acc, y, new Colour(colour), null, ymino, null, ymaxo);
            acc += 8;
        }
        double textstartx = acc;

        if (selcomp > 0)
            new GuiIcons.SelectedArmorOverlay(x + 28 + selcolour * 8, y, null, null, ymino, null, ymaxo);

        MuseRenderer.drawString(partSpec.getDisaplayName(), textstartx + 4, y);
    }

    public void drawOpenArrow(double min, double max) {
        RenderState.texturelessOn();
        Colour.LIGHTBLUE.doGL();
        GL11.glBegin(4);
        if (this.open) {
            GL11.glVertex2d(this.border.left() + 3, MuseMathUtils.clampDouble(this.border.top() + 3, min, max));
            GL11.glVertex2d(this.border.left() + 5, MuseMathUtils.clampDouble(this.border.top() + 7, min, max));
            GL11.glVertex2d(this.border.left() + 7, MuseMathUtils.clampDouble(this.border.top() + 3, min, max));
        } else {
            GL11.glVertex2d(this.border.left() + 3, MuseMathUtils.clampDouble(this.border.top() + 3, min, max));
            GL11.glVertex2d(this.border.left() + 3, MuseMathUtils.clampDouble(this.border.top() + 7, min, max));
            GL11.glVertex2d(this.border.left() + 7, MuseMathUtils.clampDouble(this.border.top() + 5, min, max));
        }
        GL11.glEnd();
        Colour.WHITE.doGL();
        RenderState.texturelessOff();
    }

    public MuseRect getBorder() {
        if (this.open)
            border.setHeight(9 + 9 * partSpecs.size());
        else
            this.border.setHeight(9.0);
        return this.border;
    }

    public boolean tryMouseClick(double x, double y) {
        ClientPlayerEntity player = minecraft.player;
        CompoundNBT tagdata;
        String tagname;

        // player clicked outside the area
        if (x < this.border.left() || x > this.border.right() || y < this.border.top() || y > this.border.bottom())
            return false;

            // opens the list of partSpecs
        else if (x > this.border.left() + 2 && x < this.border.left() + 8 && y > this.border.top() + 2 && y < this.border.top() + 8) {
            this.open = !this.open;
            this.getBorder();
            return true;

            // player clicked one of the icons for off/on/glowOn
        } else if (x < this.border.left() + 24 && y > this.border.top() + 8) {
            int lineNumber = (int) ((y - this.border.top() - 8) / 8);
            int columnNumber = (int) ((x - this.border.left()) / 8);
            PartSpecBase spec = partSpecs.get(Math.max(Math.min(lineNumber, partSpecs.size() - 1), 0));
            MuseLogger.logger.debug("Line " + lineNumber + " Column " + columnNumber);

            switch (columnNumber) {
                // removes the associated tag from the render tag making the part not isEnabled
                case 0: {
                    if (spec instanceof TexturePartSpec)
                        tagname = NuminaConstants.NBT_TEXTURESPEC_TAG;
                    else
                        tagname = ModelRegistry.getInstance().makeName(spec);
//                    if (player.world.isRemote)
//                        MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticInfo(this.getSelectedItem().inventorySlot, tagname, new CompoundNBT()));
//
//

                    this.updateItems();
                    return true;
                }

                // set part to isEnabled
                case 1: {
                    if (spec instanceof TexturePartSpec)
                        tagname = NuminaConstants.NBT_TEXTURESPEC_TAG;
                    else
                        tagname = ModelRegistry.getInstance().makeName(spec);
                    tagdata = this.getOrMakeSpecTag(spec);
                    if (spec instanceof ModelPartSpec)
                        ((ModelPartSpec) spec).setGlow(tagdata, false);
//                    if (player.world.isRemote)
//                        MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticInfo(this.getSelectedItem().inventorySlot, tagname, tagdata));
//
//
                    this.updateItems();
                    return true;
                }

                // glow on
                case 2: {
                    if (spec instanceof ModelPartSpec) {
                        tagname = ModelRegistry.getInstance().makeName(spec);
                        tagdata = this.getOrMakeSpecTag(spec);
                        ((ModelPartSpec) spec).setGlow(tagdata, true);
//                        if (player.world.isRemote)
//                            MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticInfo(this.getSelectedItem().inventorySlot, tagname, tagdata));
//

                        this.updateItems();
                        return true;
                    }
                    return false;
                }
                default:
                    return false;
            }
        }
        // player clicked a color icon
        else if (x > this.border.left() + 28 && x < this.border.left() + 28 + this.colourframe.colours().length * 8) {
            int lineNumber = (int) ((y - this.border.top() - 8) / 8);
            int columnNumber = (int) ((x - this.border.left() - 28) / 8);
            PartSpecBase spec = partSpecs.get(Math.max(Math.min(lineNumber, partSpecs.size() - 1), 0));

            if (spec instanceof TexturePartSpec)
                tagname = NuminaConstants.NBT_TEXTURESPEC_TAG;
            else
                tagname = ModelRegistry.getInstance().makeName(spec);

            tagdata = this.getOrMakeSpecTag(spec);
            spec.setColourIndex(tagdata, columnNumber);
//            if (player.world.isRemote) {
//                MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticInfo(this.getSelectedItem().inventorySlot, tagname, tagdata));
//            }
            return true;
        }
        return false;
    }
}