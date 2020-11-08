package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.client.sound.MPASoundDictionary;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibSettings;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class SwimAssistModule extends AbstractPowerModule {
    public SwimAssistModule() {
        super();
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.LEGSONLY, MPASettings::getModuleConfig);
            this.ticker.addTradeoffProperty(MPAConstants.THRUST, MPAConstants.ENERGY_CONSUMPTION, 1000, "FE");
            this.ticker.addTradeoffProperty(MPAConstants.THRUST, MPAConstants.SWIM_BOOST_AMOUNT, 1, "m/s");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                if (player.isInWater() && !(player.isPassenger())) {
                    PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                    if (playerInput.moveForward != 0 || playerInput.moveStrafe != 0 || playerInput.jumpKey || playerInput.sneakKey) {
                        double moveRatio = 0;
                        if (playerInput.moveForward != 0) {
                            moveRatio += playerInput.moveForward * playerInput.moveForward;
                        }
                        if (playerInput.moveStrafe != 0) {
                            moveRatio += playerInput.moveStrafe * playerInput.moveStrafe;
                        }
                        if (playerInput.jumpKey || playerInput.sneakKey) {
                            moveRatio += 0.2 * 0.2;
                        }
                        double swimAssistRate = applyPropertyModifiers(MPAConstants.SWIM_BOOST_AMOUNT) * 0.05 * moveRatio;
                        double swimEnergyConsumption = applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
                        if (swimEnergyConsumption < ElectricItemUtils.getPlayerEnergy(player)) {
                            if (player.world.isRemote && MPALibSettings.useSounds()) {
                                Musique.playerSound(player, MPASoundDictionary.SWIM_ASSIST, SoundCategory.PLAYERS, 1.0f, 1.0f, true);
                            }
                            MovementManager.INSTANCE.thrust(player, swimAssistRate, true);
                        } else {
                            if (player.world.isRemote && MPALibSettings.useSounds()) {
                                Musique.stopPlayerSound(player, MPASoundDictionary.SWIM_ASSIST);
                            }
                        }
                    } else {
                        if (player.world.isRemote && MPALibSettings.useSounds()) {
                            Musique.stopPlayerSound(player, MPASoundDictionary.SWIM_ASSIST);
                        }
                    }
                } else {
                    if (player.world.isRemote && MPALibSettings.useSounds()) {
                        Musique.stopPlayerSound(player, MPASoundDictionary.SWIM_ASSIST);
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack item) {
                if (player.world.isRemote && MPALibSettings.useSounds()) {
                    Musique.stopPlayerSound(player, MPASoundDictionary.SWIM_ASSIST);
                }
            }
        }
    }
}