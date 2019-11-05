package com.github.lehjr.modularpowerarmor.item.module.armor;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DiamondPlatingModule extends AbstractPowerModule {
    public DiamondPlatingModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            moduleCap = new PowerModule(module, EnumModuleCategory.ARMOR, EnumModuleTarget.ARMORONLY, CommonConfig.moduleConfig);
            moduleCap.addBasePropertyDouble(Constants.ARMOR_VALUE_PHYSICAL, 5, MPALIbConstants.MODULE_TRADEOFF_PREFIX + Constants.ARMOR_POINTS);
            moduleCap.addBasePropertyDouble(Constants.MAXIMUM_HEAT, 400);
            moduleCap.addBasePropertyDouble(Constants.KNOCKBACK_RESISTANCE, 0.25, "");
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == PowerModuleCapability.POWER_MODULE;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) moduleCap;
            }

            return null;
        }
    }
}