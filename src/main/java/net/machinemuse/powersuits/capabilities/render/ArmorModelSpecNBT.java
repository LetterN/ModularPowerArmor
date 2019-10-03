package net.machinemuse.powersuits.capabilities.render;

import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.render.IArmorModelSpecNBT;
import net.machinemuse.numina.capabilities.render.ModelSpecNBT;
import net.machinemuse.numina.client.render.modelspec.*;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ArmorModelSpecNBT extends ModelSpecNBT implements IArmorModelSpecNBT {
    public ArmorModelSpecNBT(@Nonnull ItemStack itemStackIn) {
        super(itemStackIn);
    }

    @Override
    public EnumSpecType getSpecType() {
        CompoundNBT itemTag = MuseNBTUtils.getMuseItemTag(getItemStack());
        CompoundNBT renderTag = itemTag.getCompound(NuminaConstants.TAG_RENDER);

        try {
            TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompound(NuminaConstants.NBT_TEXTURESPEC_TAG));
            if (partSpec != null) {
                return EnumSpecType.ARMOR_SKIN;
            }
        } catch (Exception ignored) {
        }

        SpecBase modelSpec = ModelRegistry.getInstance().getModel(renderTag.getCompound(NuminaConstants.TAG_MODEL));
        if (modelSpec instanceof ModelSpec) {
            return EnumSpecType.ARMOR_MODEL;
        }
        return EnumSpecType.NONE;
    }

    @Override
    public CompoundNBT getDefaultRenderTag() {
        if (getItemStack().isEmpty())
            return new CompoundNBT();

        List<CompoundNBT> prefArray = new ArrayList<>();

        // ModelPartSpecs
        ListNBT specList = new ListNBT();

        // TextureSpecBase (only one texture visible at a time)
        CompoundNBT texSpecTag = new CompoundNBT();

        // List of EnumColour indexes
        List<Integer> colours = new ArrayList<>();

        // temp data holder
        CompoundNBT tempNBT;

        EquipmentSlotType slot = getItemStack().getEquipmentSlot();

        for (SpecBase spec : ModelRegistry.getInstance().getSpecs()) {
            // Only generate NBT data from Specs marked as "default"
            if (spec.isDefault()) {
                if (getItemStack().getItem() instanceof ArmorItem) {
                    colours = addNewColourstoList(colours, spec.getColours()); // merge new color int arrays in

                    // Armor Skin
                    if (spec.getSpecType().equals(EnumSpecType.ARMOR_SKIN) && spec.get(slot.getName()) != null) {
                        // only a single texture per equipment slot can be used at a time
                        texSpecTag = spec.get(slot.getName()).multiSet(new CompoundNBT(),
                                getNewColourIndex(colours, spec.getColours(), spec.get(slot.getName()).getDefaultColourIndex()));
                    }

                    // Armor models
                    else if (spec.getSpecType().equals(EnumSpecType.ARMOR_MODEL) && CommonConfig.COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS.get()) {
                        for (PartSpecBase partSpec : spec.getPartSpecs()) {
                            if (partSpec.getBinding().getSlot() == slot) {
                                /*
                                // jet pack model not displayed by default
                                if (partSpec.binding.getItemState().equals("all") ||
                                        (partSpec.binding.getItemState().equals("jetpack") &&
                                                ModuleManager.INSTANCE.itemHasModule(stack, MPSModuleConstants.MODULE_JETPACK__DATANAME))) { */
                                prefArray.add(((ModelPartSpec) partSpec).multiSet(new CompoundNBT(),
                                        getNewColourIndex(colours, spec.getColours(), partSpec.getDefaultColourIndex()),
                                        ((ModelPartSpec) partSpec).getGlow()));
                                /*} */
                            }
                        }
                    }
                }
            }
        }

        CompoundNBT nbt = new CompoundNBT();
        for (CompoundNBT elem : prefArray) {
            nbt.put(elem.getString(NuminaConstants.TAG_MODEL) + "." + elem.getString(NuminaConstants.TAG_PART), elem);
        }

        if (!specList.isEmpty())
            nbt.put(NuminaConstants.NBT_SPECLIST_TAG, specList);

        if (!texSpecTag.isEmpty())
            nbt.put(NuminaConstants.NBT_TEXTURESPEC_TAG, texSpecTag);

        nbt.put(NuminaConstants.TAG_COLOURS, new IntArrayNBT(colours));

        return nbt;
    }

    @Override
    public String getArmorTexture() {
        CompoundNBT itemTag = MuseNBTUtils.getMuseItemTag(getItemStack());
        CompoundNBT renderTag = itemTag.getCompound(NuminaConstants.TAG_RENDER);
        try {
            TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompound(NuminaConstants.NBT_TEXTURESPEC_TAG));
            return partSpec.getTextureLocation();
        } catch (Exception ignored) {
            return NuminaConstants.BLANK_ARMOR_MODEL_PATH;
        }
    }
}
