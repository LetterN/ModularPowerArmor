package com.github.lehjr.modularpowerarmor.client.model.block;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.model.TransformationHelper;

@OnlyIn(Dist.CLIENT)
public class TinkerTableModel<T extends Entity> extends EntityModel<T> {
    ModelRenderer cube;
    ModelRenderer screen3;
    ModelRenderer screen2;
    ModelRenderer screen1;
    ModelRenderer middletable;
    ModelRenderer uppertable;
    ModelRenderer particles;
    ModelRenderer footbase;
    ModelRenderer foot1;
    ModelRenderer fatfoot2;
    ModelRenderer fatfoot1;
    ModelRenderer backsupport;
    ModelRenderer tank3;
    ModelRenderer tank2;
    ModelRenderer tank1;
    ModelRenderer wireshort4;
    ModelRenderer wireshort3;
    ModelRenderer wireshort2;
    ModelRenderer wireshort1;
    ModelRenderer wirelong1;

    public TinkerTableModel() {
        super(RenderType::getEntityTranslucentCull);

        this.textureWidth = 112;
        this.textureHeight = 70;

        this.cube = new ModelRenderer(this, 96, 20);
        this.cube.mirror = true;
        this.cube.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cube.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);


        this.screen3 = new ModelRenderer(this, 1, 1);
        this.screen3.mirror = true;
        this.screen3.setRotationPoint(-9.67F, 3.47F, -7.0F);
        this.screen3.addBox(0.0F, 0.0F, 0.0F, 11.0F, 0.0F, 14.0F, 0.0F, 0.0F, 0.0F);


        this.screen2 = new ModelRenderer(this, 0, 32);
        this.screen2.mirror = true;
        this.screen2.setRotationPoint(2.0F, 4.966667F, -6.0F);
        this.screen2.addBox(0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 11.0F, 0.0F, 0.0F, 0.0F);


        this.screen1 = new ModelRenderer(this, 3, 20);
        this.screen1.mirror = true;
        this.screen1.setRotationPoint(-6.0F, 2.47F, 3.0F);
        this.screen1.addBox(0.0F, 0.0F, 0.0F, 14.0F, 0.0F, 7.0F, 0.0F, 0.0F, 0.0F);


        this.middletable = new ModelRenderer(this, 40, 49);
        this.middletable.setRotationPoint(-4.0F, 10.0F, -4.0F);
        this.middletable.addBox(-5.0F, 1.0F, -5.0F, 17.0F, 3.0F, 18.0F, 0.0F, 0.0F, 0.0F);


