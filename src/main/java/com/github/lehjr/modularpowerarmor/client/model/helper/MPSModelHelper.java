package com.github.lehjr.modularpowerarmor.client.model.helper;

import com.github.lehjr.modularpowerarmor.client.render.modelspec.ModelSpecXMLReader;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class MPSModelHelper {
    static {
        new MPSModelHelper();
    }

    // One pass just to register the textures called from texture stitch event
    // another to register the models called from model bake event (second run)
    public static void loadArmorModels(@Nullable TextureMap map) {
        ArrayList<String> resourceList = new ArrayList<String>() {{
            add("/assets/modularpowerarmor/modelspec/armor2.xml");
            add("/assets/modularpowerarmor/modelspec/default_armor.xml");
            add("/assets/modularpowerarmor/modelspec/default_armorskin.xml");
            add("/assets/modularpowerarmor/modelspec/armor_skin2.xml");
            add("/assets/modularpowerarmor/modelspec/default_powerfist.xml");
        }};

        for (String resourceString : resourceList) {
            parseSpecFile(resourceString, map);
        }

        ModelPowerFistHelper.INSTANCE.loadPowerFistModels(map);
    }

    public static void parseSpecFile(String resourceString, @Nullable TextureMap map) {
        URL resource = MPSModelHelper.class.getResource(resourceString);
        ModelSpecXMLReader.INSTANCE.parseFile(resource, map);
    }
}