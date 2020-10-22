package com.github.lehjr.modularpowerarmor.item.module.movement;


import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Created by Eximius88 on 2/3/14.
 */
public class DimensionalRiftModule extends AbstractPowerModule {
    public DimensionalRiftModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TOOLONLY, MPASettings.getModuleConfig());
            rightClick.addBaseProperty(MPAConstants.HEAT_GENERATION, 55);
            rightClick.addBaseProperty(MPAConstants.ENERGY_CONSUMPTION, 200000);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClick));
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (!playerIn.isPassenger() && !playerIn.isBeingRidden() && playerIn.isNonBoss() && !playerIn.world.isRemote()) {
                    BlockPos coords = playerIn.getBedPosition().isPresent() ? playerIn.getBedPosition().get() : playerIn.world.getSpawnPoint();

                    while (!worldIn.isAirBlock(coords) && !worldIn.isAirBlock(coords.up())) {
                        coords = coords.up();
                    }

                    int energyConsumption = (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
                    int playerEnergy = ElectricItemUtils.getPlayerEnergy(playerIn);
                    if (playerEnergy >= energyConsumption) {
                        playerIn.changeDimension(DimensionType.OVERWORLD, new CommandTeleporter(coords));
                        ElectricItemUtils.drainPlayerEnergy(playerIn, getEnergyUsage());
                        HeatUtils.heatPlayer(playerIn, applyPropertyModifiers(MPAConstants.HEAT_GENERATION));
                        return ActionResult.resultSuccess(itemStackIn);
                    }
                }
                return ActionResult.resultPass(itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
            }
        }
    }

    private static class CommandTeleporter implements ITeleporter {
        private final BlockPos targetPos;

        private CommandTeleporter(BlockPos targetPos) {
            this.targetPos = targetPos;
        }

        @Override
        public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
            entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
            return repositionEntity.apply(false);
        }
    }
}
