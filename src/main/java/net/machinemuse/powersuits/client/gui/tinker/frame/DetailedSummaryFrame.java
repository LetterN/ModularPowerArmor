package net.machinemuse.powersuits.client.gui.tinker.frame;

import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.constants.NuminaNBTConstants;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.item.MuseItemUtils;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.numina.string.MuseStringUtils;
import net.machinemuse.powersuits.basemod.ModuleManager;
import net.machinemuse.powersuits.constants.MPSModuleConstants;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class DetailedSummaryFrame extends ScrollableFrame {
    public static final double SCALEFACTOR = 1;
    protected PlayerEntity player;
    protected int slotPoints;
    protected int energy;
    protected double armor;
    protected ItemSelectionFrame itemSelectionFrame;

    public DetailedSummaryFrame(PlayerEntity player,
                                MusePoint2D topleft,
                                MusePoint2D bottomright,
                                Colour borderColour,
                                Colour insideColour,
                                ItemSelectionFrame itemSelectionFrame) {
        super(topleft.times(1.0 / SCALEFACTOR), bottomright.times(1.0 / SCALEFACTOR), borderColour, insideColour);
        this.player = player;
        this.itemSelectionFrame = itemSelectionFrame;
    }

    @Override
    public void update(double mousex, double mousey) {
        energy = 0;
        armor = 0;


        for (ItemStack stack : MuseItemUtils.getModularItemsEquipped(player)) {
            energy += ElectricItemUtils.getItemEnergy(stack);
            armor += ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.ARMOR_VALUE_PHYSICAL);
            armor += ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.ARMOR_VALUE_ENERGY);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)  {
        if (player != null) {
            GL11.glPushMatrix();
            GL11.glScaled(SCALEFACTOR, SCALEFACTOR, SCALEFACTOR);
            super.render(mouseX, mouseY, partialTicks);
            int margin = 4;
            int nexty = (int) border.top() + margin;
            MuseRenderer.drawCenteredString(I18n.format("gui.powersuits.equippedTotals"), (border.left() + border.right()) / 2, nexty);
            nexty += 10;

            // Max Energy
            String formattedValue = MuseStringUtils.formatNumberFromUnits(energy, AbstractPowerModule.getUnit(NuminaNBTConstants.MAXIMUM_ENERGY));
            String name = I18n.format("gui.powersuits.energyStorage");
            double valueWidth = MuseRenderer.getStringWidth(formattedValue);
            double allowedNameWidth = border.width() - valueWidth - margin * 2;
            List<String> namesList = MuseStringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            for (int i = 0; i < namesList.size(); i++) {
                MuseRenderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            MuseRenderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
            nexty += 10 * namesList.size() + 1;

            // Slot points
            if (slotPoints > 0) {
                formattedValue = MuseStringUtils.wrapFormatTags(MuseStringUtils.formatNumberFromUnits(slotPoints, "pts"), MuseStringUtils.FormatCodes.BrightGreen);
                name = I18n.format("gui.powersuits.slotpoints");
                valueWidth = MuseRenderer.getStringWidth(formattedValue);
                allowedNameWidth = border.width() - valueWidth - margin * 2;
                namesList = MuseStringUtils.wrapStringToVisualLength(name, allowedNameWidth);
                assert namesList != null;
                for (int i = 0; i < namesList.size(); i++) {
                    MuseRenderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
                }
                MuseRenderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
                nexty += 10 * namesList.size() + 1;
            }

            // Armor points
            formattedValue = MuseStringUtils.formatNumberFromUnits(armor, "pts");
            name = I18n.format("gui.powersuits.armor");
            valueWidth = MuseRenderer.getStringWidth(formattedValue);
            allowedNameWidth = border.width() - valueWidth - margin * 2;
            namesList = MuseStringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            assert namesList != null;
            for (int i = 0; i < namesList.size(); i++) {
                MuseRenderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            MuseRenderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);

            GL11.glPopMatrix();
        }
    }

    @Override
    public void onMouseDown(double x, double y, int button) {
    }

    @Override
    public void onMouseUp(double x, double y, int button) {
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        return null;
    }
}
