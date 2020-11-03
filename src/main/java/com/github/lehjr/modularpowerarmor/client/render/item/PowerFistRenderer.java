//package com.github.lehjr.modularpowerarmor.client.render.item;
//
//import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
//import com.github.lehjr.modularpowerarmor.client.model.item.PowerFistModel2;
//import com.github.lehjr.modularpowerarmor.network.MPAPackets;
//import com.github.lehjr.modularpowerarmor.network.packets.CosmeticInfoPacket;
//import com.github.lehjr.mpalib.basemod.MPALibConstants;
//import com.github.lehjr.mpalib.util.capabilities.render.IHandHeldModelSpecNBT;
//import com.github.lehjr.mpalib.util.capabilities.render.ModelSpecNBTCapability;
//import com.github.lehjr.mpalib.util.capabilities.render.modelspec.ModelPartSpec;
//import com.github.lehjr.mpalib.util.capabilities.render.modelspec.ModelRegistry;
//import com.github.lehjr.mpalib.util.capabilities.render.modelspec.ModelSpec;
//import com.github.lehjr.mpalib.util.capabilities.render.modelspec.PartSpecBase;
//import com.github.lehjr.mpalib.util.math.Colour;
//import com.github.lehjr.mpalib.util.nbt.NBTTagAccessor;
//import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.vertex.IVertexBuilder;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.client.renderer.model.ItemCameraTransforms;
//import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.math.vector.TransformationMatrix;
//
//public class PowerFistRenderer extends ItemStackTileEntityRenderer {
//    PowerFistModel2 powerFist = new PowerFistModel2();
//    boolean isFiring = false;
//
//    @Override
//    public void func_239207_a_/*render*/(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
//
//
//        IVertexBuilder builder = buffer.getBuffer(powerFist.getRenderType(MPAConstants.POWER_FIST_TEXTURE));
//
//        stack.getCapability(ModelSpecNBTCapability.RENDER).ifPresent(specNBTCap -> {
//            if (specNBTCap instanceof IHandHeldModelSpecNBT) {
//                CompoundNBT renderSpec = specNBTCap.getRenderTag();
//
//                // Set the tag on the item so this lookup isn't happening on every loop.
//                if (renderSpec == null || renderSpec.isEmpty()) {
//                    renderSpec = specNBTCap.getDefaultRenderTag();
//
//                    // first person transform type insures THIS client's player is the one holding the item rather than this
//                    // client's player seeing another player holding it
//                    if (renderSpec != null && !renderSpec.isEmpty() &&
//                            (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND ||
//                                    (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND))) {
//                        PlayerEntity player = Minecraft.getInstance().player;
//                        int slot = -1;
//                        if (player.getHeldItemMainhand().equals(stack)) {
//                            slot = player.inventory.currentItem;
//                        } else {
//                            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
//                                if (player.inventory.getStackInSlot(i).equals(stack)) {
//                                    slot = i;
//                                    break;
//                                }
//                            }
//                        }
//
//                        if (slot != -1) {
//                            specNBTCap.setRenderTag(renderSpec, MPALibConstants.TAG_RENDER);
//                            MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(slot, MPALibConstants.TAG_RENDER, renderSpec));
//                        }
//                    }
//                }
//
//                if (renderSpec != null) {
//                    int[] colours = renderSpec.getIntArray(MPALibConstants.TAG_COLOURS);
//                    Colour partColor;
//                    TransformationMatrix transform;
//
//                    for (CompoundNBT nbt : NBTTagAccessor.getValues(renderSpec)) {
//                        PartSpecBase partSpec = ModelRegistry.getInstance().getPart(nbt);
//
//                        String partName = nbt.getString("part");
//
//
//
//                        if (partSpec instanceof ModelPartSpec) {
//
//                            // only process this part if it's for the correct hand
//                            if (partSpec.getBinding().getTarget().name().toUpperCase().equals(
//                                    transformType.equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) ||
//                                            transformType.equals(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) ?
//                                            "LEFTHAND" : "RIGHTHAND")) {
//
//                                transform = ((ModelSpec) partSpec.spec).getTransform(transformType);
//                                String itemState = partSpec.getBinding().getItemState();
//
//                                int ix = partSpec.getColourIndex(nbt);
//                                if (ix < colours.length && ix >= 0) {
//                                    partColor = new Colour(colours[ix]);
//                                } else {
//                                    partColor = Colour.WHITE;
//                                }
//                                boolean glow = ((ModelPartSpec) partSpec).getGlow(nbt);
//
//                                if ((!isFiring && (itemState.equals("all") || itemState.equals("normal"))) ||
//                                        (isFiring && (itemState.equals("all") || itemState.equals("firing")))) {
//
//                                    System.out.println("partname: " + partName);
//                                    matrixStack.push();
//                                    matrixStack.translate(transform.getTranslation().getX(), transform.getTranslation().getY(), transform.getTranslation().getZ());
//                                    matrixStack.rotate(transform.getRightRot());
//                                    matrixStack.scale(transform.getScale().getX(), transform.getScale().getX(), transform.getScale().getZ());
//                                    matrixStack.pop();
//
////
//                                    powerFist.renderPart(partName, matrixStack, builder, combinedLight, combinedOverlay, partColor.r, partColor.g, partColor.b, partColor.a);
//
//
//
//
////                                    builder.addAll(ModelHelper.getColouredQuadsWithGlowAndTransform(((ModelPartSpec) partSpec).getPart().getQuads(state, side, rand, extraData), partColor, transform, glow));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        });
//
//
//
//              /*
//        matrixStack.scale(0.5F, 0.5F, 0.5F);
//
//
//
//
//                        <transformationMatrix type="third_person_right_hand" translation="8, 8.01, 9" rotation="-15, 180, 0" scale="0.630, 0.630, 0.630"/>
//                <transformationMatrix type="first_person_right_hand" translation="11.8, 8, 7" rotation="-16, -162, 0" scale="0.5, 0.5, 0.5"/>
//                <transformationMatrix type="ground" translation="0, 5, 0" rotation="0,0,0" scale="0.630, 0.630, 0.630"/>
//
//
//                            <modelTransforms>
//                <transformationMatrix type="third_person_right_hand" translation="8, 8.01, 9" rotation="-15, 180, 0" scale="0.630, 0.630, 0.630"/>
//                <transformationMatrix type="first_person_right_hand" translation="11.8, 8, 7" rotation="-16, -162, 0" scale="0.5, 0.5, 0.5"/>
//                <transformationMatrix type="ground" translation="0, 5, 0" rotation="0,0,0" scale="0.630, 0.630, 0.630"/>
//            </modelTransforms>
//         */
//
//
////        matrixStack.rotate(Vector3f.ZP.rotationDegrees(270F));
////        matrixStack.rotate(Vector3f.XP.rotationDegrees(90F));
////        for (String partName : powerFist.partlMap.keySet()) {
////            powerFist.renderPart(partName, matrixStack, builder, combinedLight, combinedOverlay, Colour.WHITE.r, Colour.WHITE.g, Colour.WHITE.b, Colour.WHITE.a);
////        }
//
//
//    }
//
//    String getPrefixString(ItemCameraTransforms.TransformType transformType) {
//        switch ((transformType)) {
//            case FIRST_PERSON_LEFT_HAND:
//            case THIRD_PERSON_LEFT_HAND: {
//                return "powerfist_left.";
//            }
//
//            default:
//                return "powerfist_right.";
//        }
//    }
//}