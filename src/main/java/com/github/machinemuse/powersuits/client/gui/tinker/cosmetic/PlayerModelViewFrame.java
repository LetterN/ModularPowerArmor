/*
 * ModularPowersuits (Maintenance builds by lehjr)
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

import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Objects;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:25 PM, 5/2/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
public class PlayerModelViewFrame implements IGuiFrame {
    ItemSelectionFrame itemSelector;
    DrawableRect border;
    double anchorx = 0;
    double anchory = 0;
    int dragging = -1;
    double lastdWheel = 0;
    double rotx = 0;
    double roty = 0;
    double offsetx = 0;
    double offsety = 29.0D;
    double zoom = 30;
    int mouseX = 0;
    int mouseY = 0;
    private float oldMouseX = 20F;
    private float oldMouseY = 20F;

    public PlayerModelViewFrame(ItemSelectionFrame itemSelector, Point2D topleft, Point2D bottomright, Colour borderColour, Colour insideColour) {
        this.itemSelector = itemSelector;
        this.border = new DrawableRect(topleft, bottomright, borderColour, insideColour);
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.border.setTargetDimensions(left, top, right, bottom);
    }

    public EntityEquipmentSlot getEquipmentSlot() {
        ItemStack selectedItem = getSelectedItem().getStack();
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

    ClickableItem getSelectedItem() {
        return itemSelector.getSelectedItem();
    }


    @Override
    public boolean onMouseDown(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            dragging = button;
            anchorx = x;
            anchory = y;
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseUp(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            dragging = -1;
            return true;
        }
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        if (this.mouseX != mousex)
            this.oldMouseX = this.mouseX;
        this.mouseX = (int) mousex;

        if (this.mouseY != mousey)
            this.oldMouseY = this.mouseY;
        this.mouseY = (int) mousey;


        if (border.containsPoint(mousex, mousey)) {
            double dscroll = (lastdWheel - Mouse.getDWheel()) / 120;
            zoom = zoom * Math.pow(1.1, dscroll);
            lastdWheel = Mouse.getDWheel();
        }
        double dx = mousex - anchorx;
        double dy = mousey - anchory;
        switch (dragging) {
            case 0: {
                rotx = MathUtils.clampDouble(rotx + dy, -90, 90);
                roty = roty - dx;
                anchorx = mousex;
                anchory = mousey;
                break;
            }
            case 1: {
                offsetx = offsetx + dx;
                offsety = offsety + dy;
                anchorx = mousex;
                anchory = mousey;
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void render(int i, int i1, float v) {
        Minecraft mc = Minecraft.getMinecraft();
        border.draw();
        // set color to normal state
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        float mouseX = (float) (border.left() + 51) - this.oldMouseX;
        float mouseY = (float) ((int) border.top() + 75 - 50) - this.oldMouseY;
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(border.centerx() + offsetx, border.centery() + offsety, 50.0F);
        GlStateManager.scale((float) (-zoom), (float) zoom, (float) zoom);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // turn model right side up

        float f = mc.player.renderYawOffset;
        float f1 = mc.player.rotationYaw;
        float f2 = mc.player.rotationPitch;
        float f3 = mc.player.prevRotationYawHead;
        float f4 = mc.player.rotationYawHead;
        // XRotation with mouse look
        GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

        GlStateManager.rotate((float) rotx, 1, 0, 0);
        GlStateManager.rotate((float) roty, 0, 1, 0);

        mc.player.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        mc.player.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        mc.player.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        mc.player.rotationYawHead = mc.player.rotationYaw;
        mc.player.prevRotationYawHead = mc.player.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(mc.player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        mc.player.renderYawOffset = f;
        mc.player.rotationYaw = f1;
        mc.player.rotationPitch = f2;
        mc.player.prevRotationYawHead = f3;
        mc.player.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }
}