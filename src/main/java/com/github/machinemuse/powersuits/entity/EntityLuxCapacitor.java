/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.entity;

import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.block.BlockLuxCapacitor;
import com.github.machinemuse.powersuits.block.TileEntityLuxCapacitor;
import com.github.machinemuse.powersuits.basemod.MPSItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import static com.github.machinemuse.powersuits.block.BlockLuxCapacitor.COLOR;

public class EntityLuxCapacitor extends EntityThrowable implements IEntityAdditionalSpawnData {
    public Colour color;

    public EntityLuxCapacitor(World par1World) {
        super(par1World);
        if (color == null)
            color = Colour.WHITE;
    }

    public EntityLuxCapacitor(World world, EntityLivingBase shootingEntity, Colour color) {
        super(world, shootingEntity);
        this.color = color != null ? color : BlockLuxCapacitor.defaultColor;
        Vec3d direction = shootingEntity.getLookVec().normalize();
        double speed = 1.0;
        this.motionX = direction.x * speed;
        this.motionY = direction.y * speed;
        this.motionZ = direction.z * speed;
        double r = 0.4375;
        double xoffset = 0.1;
        double yoffset = 0;
        double zoffset = 0;
        double horzScale = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double horzx = direction.x / horzScale;
        double horzz = direction.z / horzScale;
        this.posX = shootingEntity.posX + direction.x * xoffset - direction.y * horzx * yoffset - horzz * zoffset;
        this.posY = shootingEntity.posY + shootingEntity.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
        this.posZ = shootingEntity.posZ + direction.z * xoffset - direction.y * horzz * yoffset + horzx * zoffset;
        this.setEntityBoundingBox(new AxisAlignedBB(posX - r, posY - 0.0625, posZ - r, posX + r, posY + 0.0625, posZ + r));
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (this.ticksExisted > 400) {
            this.setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult hitResult) {
        if (color == null)
            color = Colour.WHITE;

        if (!this.isDead && hitResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            EnumFacing dir = hitResult.sideHit.getOpposite();
            int x = hitResult.getBlockPos().getX() - dir.getXOffset();
            int y = hitResult.getBlockPos().getY() - dir.getYOffset();
            int z = hitResult.getBlockPos().getZ() - dir.getZOffset();
            if (y > 0) {
                BlockPos blockPos = new BlockPos(x, y, z);
                if (MPSItems.INSTANCE.luxCapacitor.canPlaceAt(world, blockPos, dir)) {
                    IBlockState blockState = MPSItems.INSTANCE.luxCapacitor.getStateForPlacement(world, blockPos, dir, hitResult.getBlockPos().getX(), hitResult.getBlockPos().getY(), hitResult.getBlockPos().getZ(), 0, null, EnumHand.MAIN_HAND);
                    world.setBlockState(blockPos, ((IExtendedBlockState) blockState).withProperty(COLOR, color));
                    world.setTileEntity(blockPos, new TileEntityLuxCapacitor(color));
                } else {
                    for (EnumFacing facing : EnumFacing.values()) {
                        if (MPSItems.INSTANCE.luxCapacitor.canPlaceAt(world, blockPos, facing)) {
                            IBlockState blockState = MPSItems.INSTANCE.luxCapacitor.getStateForPlacement(world, blockPos, facing, hitResult.getBlockPos().getX(), hitResult.getBlockPos().getY(), hitResult.getBlockPos().getZ(), 0, null, EnumHand.MAIN_HAND);
                            world.setBlockState(blockPos, ((IExtendedBlockState) blockState).withProperty(COLOR, color));
                            world.setTileEntity(blockPos, new TileEntityLuxCapacitor(color));
                            break;
                        }
                    }
                }
                this.setDead();
            }
        }
    }

    /* using these to sync color between client and server, since without this, color isn't initialized */
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if (color == null)
            color = Colour.WHITE;
        buffer.writeInt(color.getInt());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.color = new Colour(additionalData.readInt());
    }
}