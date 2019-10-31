package net.machinemuse.powersuits.powermodule.environmental;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibConfig;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import net.machinemuse.powersuits.api.constants.MPSModuleConstants;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.common.ModuleManager;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class WaterElectrolyzerModule extends PowerModuleBase implements IPlayerTickModule, IToggleableModule {
    public WaterElectrolyzerModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.lvcapacitor, 1));
        addBasePropertyDouble(MPSModuleConstants.WATERBREATHING_ENERGY_CONSUMPTION, 10000, "RF");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ENVIRONMENTAL;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_WATER_ELECTROLYZER__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        int energy = ElectricItemUtils.getPlayerEnergy(player);
        int energyConsumption = (int) Math.round(ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.WATERBREATHING_ENERGY_CONSUMPTION));
        if (energy > energyConsumption && player.getAir() < 10) {

            if ((FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) && MPALibConfig.useSounds()) {
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_ELECTROLYZER, SoundCategory.PLAYERS, 1.0f, player.getPosition());
            }
            ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
            player.setAir(300);
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.waterElectrolyzer;
    }
}