package com.github.lehjr.modularpowerarmor.client.gui.crafting;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.client.gui.common.TabSelectFrame;
import com.github.lehjr.modularpowerarmor.container.MPACraftingContainer;
import com.github.lehjr.mpalib.client.gui.ExtendedContainerScreen;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableArrow;
import com.github.lehjr.mpalib.client.gui.clickable.TexturedButton;
import com.github.lehjr.mpalib.client.gui.frame.InventoryFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRelativeRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.client.sound.SoundDictionary;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.stream.IntStream;


@OnlyIn(Dist.CLIENT)
public class TinkerCraftingGUI extends ExtendedContainerScreen<MPACraftingContainer> implements IRecipeShownListener {
    /** the recipe book */
    private final MPARecipeBookGui recipeBookGui = new MPARecipeBookGui();
    /** determins if the recipe book gui will be over the crafting gui */
    private boolean widthTooNarrow;

    protected MPACraftingContainer container;

    /** The outer green rectangle */
    protected DrawableRelativeRect backgroundRect;
    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8F);
    protected final Colour gridBackGound = new Colour(0.545F, 0.545F, 0.545F, 1);

    protected InventoryFrame craftingGrid, mainInventory, hotbar;

    protected DrawableRelativeRect result;
    protected ClickableArrow arrow;
    protected TexturedButton recipeBookButton;
    protected TabSelectFrame tabSelectFrame;
    final int slotWidth = 18;
    final int slotHeight = 18;
    int spacer = 7;

    /**
     * Fun fact: initializing GUI element placement values in the constructor is meaningless because the placement
     *  values aren't set until init()
     */
    public TinkerCraftingGUI(MPACraftingContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.minecraft = Minecraft.getInstance();
        PlayerEntity player = getMinecraft().player;

        tabSelectFrame = new TabSelectFrame(player, 3, getBlitOffset());
        backgroundRect = new DrawableRelativeRect(0, 0, 0, 0, true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));
        this.container = container;

        // slot 0
        result = new DrawableRelativeRect(0, 0, 0, 0, true,
                gridBackGound, gridBorderColour);

        // slot 1-9
        craftingGrid = new InventoryFrame(container,
                new Point2F(0,0), new Point2F(0, 0),
                getBlitOffset(),
                gridBackGound, gridBorderColour, gridColour,
                3, 3, new ArrayList<Integer>(){{
            IntStream.range(1, 10).forEach(i-> add(i));
        }});
        frames.add(craftingGrid);

        // slot 10-36
        mainInventory = new InventoryFrame(container,
                new Point2F(0,0), new Point2F(0, 0),
                getBlitOffset(),
                gridBackGound, gridBorderColour, gridColour,
                9, 3, new ArrayList<Integer>(){{
            IntStream.range(10, 37).forEach(i-> add(i));
        }});
        frames.add(mainInventory);

        hotbar = new InventoryFrame(container,
                new Point2F(0,0), new Point2F(0, 0),
                getBlitOffset(),
                gridBackGound, gridBorderColour, gridColour,
                9, 1, new ArrayList<Integer>(){{
            IntStream.range(37, 46).forEach(i-> add(i));
        }});
        frames.add(hotbar);

        arrow = new ClickableArrow(0, 0, 0, 0, true, gridBackGound, Colour.WHITE, gridBorderColour);
        arrow.show();

        recipeBookButton = new TexturedButton(
                0, 0, 0, 0, true,
                gridBackGound,
                gridBackGound,
                gridBorderColour,
                gridBorderColour,
                16,
                16,
                new ResourceLocation(MPAConstants.TEXTURE_PREFIX + "gui/recipe_book_button.png"));
        recipeBookButton.setEnabled(true);
        recipeBookButton.setVisible(true);
