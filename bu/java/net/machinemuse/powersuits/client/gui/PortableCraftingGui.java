package net.machinemuse.powersuits.client.gui;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortableCraftingGui extends GuiCrafting { // ==> extends GuiContainer implements IRecipeShownListener
    public PortableCraftingGui(EntityPlayer player, World world, BlockPos pos) {
        super(player.inventory, world, pos);
    }
}