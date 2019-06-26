package net.machinemuse.powersuits.entity;

import net.machinemuse.powersuits.basemod.MPSItems;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class PlasmaBoltEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {
    public static final int SIZE = 24;
    public double size;
    public double damagingness;
    public double explosiveness;
    public Entity shootingEntity;

    public PlasmaBoltEntity(EntityType<? extends PlasmaBoltEntity> entityType, World world) {
        super(entityType, world);
    }

    public PlasmaBoltEntity(World world, LivingEntity shootingEntity, double explosivenessIn, double damagingnessIn, int chargeTicks) {
        super(MPSItems.PLASMA_BOLT_ENTITY_TYPE, world);
        this.shootingEntity = shootingEntity;
        this.size = ((chargeTicks) > 50 ? 50 : chargeTicks);
        this.explosiveness = explosivenessIn;
        this.damagingness = damagingnessIn;
        Vec3d direction = shootingEntity.getLookVec().normalize();
        double scale = 1.0;
        this.setMotion(
                direction.x * scale,
                direction.y * scale,
                direction.z * scale
        );
        double r = this.size / 50.0;
        double xoffset = 1.3f + r - direction.y * shootingEntity.getEyeHeight();
        double yoffset = -.2;
        double zoffset = 0.3f;
        double horzScale = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double horzx = direction.x / horzScale;
        double horzz = direction.z / horzScale;
        this.posX = shootingEntity.posX + direction.x * xoffset - direction.y * horzx * yoffset - horzz * zoffset;
        this.posY = shootingEntity.posY + shootingEntity.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
        this.posZ = shootingEntity.posZ + direction.z * xoffset - direction.y * horzz * yoffset + horzx * zoffset;
        this.setBoundingBox(new AxisAlignedBB(posX - r, posY - r, posZ - r, posX + r, posY + r, posZ + r));
    }

    @Override
    protected void registerData() {

    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.ticksExisted > this.getMaxLifetime()) {
            this.remove();
        }
        if (this.isInWater()) {
            this.remove();
            for (int var3 = 0; var3 < this.size; ++var3) {
                this.world.addParticle(ParticleTypes.FLAME,
                        this.posX + Math.random() * 1, this.posY + Math.random() * 1, this.posZ + Math.random()
                                * 0.1,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    public int getMaxLifetime() {
        return 200;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }
//
//    @Override
//    public void read(CompoundNBT var1) {
//        this.setDead();
//    }
//
//    @Override
//    public void write(CompoundNBT var1) {
//    }






    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        double damage = this.size / 50.0 * this.damagingness;
        switch (result.getType()) {
            case ENTITY:
                EntityRayTraceResult rayTraceResult = (EntityRayTraceResult) result;
                if (rayTraceResult.getEntity() != null && rayTraceResult.getEntity() != shootingEntity) {
                    rayTraceResult.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (int) damage);
                }
                break;
            case BLOCK:
                break;
            default:
                break;
        }
        if (!this.world.isRemote) { // Dist.SERVER
            boolean flag = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);

            // FIXME: this is probably all wrone
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float) (this.size / 50.0f * 3 * this.explosiveness), flag ? Explosion.Mode.DESTROY : Explosion.Mode.BREAK);
        }
        for (int var3 = 0; var3 < 8; ++var3) {
            this.world.addParticle(ParticleTypes.FLAME,
                    this.posX + Math.random() * 0.1, this.posY + Math.random() * 0.1, this.posZ + Math.random() * 0.1,
                    0.0D, 0.0D, 0.0D);
        }
        if (!this.world.isRemote) {
            this.remove();
        }
    }

    /**
     * Called by the server when constructing the spawn packet.
     * Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeDouble(this.size);
    }

    /**
     * Called by the client when it receives a Entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param additionalData The packet data stream
     */
    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.size = additionalData.readDouble();
    }
}