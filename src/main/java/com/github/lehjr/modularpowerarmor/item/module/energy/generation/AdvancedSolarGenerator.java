package com.github.lehjr.modularpowerarmor.item.module.energy.generation;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Eximius88 on 1/12/14.
 */


/**
 * Created by Eximius88 on 1/12/14.
 */
public class AdvancedSolarGenerator extends AbstractPowerModule {
    public AdvancedSolarGenerator(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.HEADONLY, MPAConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(Constants.ENERGY_GENERATION_DAY, 45000, "RF");
            this.ticker.addBasePropertyDouble(Constants.ENERGY_GENERATION_NIGHT, 1500, "RF");
            this.ticker.addBasePropertyDouble(Constants.HEAT_GENERATION_DAY, 15);
            this.ticker.addBasePropertyDouble(Constants.HEAT_GENERATION_NIGHT, 5);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) ticker;
            }
            return null;
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemPowerArmorHelmet) {
                    World world = player.world;
                    boolean isRaining, canRain = true;
                    if (world.getTotalWorldTime() % 20 == 0) {
                        canRain = world.getBiome(player.getPosition()).canRain();
                    }
                    isRaining = canRain && (world.isRaining() || world.isThundering());
                    boolean sunVisible = world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().up());
                    boolean moonVisible = !world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().up());

                    if (!world.isRemote && world.provider.hasSkyLight() && (world.getTotalWorldTime() % 80) == 0) {
                        double lightLevelScaled = (world.getLightFor(EnumSkyBlock.SKY, player.getPosition().up()) - world.getSkylightSubtracted())/15D;

                        if (sunVisible) {
                            ElectricItemUtils.givePlayerEnergy(player, (int) (applyPropertyModifiers(Constants.ENERGY_GENERATION_DAY) * lightLevelScaled));
                            HeatUtils.heatPlayer(player, applyPropertyModifiers(Constants.HEAT_GENERATION_DAY) * lightLevelScaled / 2);

                        } else if (moonVisible) {
                            ElectricItemUtils.givePlayerEnergy(player, (int) (applyPropertyModifiers(Constants.ENERGY_GENERATION_NIGHT) * lightLevelScaled));
                            HeatUtils.heatPlayer(player, applyPropertyModifiers(Constants.HEAT_GENERATION_NIGHT) * lightLevelScaled / 2);
                        }
                    }
                }
            }
        }
    }
}