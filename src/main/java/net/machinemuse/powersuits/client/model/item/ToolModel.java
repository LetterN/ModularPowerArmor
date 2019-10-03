// Date: 1/13/2013 3:17:20 AM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package net.machinemuse.powersuits.client.model.item;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.client.render.MuseTextureUtils;
import net.machinemuse.numina.client.render.RenderState;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.client.render.entity.EntityRendererPlasmaBolt;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;

public class ToolModel extends Model {
    public int boltSize;
    boolean isRightHand;

    public RendererModel mainarm;
    public RendererModel armorright;
    public RendererModel armorleft;
    public RendererModel wristtopright;
    public RendererModel wristtopleft;
    public RendererModel wristbottomright;
    public RendererModel wristbottomleft;
    public RendererModel index1;
    public RendererModel index2;
    public RendererModel middlefinger1;
    public RendererModel middlefinger2;
    public RendererModel ringfinger1;
    public RendererModel ringfinger2;
    public RendererModel pinky1;
    public RendererModel pinky2;
    public RendererModel thumb1;
    public RendererModel thumb2;
    public RendererModel fingerguard;
    public RendererModel crystalholder;
    public RendererModel crystal;
    public RendererModel supportright1;
    public RendererModel supportright2;
    public RendererModel supportright3;
    public RendererModel supportright4;
    public RendererModel supportright5;
    public RendererModel supportbaseright;
    public RendererModel palm;
    public RendererModel supportbaseleft;
    public RendererModel supportleftfront;
    public RendererModel supportrightfront;
    public RendererModel supportleft1;
    public RendererModel supportleft2;
    public RendererModel supportleft3;
    public RendererModel supportleft4;
    public RendererModel supportleft5;

    public ToolModel(boolean isRightHand) {
        textureWidth = 64;
        textureHeight = 32;
        this.isRightHand = isRightHand;

        mainarm = new RendererModel(this, 0, 16);
        mainarm.addBox(-3.0F, 0.0F, -8.0F, 6, 6, 10);
        mainarm.setRotationPoint(0.0F, 0.0F, 0.0F);
        mainarm.setTextureSize(64, 32);
        mainarm.mirror = true;
        setRotation(mainarm, 0.2617994F, 0.0F, 0.0F);


        armorright = new RendererModel(this, 42, 0);
        armorright.mirror = true;
        armorright.addBox(1.0F, -1.0F, -9.0F, 3, 5, 8);
        armorright.setRotationPoint(0.0F, 0.0F, 0.0F);
        armorright.setTextureSize(64, 32);
        setRotation(armorright, 0.2617994F, 0.0F, 0.0F);


        armorleft = new RendererModel(this, 42, 0);
        armorleft.addBox(-4.0F, -1.0F, -9.0F, 3, 5, 8);
        armorleft.setRotationPoint(0.0F, 0.0F, 0.0F);
        armorleft.setTextureSize(64, 32);
        armorleft.mirror = true;
        setRotation(armorleft, 0.2617994F, 0.0F, 0.0F);


        wristtopright = new RendererModel(this, 0, 11);
        wristtopright.addBox(1.0F, 1.0F, 2.0F, 1, 1, 4);
        wristtopright.setRotationPoint(0.0F, 0.0F, 0.0F);
        wristtopright.setTextureSize(64, 32);
        wristtopright.mirror = true;
        setRotation(wristtopright, 0.2617994F, 0.0F, 0.0F);


        wristtopleft = new RendererModel(this, 0, 11);
        wristtopleft.addBox(-2.0F, 1.0F, 2.0F, 1, 1, 4);
        wristtopleft.setRotationPoint(0.0F, 0.0F, 0.0F);
        wristtopleft.setTextureSize(64, 32);
        wristtopleft.mirror = true;
        setRotation(wristtopleft, 0.2617994F, 0.0F, 0.0F);


        wristbottomright = new RendererModel(this, 0, 11);
        wristbottomright.addBox(1.0F, 3.0F, 2.0F, 1, 1, 4);
        wristbottomright.setRotationPoint(0.0F, 0.0F, 0.0F);
        wristbottomright.setTextureSize(64, 32);
        wristbottomright.mirror = true;
        setRotation(wristbottomright, 0.2617994F, 0.0F, 0.0F);


        wristbottomleft = new RendererModel(this, 0, 11);
        wristbottomleft.addBox(-2.0F, 3.0F, 2.0F, 1, 1, 4);
        wristbottomleft.setRotationPoint(0.0F, 0.0F, 0.0F);
        wristbottomleft.setTextureSize(64, 32);
        wristbottomleft.mirror = true;
        setRotation(wristbottomleft, 0.2617994F, 0.0F, 0.0F);


        index1 = new RendererModel(this, 34, 13);
        index1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 5);
        index1.setRotationPoint((isRightHand) ? -3.5F : 2.5F, -1.5F, 10.0F);
        index1.setTextureSize(64, 32);
        index1.mirror = true;
        setRotation(index1, 0.2617994F, 0.0F, 0.0F);


