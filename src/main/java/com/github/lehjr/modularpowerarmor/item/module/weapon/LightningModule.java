package com.github.lehjr.modularpowerarmor.item.module.weapon;


import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.util.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.util.heat.HeatUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Created by User: Andrew2448
 * 5:56 PM 6/14/13
 */
public class LightningModule extends AbstractPowerModule {
    public LightningModule() {
        super();
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClickie;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClickie = new RightClickie(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, MPASettings::getModuleConfig);
            this.rightClickie.addBaseProperty(MPAConstants.ENERGY_CONSUMPTION, 4900000, "FE");
            this.rightClickie.addBaseProperty(MPAConstants.HEAT_EMISSION, 100, "");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClickie));
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (hand == Hand.MAIN_HAND) {
                    int energyConsumption = getEnergyUsage();
                    if (energyConsumption < ElectricItemUtils.getPlayerEnergy(playerIn)) {
                        if (!worldIn.isRemote()) {
                            double range = 64;

                            RayTraceResult raytraceResult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY, range);
                            if (raytraceResult != null && raytraceResult.getType() != RayTraceResult.Type.MISS) {
                                if(worldIn instanceof ServerWorld) {
                                    ElectricItemUtils.drainPlayerEnergy(playerIn, energyConsumption);
                                    HeatUtils.heatPlayer(playerIn, applyPropertyModifiers(MPAConstants.HEAT_EMISSION));
                                    LightningBoltEntity sparkie = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, worldIn);
                                    sparkie.setPosition(raytraceResult.getHitVec().x, raytraceResult.getHitVec().y, raytraceResult.getHitVec().z);
                                    sparkie.setCaster((ServerPlayerEntity) playerIn);
                                    ((ServerWorld) worldIn).addEntityIfNotDuplicate(sparkie);
                                }
                            }
                        }
                        return ActionResult.resultSuccess(itemStackIn);
                    }
                }
                return ActionResult.resultPass(itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) Math.round(applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION));
            }
        }
    }
}