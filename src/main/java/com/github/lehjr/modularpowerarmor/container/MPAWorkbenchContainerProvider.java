package com.github.lehjr.modularpowerarmor.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class MPAWorkbenchContainerProvider implements INamedContainerProvider {
    int typeIndex;
    public MPAWorkbenchContainerProvider(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    @Override
    public ITextComponent getDisplayName() {
        switch(typeIndex) {
            case 0:
                return new TranslationTextComponent("gui.modularpowerarmor.tab.workbench");
            default:
                return new TranslationTextComponent("container.crafting");
        }
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        switch(typeIndex) {
            case 0:
                return new MPAWorkbenchContainer(windowId, playerInventory);
            default:
                return null;
//                return new MPACraftingContainer(windowId, playerInventory);
        }
    }
}