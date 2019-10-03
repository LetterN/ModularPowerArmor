package net.machinemuse.powersuits.item.module.miningenhancement;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.enchantment.IEnchantmentModule;
import net.machinemuse.numina.capabilities.module.miningenhancement.IMiningEnhancementModule;
import net.machinemuse.numina.capabilities.module.miningenhancement.MiningEnhancement;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FortuneModule extends AbstractPowerModule {
    public FortuneModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IMiningEnhancementModule miningEnhancement;
        IEnchantmentModule enchantmentModule;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.miningEnhancement = new Enhancement(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.miningEnhancement.addBasePropertyDouble(MPSConstants.FORTUNE_ENERGY_CONSUMPTION, 500, "RF");
            this.miningEnhancement.addTradeoffPropertyDouble(MPSConstants.ENCHANTMENT_LEVEL, MPSConstants.FORTUNE_ENERGY_CONSUMPTION, 9500);
            this.miningEnhancement.addIntTradeoffProperty(MPSConstants.ENCHANTMENT_LEVEL, MPSConstants.FORTUNE_ENCHANTMENT_LEVEL, 3, "", 1, 1);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> miningEnhancement));
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
            public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
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
                return (int) applyPropertyModifiers(MPSConstants.FORTUNE_ENERGY_CONSUMPTION);
            }

            @Override
            public Enchantment getEnchantment() {
                return Enchantments.FORTUNE;
            }

            @Override
            public int getLevel(@Nonnull ItemStack itemStack) {
                return (int) applyPropertyModifiers(MPSConstants.FORTUNE_ENCHANTMENT_LEVEL);
            }
        }
    }
}