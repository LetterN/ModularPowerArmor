package com.github.lehjr.modularpowerarmor.client.gui.scanner;

import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.capabilities.ItemHandlerPowerFist;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Copied from Scannable: li.cil.scannable.common.container.ContainerScanner
 */
public class ScannerContainer extends Container {
    private final EntityPlayer player;
    private final EnumHand hand;

    public ScannerContainer(final EntityPlayer player, @Nonnull final EnumHand hand) {
        this.player = player;
        this.hand = hand;

        final IItemHandlerModifiable itemHandler = (IItemHandlerModifiable) player.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        assert itemHandler instanceof ItemHandlerPowerFist;

        final IItemHandler activeModules = ((ItemHandlerPowerFist) itemHandler).getActiveModules();
        for (int slot = 0; slot < activeModules.getSlots(); ++slot) {
            addSlotToContainer(new SlotItemHandler(activeModules, slot, 62 + slot * 18, 20));
        }

        final IItemHandler storedModules = ((ItemHandlerPowerFist) itemHandler).getInactiveModules();
        for (int slot = 0; slot < storedModules.getSlots(); ++slot) {
            addSlotToContainer(new SlotItemHandler(storedModules, slot, 62 + slot * 18, 46));
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, row * 18 + 77));
            }
        }

        for (int slot = 0; slot < 9; ++slot) {
            addSlotToContainer(new Slot(player.inventory, slot, 8 + slot * 18, 135));
        }
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public EnumHand getHand() {
        return hand;
    }

    // --------------------------------------------------------------------- //
    // Container
    @Override // FIXME: is it even possible to open this otherwise if it's only called from the module?
    public boolean canInteractWith(final EntityPlayer player) {
        return player == this.player &&
                java.util.Optional.ofNullable(player.getHeldItem(hand)
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                    if (iItemHandler instanceof IModeChangingItem && !((IModeChangingItem) iItemHandler).getActiveModule().isEmpty()) {
                        return ((IModeChangingItem) iItemHandler).getActiveModule().getItem().getRegistryName().toString().equals(RegistryNames.MODULE_ORE_SCANNER__REGNAME);
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
        final Slot from = inventorySlots.get(index);
        if (from == null) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = from.getStack().copy();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final boolean intoPlayerInventory = from.inventory != player.inventory;
        final ItemStack fromStack = from.getStack();

        final int step, begin;
        if (intoPlayerInventory) {
            step = -1;
            begin = inventorySlots.size() - 1;
        } else {
            step = 1;
            begin = 0;
        }

        if (fromStack.getMaxStackSize() > 1) {
            for (int i = begin; i >= 0 && i < inventorySlots.size(); i += step) {
                final Slot into = inventorySlots.get(i);
                if (into.inventory == from.inventory) {
                    continue;
                }

                final ItemStack intoStack = into.getStack();
                if (intoStack.isEmpty()) {
                    continue;
                }

                final boolean itemsAreEqual = fromStack.isItemEqual(intoStack) && ItemStack.areItemStackTagsEqual(fromStack, intoStack);
                if (!itemsAreEqual) {
                    continue;
                }

                final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getItemStackLimit(stack));
                final int spaceInSlot = maxSizeInSlot - intoStack.getCount();
                if (spaceInSlot <= 0) {
                    continue;
                }

                final int itemsMoved = Math.min(spaceInSlot, fromStack.getCount());
                if (itemsMoved <= 0) {
                    continue;
                }

                intoStack.grow(from.decrStackSize(itemsMoved).getCount());
                into.onSlotChanged();

                if (from.getStack().isEmpty()) {
                    break;
                }
            }
        }

        for (int i = begin; i >= 0 && i < inventorySlots.size(); i += step) {
            if (from.getStack().isEmpty()) {
                break;
            }

            final Slot into = inventorySlots.get(i);
            if (into.inventory == from.inventory) {
                continue;
            }

            if (into.getHasStack()) {
                continue;
            }

            if (!into.isItemValid(fromStack)) {
                continue;
            }

            final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getItemStackLimit(fromStack));
            final int itemsMoved = Math.min(maxSizeInSlot, fromStack.getCount());
            into.putStack(from.decrStackSize(itemsMoved));
        }

        return from.getStack().getCount() < stack.getCount() ? from.getStack() : ItemStack.EMPTY;
    }
}