//        recipeBookButton.setTextureOffsetX(-8.0F);
//        recipeBookButton.setTextureOffsetY(-8.0F);
    }

    private void guiElementsMoveLeft(float moveAmmount) {
        backgroundRect.setLeft(backgroundRect.left() + moveAmmount);

        hotbar.setUlShift(getUlOffset());
        hotbar.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer);

        mainInventory.setUlShift(getUlOffset());
        mainInventory.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer);

        craftingGrid.setUlShift(getUlOffset());
        craftingGrid.init(
                backgroundRect.finalLeft() + 29,
                backgroundRect.finalTop() + 16,
                backgroundRect.finalLeft() + 29  + 3 * slotWidth,
                backgroundRect.finalTop() + 16 + 3 * slotHeight);

        result.setLeft(result.left() + moveAmmount);
        arrow.setLeft(arrow.left() + moveAmmount);
        recipeBookButton.setLeft(recipeBookButton.left() + moveAmmount);
        tabSelectFrame.init((recipeBookGui.isVisible() ? recipeBookGui.getGuiLeft() : guiLeft),
                getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());
    }

    Point2F getUlOffset () {
        return new Point2F(guiLeft + 8, guiTop + 8);
    }

    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        // do not call anything recipe book related before this
        this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        this.recipeBookGui.setZLevel(getBlitOffset());
        backgroundRect.setTargetDimensions(new Point2F(getGuiLeft(), getGuiTop()), new Point2F(getXSize(), getYSize()));

        hotbar.setUlShift(getUlOffset());
        hotbar.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer);


        mainInventory.setUlShift(getUlOffset());
        mainInventory.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight - 3.25F - 3 * slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer - slotHeight - 3.25F);

        craftingGrid.setUlShift(getUlOffset());
        craftingGrid.init(
                backgroundRect.finalLeft() + 29,
                backgroundRect.finalTop() + 16,
                backgroundRect.finalLeft() + 29  + 3 * slotWidth,
                backgroundRect.finalTop() + 16 + 3 * slotHeight);

        result.setTargetDimensions(new Point2F(getGuiLeft() + 120, getGuiTop() + 31), new Point2F(24, 24));

        arrow.setTargetDimensions(new Point2F(getGuiLeft() + 90, getGuiTop() + 31), new Point2F(24, 24));
        arrow.setOnPressed(press-> {
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
            container.transferStackInSlot(this.getMinecraft().player, container.getOutputSlot());
        });

        tabSelectFrame.init((recipeBookGui.isVisible() ? recipeBookGui.getGuiLeft() : guiLeft),
                getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());

        this.children.add(this.recipeBookGui);
        this.setFocusedDefault(this.recipeBookGui);

        recipeBookButton.setTargetDimensions(new Point2F(getGuiLeft() + 5, this.height / 2 - 49), new Point2F(18, 20));
        recipeBookButton.setOnPressed((button)-> {
            /** this is everything the button does when pressed */
            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            float oldLeft = guiLeft;
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            guiElementsMoveLeft(guiLeft - oldLeft);
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
        });
    }

    @Override
    public void tick() {
        super.tick();
        this.recipeBookGui.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        // TODO: make items and textured buttons not show until gui done growing
        if (backgroundRect.width() == getXSize()) {
            recipeBookButton.render(mouseX, mouseY, partialTicks, getBlitOffset());
            if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
                this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
                this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            } else {
                this.recipeBookGui.render(mouseX, mouseY, partialTicks);
                super.render(mouseX, mouseY, partialTicks);
                this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
            }
            this.renderHoveredToolTip(mouseX, mouseY);
            this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
            this.func_212932_b(this.recipeBookGui);
            tabSelectFrame.render(mouseX, mouseY, partialTicks);
        }
        arrow.render(mouseX, mouseY, partialTicks, getBlitOffset());
        drawToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 28.0F, 6.0F, Colour.WHITE.getInt());
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), Colour.WHITE.getInt());
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }

    @Override
    protected boolean isPointInRegion(int targetPosX, int targetPosY, int targetWidth, int targetHeight, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(targetPosX, targetPosY, targetWidth, targetHeight, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.tabSelectFrame.mouseClicked(mouseX, mouseY, button))
            return true;

        if (this.recipeBookButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (this.arrow.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookGui.isVisible() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    double yVal = 37;

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int scaledWidth, int scaledHeight, int button) {
        boolean flag = mouseX < (double) scaledWidth || mouseY < (double) scaledHeight || mouseX >= (double) (scaledWidth + this.xSize) || mouseY >= (double) (scaledHeight + this.ySize);
        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, button) && flag;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    public void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        backgroundRect.draw(getBlitOffset());
        result.draw(getBlitOffset());
//        arrow.draw();
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    @Override
    public void removed() {
        this.recipeBookGui.removed();
        super.removed();
    }

    @Override
    public RecipeBookGui getRecipeGui() {
        return this.recipeBookGui;
    }
}