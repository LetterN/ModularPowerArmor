package com.github.lehjr.modularpowerarmor.client.model.item.armor;

import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import net.minecraft.client.model.ModelBiped;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 10:01 PM, 11/07/13
 */
public class ArmorModelInstance {
    private static ModelBiped instance = null;

    public static ModelBiped getInstance() {
        if (instance == null) {
            if (ModCompatibility.isRenderPlayerAPILoaded()) {
                try {
                    MPALibLogger.logger.info("Attempting to load SmartMoving armor model.");
                    instance = Class.forName("com.github.lehjr.modularpowerarmor.client.model.item.armor.SMovingArmorModel").asSubclass(ModelBiped.class).newInstance();
                    MPALibLogger.logger.info("SmartMoving armor model loaded successfully!");
                } catch (Exception e) {
                    MPALibLogger.logger.info("Smart Moving armor model did not loadButton successfully. Either Smart Moving is not installed, or there was another problem.");
                    instance = new HighPolyArmor();
                }
            }
            else {
                instance = new HighPolyArmor();
            }
        }
        return instance;
    }
}