package net.machinemuse.powersuits.item.module.energy.generation;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.tickable.IPlayerTickModule;
import net.machinemuse.numina.capabilities.module.tickable.PlayerTickModule;
import net.machinemuse.numina.capabilities.module.toggleable.IToggleableModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BasicSolarGeneratorModule extends AbstractPowerModule {
    public BasicSolarGeneratorModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundNBT nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.HEADONLY, CommonConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(MPSConstants.ENERGY_GENERATION_DAY, 15000, "RF");
            this.ticker.addBasePropertyDouble(MPSConstants.ENERGY_GENERATION_NIGHT, 1500, "RF");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack itemStack) {
                World world = player.world;
                boolean isRaining, canRain = true;
                if (world.getGameTime() % 20 == 0) {
                    canRain = !(world.getBiome(player.getPosition()).getPrecipitation() == Biome.RainType.NONE);
                }

                isRaining = canRain && (world.isRaining() || world.isThundering());
                boolean sunVisible = world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().add(0, 1, 0));
                boolean moonVisible = !world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().add(0, 1, 0));
                if (!world.isRemote && world.dimension.hasSkyLight() && (world.getGameTime() % 80) == 0) {
                    double lightLevelScaled = (world.getLightFor(LightType.SKY, player.getPosition().up()) - world.getSkylightSubtracted())/15D;
                    if (sunVisible) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (applyPropertyModifiers(MPSConstants.ENERGY_GENERATION_DAY) * lightLevelScaled));
                    } else if (moonVisible) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (applyPropertyModifiers(MPSConstants.ENERGY_GENERATION_NIGHT) * lightLevelScaled));
                    }
                }
            }
        }
    }
}