        index2 = new RendererModel(this, 34, 13);
        index2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4);
        index2.setRotationPoint(0.0F, 0.0F, 5.0F);
        index2.setTextureSize(64, 32);
        index2.mirror = true;
        index1.addChild(index2);
        setRotation(index2, -0.2617994F * 2, 0.0F, 0.0F);


        middlefinger1 = new RendererModel(this, 34, 13);
        middlefinger1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 6);
        middlefinger1.setRotationPoint((isRightHand) ? -1.5F : 0.5F, -1.5F, 10.0F);
        middlefinger1.setTextureSize(64, 32);
        middlefinger1.mirror = true;
        setRotation(middlefinger1, 0.2617994F, 0.0F, 0.0F);


        middlefinger2 = new RendererModel(this, 34, 13);
        middlefinger2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4);
        middlefinger2.setRotationPoint(0.0F, 0.0F, 6.0F);
        middlefinger2.setTextureSize(64, 32);
        middlefinger2.mirror = true;
        setRotation(middlefinger2, -0.3444116F, 0.0F, 0.0F);


        ringfinger1 = new RendererModel(this, 34, 13);
        ringfinger1.addBox(-.5F, -.5F, 0.0F, 1, 1, 5);
        ringfinger1.setRotationPoint((isRightHand) ? 0.5F : -1.5F, -1.5F, 10.0F);
        ringfinger1.setTextureSize(64, 32);
        ringfinger1.mirror = true;
        setRotation(ringfinger1, 0.2617994F, 0.0F, 0.0F);


        ringfinger2 = new RendererModel(this, 34, 13);
        ringfinger2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4);
        ringfinger2.setRotationPoint(0.0F, 0.0F, 5.0F);
        ringfinger2.setTextureSize(64, 32);
        ringfinger2.mirror = true;
        setRotation(ringfinger2, -0.2617994F, 0.0F, 0.0F);


        pinky1 = new RendererModel(this, 34, 13);
        pinky1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4);
        pinky1.setRotationPoint((isRightHand) ? 2.5F : -3.5F, -1.5F, 10F);
        pinky1.setTextureSize(64, 32);
        pinky1.mirror = true;
        setRotation(pinky1, 0.2617994F, 0.0F, 0.0F);


        pinky2 = new RendererModel(this, 34, 13);
        pinky2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4);
        pinky2.setRotationPoint(0.0F, 0.0F, 4.0F);
        pinky2.setTextureSize(64, 32);
        pinky2.mirror = true;
        setRotation(pinky2, -0.4537856F, 0.0F, 0.0F);


        thumb1 = new RendererModel(this, 16, 9);
        thumb1.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 4);
        thumb1.setRotationPoint((isRightHand) ?-4.0F : 3.0F, 1.5F, 8.0F);
        thumb1.setTextureSize(64, 32);
        thumb1.mirror = true;
        setRotation(thumb1, 0.0F, (isRightHand) ? -0.4014257F : 0.4014257F, 0.0F);


        thumb2 = new RendererModel(this, 10, 0);
        thumb2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3);
        thumb2.setRotationPoint(0.0F, 0.0F, 4.0F);
        thumb2.setTextureSize(64, 32);
        thumb2.mirror = true;
        setRotation(thumb2, 0.0F, 0.0F, 0.0F);


        fingerguard = new RendererModel(this, 28, 9);
        fingerguard.addBox(-3.0F, -2.0F, 8.0F, 5, 2, 2);
        fingerguard.setRotationPoint(0.0F, 0.0F, 0.0F);
        fingerguard.setTextureSize(64, 32);
        fingerguard.mirror = true;
        setRotation(fingerguard, 0.0F, 0.0F, 0.0F);


        crystalholder = new RendererModel(this, 48, 13);
        crystalholder.addBox(-2.0F, -1.0F, -3.0F, 4, 4, 4);
        crystalholder.setRotationPoint(0.0F, 0.0F, 0.0F);
        crystalholder.setTextureSize(64, 32);
        crystalholder.mirror = true;
        setRotation(crystalholder, 0.0F, 0.0F, 0.0F);


        crystal = new RendererModel(this, 32, 27);
        crystal.addBox(-1.0F, -2.0F, -2.0F, 2, 2, 2);
        crystal.setRotationPoint(0.0F, 0.0F, 0.0F);
        crystal.setTextureSize(64, 32);
        crystal.mirror = true;
        setRotation(crystal, 0.0F, 0.0F, 0.0F);


        supportright1 = new RendererModel(this, 54, 27);
        supportright1.addBox(-1.8F, -0.8F, -6.066667F, 4, 1, 1);
        supportright1.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportright1.setTextureSize(64, 32);
        supportright1.mirror = true;
        setRotation(supportright1, 0.2722714F, -1.066972F, 0.0F);


        supportright2 = new RendererModel(this, 52, 21);
        supportright2.addBox(4.0F, 0.4666667F, 2.5F, 2, 2, 1);
        supportright2.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportright2.setTextureSize(64, 32);
        supportright2.mirror = true;
        setRotation(supportright2, 0.0F, 0.6329786F, 0.0F);


        supportright3 = new RendererModel(this, 52, 21);
        supportright3.addBox((isRightHand) ? 5.1F : 5.4F, 1.0F, (isRightHand) ? -0.8333333F : -0.5F, 1, 1, 5);
        supportright3.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportright3.setTextureSize(64, 32);
        supportright3.mirror = true;
        setRotation(supportright3, 0.0F, 0.0F, 0.0F);


        supportright4 = new RendererModel(this, 52, 21);
        supportright4.addBox(5.633333F, 0.4666667F, 1.7F, 2, 2, 1);
        supportright4.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportright4.setTextureSize(64, 32);
        supportright4.mirror = true;
        setRotation(supportright4, 0.0F, -0.3688404F, 0.0F);


        supportright5 = new RendererModel(this, 54, 27);
        supportright5.addBox(-2.866667F, 1F, 6.333333F, 4, 1, 1);
        supportright5.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportright5.setTextureSize(64, 32);
        supportright5.mirror = true;
        setRotation(supportright5, 0.0F, 0.7714355F, 0.0F);


        supportbaseright = new RendererModel(this, 47, 21);
        supportbaseright.addBox(1.433333F, -0.6666667F, -5.4F, 3, 3, 5);
        supportbaseright.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportbaseright.setTextureSize(64, 32);
        supportbaseright.mirror = true;
        setRotation(supportbaseright, 0.2617994F, 0.0F, 0.0F);


        palm = new RendererModel(this, 18, 0);
        palm.addBox(-4.0F, -1.0F, 5.0F, 7, 4, 5);
        palm.setRotationPoint(0.0F, 0.0F, 0.0F);
        palm.setTextureSize(64, 32);
        palm.mirror = true;
        setRotation(palm, 0.0F, 0.0F, 0.0F);


        supportbaseleft = new RendererModel(this, 47, 21);
        supportbaseleft.addBox(-4.4F, -0.6666667F, -5.4F, 3, 3, 5);
        supportbaseleft.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportbaseleft.setTextureSize(64, 32);
        supportbaseleft.mirror = true;
        setRotation(supportbaseleft, 0.2617994F, 0.0F, 0.0F);


        supportleftfront = new RendererModel(this, 49, 23);
        supportleftfront.addBox((isRightHand) ? -4.333333F : -3.333333F, 0.3333333F, 4.666667F, 1, 2, 3);
        supportleftfront.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportleftfront.setTextureSize(64, 32);
        supportleftfront.mirror = true;
        setRotation(supportleftfront, 0.0F, 0.0F, 0.0F);


        supportrightfront = new RendererModel(this, 49, 23);
        supportrightfront.addBox((isRightHand) ? 2.3F : 3.3F, 0.3333333F, 4.666667F, 1, 2, 3);
        supportrightfront.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportrightfront.setTextureSize(64, 32);
        supportrightfront.mirror = true;
        setRotation(supportrightfront, 0.0F, 0.0F, 0.0F);


        supportleft1 = new RendererModel(this, 54, 27);
        supportleft1.addBox(-2.2F, -0.4F, -6.066667F, 4, 1, 1);
        supportleft1.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportleft1.setTextureSize(64, 32);
        supportleft1.mirror = true;
        setRotation(supportleft1, 0.2722714F, 1.066978F, 0.0F);


        supportleft2 = new RendererModel(this, 52, 21);
        supportleft2.addBox(-6.0F, 0.4666667F, 2.5F, 2, 2, 1);
        supportleft2.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportleft2.setTextureSize(64, 32);
        supportleft2.mirror = true;
        setRotation(supportleft2, 0.0F, -0.6329727F, 0.0F);


        supportleft3 = new RendererModel(this, 52, 21);
        supportleft3.addBox((isRightHand) ? -6.5F : -6.3F, 1.0F, (isRightHand) ? -0.5F : -0.83F, 1, 1, 5);
        supportleft3.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportleft3.setTextureSize(64, 32);
        supportleft3.mirror = true;
        setRotation(supportleft3, 0.0F, 0.0F, 0.0F);


        supportleft4 = new RendererModel(this, 52, 21);
        supportleft4.addBox(-7.9F, 0.4666667F, 1.7F, 2, 2, 1);
        supportleft4.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportleft4.setTextureSize(64, 32);
        supportleft4.mirror = true;
        setRotation(supportleft4, 0.0F, 0.3688462F, 0.0F);


        supportleft5 = new RendererModel(this, 54, 27);
        supportleft5.addBox(-0.8666667F, 1F, 7F, 4, 1, 1);
        supportleft5.setRotationPoint(0.0F, 0.0F, 0.0F);
        supportleft5.setTextureSize(64, 32);
        supportleft5.mirror = true;
        setRotation(supportleft5, 0.0F, -0.7714355F, 0.0F);


        index1.addChild(index2);
        middlefinger1.addChild(middlefinger2);
        ringfinger1.addChild(ringfinger2);
        pinky1.addChild(pinky2);
        thumb1.addChild(thumb2);


        palm.addChild(index1);
        palm.addChild(middlefinger1);
        palm.addChild(ringfinger1);
        palm.addChild(pinky1);
        palm.addChild(thumb1);
    }

