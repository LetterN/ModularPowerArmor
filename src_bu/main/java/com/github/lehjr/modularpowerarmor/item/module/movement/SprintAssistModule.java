package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ported by leon on 10/18/16.
 */
public class SprintAssistModule extends AbstractPowerModule {
    public SprintAssistModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.LEGSONLY, CommonConfig.moduleConfig);

            this.ticker.addBasePropertyDouble(Constants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.SPRINT_ASSIST, Constants.SPRINT_ENERGY_CONSUMPTION, 100);
            this.ticker.addBasePropertyDouble(Constants.SPRINT_SPEED_MULTIPLIER, .01, "%");
            this.ticker.addTradeoffPropertyDouble(Constants.SPRINT_ASSIST, Constants.SPRINT_SPEED_MULTIPLIER, 2.49);

            this.ticker.addBasePropertyDouble(Constants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.COMPENSATION, Constants.SPRINT_ENERGY_CONSUMPTION, 20);
            this.ticker.addBasePropertyDouble(Constants.FOOD_COMPENSATION, 0, "%");
            this.ticker.addTradeoffPropertyDouble(Constants.COMPENSATION, Constants.FOOD_COMPENSATION, 1);

            this.ticker.addBasePropertyDouble(Constants.WALKING_ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.WALKING_ASSISTANCE, Constants.WALKING_ENERGY_CONSUMPTION, 100);
            this.ticker.addBasePropertyDouble(Constants.WALKING_SPEED_MULTIPLIER, 0.01, "%");
            this.ticker.addTradeoffPropertyDouble(Constants.WALKING_ASSISTANCE, Constants.WALKING_SPEED_MULTIPLIER, 1.99);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IPlayerTickModule) {
                System.out.println("ismodule online: " + ticker.isAllowed());
                ticker.updateFromNBT();
                System.out.println("ismodule online (after update): " + ticker.isAllowed());
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
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
                        double sprintCost = applyPropertyModifiers(Constants.SPRINT_ENERGY_CONSUMPTION);
                        if (sprintCost < totalEnergy) {
                            double sprintMultiplier = applyPropertyModifiers(Constants.SPRINT_SPEED_MULTIPLIER);
                            double exhaustionComp = applyPropertyModifiers(Constants.FOOD_COMPENSATION);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (sprintCost * horzMovement * 5));
                            MovementManager.setMovementModifier(itemStack, sprintMultiplier, player);
                            player.getFoodStats().addExhaustion((float) (-0.01 * exhaustion * exhaustionComp));
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    } else {
                        double cost = applyPropertyModifiers(Constants.WALKING_ENERGY_CONSUMPTION);
                        if (cost < totalEnergy) {
                            double walkMultiplier = applyPropertyModifiers(Constants.WALKING_SPEED_MULTIPLIER);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (cost * horzMovement * 5));
                            MovementManager.setMovementModifier(itemStack, walkMultiplier, player);
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, @Nonnull ItemStack itemStack) {
                MovementManager.setMovementModifier(itemStack, 0, player);
            }
        }
    }
}