package net.machinemuse.powersuits.item.module.armor;

import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.item.module.ItemAbstractPowerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemModulePlatingDiamond extends ItemAbstractPowerModule {
    public ItemModulePlatingDiamond(String regName) {
        super(regName);
    }
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_ARMOR, EnumModuleTarget.ARMORONLY, MPSConfig.INSTANCE);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ARMOR_VALUE_PHYSICAL, 5, NuminaConstants.MODULE_TRADEOFF_PREFIX + MPSConstants.ARMOR_POINTS);
            this.moduleCap.addBasePropertyDouble(MPSConstants.MAXIMUM_HEAT, 400);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
        }
    }
}