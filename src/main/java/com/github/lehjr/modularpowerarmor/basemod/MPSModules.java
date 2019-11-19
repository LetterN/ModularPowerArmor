//package com.github.lehjr.modularpowerarmor.basemod;
//
//import com.github.lehjr.modularpowerarmor.config.MPAConfig;
//import com.github.lehjr.modularpowerarmor.item.module.armor.DiamondPlatingModule;
//import com.github.lehjr.modularpowerarmor.item.module.armor.EnergyShieldModule;
//import com.github.lehjr.modularpowerarmor.item.module.armor.IronPlatingModule;
//import com.github.lehjr.modularpowerarmor.item.module.armor.LeatherPlatingModule;
//import com.github.lehjr.modularpowerarmor.item.module.cosmetic.TransparentArmorModule;
//import com.github.lehjr.modularpowerarmor.item.module.energy.generation.AdvancedSolarGenerator;
//import com.github.lehjr.modularpowerarmor.item.module.energy.generation.KineticGeneratorModule;
//import com.github.lehjr.modularpowerarmor.item.module.energy.generation.SolarGeneratorModule;
//import com.github.lehjr.modularpowerarmor.item.module.energy.generation.ThermalGeneratorModule;
//import com.github.lehjr.modularpowerarmor.item.module.environmental.*;
//import com.github.lehjr.modularpowerarmor.item.module.miningenhancement.*;
//import com.github.lehjr.modularpowerarmor.item.module.movement.*;
//import com.github.lehjr.modularpowerarmor.item.module.special.ClockModule;
//import com.github.lehjr.modularpowerarmor.item.module.special.CompassModule;
//import com.github.lehjr.modularpowerarmor.item.module.special.InvisibilityModule;
//import com.github.lehjr.modularpowerarmor.item.module.special.MagnetModule;
//import com.github.lehjr.modularpowerarmor.item.module.tool.*;
//import com.github.lehjr.modularpowerarmor.item.module.vision.BinocularsModule;
//import com.github.lehjr.modularpowerarmor.item.module.vision.NightVisionModule;
//import com.github.lehjr.modularpowerarmor.item.module.vision.ThaumGogglesModule;
//import com.github.lehjr.modularpowerarmor.item.module.weapon.*;
//import com.github.lehjr.mpalib.basemod.MPALibLogger;
//import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
//import com.github.lehjr.mpalib.misc.ModCompatibility;
//
//public class MPSModules {
//    public static void addModule(IPowerModule module) {
//        if (MPAConfig.INSTANCE.getModuleAllowedorDefault(module.getDataName(), true))
//            ModuleManager.INSTANCE.addModule(module);
//    }
//
//    /**
//     * Load all the modules in the config file into memory. Eventually. For now,
//     * they are hardcoded.
//     */
//    public static void loadPowerModules() {
//        // FIXME: these need to be sorted
//        /* Armor -------------------------------- */
//        addModule(new LeatherPlatingModule(EnumModuleTarget.ARMORONLY));
//        addModule(new IronPlatingModule(EnumModuleTarget.ARMORONLY));
//        addModule(new DiamondPlatingModule(EnumModuleTarget.ARMORONLY));
//        addModule(new EnergyShieldModule(EnumModuleTarget.ARMORONLY));
//
//        /* Cosmetic ----------------------------- */
//        addModule(new TransparentArmorModule(EnumModuleTarget.ARMORONLY));
//
//
//        /* Energy ------------------------------- */
//        addModule(new BasicBatteryModule(EnumModuleTarget.ALLITEMS));
//        addModule(new AdvancedBatteryModule(EnumModuleTarget.ALLITEMS));
//        addModule(new EliteBatteryModule(EnumModuleTarget.ALLITEMS));
//        addModule(new UltimateBatteryModule(EnumModuleTarget.ALLITEMS));
//
//
//        /* Power Fist --------------------------- */
//        addModule(new AxeModule(EnumModuleTarget.TOOLONLY));
//        addModule(new PickaxeModule(EnumModuleTarget.TOOLONLY));
//        addModule(new DiamondPickUpgradeModule(EnumModuleTarget.TOOLONLY));
//        addModule(new ShovelModule(EnumModuleTarget.TOOLONLY));
//        addModule(new ShearsModule(EnumModuleTarget.TOOLONLY));
//        addModule(new HoeModule(EnumModuleTarget.TOOLONLY));
//        addModule(new LuxCapacitorModule(EnumModuleTarget.TOOLONLY));
//        addModule(new FieldTinkerModule(EnumModuleTarget.TOOLONLY));
//        addModule(new MeleeAssistModule(EnumModuleTarget.TOOLONLY));
//        addModule(new PlasmaCannonModule(EnumModuleTarget.TOOLONLY));
//        addModule(new RailgunModule(EnumModuleTarget.TOOLONLY));
//        addModule(new BladeLauncherModule(EnumModuleTarget.TOOLONLY));
//        addModule(new BlinkDriveModule(EnumModuleTarget.TOOLONLY));
//        addModule(new PortableCraftingModule(EnumModuleTarget.TOOLONLY));
//        addModule(new LeafBlowerModule(EnumModuleTarget.TOOLONLY));
//        addModule(new FlintAndSteelModule(EnumModuleTarget.TOOLONLY));
//        addModule(new LightningModule(EnumModuleTarget.TOOLONLY));
//        addModule(new DimensionalRiftModule(EnumModuleTarget.TOOLONLY));
//        // Mining Enhancements
//        addModule(new AOEPickUpgradeModule(EnumModuleTarget.TOOLONLY));
//        addModule(new AquaAffinityModule(EnumModuleTarget.TOOLONLY));
//        addModule(new FortuneModule(EnumModuleTarget.TOOLONLY));
//        addModule(new SilkTouchModule(EnumModuleTarget.TOOLONLY));
//
//
//        /* Helmet ------------------------------- */
//        addModule(new WaterElectrolyzerModule(EnumModuleTarget.HEADONLY));
//        addModule(new NightVisionModule(EnumModuleTarget.HEADONLY));
//        addModule(new BinocularsModule(EnumModuleTarget.HEADONLY));
//        addModule(new FlightControlModule(EnumModuleTarget.HEADONLY));
//        addModule(new SolarGeneratorModule(EnumModuleTarget.HEADONLY));
//        addModule(new AutoFeederModule(EnumModuleTarget.HEADONLY));
//        addModule(new ClockModule(EnumModuleTarget.HEADONLY));
//        addModule(new CompassModule(EnumModuleTarget.HEADONLY));
//        addModule(new AdvancedSolarGenerator(EnumModuleTarget.HEADONLY));
//
//
//        /* Chestplate --------------------------- */
//        addModule(new ParachuteModule(EnumModuleTarget.TORSOONLY));
//        addModule(new GliderModule(EnumModuleTarget.TORSOONLY));
//        addModule(new JetPackModule(EnumModuleTarget.TORSOONLY));
//        addModule(new InvisibilityModule(EnumModuleTarget.TORSOONLY));
//        addModule(new BasicCoolingSystemModule(EnumModuleTarget.TORSOONLY));
//        addModule(new MagnetModule(EnumModuleTarget.TORSOONLY));
//        addModule(new ThermalGeneratorModule(EnumModuleTarget.TORSOONLY));
//        addModule(new MobRepulsorModule(EnumModuleTarget.TORSOONLY));
//        addModule(new AdvancedCoolingSystem(EnumModuleTarget.TORSOONLY));
//        //addModule(new CoalGenerator(TORSOONLY)); //TODO: Finish
//
//
//        /* Legs --------------------------------- */
//        addModule(new SprintAssistModule(EnumModuleTarget.LEGSONLY));
//        addModule(new JumpAssistModule(EnumModuleTarget.LEGSONLY));
//        addModule(new SwimAssistModule(EnumModuleTarget.LEGSONLY));
//        addModule(new KineticGeneratorModule(EnumModuleTarget.LEGSONLY));
//        addModule(new ClimbAssistModule(EnumModuleTarget.LEGSONLY));
//
//
//        /* Feet --------------------------------- */
//        addModule(new JetBootsModule(EnumModuleTarget.FEETONLY));
//        addModule(new ShockAbsorberModule(EnumModuleTarget.FEETONLY));
//
//
//        /** Conditional loading ------------------------------------------------------------------- */
//        // Thaumcraft
//        if (ModCompatibility.isThaumCraftLoaded())
//            addModule(new ThaumGogglesModule(EnumModuleTarget.HEADONLY));
//
//        // CoFHCore
//        if (ModCompatibility.isCOFHCoreLoaded())
//            addModule(new OmniWrenchModule(EnumModuleTarget.TOOLONLY));
//
//        // Mekanism
//        if (ModCompatibility.isMekanismLoaded())
//            addModule(new MADModule(EnumModuleTarget.TOOLONLY));
//
//        // Industrialcraft
//        if (ModCompatibility.isIndustrialCraftLoaded()) {
//            addModule(new HazmatModule(EnumModuleTarget.ARMORONLY));
//            addModule(new TreetapModule(EnumModuleTarget.TOOLONLY));
//        }
//
//        // Galacticraft
//        if (ModCompatibility.isGalacticraftLoaded())
//            addModule(new AirtightSealModule(EnumModuleTarget.HEADONLY));
//
//        // Forestry
//        if (ModCompatibility.isForestryLoaded()) {
//            addModule(new GrafterModule(EnumModuleTarget.TOOLONLY));
//            addModule(new ScoopModule(EnumModuleTarget.TOOLONLY));
//            addModule(new ApiaristArmorModule(EnumModuleTarget.ARMORONLY));
//        }
//
//        // Chisel
//        if (ModCompatibility.isChiselLoaded())
//            try {
//                addModule(new ChiselModule(EnumModuleTarget.TOOLONLY));
//            } catch (Exception e) {
//                MPALibLogger.logException("Couldn't add Chisel module", e);
//            }
//
//        // Applied Energistics
//        if (ModCompatibility.isAppengLoaded()) {
//            addModule(new AppEngWirelessModule(EnumModuleTarget.TOOLONLY));
//
//            // Extra Cells 2
//            if (ModCompatibility.isExtraCellsLoaded())
//                addModule(new AppEngWirelessFluidModule(EnumModuleTarget.TOOLONLY));
//        }
//
//        // Multi-Mod Compatible OmniProbe
//        if (ModCompatibility.isEnderIOLoaded() || ModCompatibility.isMFRLoaded() || ModCompatibility.isRailcraftLoaded())
//            addModule(new OmniProbeModule(EnumModuleTarget.TOOLONLY));
//
//// TODO: on hold for now. Needs a conditional fiuld tank and handler. May not be worth it.
//        // Compact Machines
//        if (ModCompatibility.isCompactMachinesLoaded())
//            addModule(new PersonalShrinkingModule(EnumModuleTarget.TOOLONLY));
//
//        // Refined Storage
//        if (ModCompatibility.isRefinedStorageLoaded())
//            addModule(new RefinedStorageWirelessModule(EnumModuleTarget.TOOLONLY));
//
//        // Scannable
//        if (ModCompatibility.isScannableLoaded())
//            addModule(new OreScannerModule(EnumModuleTarget.TOOLONLY));
//    }
//}