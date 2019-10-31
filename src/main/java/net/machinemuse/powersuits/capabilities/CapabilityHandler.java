package net.machinemuse.powersuits.capabilities;

import com.github.lehjr.mpalib.capabilities.player.CapabilityPlayerKeyStates;
import net.machinemuse.powersuits.api.constants.MPSModConstants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Capability handler
 *
 * This class is responsible for attaching our capabilities
 */
public class CapabilityHandler {
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent event) {
        if (!(event.getObject() instanceof EntityPlayer)) return;
        event.addCapability(new ResourceLocation(MPSModConstants.MODID, "IPlayerValues"), new CapabilityPlayerKeyStates());
    }
}