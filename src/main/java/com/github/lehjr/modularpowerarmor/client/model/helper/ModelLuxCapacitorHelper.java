package com.github.lehjr.modularpowerarmor.client.model.helper;


import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import com.github.lehjr.mpalib.math.Colour;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.github.lehjr.modularpowerarmor.common.MPSItems;
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public enum ModelLuxCapacitorHelper {
    INSTANCE;

    private static final ResourceLocation baseModelLocation = new ResourceLocation(Constants.RESOURCE_PREFIX + "block/luxcapacitor/luxcapacitor_base.obj");
    private static final ResourceLocation lensModelLocation = new ResourceLocation(Constants.RESOURCE_PREFIX + "block/luxcapacitor/luxcapacitor_lens.obj");

    /*
     * Guava chache for the list of baked quads.
     * The "ColoredQuadHelperThingie" is just easier and cleaner than using multi level maps.
     */
    public static LoadingCache<ColoredQuadHelperThingie, List<BakedQuad>> luxCapColoredQuadMap = CacheBuilder.newBuilder()
            .maximumSize(40)
            .build(new CacheLoader<ColoredQuadHelperThingie, List<BakedQuad>>() {
                @Override
                public List<BakedQuad> load(ColoredQuadHelperThingie key) throws Exception {
                    return getQuads(key.getColour(), key.getFacing());
                }

                public IBakedModel getBase(@Nullable EnumFacing facing) {
                    return ModelHelper.getBakedModel(baseModelLocation, TRSRTransformation.from((facing != null) ? facing : EnumFacing.NORTH));
                }

                public IBakedModel getLens(@Nullable EnumFacing facing) {
                    return ModelHelper.getBakedModel(lensModelLocation, TRSRTransformation.from((facing != null) ? facing : EnumFacing.NORTH));
                }

                List<BakedQuad> getBaseQuads(@Nullable EnumFacing facing) {
                    facing = (facing != null) ? facing : EnumFacing.NORTH;

                    TRSRTransformation transform = TRSRTransformation.from(facing);
                    IBakedModel bakedModel = ModelHelper.getBakedModel(baseModelLocation, transform);
                    return bakedModel.getQuads(MPSItems.INSTANCE.luxCapacitor.getDefaultState().withProperty(BlockDirectional.FACING, facing), null, 0);
                }

                List<BakedQuad> getLensColoredQuads(Colour color, @Nullable EnumFacing facing) {
                    facing = (facing != null) ? facing : EnumFacing.NORTH;
                    TRSRTransformation transform = TRSRTransformation.from(facing);
                    IBakedModel bakedModel = ModelHelper.getBakedModel(lensModelLocation, transform);
                    List<BakedQuad> quads = bakedModel.getQuads(MPSItems.INSTANCE.luxCapacitor.getDefaultState().withProperty(BlockDirectional.FACING, facing), null, 0);
                    return ModelHelper.getColoredQuadsWithGlow(quads, color, true);
                }

                List<BakedQuad> getQuads(Colour color, @Nullable EnumFacing facing) {
                    List<BakedQuad> frameList = getBaseQuads(facing);
                    List<BakedQuad> lensList = getLensColoredQuads(color, facing);

                    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                    for (BakedQuad quad : frameList)
                        builder.add(quad);
                    for (BakedQuad quad : lensList)
                        builder.add(quad);
                    return builder.build();
                }
            });
}