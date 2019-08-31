package net.machinemuse.powersuits.recipe;

import com.google.gson.JsonObject;
import net.machinemuse.numina.misc.ModCompatibility;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class MPSRecipeConditionFactory implements ICondition {
    static final ResourceLocation NAME = new ResourceLocation(MPSConstants.MODID, "conditional");

    String conditionName;

    public MPSRecipeConditionFactory(String conditionName) {
        this.conditionName = conditionName;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
            switch (conditionName) {
                // Thermal Expansion
                case "thermal_expansion_recipes_enabled":
                    return CommonConfig.RECIPES_USE_THERMAL_EXPANSION.get();

                // EnderIO
                case "enderio_recipes_enabled":
                    return CommonConfig.RECIPES_USE_ENDERIO.get();

                // Original recipe loading code set priority for TechReborn recipes instead of Gregtech or Industrialcraft
                // Tech Reborn
                case "tech_reborn_recipes_enabled":
                    return CommonConfig.RECIPES_USE_TECH_REBORN.get();

                // IC2
                case "ic2_recipes_enabled":
                    return (ModCompatibility.isIndustrialCraftExpLoaded() &&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                            /* !ModCompatibility.isTechRebornLoaded()) */ CommonConfig.RECIPES_USE_IC2.get());
                // IC2 Classic
                case "ic2_classic_recipes_enabled":
                    return (ModCompatibility.isIndustrialCraftClassicLoaded()&&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                            /* !ModCompatibility.isTechRebornLoaded()) */ CommonConfig.RECIPES_USE_IC2.get());
                // Vanilla reciples only as fallback
                case "vanilla_recipes_enabled":
                    return (CommonConfig.RECIPES_USE_VANILLA.get() ||
                                // or as a fallback
                                !((CommonConfig.RECIPES_USE_THERMAL_EXPANSION.get() && ModCompatibility.isThermalExpansionLoaded()) ||
                                        (CommonConfig.RECIPES_USE_ENDERIO.get() && ModCompatibility.isEnderIOLoaded()) ||
                                        (CommonConfig.RECIPES_USE_IC2.get() && ModCompatibility.isIndustrialCraftLoaded()) ||
                                        (CommonConfig.RECIPES_USE_TECH_REBORN.get() && ModCompatibility.isTechRebornLoaded())));
                        // Either enabled in config
                    }
        return false;
    }

    public static class Serializer implements IConditionSerializer<MPSRecipeConditionFactory> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, MPSRecipeConditionFactory value) {
            // Don't think anything else needs to be added here, as this is now working

//            System.out.println("json: " + json.toString());
//            System.out.println("value: " + value.conditionName);
//            json.addProperty("condition", value.conditionName);
        }

        @Override
        public MPSRecipeConditionFactory read(JsonObject json) {
            return new MPSRecipeConditionFactory(JSONUtils.getString(json, "condition"));
        }

        @Override
        public ResourceLocation getID() {
            return MPSRecipeConditionFactory.NAME;
        }
    }
}