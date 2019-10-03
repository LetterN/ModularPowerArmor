package net.machinemuse.powersuits.item.module.miningenhancement;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.module.blockbreaking.IBlockBreakingModule;
import net.machinemuse.numina.capabilities.module.miningenhancement.IMiningEnhancementModule;
import net.machinemuse.numina.capabilities.module.miningenhancement.MiningEnhancement;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Eximius88 on 1/29/14.
 */
public class AOEPickUpgradeModule extends AbstractPowerModule {
    public AOEPickUpgradeModule(String regName) {
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

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.miningEnhancement = new Enhancement(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.miningEnhancement.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.miningEnhancement.addTradeoffPropertyDouble(MPSConstants.DIAMETER, MPSConstants.ENERGY_CONSUMPTION, 9500);
            this.miningEnhancement.addIntTradeoffProperty(MPSConstants.DIAMETER, MPSConstants.AOE_MINING_RADIUS, 5, "m", 2, 1);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> miningEnhancement));
        }

        class Enhancement extends MiningEnhancement {
            public Enhancement(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public boolean onBlockStartBreak(ItemStack itemStack, BlockPos posIn, PlayerEntity player) {
                if (player.world.isRemote)
                    return false; // fixme : check?
                AtomicBoolean harvested = new AtomicBoolean(false);
                RayTraceResult rayTraceResult = rayTrace(player.world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
                if (rayTraceResult == null || rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
                    return false;
                }
                int radius = (int) (applyPropertyModifiers(MPSConstants.AOE_MINING_RADIUS) - 1) / 2;
                if (radius == 0)
                    return false;

                Direction side = ((BlockRayTraceResult) rayTraceResult).getFace();
                Stream<BlockPos> posList;
                switch (side) {
                    case UP:
                    case DOWN:
                        posList = BlockPos.getAllInBox(posIn.north(radius).west(radius), posIn.south(radius).east(radius));
                        break;

                    case EAST:
                    case WEST:
                        posList = BlockPos.getAllInBox(posIn.up(radius).north(radius), posIn.down(radius).south(radius));
                        break;

                    case NORTH:
                    case SOUTH:
                        posList = BlockPos.getAllInBox(posIn.up(radius).west(radius), posIn.down(radius).east(radius));
                        break;

                    default:
                        posList = new ArrayList<BlockPos>().stream();
                }
                int energyUsage = this.getEnergyUsage();
                AtomicInteger blocksBroken = new AtomicInteger(0);
                posList.forEach(blockPos-> {
                    BlockState state = player.world.getBlockState(blockPos);
                    int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
                    itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(modeChanging -> {
                        if (modeChanging instanceof IModeChangingItem) {
                            for (ItemStack blockBreakingModule : ((IModeChangingItem) modeChanging).getInstalledModulesOfType(IBlockBreakingModule.class)) {

                                if (blockBreakingModule.getCapability(PowerModuleCapability.POWER_MODULE).map(b -> {
                                    if(b instanceof IBlockBreakingModule) {
                                        if (((IBlockBreakingModule) b).canHarvestBlock(itemStack, state, player, blockPos, playerEnergy - energyUsage)) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }).orElse(false)) {
                                    if (posIn == blockPos) { // center block
                                        harvested.set(true);
                                    }
                                    blocksBroken.getAndAdd(1);
                                    Block.replaceBlock(state, Blocks.AIR.getDefaultState(), player.world, blockPos, Constants.BlockFlags.DEFAULT);
                                    ElectricItemUtils.drainPlayerEnergy(player,
                                            blockBreakingModule.getCapability(PowerModuleCapability.POWER_MODULE).map(m -> {
                                                if (m instanceof IBlockBreakingModule) {
                                                    return ((IBlockBreakingModule) m).getEnergyUsage();
                                                }
                                                return 0;
                                            }).orElse(0) + energyUsage);
                                    break;
                                }
                            }
                        }
                    });
                });
                return harvested.get();
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}