package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class JumpAssistModule extends AbstractPowerModule {
    public JumpAssistModule() {
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        PlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.LEGSONLY, MPASettings::getModuleConfig);
            this.ticker.addSimpleTradeoff(MPAConstants.POWER, MPAConstants.ENERGY_CONSUMPTION, "FE", 0, 250, MPAConstants.MULTIPLIER, "%", 1, 4);
            this.ticker.addSimpleTradeoff(MPAConstants.COMPENSATION, MPAConstants.ENERGY_CONSUMPTION, "FE", 0, 50, MPAConstants.FOOD_COMPENSATION, "%", 0, 1);
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
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                if (playerInput.jumpKey) {
                    double multiplier = MovementManager.INSTANCE.getPlayerJumpMultiplier(player);
                    if (multiplier > 0) {
                        player.setMotion(player.getMotion().add(0, 0.15 * Math.min(multiplier, 1), 0));
                        MovementManager.INSTANCE.setPlayerJumpTicks(player, multiplier - 1);
                    }
                    player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                } else {
                    MovementManager.INSTANCE.setPlayerJumpTicks(player, 0);
                }
                PlayerUtils.resetFloatKickTicks(player);
            }
        }
    }
}