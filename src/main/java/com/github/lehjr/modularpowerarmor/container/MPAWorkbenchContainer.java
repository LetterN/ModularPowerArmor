package com.github.lehjr.modularpowerarmor.container;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.MoveModuleFromSlotToSlotPacket;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.CreativeInstallModuleRequestPacket;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.client.gui.slot.HideableSlot;
import com.github.lehjr.mpalib.util.client.gui.slot.HideableSlotItemHandler;
import com.github.lehjr.mpalib.util.client.gui.slot.IHideableSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Looks like slots have to be populated in the container's constructor.
 * This means that only equipped ... fixme: ...what?
 *
 */
public class MPAWorkbenchContainer
//        extends MPARecipeBookContainer<CraftingInventory> implements IModularItemToSlotMapProvider {
        extends Container implements IModularItemToSlotMapProvider {
    private final PlayerEntity player;

    // A map of the slot that holds the modular item, and the set of slots in that modular item
    private Map<Integer, List<SlotItemHandler>> modularItemToSlotMap;

    public MPAWorkbenchContainer(int windowId, PlayerInventory playerInventory) {
        super(MPAObjects.MPA_WORKBENCH_CONTAINER_TYPE.get(), windowId);
        this.player = playerInventory.player;

        modularItemToSlotMap = new HashMap<>();

        // add all player inventory slots
        for (int index = 0; index < playerInventory.getSizeInventory(); index ++) {
            this.addSlot(new HideableSlot(playerInventory, index, 0, 0));
        }

        // add all modular item slots
        for (Slot slot :  new ArrayList<Slot>(this.inventorySlots)) {
            List<SlotItemHandler> slots = new ArrayList<>();

            slot.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                if (iItemHandler instanceof IModularItem) {
                    for (int modularItemInvIndex = 0; modularItemInvIndex < iItemHandler.getSlots(); modularItemInvIndex ++) {
                        HideableSlotItemHandler slot1 =
                                (HideableSlotItemHandler) addSlot(new HideableSlotItemHandler(iItemHandler, inventorySlots.indexOf(slot), modularItemInvIndex, -1000, -1000));
                        slots.add(slot1);
                    }
                }
            });

            if (!slots.isEmpty()) {
                modularItemToSlotMap.put(slot.slotNumber, slots);
            }
        }

        for (Slot slot : this.inventorySlots) {
            if(slot instanceof IHideableSlot) {
                ((IHideableSlot) slot).disable();
            }
        }
    }

    @Override
    public boolean canMergeSlot(ItemStack itemStack, Slot slot) {
        if (slot instanceof SlotItemHandler) {
            return ((SlotItemHandler) slot).getItemHandler().isItemValid(slot.getSlotIndex(), itemStack);
        }
        return super.canMergeSlot(itemStack, slot);
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return false;
    }

    /**
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    @Override
    public boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getItemStackLimit(stack)/*.getSlotStackLimit()*/, stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    if (stack.getCount() > slot1.getItemStackLimit(stack)/*.getSlotStackLimit()*/) {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    public void creativeInstall(int slot, @Nonnull ItemStack itemStack) {
        if(this.getSlot(slot).getItemStackLimit(itemStack) > 0) {
            putStackInSlot(slot, itemStack);
//            this.detectAndSendChanges();
            MPALibPackets.CHANNEL_INSTANCE.sendToServer(new CreativeInstallModuleRequestPacket(this.windowId, slot, itemStack));
        }
    }


    public void move(int source, int target) {
        if (source == -1)
            return;
        if (target == -1)
            return;

        Slot sourceSlot = inventorySlots.get(source);
        Slot targetSlot = inventorySlots.get(target);

        ItemStack contents = sourceSlot.getStack();
        ItemStack stackCopy = contents.copy();

        if(sourceSlot.canTakeStack(player) && canMergeSlot(contents, targetSlot)) {
            targetSlot.putStack(stackCopy);
            sourceSlot.putStack(ItemStack.EMPTY);
            MPAPackets.CHANNEL_INSTANCE.sendToServer(new MoveModuleFromSlotToSlotPacket(this.windowId, source, target));
//            detectAndSendChanges();
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }


    public Map<Integer, List<SlotItemHandler>> getModularItemToSlotMap() {
        return modularItemToSlotMap;
    }

    @Override
    public Container getContainer() {
        return this;
    }
}