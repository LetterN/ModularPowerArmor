package net.machinemuse.powersuits.client.model.item;


/**
 * Author: MachineMuse (Claire Semple)
 * Created: 10:01 PM, 11/07/13
 */
public class ArmorModelInstance {
    private static HighPolyArmor instance = null;

    public static HighPolyArmor getInstance() {
        if (instance == null) {
//            if (ModCompatibility.isRenderPlayerAPILoaded()) {
//                try {
//                    MuseLogger.logInfo("Attempting to load SmartMoving armor model.");
//                    instance = Class.forName("net.machinemuse.powersuits.client.model.item.armor.SMovingArmorModel").asSubclass(ModelBiped.class).newInstance();
//                    MuseLogger.logInfo("SmartMoving armor model loaded successfully!");
//                } catch (Exception e) {
//                    MuseLogger.logInfo("Smart Moving armor model did not loadButton successfully. Either Smart Moving is not installed, or there was another problem.");
//                    instance = new HighPolyArmor();
//                }
//            }
//            else {
                instance = new HighPolyArmor();
//            }
        }
        return instance;
    }
}