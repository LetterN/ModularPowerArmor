package com.github.lehjr.modularpowerarmor.item.module.miningenhancement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.enchantment.IEnchantmentModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.IMiningEnhancementModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.MiningEnhancement;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FortuneModule extends AbstractPowerModule {
    public FortuneModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IMiningEnhancementModule miningEnhancement;
        IEnchantmentModule enchantmentModule;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.miningEnhancement = new Enhancement(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.miningEnhancement.addBasePropertyDouble(Constants.FORTUNE_ENERGY_CONSUMPTION, 500, "RF");
            this.miningEnhancement.addTradeoffPropertyDouble(Constants.ENCHANTMENT_LEVEL, Constants.FORTUNE_ENERGY_CONSUMPTION, 9500);
            this.miningEnhancement.addIntTradeoffProperty(Constants.ENCHANTMENT_LEVEL, Constants.FORTUNE_ENCHANTMENT_LEVEL, 3, "", 1, 1);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) miningEnhancement;
            }
            return null;
        }

        class Enhancement extends MiningEnhancement implements IEnchantmentModule {
            public Enhancement(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            /**
             * Called before a block is broken.  Return true to prevent default block harvesting.
             *
             * Note: In SMP, this is called on both client and server sides!
             *
             * @param itemstack The current ItemStack
             * @param pos Block's position in world
             * @param player The Player that is wielding the item
             * @return True to prevent harvesting, false to continue as normal
             */
            @Override
            public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
                if (!player.world.isRemote) {
                    if (getEnergyUsage() > ElectricItemUtils.getPlayerEnergy(player))
                        enchantmentModule.removeEnchantment(itemstack);
                    else
                        ElectricItemUtils.drainPlayerEnergy(player, getEnergyUsage());
                }
                return false;
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.FORTUNE_ENERGY_CONSUMPTION);
            }

            @Override
            public Enchantment getEnchantment() {
                return Enchantments.FORTUNE;
            }

            @Override
            public int getLevel(@Nonnull ItemStack itemStack) {
                return (int) applyPropertyModifiers(Constants.FORTUNE_ENCHANTMENT_LEVEL);
            }
        }
    }
}