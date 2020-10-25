package com.github.lehjr.modularpowerarmor.client.gui.modechanging;

import com.github.lehjr.mpalib.util.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.ModeChangeRequestPacket;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.util.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.util.client.gui.geometry.IRect;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.util.client.gui.geometry.SpiralPointToPoint2D;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;

public class RadialModeSelectionFrame implements IGuiFrame {
    boolean visible = true;
    boolean enabled = true;
    protected final long spawnTime;
    protected List<ClickableModule> modeButtons = new ArrayList<>();
    protected int selectedModuleOriginal = -1;
    protected int selectedModuleNew = -1;

    protected PlayerEntity player;
    protected Point2D center;
    protected double radius;
    protected ItemStack stack;
    Rect rect;
    float zLevel;

    public RadialModeSelectionFrame(Point2D topleft, Point2D bottomright, PlayerEntity player, float zLevel) {
        spawnTime = System.currentTimeMillis();
        this.player = player;
        rect = new Rect(topleft, bottomright);
        center = rect.center();
        this.radius = Math.min(rect.height(), rect.width());
        this.stack = player.inventory.getCurrentItem();
        this.zLevel = zLevel;
        loadItems();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        rect.setTargetDimensions(left, top, right, bottom);
        center = rect.center();
        this.radius = Math.min(rect.height(), rect.width());
        modeButtons.clear();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scroll) {
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        //Update items
        loadItems();
        //Determine which mode is selected
        if (System.currentTimeMillis() - spawnTime > 250) {
            selectModule((float) mousex, (float) mousey);
        }
        //Switch to selected mode if mode changed
        if (getSelectedModule() != null && selectedModuleOriginal != selectedModuleNew) {
            // update to detect mode changes
            selectedModuleOriginal = selectedModuleNew;
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
                if (handler instanceof IModeChangingItem) {
                    ((IModeChangingItem) handler).setActiveMode(getSelectedModule().getInventorySlot());
                    MPALibPackets.CHANNEL_INSTANCE.sendToServer(new ModeChangeRequestPacket(getSelectedModule().getInventorySlot(), player.inventory.currentItem));
                }
            });
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, int mouseX, int mouseY, float partialTicks) {
        //Draw the installed power fist modes
        for (ClickableModule mode : modeButtons) {
            mode.render(matrixStackIn, mouseX, mouseY, partialTicks, zLevel);
        }
        //Draw the selected mode indicator
        drawSelection(matrixStackIn);
    }

    private void loadItems() {
        if (player != null && modeButtons.isEmpty()) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
                if (handler instanceof IModeChangingItem) {
                    List<Integer> modes = ((IModeChangingItem) handler).getValidModes();
                    int activeMode = ((IModeChangingItem) handler).getActiveMode();
                    if (activeMode > 0)
                        selectedModuleOriginal = activeMode;
                    int modeNum = 0;
                    for (int mode : modes) {
                        ClickableModule clickie = new ClickableModule(handler.getStackInSlot(mode), new SpiralPointToPoint2D(center, radius, ((3D * Math.PI / 2) - ((2D * Math.PI * modeNum) / modes.size())), 250D), mode, EnumModuleCategory.NONE);
                        modeButtons.add(clickie);
                        modeNum ++;
                    }
                }
            });
        }
    }

    private void selectModule(float x, float y) {
        if (modeButtons != null) {
            int i = 0;
            for (ClickableModule module : modeButtons) {
                if (module.hitBox(x, y)) {
                    selectedModuleNew = i;
                    break;
                }
                i++;
            }
        }
    }

    public ClickableModule getSelectedModule() {
        if (modeButtons.size() > selectedModuleNew && selectedModuleNew != -1) {
            return modeButtons.get(selectedModuleNew);
        } else {
            return null;
        }
    }

    public void drawSelection(MatrixStack matrixStackIn) {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            Point2D pos = module.getPosition();
            Renderer.drawCircleAround(matrixStackIn, pos.getX(), pos.getY(), 10, zLevel);
        }
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            return module.getToolTip();
        }
        return null;
    }

    @Override
    public IRect getBorder() {
        return rect;
    }

    @Override
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

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