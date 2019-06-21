package net.machinemuse.powersuits.tileentity;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.tileentity.MuseTileEntity;
import net.machinemuse.powersuits.basemod.MPSItems;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/21/16.
 */
public class TileEntityTinkerTable extends MuseTileEntity {
    Direction facing;

    public TileEntityTinkerTable() {
        super(MPSItems.tinkerTableTileEntityType);
        this.facing = Direction.NORTH;
    }

    public TileEntityTinkerTable(Direction facing) {
        super(MPSItems.tinkerTableTileEntityType);
        this.facing = facing;
    }

    public Direction getFacing() {
        return (this.facing != null) ? this.facing : Direction.NORTH;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound nbt) {
        super.write(nbt);
        nbt.putInt("f", facing.ordinal());
        return nbt;
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);
        if (nbt.contains("f")) {
            facing = Direction.values()[nbt.getInt("f")];
        } else {
            MuseLogger.logger.debug("No NBT found! D:");
        }
    }
}