package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
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

/**
 * Ported by leon on 10/18/16.
 */
public class SprintAssistModule extends AbstractPowerModule {
    public SprintAssistModule() {
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

            this.ticker.addBaseProperty(MPAConstants.SPRINT_ENERGY_CONSUMPTION, 0, "FE");
            this.ticker.addTradeoffProperty(MPAConstants.SPRINT_ASSIST, MPAConstants.SPRINT_ENERGY_CONSUMPTION, 100);
            this.ticker.addBaseProperty(MPAConstants.SPRINT_SPEED_MULTIPLIER, .01F, "%");
            this.ticker.addTradeoffProperty(MPAConstants.SPRINT_ASSIST, MPAConstants.SPRINT_SPEED_MULTIPLIER, 2.49F);

            this.ticker.addBaseProperty(MPAConstants.SPRINT_ENERGY_CONSUMPTION, 0, "FE");
            this.ticker.addTradeoffProperty(MPAConstants.COMPENSATION, MPAConstants.SPRINT_ENERGY_CONSUMPTION, 20);
            this.ticker.addBaseProperty(MPAConstants.FOOD_COMPENSATION, 0, "%");
            this.ticker.addTradeoffProperty(MPAConstants.COMPENSATION, MPAConstants.FOOD_COMPENSATION, 1);

            this.ticker.addBaseProperty(MPAConstants.WALKING_ENERGY_CONSUMPTION, 0, "FE");
            this.ticker.addTradeoffProperty(MPAConstants.WALKING_ASSISTANCE, MPAConstants.WALKING_ENERGY_CONSUMPTION, 100);
            this.ticker.addBaseProperty(MPAConstants.WALKING_SPEED_MULTIPLIER, 0.01F, "%");
            this.ticker.addTradeoffProperty(MPAConstants.WALKING_ASSISTANCE, MPAConstants.WALKING_SPEED_MULTIPLIER, 1.99F);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IPlayerTickModule) {
                ticker.updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStack) {
                if (player.abilities.isFlying || player.isPassenger() || player.isElytraFlying())
                    onPlayerTickInactive(player, itemStack);

                double horzMovement = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
                if (horzMovement > 0) { // stop doing drain calculations when player hasn't moved
                    if (player.isSprinting()) {
                        double exhaustion = Math.round(horzMovement * 100.0F) * 0.01;
                        double sprintCost = applyPropertyModifiers(MPAConstants.SPRINT_ENERGY_CONSUMPTION);
                        if (sprintCost < totalEnergy) {
                            double sprintMultiplier = applyPropertyModifiers(MPAConstants.SPRINT_SPEED_MULTIPLIER);
                            double exhaustionComp = applyPropertyModifiers(MPAConstants.FOOD_COMPENSATION);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (sprintCost * horzMovement * 5));
                            MovementManager.INSTANCE.setMovementModifier(itemStack, sprintMultiplier, player);
                            player.getFoodStats().addExhaustion((float) (-0.01 * exhaustion * exhaustionComp));
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    } else {
                        double cost = applyPropertyModifiers(MPAConstants.WALKING_ENERGY_CONSUMPTION);
                        if (cost < totalEnergy) {
                            double walkMultiplier = applyPropertyModifiers(MPAConstants.WALKING_SPEED_MULTIPLIER);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (cost * horzMovement * 5));
                            MovementManager.INSTANCE.setMovementModifier(itemStack, walkMultiplier, player);
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, @Nonnull ItemStack itemStack) {
                MovementManager.INSTANCE.setMovementModifier(itemStack, 0, player);
            }
        }
    }
}