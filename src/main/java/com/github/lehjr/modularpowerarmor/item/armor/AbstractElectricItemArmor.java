package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.capabilities.ModularPowerCap;
import com.github.lehjr.mpalib.basemod.MPALibConstants;
import com.github.lehjr.mpalib.basemod.ModObjects;
import com.github.lehjr.mpalib.client.model.item.armor.ArmorModelInstance;
import com.github.lehjr.mpalib.client.model.item.armor.HighPolyArmor;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.util.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.util.capabilities.render.modelspec.EnumSpecType;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.util.string.AdditionalInfo;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractElectricItemArmor extends ArmorItem {
    public static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()};

    public AbstractElectricItemArmor(EquipmentSlotType slots, Properties builder) {
        super(MPAArmorMaterial.EMPTY_ARMOR, slots, builder);
    }

    public AbstractElectricItemArmor(EquipmentSlotType slots) {
        super(MPAArmorMaterial.EMPTY_ARMOR, slots, new Item.Properties()
                .maxStackSize(1)
                .group(MPAObjects.creativeTab)
                .defaultMaxDamage(-1)
                .setNoRepair());
    }

    //    /*
//        returning a value higher than 0 is applied damage to the armor, even if the armor is not setup to take damage.
//        TODO: does this even work???? Is this even needed?
//
//      */
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int enerConsum = (int) Math.round(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                Pair<Integer, Integer> range = ((IModularItem) iItemHandler).getRangeForCategory(EnumModuleCategory.ARMOR);
                double energyUsed = 0;
                for (int x = range.getKey(); x < range.getRight(); x ++) {
                    energyUsed += iItemHandler.getStackInSlot(x).getCapability(PowerModuleCapability.POWER_MODULE)
                            .map(pm->pm.applyPropertyModifiers(MPAConstants.ARMOR_ENERGY_CONSUMPTION)).orElse(0D);
                }
                return energyUsed;
            }
            return 0D;
        }).orElse(0D));

        // protects as long as there is energy to drain I guess
        if (enerConsum > 0 && entity instanceof LivingEntity) {
            ElectricItemUtils.drainPlayerEnergy(entity, enerConsum);
        }
        return 0;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot != this.slot) {
            return multimap;
        }

        AtomicDouble armorVal = new AtomicDouble(0);
        AtomicDouble toughnessVal = new AtomicDouble(0);
        AtomicDouble knockbackResistance = new AtomicDouble(0);

        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                // Armor **should** only occupy one slot
                Pair<Integer, Integer> range = ((IModularItem) iItemHandler).getRangeForCategory(EnumModuleCategory.ARMOR);
                if (range != null) {
                    for (int i = range.getLeft(); i < range.getRight(); i++) {
                        iItemHandler.getStackInSlot(i).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm -> {
                            if (pm.isAllowed()) {
                                // physical armor and hybrid energy/physical armor
                                double armorDouble = pm.applyPropertyModifiers(MPAConstants.ARMOR_VALUE_PHYSICAL);
                                double knockBack = 0;

                                if (pm instanceof IToggleableModule && ((IToggleableModule) pm).isModuleOnline()) {
                                    armorDouble += pm.applyPropertyModifiers(MPAConstants.ARMOR_VALUE_ENERGY);
                                }

                                if (armorDouble > 0) {
                                    knockBack = pm.applyPropertyModifiers(MPAConstants.KNOCKBACK_RESISTANCE);
                                    armorVal.getAndAdd(armorDouble);
                                }

                                if (knockBack > 0) {
                                    knockbackResistance.getAndAdd(knockBack);
                                }
                            }
                        });
                    }
                }
            }
        });

        // FIXME creates npe, but multimap is not null
//        multimap.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor modifier", armorVal.get(), AttributeModifier.Operation.ADDITION));
//        multimap.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Knockback resistance", knockbackResistance.get(), AttributeModifier.Operation.ADDITION));
//        multimap.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor toughness", toughnessVal.get(), AttributeModifier.Operation.ADDITION));

//        multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", 6, AttributeModifier.Operation.ADDITION));
//        multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Knockback resistance", 0.25, AttributeModifier.Operation.ADDITION));
//        multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", 2.5, AttributeModifier.Operation.ADDITION));

        return multimap;
    }

    /**
     * This will work for the vanilla type models. This will not work for high polly models due to how the rendering works
     * @param armor
     * @param entity
     * @param equipmentSlotType
     * @param type
     * @return
     */
    @Nullable
    @Override
    public String getArmorTexture(ItemStack armor, Entity entity, EquipmentSlotType equipmentSlotType, String type) {
        if (type == "overlay") { // this is to allow a tint to be applied tot the armor
            return MPALibConstants.BLANK_ARMOR_MODEL_PATH;
        }

        return armor.getCapability(ModelSpecNBTCapability.RENDER).map(spec->
                spec instanceof IArmorModelSpecNBT ?
                        ((IArmorModelSpecNBT) spec).getArmorTexture() :
                        AtlasTexture.LOCATION_BLOCKS_TEXTURE.toString())
                .orElse(AtlasTexture.LOCATION_BLOCKS_TEXTURE.toString());
    }

    /**
     * This is probably not going to work for the high polly models. Instead this will need to be done with an armor layer for more control
     * @param entityLiving
     * @param itemStack
     * @param armorSlot
     * @param _default
     * @return
     */
    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        if (!(entityLiving instanceof PlayerEntity)) {
            return _default;
        }

        return itemStack.getCapability(ModelSpecNBTCapability.RENDER).map(spec-> {
            CompoundNBT renderTag = spec.getRenderTag();
//            PlayerEntity player = (PlayerEntity) entityLiving;
////            // only triggered by this client's player looking at their own equipped armor
//            if (renderTag == null || renderTag.isEmpty() && player == Minecraft.getInstance().player) {
//                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
//                    if (player.inventory.getStackInSlot(i).equals(itemStack)) {
//                        renderTag = spec.getDefaultRenderTag();
//                        if (renderTag != null && !renderTag.isEmpty()) {
//                            spec.setRenderTag(renderTag, MPALIbConstants.TAG_RENDER);
//                            MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(i, MPALIbConstants.TAG_RENDER, renderTag));
//                        }
//                        break;
//                    }
//                }
//            }

            if (spec.getRenderTag() != null &&
                    (spec.getSpecType() == EnumSpecType.ARMOR_SKIN || spec.getSpecType() == EnumSpecType.NONE)) {
                return _default;
            }

            BipedModel model = ArmorModelInstance.getInstance();
            ItemStack chestplate = entityLiving.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if (chestplate.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler ->
                    iItemHandler instanceof IModularItem && ((IModularItem) iItemHandler)
                            .isModuleOnline(MPARegistryNames.ACTIVE_CAMOUFLAGE_MODULE_REGNAME)).orElse(false)) {
                ((HighPolyArmor) model).setVisibleSection(null);
            } else {
                if (renderTag != null) {
                    ((HighPolyArmor) model).setVisibleSection(slot);
                    ((HighPolyArmor) model).setRenderSpec(renderTag);
                }
            }
            return model;
        }).orElse(_default);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ModularPowerCap(stack, this.slot);
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}