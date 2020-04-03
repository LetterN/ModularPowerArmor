package com.github.lehjr.modularpowerarmor.basemod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MPACreativeTab extends ItemGroup {
    public MPACreativeTab() {
        super(MPAConstants.MOD_ID);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack createIcon() {
        Item item = null;//MPAObjects.INSTANCE.powerArmorHead;

        if (item == null) {
            item = Items.DEBUG_STICK;
        }
        return new ItemStack(item);
    }
}