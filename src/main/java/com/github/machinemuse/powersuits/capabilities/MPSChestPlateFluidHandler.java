/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

package com.github.machinemuse.powersuits.capabilities;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.legacy.module.IModuleManager;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MPSChestPlateFluidHandler implements IFluidHandler, IFluidHandlerItem, INBTSerializable<NBTTagCompound> {
    public BasicCoolingTank basicCoolingTank;
    public AdvancedCoolingTank advancedCoolingTank;
    ItemStack container;
    List<ArmorTank> subHandlers;
    List<ArmorTank> allHandlers;
    IModuleManager moduleManager;

    public MPSChestPlateFluidHandler(@Nonnull ItemStack container, IModuleManager moduleManager) {
        this.container = container;
        this.moduleManager = moduleManager;
        basicCoolingTank = new BasicCoolingTank();
        advancedCoolingTank = new AdvancedCoolingTank();
        this.subHandlers = new LinkedList<>();
        this.allHandlers = new LinkedList<ArmorTank>() {{
            add(basicCoolingTank);
            add(advancedCoolingTank);
        }};
    }

    public void addHandler(ArmorTank handler) {
        if (!subHandlers.contains(handler))
            subHandlers.add(handler);
    }

    @Nullable
    public ArmorTank getFluidTank(String dataName) {
        for (ArmorTank tank : subHandlers) {
            if (tank.moduleDataName.equals(dataName))
                return tank;
        }
        return null;
    }


    public void removeHandler(IFluidHandler handler) {
        subHandlers.remove(handler);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<IFluidTankProperties> tanks = Lists.newArrayList();
        for (IFluidHandler handler : subHandlers) {
            Collections.addAll(tanks, handler.getTankProperties());
        }
        return tanks.toArray(new IFluidTankProperties[tanks.size()]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0)
            return 0;

        resource = resource.copy();

        int totalFillAmount = 0;
        for (IFluidHandler handler : subHandlers) {
            int fillAmount = handler.fill(resource, doFill);
            totalFillAmount += fillAmount;
            resource.amount -= fillAmount;
            if (resource.amount <= 0)
                break;
        }

        NBTTagCompound nbt = serializeNBT();
        for (ArmorTank armorTank : allHandlers) {
            if (nbt.hasKey(armorTank.moduleDataName, Constants.NBT.TAG_COMPOUND)) {
                armorTank = (ArmorTank) armorTank.readFromModuleTag(nbt.getCompoundTag(armorTank.moduleDataName));
                addHandler(armorTank);
            }
        }
        return totalFillAmount;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0)
            return null;

        resource = resource.copy();

        FluidStack totalDrained = null;
        for (IFluidHandler handler : subHandlers) {
            FluidStack drain = handler.drain(resource, doDrain);
            if (drain != null) {
                if (totalDrained == null)
                    totalDrained = drain;
                else
                    totalDrained.amount += drain.amount;

                resource.amount -= drain.amount;
                if (resource.amount <= 0)
                    break;
            }
        }

        NBTTagCompound nbt = serializeNBT();
        for (ArmorTank armorTank : allHandlers) {
            if (nbt.hasKey(armorTank.moduleDataName, Constants.NBT.TAG_COMPOUND)) {
                armorTank = (ArmorTank) armorTank.readFromModuleTag(nbt.getCompoundTag(armorTank.moduleDataName));
                addHandler(armorTank);
            }
        }
        return totalDrained;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain == 0)
            return null;
        FluidStack totalDrained = null;
        for (IFluidHandler handler : subHandlers) {
            if (totalDrained == null) {
                totalDrained = handler.drain(maxDrain, doDrain);
                if (totalDrained != null) {
                    maxDrain -= totalDrained.amount;
                }
            } else {
                FluidStack copy = totalDrained.copy();
                copy.amount = maxDrain;
                FluidStack drain = handler.drain(copy, doDrain);
                if (drain != null) {
                    totalDrained.amount += drain.amount;
                    maxDrain -= drain.amount;
                }
            }

            if (maxDrain <= 0)
                break;
        }
        return totalDrained;
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    public void updateFromNBT() {
        NBTTagCompound itemNBT = NBTUtils.getMuseItemTag(container);
        if (itemNBT != null) {
            for (ArmorTank tank : allHandlers) {
                if (moduleManager.itemHasModule(container, tank.moduleDataName)) {
                    tank = (ArmorTank) tank.readFromItemTag(itemNBT);
                    addHandler(tank);
                }
            }
        }
    }

    /**
     * This is set up to only send the module tags that hold the tanks and try to minimize the amount of data sent
     */
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbtOut = new NBTTagCompound();

        for (IFluidHandler handler : subHandlers) {
            if (handler instanceof ArmorTank) {
                if (moduleManager.itemHasModule(container, ((ArmorTank) handler).moduleDataName)) {

                    // should not be null
                    NBTTagCompound moduleTag = ((ArmorTank) handler).getModuleTag();
                    if (moduleTag != null) {
                        ((ArmorTank) handler).writeToModuleTag(moduleTag);
                    }

                    nbtOut.setTag(((ArmorTank) handler).moduleDataName, moduleTag);
                }
            }
        }
        return nbtOut;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for (ArmorTank armorTank : allHandlers) {
            if (nbt.hasKey(armorTank.moduleDataName, Constants.NBT.TAG_COMPOUND)) {
                armorTank = (ArmorTank) armorTank.readFromModuleTag(nbt.getCompoundTag(armorTank.moduleDataName));
                addHandler(armorTank);
            }
        }
    }

    interface IArmorTank {
        @Nullable
        NBTTagCompound getModuleTag();
    }

    public class ArmorTank extends FluidTank implements IArmorTank {
        String moduleDataName;

        public ArmorTank(String moduleDataName) {
            super(10000);
            this.moduleDataName = moduleDataName;
        }

        @Override
        public boolean canDrainFluidType(@Nullable FluidStack fluid) {
            return true;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            if (fluid != null) {
                if (fluid.getFluid() == FluidRegistry.WATER && moduleDataName == MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME) {
                    return true;
                }

                if (fluid.getFluid() != FluidRegistry.WATER && moduleDataName == MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME) {
                    return true;
                }
            }

            // This should cover both cases above... but
            return (fluid != null && fluid.getFluid() == FluidRegistry.WATER && moduleDataName == MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME);
        }

        @Nullable
        @Override
        public NBTTagCompound getModuleTag() {
            NBTTagCompound nbt = NBTUtils.getMuseItemTag(container);
            return (NBTTagCompound) nbt.getTag(moduleDataName);
        }

//        @Override
//        public NBTTagCompound writeToItemTag(NBTTagCompound nbt) {
//            NBTTagCompound nbtModuleTag = nbt.getCompoundTag(this.moduleDataName);
//            if (nbtModuleTag != null) {
//                nbtModuleTag = writeToModuleTag(nbtModuleTag);
//                nbt.setTag(this.moduleDataName, nbtModuleTag);
//            }
//            return nbt;
//        }

        public NBTTagCompound writeToModuleTag(NBTTagCompound nbt) {
            NBTTagCompound fluidTag = this.writeToNBT(new NBTTagCompound());
            nbt.setTag(MPALIbConstants.FLUID_NBT_KEY, fluidTag);
            return nbt;
        }

        public FluidTank readFromItemTag(NBTTagCompound nbt) {
            NBTTagCompound nbtModuleTag = nbt.getCompoundTag(this.moduleDataName);
            if (nbtModuleTag != null) {
                return readFromModuleTag(nbtModuleTag);
            }
            return this;
        }

        public FluidTank readFromModuleTag(NBTTagCompound nbt) {
            NBTTagCompound fluidTag = nbt.getCompoundTag(MPALIbConstants.FLUID_NBT_KEY);
            if (fluidTag != null)
                return this.readFromNBT(fluidTag);
            return this;
        }

        @Override
        protected void onContentsChanged() {
            NBTTagCompound itemNBT = NBTUtils.getMuseItemTag(container);
            NBTTagCompound moduleTag = itemNBT.getCompoundTag(moduleDataName);

            if (moduleTag != null) {
                moduleTag = writeToModuleTag(moduleTag);
                NBTTagCompound nbtOut = new NBTTagCompound();
                nbtOut.setTag(moduleDataName, moduleTag);
                itemNBT.setTag(moduleDataName, moduleTag);
                deserializeNBT(nbtOut);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArmorTank armorTank = (ArmorTank) o;
            return Objects.equals(moduleDataName, armorTank.moduleDataName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(moduleDataName);
        }
    }

    public class BasicCoolingTank extends ArmorTank {
        public BasicCoolingTank() {
            super(MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME);
        }
    }

    public class AdvancedCoolingTank extends ArmorTank {
        public AdvancedCoolingTank() {
            super(MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME);
        }
    }
}