//    public static int xtap = 0;
//    public static int ytap = 0;
//    public static int ztap = 0;
//    public static float xOffest = 0;
//    public static float yOffest = 0;
//    public static float zOffest = 0;
//    public static float scalemodifier = 1;
//    public static boolean tap;

    public void makeChild(RendererModel child, RendererModel parent) {
        parent.addChild(child);
        child.rotationPointX -= parent.rotationPointX;
        child.rotationPointY -= parent.rotationPointY;
        child.rotationPointZ -= parent.rotationPointZ;
        child.rotateAngleX -= parent.rotateAngleX;
        child.rotateAngleY -= parent.rotateAngleY;
        child.rotateAngleZ -= parent.rotateAngleZ;
    }


//    /*
//     * Only used for setting up scale, rotation, and relative placement coordinates
//     */
//    private void transformCalibration() {
//        int numsegments = 16;
//        if (!tap) {
//
//            if (Keyboard.isKeyDown(Keyboard.KEY_INSERT)) {
//                xOffest += 0.1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
//                xOffest -= 0.1;
//                tap = true;
//            }
//
//            if (Keyboard.isKeyDown(Keyboard.KEY_HOME)) {
//                yOffest += 0.1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_END)) {
//                yOffest -= 0.1;
//                tap = true;
//            }
//
//            if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) {
//                zOffest += 0.1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_NEXT)) {
//                zOffest -= 0.1;
//                tap = true;
//            }
//
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)) {
//                xtap += 1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) {
//                ytap += 1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3)) {
//                ztap += 1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) {
//                xtap -= 1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
//                ytap -= 1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)) {
//                ztap -= 1;
//                tap = true;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)) {
//                xtap = 0;
//                ytap = 0;
//                ztap = 0;
//
//                xOffest = 0;
//                yOffest = 0;
//                zOffest = 0;
//
//                scalemodifier = 1;
//
//                tap = true;
//            }
//              // this probably needs a bit more work, int's are too big.
////            if (Keyboard.isKeyDown(Keyboard.KEY_SCROLL)) {
////                scalemodifier -= 1;
////                tap = true;
////            }
////            if (Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
////                scalemodifier += 1;
////                tap = true;
////            }
//
//            if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0)) {
//                System.out.println("xrot: " + xtap + ", yrot: " + ytap + ", zrot: " + ztap);
//
//                System.out.println("xOffest: " + xOffest + ", yOffest: " + yOffest + ", zOffest: " + zOffest);
//
//                System.out.println("scaleModifier: " + scalemodifier);
//
//                tap = true;
//            }
//        }
//        else {
//            if (!Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0) && !Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1) && !Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)
//                    && !Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3) && !Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)
//                    && !Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5) && !Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)) {
//                tap = false;
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
//                tap = false;
//            }
//        }
//    }

    public void render(Entity entity, float scale, ItemCameraTransforms.TransformType cameraTransformTypeIn, Colour c1, Colour glow) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_CULL_FACE);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        MuseTextureUtils.pushTexture(MPSConstants.SEBK_TOOL_TEXTURE);// Fixme

        if (c1 != null) {
            c1.doGL();
        }
        double scale1 = 1.0 / 16.0; // 0.0625