        this.uppertable = new ModelRenderer(this, 56, 28);
        this.uppertable.mirror = true;
        this.uppertable.setRotationPoint(-8.0F, 10.0F, -8.0F);
        this.uppertable.addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 16.0F, 0.0F, 0.0F, 0.0F);


        this.particles = new ModelRenderer(this, 90, 0);
        this.particles.mirror = true;
        this.particles.setRotationPoint(-3.0F, 15.0F, -3.0F);
        this.particles.addBox(0.0F, 0.0F, 0.0F, 6.0F, 7.0F, 5.0F, 0.0F, 0.0F, 0.0F);


        this.footbase = new ModelRenderer(this, 0, 54);
        this.footbase.mirror = true;
        this.footbase.setRotationPoint(-1.0F, 14.0F, -1.0F);
        this.footbase.addBox(-5.0F, 8.0F, -5.0F, 12.0F, 2.0F, 11.0F, 0.0F, 0.0F, 0.0F);


        this.foot1 = new ModelRenderer(this, 82, 13);
        this.foot1.mirror = true;
        this.foot1.setRotationPoint(-7.0F, 21.0F, -2.0F);
        this.foot1.addBox(0.0F, 0.0F, 0.0F, 4.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);


        this.fatfoot2 = new ModelRenderer(this, 96, 13);
        this.fatfoot2.mirror = true;
        this.fatfoot2.setRotationPoint(2.0F, 21.0F, -3.0F);
        this.fatfoot2.addBox(0.0F, 0.0F, 0.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fatfoot2, 3.141592653589793F, -8.742277643018003E-8F, 3.141592653589793F);


        this.fatfoot1 = new ModelRenderer(this, 96, 13);
        this.fatfoot1.mirror = true;
        this.fatfoot1.setRotationPoint(-2.0F, 21.0F, 2.0F);
        this.fatfoot1.addBox(0.0F, 0.0F, 0.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);


        this.backsupport = new ModelRenderer(this, 38, 34);
        this.backsupport.mirror = true;
        this.backsupport.setRotationPoint(3.0F, 14.0F, -2.0F);
        this.backsupport.addBox(0.0F, 0.0F, -2.0F, 2.0F, 8.0F, 7.0F, 0.0F, 0.0F, 0.0F);


        this.tank3 = new ModelRenderer(this, 51, 18);
        this.tank3.setRotationPoint(6.0F, 10.0F, 3.0F);
        this.tank3.addBox(0.0F, 0.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, 0.0F, 0.0F);


        this.tank2 = new ModelRenderer(this, 51, 18);
        this.tank2.setRotationPoint(6.0F, 10.0F, -2.0F);
        this.tank2.addBox(0.0F, 0.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, 0.0F, 0.0F);


        this.tank1 = new ModelRenderer(this, 51, 18);
        this.tank1.setRotationPoint(6.0F, 10.0F, -7.0F);
        this.tank1.addBox(0.0F, 0.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, 0.0F, 0.0F);


        this.wireshort4 = new ModelRenderer(this, 71, 15);
        this.wireshort4.mirror = true;
        this.wireshort4.setRotationPoint(7.0F, 15.0F, -1.0F);
        this.wireshort4.addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);


        this.wireshort3 = new ModelRenderer(this, 71, 15);
        this.wireshort3.mirror = true;
        this.wireshort3.setRotationPoint(7.0F, 15.0F, -6.0F);
        this.wireshort3.addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);


        this.wireshort2 = new ModelRenderer(this, 69, 13);
        this.wireshort2.mirror = true;
        this.wireshort2.setRotationPoint(5.0F, 17.0F, -1.0F);
        this.wireshort2.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);


        this.wireshort1 = new ModelRenderer(this, 71, 15);
        this.wireshort1.mirror = true;
        this.wireshort1.setRotationPoint(7.0F, 15.0F, -1.0F);
        this.wireshort1.addBox(0.0F, 0.0F, 5.0F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);


        this.wirelong1 = new ModelRenderer(this, 77, 1);
        this.wirelong1.mirror = true;
        this.wirelong1.setRotationPoint(7.0F, 17.0F, -6.0F);
        this.wirelong1.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 11.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

        matrixStackIn.push();
        matrixStackIn.rotate(TransformationHelper.quatFromXYZ(new Vector3f(180.0F, 0.0F, 0.0F), true));
        matrixStackIn.translate(0.5F, -1.5F, -0.5F);
        ImmutableList.of(this.fatfoot2, this.tank2, this.wireshort4, this.wireshort3, this.screen2, /*this.cube, */this.screen3, this.screen1, this.wireshort2, this.wireshort1, this.tank3, this.uppertable, this.middletable, this.fatfoot1, this.particles, this.wirelong1, this.foot1, this.tank1, this.backsupport, this.footbase).forEach((modelRenderer) -> {
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });

        matrixStackIn.pop();
        int timestep = (int) ((System.currentTimeMillis()) % 10000);
        float radians = (float) (timestep * Math.PI / 5000.0F);
        matrixStackIn.push();

        matrixStackIn.translate(0.5f, 1.05f, 0.5f);
        matrixStackIn.translate(0, 0.02f * Math.sin(radians * 3), 0);
        matrixStackIn.rotate(Vector3f.YP.rotation((radians)));
        matrixStackIn.rotate(Vector3f.XP.rotation(45F));

        // arctangent of 0.5.
        matrixStackIn.rotate(new Vector3f(0,1,1).rotation(35.2643897f));

//        matrixStackIn.push();
        matrixStackIn.scale(0.25F, 0.25F, 0.25F);
        cube.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1, 1, 1, 0.8F);
//        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}