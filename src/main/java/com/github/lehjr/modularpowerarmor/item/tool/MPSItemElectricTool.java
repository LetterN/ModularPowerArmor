package com.github.lehjr.modularpowerarmor.item.tool;

import com.github.lehjr.modularpowerarmor.utils.AdditionalInfo;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Ported to Java by lehjr on 11/4/16.
 */
public class MPSItemElectricTool extends ItemTool {
    public static final Set<Block> blocksEffectiveOn = Sets.newHashSet(new Block[]{
            Blocks.COBBLESTONE, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE_SLAB2, Blocks.STONE, Blocks.SANDSTONE,
            Blocks.MOSSY_COBBLESTONE, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
            Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE,
            Blocks.LIT_REDSTONE_ORE, Blocks.RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.ACTIVATOR_RAIL});

    protected MPSItemElectricTool(float attackDamageIn, float attackSpeedIn, ToolMaterial material) {
        super(attackDamageIn, attackSpeedIn, material, blocksEffectiveOn);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> currentTipList, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(stack, worldIn, currentTipList, flagIn);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !oldStack.equals(newStack);
    }
}