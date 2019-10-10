package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.render.ModelSpecNBTCapability;
import net.machinemuse.numina.client.gui.GuiIcons;
import net.machinemuse.numina.client.gui.clickable.ClickableLabel;
import net.machinemuse.numina.client.gui.clickable.ClickableSlider;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.geometry.MuseRelativeRect;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.gui.scrollable.ScrollableLabel;
import net.machinemuse.numina.client.gui.scrollable.ScrollableRectangle;
import net.machinemuse.numina.client.gui.scrollable.ScrollableSlider;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.MusePacketColourInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:19 AM, 03/05/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
public class ColourPickerFrame extends ScrollableFrame {
    public ItemSelectionFrame itemSelector;
    public ScrollableSlider rslider;
    public ScrollableSlider gslider;
    public ScrollableSlider bslider;
    public ScrollableSlider aslider;
    ScrollableColourBox colourBox;
    String COLOUR_PREFIX = I18n.format("gui.powersuits.colourPrefix");

    public ScrollableLabel colourLabel;

    public ScrollableSlider selectedSlider;
    public int selectedColour;
    public int decrAbove;
    ScrollableRectangle[] rectangles;

    public ColourPickerFrame(MusePoint2D topleft, MusePoint2D bottomright, Colour borderColour, Colour insideColour, ItemSelectionFrame itemSelector) {
        super(topleft, bottomright, borderColour, insideColour);
        this.itemSelector = itemSelector;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);

        if (itemSelector.hasNoItems()) {
            this.disable();
        } else {
            this.enable();
        }

        this.rectangles = new ScrollableRectangle[6];
        this.totalsize = 120;

        // sliders boxes 0-3
        this.rslider = getScrollableSlider("red", null, 0);
        this.gslider = getScrollableSlider("green", rslider, 1);
        this.bslider = getScrollableSlider("blue", gslider, 2);
        this.aslider = getScrollableSlider("alpha", bslider, 3);

        // box 4 is for the color icon stuff
        this.colourBox = new ScrollableColourBox(new MuseRelativeRect(border.left(), this.aslider.bottom(), this.border.right(), this.aslider.bottom()+ 25));
        this.colourBox.setMeBelow(aslider);
        rectangles[4] = colourBox;

        // box 5 is the label
        MuseRelativeRect colourLabelBox = new MuseRelativeRect(border.left(), this.colourBox.bottom(), this.border.right(), this.colourBox.bottom() + 20);
        this.colourLabel = new ScrollableLabel(
                new ClickableLabel(COLOUR_PREFIX, new MusePoint2D(colourLabelBox.centerx(), colourLabelBox.centery())), colourLabelBox);

        colourLabel.setMeBelow(colourBox);
        rectangles[5] = this.colourLabel;

