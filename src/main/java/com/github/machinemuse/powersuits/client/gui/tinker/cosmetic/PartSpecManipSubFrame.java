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

package com.github.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.IHandHeldModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.client.gui.GuiIcons;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.render.RenderState;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.render.modelspec.*;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.CosmeticInfoPacket;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


/**
 * Author: MachineMuse (Claire Semple)
 * Created: 1:46 AM, 30/04/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
@SideOnly(Side.CLIENT)
public class PartSpecManipSubFrame {
    public SpecBase model;
    public ColourPickerFrame colourframe;
    public ItemSelectionFrame itemSelector;
    public RelativeRect border;
    public List<PartSpecBase> partSpecs;
    public boolean open;

    public PartSpecManipSubFrame(SpecBase model, ColourPickerFrame colourframe, ItemSelectionFrame itemSelector, RelativeRect border) {
        this.model = model;
        this.colourframe = colourframe;
        this.itemSelector = itemSelector;
        this.border = border;
        this.partSpecs = this.getPartSpecs();
        this.open = true;
    }

    /**
     * get all valid parts of model for the equipment itemSlot
     * Don't bother converting to Java stream with filter, the results are several times slower
     */
    private List<PartSpecBase> getPartSpecs() {
        List<PartSpecBase> specsArray = new ArrayList<>();
        Iterator<PartSpecBase> specIt = model.getPartSpecs().iterator();

        if (getSelectedItem() != null) {
            Optional.ofNullable(getSelectedItem().getItem().getCapability(ModelSpecNBTCapability.RENDER, null)).ifPresent(specNBT ->{
                PartSpecBase spec;

                while (specIt.hasNext()) {
                    spec = specIt.next();

                    // this COULD fail here if the wrong capability is applied, otherwise should be fine.
                    if (specNBT instanceof IArmorModelSpecNBT) {
                        EntityEquipmentSlot slot = EntityMob.getSlotForItemStack(getSelectedItem().getItem());
                        if (spec.getBinding().getSlot() == slot) {
                            specsArray.add(spec);
                        }
                    } else if (specNBT instanceof IHandHeldModelSpecNBT) {
                        if (spec.getBinding().getSlot().getSlotType().equals(EntityEquipmentSlot.Type.HAND)) {
                            specsArray.add(spec);
                        }
                    }
                }
            });
        }
        return specsArray;
    }

    public ClickableItem getSelectedItem() {
        return this.itemSelector.getSelectedItem();
    }

    @Nullable
    public NBTTagCompound getOrDontGetSpecTag(PartSpecBase partSpec) {
        if (this.getSelectedItem() == null) {
            return null;
        }

        return Optional.ofNullable(getSelectedItem().getItem().getCapability(ModelSpecNBTCapability.RENDER, null)).map(specNBT->{
            NBTTagCompound renderTag = specNBT.getRenderTag();
            NBTTagCompound specTag = null;

            if (renderTag != null && !renderTag.isEmpty()) {
                // there can be many ModelPartSpecs
                if (partSpec instanceof ModelPartSpec) {
                    String name = ModelRegistry.getInstance().makeName(partSpec);
                    specTag = (renderTag.hasKey(name) ? renderTag.getCompoundTag(name) : null);
                }
                // Only one TexturePartSpec is allowed at a time, so figure out if this one is enabled
                if (partSpec instanceof TexturePartSpec && renderTag.hasKey(MPALIbConstants.NBT_TEXTURESPEC_TAG)) {
                    NBTTagCompound texSpecTag = renderTag.getCompoundTag(MPALIbConstants.NBT_TEXTURESPEC_TAG);
                    if (partSpec.spec.getOwnName().equals(texSpecTag.getString(MPALIbConstants.TAG_MODEL))) {
                        specTag = renderTag.getCompoundTag(MPALIbConstants.NBT_TEXTURESPEC_TAG);
                    }
                }
            }
            return specTag;
        }).orElse(null);
    }

    public NBTTagCompound getOrMakeSpecTag(PartSpecBase partSpec) {
        String name;
        NBTTagCompound nbt = getSpecTag(partSpec);
        if (nbt.isEmpty()) {
            if (partSpec instanceof ModelPartSpec) {
                name = ModelRegistry.getInstance().makeName(partSpec);
                ((ModelPartSpec) partSpec).multiSet(nbt, null, null);
            } else {
                name = MPALIbConstants.NBT_TEXTURESPEC_TAG;
                partSpec.multiSet(nbt, null);
            }

            // update the render tag client side. The server side update is called below.
            if (getSelectedItem() != null) {
                Optional.ofNullable(this.getSelectedItem().getItem().getCapability(ModelSpecNBTCapability.RENDER, null)).ifPresent(specNBT->{
                    NBTTagCompound renderTag  = specNBT.getRenderTag();
                    if (renderTag != null && !renderTag.isEmpty()) {
                        renderTag.setTag(name, nbt);
                        specNBT.setRenderTag(renderTag, MPALIbConstants.TAG_RENDER);
                    }
                });
            }
        }
        return nbt;
    }

    public void updateItems() {
        this.partSpecs = getPartSpecs();
        this.border.setHeight((partSpecs.size() > 0) ? (partSpecs.size() * 8 + 10) : 0);
    }

    public void drawPartial(double min, double max) {
        if (partSpecs.size() > 0) {
            Renderer.drawString(model.getDisaplayName(), border.left() + 8, border.top());
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
            NBTTagCompound tagdata = getOrDontGetSpecTag(spec);

            if (tagdata != null) {
                int oldindex = spec.getColourIndex(tagdata);
                if (oldindex >= index && oldindex > 0) {
                    spec.setColourIndex(tagdata, oldindex - 1);
                    MPALibPackets.sendToServer(new CosmeticInfoPacket(getSelectedItem().inventorySlot, tagname, tagdata));
                }
            }
        }
    }

    public NBTTagCompound getSpecTag(PartSpecBase partSpec) {
        NBTTagCompound nbt = getOrDontGetSpecTag(partSpec);
        return nbt != null ? nbt : new NBTTagCompound();
    }

    public void drawSpecPartial(double x, double y, PartSpecBase partSpec, double ymino, double ymaxo) {
        NBTTagCompound  tag = this.getSpecTag(partSpec);
        int selcomp = tag.isEmpty() ? 0 : (partSpec instanceof ModelPartSpec && ((ModelPartSpec) partSpec).getGlow(tag) ? 2 : 1);
        int selcolour = partSpec.getColourIndex(tag);
        new GuiIcons.TransparentArmor(x, y, null, null, ymino, null, ymaxo);
        new GuiIcons.NormalArmor(x + 8, y, null, null, ymino, null, ymaxo);

        if (partSpec instanceof ModelPartSpec) {
            new GuiIcons.GlowArmor(x + 16, y, null, null, ymino, null, ymaxo);
        }

        new GuiIcons.SelectedArmorOverlay(x + selcomp * 8, y, null, null, ymino, null, ymaxo);

        double acc = (x + 28);
        for (int colour : colourframe.colours()) {
            new GuiIcons.ArmourColourPatch(acc, y, new Colour(colour), null, ymino, null, ymaxo);
            acc += 8;
        }
        double textstartx = acc;

        if (selcomp > 0) {
            new GuiIcons.SelectedArmorOverlay(x + 28 + selcolour * 8, y, null, null, ymino, null, ymaxo);
        }

        Renderer.drawString(I18n.format(partSpec.getDisaplayName()), textstartx + 4, y);
    }

    public void drawOpenArrow(double min, double max) {
        RenderState.texturelessOn();
        Colour.LIGHTBLUE.doGL();
        GL11.glBegin(4);
        if (this.open) {
            GL11.glVertex2d(this.border.left() + 3, MathUtils.clampDouble(this.border.top() + 3, min, max));
            GL11.glVertex2d(this.border.left() + 5, MathUtils.clampDouble(this.border.top() + 7, min, max));
            GL11.glVertex2d(this.border.left() + 7, MathUtils.clampDouble(this.border.top() + 3, min, max));
        } else {
            GL11.glVertex2d(this.border.left() + 3, MathUtils.clampDouble(this.border.top() + 3, min, max));
            GL11.glVertex2d(this.border.left() + 3, MathUtils.clampDouble(this.border.top() + 7, min, max));
            GL11.glVertex2d(this.border.left() + 7, MathUtils.clampDouble(this.border.top() + 5, min, max));
        }
        GL11.glEnd();
        Colour.WHITE.doGL();
        RenderState.texturelessOff();
    }

    public Rect getBorder() {
        border.setHeight(this.open ? (9 + 9 * partSpecs.size()) : 9.0);
        return this.border;
    }

    public boolean tryMouseClick(double x, double y) {
        NBTTagCompound tagdata;
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
            MPALibLogger.logger.debug("Line " + lineNumber + " Column " + columnNumber);

            switch (columnNumber) {
                // removes the associated tag from the render tag making the part not isEnabled
                case 0: {
                    tagname = spec instanceof TexturePartSpec ? MPALIbConstants.NBT_TEXTURESPEC_TAG : ModelRegistry.getInstance().makeName(spec);
                    MPALibPackets.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, new NBTTagCompound()));

                    this.updateItems();
                    return true;
                }

                // set part to isEnabled
                case 1: {
                    tagname = spec instanceof TexturePartSpec ? MPALIbConstants.NBT_TEXTURESPEC_TAG : ModelRegistry.getInstance().makeName(spec);
                    tagdata = this.getOrMakeSpecTag(spec);
                    if (spec instanceof ModelPartSpec) {
                        ((ModelPartSpec) spec).setGlow(tagdata, false);
                    }
                    MPALibPackets.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, tagdata));

                    this.updateItems();
                    return true;
                }

                // glow on
                case 2: {
                    if (spec instanceof ModelPartSpec) {
                        tagname = ModelRegistry.getInstance().makeName(spec);
                        tagdata = this.getOrMakeSpecTag(spec);
                        ((ModelPartSpec) spec).setGlow(tagdata, true);
                        MPALibPackets.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, tagdata));
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
            tagname = spec instanceof TexturePartSpec ? MPALIbConstants.NBT_TEXTURESPEC_TAG : ModelRegistry.getInstance().makeName(spec);
            tagdata = this.getOrMakeSpecTag(spec);
            spec.setColourIndex(tagdata, columnNumber);
            MPALibPackets.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, tagdata));
            return true;
        }
        return false;
    }
}