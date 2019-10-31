package net.machinemuse.powersuits.client.control;

import com.github.lehjr.mpalib.capabilities.player.CapabilityPlayerKeyStates;
import com.github.lehjr.mpalib.capabilities.player.IPlayerKeyStates;
import com.github.lehjr.mpalib.legacy.item.IModeChangingItem;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.PlayerUpdatePacket;
import net.machinemuse.powersuits.common.ModularPowersuits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeybindKeyHandler {
    public static final String mps = "Modular Powersuits";
    public static final KeyBinding openKeybindGUI = new KeyBinding("Open MPS Keybind GUI", Keyboard.KEY_NONE, mps);
    public static final KeyBinding goDownKey = new KeyBinding("Go Down (MPS Flight Control)", Keyboard.KEY_Z, mps);
    public static final KeyBinding cycleToolBackward = new KeyBinding("Cycle Tool Backward (MPS)", Keyboard.KEY_NONE, mps);
    public static final KeyBinding cycleToolForward = new KeyBinding("Cycle Tool Forward (MPS)", Keyboard.KEY_NONE, mps);
    public static final KeyBinding openCosmeticGUI = new KeyBinding("Cosmetic (MPS)", Keyboard.KEY_NONE, mps);

    public KeybindKeyHandler() {
        ClientRegistry.registerKeyBinding(openKeybindGUI);
        ClientRegistry.registerKeyBinding(goDownKey);
        ClientRegistry.registerKeyBinding(cycleToolBackward);
        ClientRegistry.registerKeyBinding(cycleToolForward);
        ClientRegistry.registerKeyBinding(openCosmeticGUI);
    }

    public void updatePlayerValues(EntityPlayerSP clientPlayer, Boolean downKeyState, Boolean jumpKeyState) {
        boolean markForSync = false;

        IPlayerKeyStates playerCap = clientPlayer.getCapability(CapabilityPlayerKeyStates.PLAYER_KEYSTATES, null);
        if (playerCap != null) {
            if(downKeyState != null)
                if (playerCap.getDownKeyState() != downKeyState) {
                    playerCap.setDownKeyState(downKeyState);
                    markForSync = true;
                }

            if (jumpKeyState != null)
                if (playerCap.getJumpKeyState() != jumpKeyState) {
                    playerCap.setJumpKeyState(jumpKeyState);
                    markForSync = true;
                }

            if (markForSync) {
                MPALibPackets.sendToServer(new PlayerUpdatePacket(playerCap.getDownKeyState(), playerCap.getJumpKeyState()));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayerSP player = mc.player;

            // Only activate if there is a player to work with
            if (mc.inGameHasFocus) {
                updatePlayerValues(player, goDownKey.isKeyDown() , mc.gameSettings.keyBindJump.isKeyDown());
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        int key = Keyboard.getEventKey();
        boolean pressed = Keyboard.getEventKeyState();
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        KeyBinding[] hotbarKeys = mc.gameSettings.keyBindsHotbar;

        // Only activate if there is a player to work with
        if (player == null || !mc.inGameHasFocus) {
            return;
        }

        if (pressed) {
            if (player.inventory.getCurrentItem().getItem() instanceof IModeChangingItem) {
                IModeChangingItem mci = (IModeChangingItem) player.inventory.getCurrentItem().getItem();

                if (player.inventory.currentItem < hotbarKeys.length && key == hotbarKeys[player.inventory.currentItem].getKeyCode()) {
                    World world = mc.world;
                    if (mc.inGameHasFocus) {
                        player.openGui(ModularPowersuits.getInstance(), 4, world, 0, 0, 0);
                    }
                    // cycleToolBackward/cycleToolForward aren't related to the mouse wheel unless bound to that
                } else if (cycleToolBackward.isPressed()) {
                    mc.playerController.updateController();
                    mci.cycleMode(player.inventory.getStackInSlot(player.inventory.currentItem), player, 1);
                } else if (cycleToolForward.isPressed()) {
                    mc.playerController.updateController();
                    mci.cycleMode(player.inventory.getStackInSlot(player.inventory.currentItem), player, -1);
                }
            }

            if (openKeybindGUI.isPressed()) {
                World world = mc.world;
                if (mc.inGameHasFocus) {
                    player.openGui(ModularPowersuits.getInstance(), 1, world, 0, 0, 0);
                }
            } else if (openCosmeticGUI.isPressed()) {
                World world = mc.world;
                if (mc.inGameHasFocus) {
                    player.openGui(ModularPowersuits.getInstance(), 2, world, 0, 0, 0);
                }
            }
        }
    }
}