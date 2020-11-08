package com.github.lehjr.modularpowerarmor.client.gui.modding.cosmetic;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.client.gui.common.TabSelectFrame;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.mpalib.util.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.util.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.text.ITextComponent;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:32 PM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class CosmeticGui extends ContainerlessGui {
    protected DrawableRect backgroundRect;
    PlayerEntity player;
    final int spacer = 7;

    ItemSelectionFrame itemSelectFrame;
    PlayerModelViewFrame renderframe;
    ColourPickerFrame colourpicker;
    PartManipContainer partframe;


    TabSelectFrame tabSelectFrame;

//    LoadSaveResetSubFrame loadSaveResetSubFrame;

    protected final boolean allowCosmeticPresetCreation;
    protected final boolean usingCosmeticPresets;

    public CosmeticGui(PlayerInventory inventory, ITextComponent title) {
        super(title);
        this.player = inventory.player;
        this.minecraft = Minecraft.getInstance();

        usingCosmeticPresets = !MPASettings.useLegacyCosmeticSystem();
        if (usingCosmeticPresets) {
            // check if player is the server owner
            if (minecraft.isSingleplayer()) {
                allowCosmeticPresetCreation = player.getName().equals(minecraft.getIntegratedServer().getServerOwner());
            } else {
                // check if player is top level op
                OpEntry opEntry = minecraft.player.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
                int opLevel = opEntry != null ? opEntry.getPermissionLevel() : 0;
                allowCosmeticPresetCreation = opLevel == 4;
            }
        } else {
            allowCosmeticPresetCreation = false;
        }

        backgroundRect = new DrawableRect(absX(-1), absY(-1), absX(1), absY(1), true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));

        itemSelectFrame = new ItemSelectionFrame(
                null,
                new Point2D(absX(-0.95), absY(-0.95)),
                new Point2D(absX(-0.78), absY(-0.025)),
                this.getBlitOffset(),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHT_BLUE.withAlpha(0.8F),
                player);
        addFrame(itemSelectFrame);

        renderframe = new PlayerModelViewFrame(
                itemSelectFrame,
                new Point2D(absX(-0.75), absY(-0.95)),
                new Point2D(absX(0.15), absY(-0.025)),
                this.getBlitOffset(),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHT_BLUE.withAlpha(0.8F));
        addFrame(renderframe);

        colourpicker = new ColourPickerFrame(
                new Point2D(absX(0.18),
                        absY(-0.95)),

                new Point2D(absX(0.95),
                        absY(-0.27)),
                this.getBlitOffset(),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHT_BLUE.withAlpha(0.8F),
                itemSelectFrame);
        addFrame(colourpicker);

        partframe = new PartManipContainer(
                itemSelectFrame, colourpicker,
                new Point2D(absX(-0.95F), absY(0.025f)),
                new Point2D(absX(+0.95F), absY(0.95f)),
                this.getBlitOffset(),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHT_BLUE.withAlpha(0.8F));
        addFrame(partframe);

        tabSelectFrame = new TabSelectFrame(player, 2, this.getBlitOffset());
        addFrame(tabSelectFrame);

        itemSelectFrame.setDoOnNewSelect(doThis -> partframe.getModelframes());
    }

    public void rescale() {
        this.setXSize((Math.min(minecraft.getMainWindow().getScaledWidth()- 50, 500)));
        this.setYSize((Math.min(minecraft.getMainWindow().getScaledHeight() - 50, 300)));
    }


    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void init() {
        super.init();
        rescale();

        backgroundRect.setTargetDimensions(getGuiLeft(), getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());

        itemSelectFrame.init(
                backgroundRect.finalLeft()  + spacer,
                backgroundRect.finalTop() + spacer,
                backgroundRect.finalLeft() + spacer + 36,
                backgroundRect.centery() - spacer
        );

        renderframe.init(
                backgroundRect.finalLeft() + spacer + 36 + spacer,
                backgroundRect.finalTop() + spacer,
                backgroundRect.finalRight() - spacer - 150 -spacer,
                backgroundRect.centery() - spacer
        );

        colourpicker.init(
                backgroundRect.finalRight() - spacer - 150,
                backgroundRect.finalTop() + spacer,
                backgroundRect.finalRight() - spacer,
                backgroundRect.centery() - spacer
        );

        partframe.init(
                backgroundRect.finalLeft()  + spacer,
                backgroundRect.centery(),
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalBottom() - spacer

        );


//        CosmeticPresetContainer cosmeticFrame = new CosmeticPresetContainer(
//                itemSelect, colourpicker,
//                new Point2F(absX(-0.95F), absY(0.025f)),
//                new Point2F(absX(+0.95F), absY(0.95f)),
//                Colour.LIGHTBLUE.withAlpha(0.8F),
//                Colour.DARKBLUE.withAlpha(0.8F));
//        frames.add(cosmeticFrame);
//
//        // if not using presets then only the reset button is displayed
//        loadSaveResetSubFrame = new LoadSaveResetSubFrame(
//                colourpicker,
//                player,
//                new Rect(
//                        absX(0.18f),
//                        absY(-0.23f),
//                        absX(0.95f),
//                        absY(-0.025f)),
//                Colour.LIGHTBLUE.withAlpha(0.8F),
//                Colour.DARKBLUE.withAlpha(0.8F),
//                itemSelect,
//                usingCosmeticPresets,
//                allowCosmeticPresetCreation,
//                partframe,
//                cosmeticFrame);
//        frames.add(loadSaveResetSubFrame);
//


        tabSelectFrame.init(getGuiLeft(), getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (backgroundRect.width() == backgroundRect.finalWidth() && backgroundRect.height() == backgroundRect.finalHeight()) {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            drawToolTip(matrixStack, mouseX, mouseY);
        } else {
            this.renderBackground(matrixStack);
        }
    }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        super.renderBackground(matrixStack);
        this.backgroundRect.draw(matrixStack, this.getBlitOffset());
    }

    @Override
    public void onClose() {
        super.onClose();
//        loadSaveResetSubFrame.onClose();
    }
    // FIXME!!! I have no idea what this return value should be based on... 0 documentation
//    @Override
//    public boolean charTyped(char typedChar, int keyCode) {
//        boolean ret = super.charTyped(typedChar, keyCode);
//        if (loadSaveResetSubFrame != null)
//            loadSaveResetSubFrame.charTyped(typedChar, keyCode);
//        return ret;
//    }
}