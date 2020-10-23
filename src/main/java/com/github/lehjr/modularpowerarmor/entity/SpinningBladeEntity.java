package com.github.lehjr.modularpowerarmor.entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.mpalib.util.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

public class SpinningBladeEntity extends ThrowableEntity {
    public static final int SIZE = 24;
    public double damage;
    public ItemStack shootingItem = ItemStack.EMPTY;

    public SpinningBladeEntity(EntityType<? extends SpinningBladeEntity> entityType, World world) {
        super(entityType, world);
    }

    public SpinningBladeEntity(World worldIn, LivingEntity shootingEntity) {
        super(MPAObjects.SPINNING_BLADE_ENTITY_TYPE.get(), shootingEntity, worldIn);
        this.setShooter(shootingEntity);
        if (shootingEntity instanceof PlayerEntity) {
            AtomicDouble atomicDamage = new AtomicDouble(0);

            this.shootingItem = ((PlayerEntity) shootingEntity).inventory.getCurrentItem();
            this.shootingItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModeChangingItem -> {
                if (iModeChangingItem instanceof IModeChangingItem) {
                    ((IModeChangingItem) iModeChangingItem).getActiveModule().getCapability(PowerModuleCapability.POWER_MODULE)
                            .ifPresent(m-> atomicDamage.getAndAdd(m.applyPropertyModifiers(MPAConstants.BLADE_DAMAGE)));
                }
            });
            damage = atomicDamage.get();
        }
        Vector3d direction = shootingEntity.getLookVec().normalize();
        double speed = 1.0;
        double scale = 1;
        this.setMotion(
                direction.x * speed,
                direction.y * speed,
                direction.z * speed
        );
        double r = 1;
        double xoffset = 1.3f + r - direction.y * shootingEntity.getEyeHeight();
        double yoffset = -.2;
        double zoffset = 0.3f;
        double horzScale = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double horzx = direction.x / horzScale;
        double horzz = direction.z / horzScale;
        this.setPosition(
                // x
                (shootingEntity.getPosX() + direction.x * xoffset - direction.y * horzx * yoffset - horzz * zoffset),
                // y
                (shootingEntity.getPosY() + shootingEntity.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset),
                // z
                (shootingEntity.getPosZ() + direction.z * xoffset - direction.y * horzz * yoffset + horzx * zoffset)
        );
        this.setBoundingBox(new AxisAlignedBB(getPosX() - r, getPosY() - r, getPosZ() - r, getPosX() + r, getPosY() + r, getPosZ() + r));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ticksExisted > this.getMaxLifetime()) {
            this.remove();
        }
    }

    @Override
    protected void onImpact(RayTraceResult hitResult) {
        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            World world = this.world;
            if (world == null) {
                return;
            }

            BlockRayTraceResult result = (BlockRayTraceResult) hitResult;
            Block block = world.getBlockState(result.getPos()).getBlock();
            if (block instanceof IForgeShearable && this.func_234616_v_() instanceof PlayerEntity) {
                IForgeShearable target = (IForgeShearable) block;
                if (target.isShearable(this.shootingItem, world, result.getPos()) && !world.isRemote) {
                    // onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune)

                    List<ItemStack> drops = target.onSheared((PlayerEntity) this.func_234616_v_(), this.shootingItem, world, result.getPos(),
                            EnchantmentHelper.getEnchantmentLevel(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("fortune")), this.shootingItem));
                    Random rand = new Random();

                    for (ItemStack stack : drops) {
                        float f = 0.7F;
                        double d = rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double d1 = rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double d2 = rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        ItemEntity entityitem = new ItemEntity(world, result.getPos().getX() + d, result.getPos().getY() + d1, result.getPos().getZ() + d2, stack);
                        entityitem.setPickupDelay(10);
                        world.addEntity(entityitem);
                    }
//                    if (this.shootingEntity instanceof PlayerEntity) {
//                        ((PlayerEntity) shootingEntity).addStat(StatList.getBlockStats(block), 1);
//                    }
                }
                world.destroyBlock(result.getPos(), true);// Destroy block and drop item
            } else { // Block hit was not IForgeShearable
                this.remove();
            }
        } else if (hitResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult)hitResult).getEntity() != func_234616_v_()) {
            EntityRayTraceResult result = (EntityRayTraceResult) hitResult;
            if (result.getEntity() instanceof IForgeShearable) {
                IForgeShearable target = (IForgeShearable) result.getEntity();
                Entity entity = result.getEntity();
                if (target.isShearable(this.shootingItem, entity.world, entity.getPosition()) && this.func_234616_v_() instanceof PlayerEntity) {
                    List<ItemStack> drops = target.onSheared((PlayerEntity) func_234616_v_(), this.shootingItem, entity.world,
                            entity.getPosition(),
                            EnchantmentHelper.getEnchantmentLevel(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("fortune")), this.shootingItem));

                    Random rand = new Random();
                    for (ItemStack drop : drops) {
                        ItemEntity ent = entity.entityDropItem(drop, 1.0F);

                        ent.setMotion(ent.getMotion().add(
                                (rand.nextFloat() - rand.nextFloat()) * 0.1F,
                                rand.nextFloat() * 0.05,
                                (rand.nextFloat() - rand.nextFloat()) * 0.1F
                        ));
                    }
                }
            } else {
                result.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, func_234616_v_()), (int) damage);
            }
        }
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    public int getMaxLifetime() {
        return 200;
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