        this.selectedSlider = null;
        this.selectedColour = 0;
        this.decrAbove = -1;
    }

    public ScrollableSlider getScrollableSlider(String id, ScrollableRectangle prev, int index) {
        MuseRelativeRect newborder = new MuseRelativeRect(border.left(), prev != null ? prev.bottom() : this.border.top(),
                this.border.right(), (prev != null ? prev.bottom() : this.border.top()) + 18);
        ClickableSlider slider =
                new ClickableSlider(new MusePoint2D(newborder.centerx(), newborder.centery()), newborder.width() - 15, id,
                        I18n.format(NuminaConstants.MODULE_TRADEOFF_PREFIX + id));
        ScrollableSlider scrollableSlider = new ScrollableSlider(slider, newborder);
        scrollableSlider.setMeBelow((prev != null) ? prev : null);
        rectangles[index] = scrollableSlider;
        return scrollableSlider;
    }

    public int[] colours() {
        return (getOrCreateColourTag() != null) ? getOrCreateColourTag().getIntArray() : new int[0];
    }

    public IntArrayNBT getOrCreateColourTag() {
        if (this.itemSelector.getSelectedItem() == null) {
            return null;
        }
        return itemSelector.getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).map(spec->{
            CompoundNBT renderSpec = spec.getMuseRenderTag();
            if (renderSpec != null && !renderSpec.isEmpty()) {
                return new IntArrayNBT(spec.getColorArray());
            }
            return new IntArrayNBT(new int[0]);
        }).orElse(new IntArrayNBT(new int[0]));
    }

    public IntArrayNBT setColourTagMaybe(List<Integer> intList) {
        if (this.itemSelector.getSelectedItem() == null) {
            return null;
        }
        return itemSelector.getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).map(spec->{
            CompoundNBT renderSpec = spec.getMuseRenderTag();
            renderSpec.put(NuminaConstants.TAG_COLOURS, new IntArrayNBT(intList));
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player.world.isRemote) {
                MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketColourInfo(this.itemSelector.getSelectedItem().getSlotIndex(), this.colours()));
            }
            return (IntArrayNBT) renderSpec.get(NuminaConstants.TAG_COLOURS);
        }).orElse(new IntArrayNBT(new int[0]));
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (this.isEnabled())
            this.selectedSlider = null;
        return false;
    }

    public DrawableMuseRect getBorder(){
        return this.border;
    }

    @Override
    public void update(double mousex, double mousey) {
        super.update(mousex, mousey);
        if (this.isEnabled()) {
            if (this.selectedSlider != null) {
                this.selectedSlider.getSlider().setValueByX(mousex);
                if (colours().length > selectedColour) {
                    colours()[selectedColour] = Colour.getInt(rslider.getValue(), gslider.getValue(), bslider.getValue(), aslider.getValue());

                    ClientPlayerEntity player = Minecraft.getInstance().player;
                    if (player.world.isRemote)
                        MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketColourInfo(itemSelector.getSelectedItem().inventorySlot, colours()));
                }
                // this just sets up the sliders on selecting an item
            } else if (itemSelector.getSelectedItem() != null && colours().length > 0) {
                if (selectedColour <= colours().length -1)
                    onSelectColour(selectedColour);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisibile()) {
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());

            if (colours().length > selectedColour) {
                colourLabel.setText(COLOUR_PREFIX + " 0X" + new Colour(colours()[selectedColour]).hexColour());
            }

            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -currentscrollpixels, 0);
            for (ScrollableRectangle f : rectangles) {
                f.render(mouseX, mouseY, partialTicks);
            }
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        return null;
    }

    public void onSelectColour(int i) {
        Colour c = new Colour(this.colours()[i]);
        this.rslider.setValue(c.r);
        this.gslider.setValue(c.g);
        this.bslider.setValue(c.b);
        this.aslider.setValue(c.a);
        this.selectedColour = i;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.isEnabled()) {
            y = y + currentscrollpixels;

            if (this.rslider.hitBox(x, y))
                this.selectedSlider = this.rslider;
            else if (this.gslider.hitBox(x, y))
                this.selectedSlider = this.gslider;
            else if (this.bslider.hitBox(x, y))
                this.selectedSlider = this.bslider;
            else if (this.aslider.hitBox(x, y))
                this.selectedSlider = this.aslider;
            else
                this.selectedSlider = null;

            colourBox.addColour(x, y);
            colourBox.removeColour(x, y);

            if (colourLabel.hitbox(x, y) && colours().length > selectedColour) {
                // todo: insert chat to player...
//            System.out.println("copying to clipboard: " + "0x" + new Colour(selectedColour).hexColour());
                StringSelection selection = new StringSelection(new Colour(selectedColour).hexColour());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        }
        return false;
    }

    public int[] getIntArray(IntArrayNBT e) {
        if (e == null) // null when no armor item selected
            return new int[0];
        return e.getIntArray();
    }

    class ScrollableColourBox extends ScrollableRectangle {
        public ScrollableColourBox(MuseRelativeRect relativeRect) {
            super(relativeRect);
        }

        boolean addColour(double x, double y) {
            if (y > this.centery() + 8.5 && y < this.centery() + 16.5 ) {
                int colourCol = (int) (x - left() - 8.0) / 8;
                if (colourCol >= 0 && colourCol < colours().length) {
                    onSelectColour(colourCol);
                } else if (colourCol == colours().length) {
                    MuseLogger.logger.debug("Adding");
                    List<Integer> intList = Arrays.stream(getIntArray(getOrCreateColourTag())).boxed().collect(Collectors.toList());
                    intList.add(Colour.WHITE.getInt());
                    setColourTagMaybe(intList);
                }
                return true;
            }
            return false;
        }

        boolean removeColour(double x, double y) {
            if (y > this.centery() + 0.5 && y < this.centery() + 8.5 && x > left() + 8 + selectedColour * 8 && x < left() + 16 + selectedColour * 8) {
                IntArrayNBT IntArrayNBT = getOrCreateColourTag();
                List<Integer> intList = Arrays.stream(getIntArray(IntArrayNBT)).boxed().collect(Collectors.toList());

                if (intList.size() > 1 && selectedColour <= intList.size() -1) {
                    intList.remove(selectedColour); // with integer list, will default to index rather than getValue

                    setColourTagMaybe(intList);

                    decrAbove = selectedColour;
                    if (selectedColour == getIntArray(IntArrayNBT).length) {
                        selectedColour = selectedColour - 1;
                    }

                    ClientPlayerEntity player = Minecraft.getInstance().player;
                    if (player.world.isRemote)
                        MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketColourInfo(itemSelector.getSelectedItem().getSlotIndex(), IntArrayNBT.getIntArray()));
                }
                return true;
            }
            return false;
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            // colours
            for (int i=0; i < colours().length; i++) {
                new GuiIcons.ArmourColourPatch(this.left() + 8 + i * 8, this.centery() + 8 , new Colour(colours()[i]), null, null, null, null);
            }

            new GuiIcons.ArmourColourPatch(this.left() + 8 + colours().length * 8, this.centery() + 8, Colour.WHITE, null, null, null, null);
            new GuiIcons.SelectedArmorOverlay(this.left() + 8 + selectedColour * 8, this.centery() + 8, Colour.WHITE, null, null, null, null);
            new GuiIcons.MinusSign(this.left() + 8 + selectedColour * 8, this.centery(), Colour.RED, null, null, null, null);
            new GuiIcons.PlusSign(this.left() + 8 + colours().length * 8, this.centery() + 8, Colour.GREEN, null, null, null, null);
        }
    }
}