//        transformCalibration();// only needed for setting up the model

        if (cameraTransformTypeIn == FIRST_PERSON_RIGHT_HAND || cameraTransformTypeIn == FIRST_PERSON_LEFT_HAND) {
            GL11.glScaled(-scale1, scale1, scale1); // negative scale mirrors the model
            GL11.glRotatef(-182, 1, 0, 0);

//            GL11.glTranslatef(xOffest, yOffest, zOffest);
//            GL11.glRotatef(xtap, 1, 0, 0);
//            GL11.glRotatef(ytap, 0, 1, 0);
//            GL11.glRotatef(ztap, 0, 0, 1);

        } else {
            GL11.glScaled(scale1, scale1, scale1);
            GL11.glTranslatef(0, 0, 1.3f);
            GL11.glRotatef(-196, 1, 0, 0);

//            GL11.glTranslatef(xOffest, yOffest, zOffest);
//            GL11.glRotatef(xtap, 1, 0, 0);
//            GL11.glRotatef(ytap, 0, 1, 0);
//            GL11.glRotatef(ztap, 0, 0, 1);
        }

        GL11.glPushMatrix();
        if (cameraTransformTypeIn == FIRST_PERSON_RIGHT_HAND || cameraTransformTypeIn == FIRST_PERSON_LEFT_HAND) {
            mainarm.render(scale);
        }
        armorright.render(scale);
        armorleft.render(scale);
        wristtopright.render(scale);
        wristtopleft.render(scale);
        wristbottomright.render(scale);
        wristbottomleft.render(scale);
        fingerguard.render(scale);
        crystalholder.render(scale);
        supportright1.render(scale);
        supportright2.render(scale);
        supportright3.render(scale);
        supportright4.render(scale);
        supportright5.render(scale);
        supportbaseright.render(scale);
        palm.render(scale);
        supportbaseleft.render(scale);
        supportleftfront.render(scale);
        supportrightfront.render(scale);
        supportleft1.render(scale);
        supportleft2.render(scale);
        supportleft3.render(scale);
        supportleft4.render(scale);
        supportleft5.render(scale);
        RenderState.glowOn();
        if (glow != null)
            glow.doGL();
        crystal.render(scale);
        Colour.WHITE.doGL();

        // todo: move this to ModelPowerFist
        if (boltSize != 0) {
            GL11.glTranslated(-1, 1, 16);
            GL11.glPushMatrix();
            EntityRendererPlasmaBolt.doRender(boltSize);
            GL11.glPopMatrix();
        }

        RenderState.glowOff();
        MuseTextureUtils.popTexture();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void setRotation(RendererModel model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are
     * used for animating the movement of arms and legs, where par1 represents
     * the time(so that arms and legs swing back and forth) and par2 represents
     * how "far" arms and legs can swing at most.
     */
    public void setPose(float indexOpen, float indexFlex, float thumbOpen, float thumbFlex, float otherFingersOpen, float otherFingersFlex) {
        index1.rotateAngleX = indexOpen;
        index2.rotateAngleX = indexFlex;
        middlefinger1.rotateAngleX = otherFingersOpen;
        middlefinger2.rotateAngleX = otherFingersFlex;
        ringfinger1.rotateAngleX = otherFingersOpen;
        ringfinger2.rotateAngleX = otherFingersFlex;
        pinky1.rotateAngleX = otherFingersOpen - 0.1f;
        pinky2.rotateAngleX = otherFingersFlex;
        thumb1.rotateAngleY = (isRightHand) ? -thumbOpen : thumbOpen;
        thumb2.rotateAngleY = (isRightHand) ? -thumbFlex : thumbFlex;
    }

    public void setPoseForPlayer(PlayerEntity player, ItemStack itemStack) {
        if (player.isHandActive() && player.inventory.getStackInSlot(player.inventory.currentItem) == itemStack
                && itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
            if (iItemHandler instanceof IModeChangingItem) {

                // fixme: check


            }
            return false;
        }).orElse(false)) {
            setPose(1.5f, -1, 1.5f, -1, 1.5f, -1);
            int actualCount = (-player.getItemInUseCount() + 72000);
            this.boltSize = actualCount > 50 ? 50 : actualCount;
        } else {
            setPose(0.5f, -1, 0.5f, -1, 0.5f, -1);
            this.boltSize = 0;
        }
    }

    public void setNeutralPose() {
        setPose(0.5f, -1, 0.5f, -1, 0.5f, -1);
        this.boltSize = 0;
    }
}