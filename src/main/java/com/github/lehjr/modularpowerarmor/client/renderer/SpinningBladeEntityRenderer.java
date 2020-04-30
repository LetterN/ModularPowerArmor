package com.github.lehjr.modularpowerarmor.client.renderer;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.entity.SpinningBladeEntity;
import com.github.lehjr.mpalib.client.render.entity.MPALibEntityRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.Random;

public class SpinningBladeEntityRenderer extends MPALibEntityRenderer<SpinningBladeEntity> {
    public SpinningBladeEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    public static final ResourceLocation textureLocation = new ResourceLocation(MPAConstants.TEXTURE_PREFIX + "item/module/weapon/spinningblade.png");

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(SpinningBladeEntity entity) {
        return textureLocation;
    }

    private final Random random = new Random();

    @Override
    public void render(SpinningBladeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        ItemStack itemstack = new ItemStack(MPAObjects.bladeLauncher);
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
        this.random.setSeed((long) i);
        IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(itemstack, entityIn.world, (LivingEntity) null);

        matrixStackIn.rotate(TransformationHelper.quatFromXYZ(new Vector3f(90, 0, 0), true));
//        double motionscale = Math.sqrt(entityIn.getMotion().z * entityIn.getMotion().z +entityIn.getMotion().x * entityIn.getMotion().x);
//        GL11.glRotatef(-entity.rotationPitch, (float) (entity.getMotion().z /
//                motionscale), 0.0f, (float) (- entity.getMotion().x / motionscale));
        int time = (int) System.currentTimeMillis() % 360;
        matrixStackIn.rotate(TransformationHelper.quatFromXYZ(new Vector3f(0, 0, time / 2), true));

        boolean flag = ibakedmodel.isGui3d();
        matrixStackIn.push();
        Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
        matrixStackIn.pop();
        if (!flag) {
            matrixStackIn.translate(0.0, 0.0, 0.09375F);
        }
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}