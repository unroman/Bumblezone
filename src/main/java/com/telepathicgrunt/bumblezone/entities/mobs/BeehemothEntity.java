package com.telepathicgrunt.bumblezone.entities.mobs;

import com.telepathicgrunt.bumblezone.entities.BeeInteractivity;
import com.telepathicgrunt.bumblezone.entities.goals.BeehemothAIRide;
import com.telepathicgrunt.bumblezone.modinit.BzItems;
import com.telepathicgrunt.bumblezone.modinit.BzSounds;
import com.telepathicgrunt.bumblezone.tags.BzItemTags;
import com.telepathicgrunt.bumblezone.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class BeehemothEntity extends TamableAnimal implements FlyingAnimal {
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(BeehemothEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> QUEEN = SynchedEntityData.defineId(BeehemothEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FRIENDSHIP = SynchedEntityData.defineId(BeehemothEntity.class, EntityDataSerializers.INT);

    public static final int TICKS_PER_FLAP = Mth.ceil(1.4959966F);
    private boolean stopWandering = false;
    private final boolean hasItemTarget = false;

    public float offset1, offset2, offset3, offset4, offset5, offset6;

    public BeehemothEntity(EntityType<? extends BeehemothEntity> type, Level world) {
        super(type, world);
        this.moveControl = new MoveHelperController(this);
        this.offset1 = (this.random.nextFloat() - 0.5f);
        this.offset2 = (this.random.nextFloat() - 0.5f);
        this.offset3 = (this.random.nextFloat() - 0.5f);
        this.offset4 = (this.random.nextFloat() - 0.5f);
        this.offset5 = (this.random.nextFloat() - 0.5f);
        this.offset6 = (this.random.nextFloat() - 0.5f);
    }

    private static final TranslatableComponent QUEEN_NAME = new TranslatableComponent("entity.the_bumblezone.beehemoth_queen");

    @Override
    protected Component getTypeName() {
        if (isQueen()) {
            return QUEEN_NAME;
        }
        return super.getTypeName();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
        this.entityData.define(QUEEN, false);
        this.entityData.define(FRIENDSHIP, 0);
    }

    public boolean isQueen() {
        return this.entityData.get(QUEEN);
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    public void setQueen(boolean queen) {
        this.entityData.set(QUEEN, queen);
    }


    public int getFriendship() {
        return this.entityData.get(FRIENDSHIP);
    }

    public void setFriendship(Integer newFriendship) {
        this.entityData.set(FRIENDSHIP, Math.min(newFriendship, 1000));
    }

    public void addFriendship(Integer deltaFriendship) {
        this.entityData.set(FRIENDSHIP, getFriendship() + deltaFriendship);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("saddled", isSaddled());
        tag.putBoolean("queen", isQueen());
        tag.putInt("friendship", getFriendship());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSaddled(tag.getBoolean("saddled"));
        setQueen(tag.contains("queen") && tag.getBoolean("queen"));
        setFriendship(tag.getInt("friendship"));
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && source.getEntity().getUUID().equals(getOwnerUUID())) {
            addFriendship((int) (-3 * amount));
        }
        else {
            addFriendship((int) -amount);
        }

        spawnMadParticles();
        return super.hurt(source, amount);
    }

    public static AttributeSupplier.Builder getAttributeBuilder() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 42.0D)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 128.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeehemothAIRide(this, 0.85D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.5D, Ingredient.of(BzItems.HONEY_BUCKET), false));
        this.goalSelector.addGoal(4, new RandomFlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 60));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new FloatGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new DirectPathNavigator(this, pLevel);
    }

    public static boolean checkMobSpawnRules(EntityType<? extends Mob> entityType, LevelAccessor iWorld, MobSpawnType spawnReason, BlockPos blockPos, Random random) {
        return true;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader worldReader) {
        AABB box = this.getBoundingBox();
        return !worldReader.containsAnyLiquid(box) && worldReader.getBlockStates(box).noneMatch(state -> state.getMaterial().blocksMotion()) && worldReader.isUnobstructed(this);
    }

    @Override
    public Entity getControllingPassenger() {
        for (Entity p : this.getPassengers()) {
            return p;
        }
        return null;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        ResourceLocation itemRL = Registry.ITEM.getKey(item);
        if (this.level.isClientSide) {
            if (this.isTame() && this.isOwnedBy(player)) {
                return InteractionResult.SUCCESS;
            } else {
                return !(this.getHealth() < this.getMaxHealth()) && this.isTame() ? InteractionResult.PASS : InteractionResult.SUCCESS;
            }
        }
        else {
            // Healing and befriending Beehemoth
            if (this.isTame()) {
                if (this.isOwnedBy(player)) {
                    if (BzItemTags.BEE_FEEDING_ITEMS.contains(item)) {
                        if (BzItemTags.HONEY_BUCKETS.contains(item)) {
                            this.heal(this.getMaxHealth() - this.getHealth());
                            BeeInteractivity.calmAndSpawnHearts(this.level, player, this, 0.8f, 5);
                            addFriendship(5);
                        }
                        else if (itemRL.getPath().contains("honey")) {
                            this.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 2, false, false, false));
                            BeeInteractivity.calmAndSpawnHearts(this.level, player, this, 0.3f, 3);
                            addFriendship(3);
                        }
                        else {
                            this.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1, false, false, false));
                            BeeInteractivity.calmAndSpawnHearts(this.level, player, this, 0.1f, 3);
                            addFriendship(1);
                        }

                        if (!player.isCreative()) {
                            // remove current item
                            stack.shrink(1);
                            GeneralUtils.givePlayerItem(player, hand, new ItemStack(item), true);
                        }

                        player.swing(hand, true);
                        return InteractionResult.CONSUME;
                    }

                    if (item == Items.SADDLE && !isSaddled()) {
                        this.usePlayerItem(player, hand, stack);
                        this.setSaddled(true);
                        return InteractionResult.CONSUME;
                    }

                    if (stack.isEmpty() && isSaddled() && player.isShiftKeyDown()) {
                        setSaddled(false);
                        ItemStack saddle = new ItemStack(Items.SADDLE);
                        if (player.addItem(saddle)) {
                            ItemEntity entity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), saddle);
                            player.level.addFreshEntity(entity);
                        }
                    }

                    if (stack.isEmpty() && !this.isVehicle() && !player.isSecondaryUseActive()) {
                        if (!this.level.isClientSide) {
                            player.startRiding(this);
                        }

                        return InteractionResult.sidedSuccess(this.level.isClientSide);
                    }
                }
            }
            // Taming Beehemoth
            else if (BzItemTags.BEE_FEEDING_ITEMS.contains(item)) {
                if(getFriendship() >= 0) {
                    int tameChance;
                    if (BzItemTags.HONEY_BUCKETS.contains(item)) {
                        tameChance = 5;
                    }
                    else if (itemRL.getPath().contains("honey")) {
                        tameChance = 10;
                    }
                    else {
                        tameChance = 15;
                    }

                    if (this.random.nextInt(tameChance) == 0) {
                        this.tame(player);
                        setFriendship(6);
                        this.setOrderedToSit(true);
                        this.level.broadcastEntityEvent(this, (byte) 7);
                    }
                    else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                    }
                }
                else {
                    addFriendship(1);
                    if (BzItemTags.HONEY_BUCKETS.contains(item)) {
                        addFriendship(3);
                    }
                    else if (itemRL.getPath().contains("honey")) {
                        addFriendship(2);
                    }
                    else {
                        addFriendship(1);
                    }
                }

                if (!player.isCreative()) {
                    // remove current item
                    stack.shrink(1);
                    GeneralUtils.givePlayerItem(player, hand, new ItemStack(item), true);
                }
                this.setPersistenceRequired();
                player.swing(hand, true);

                if(getFriendship() < 0) {
                    spawnMadParticles();
                }

                return InteractionResult.CONSUME;
            }

            InteractionResult actionresulttype1 = super.mobInteract(player, hand);
            if (actionresulttype1.consumesAction()) {
                this.setPersistenceRequired();
            }

            if(getFriendship() < 0) {
                spawnMadParticles();
            }

            return actionresulttype1;
        }
    }

    private void spawnMadParticles() {
        if (!this.level.isClientSide())
        {
            ((ServerLevel) this.level).sendParticles(
                    ParticleTypes.ANGRY_VILLAGER,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    Math.min(Math.max(1, getFriendship() / -3), 7),
                    this.level.getRandom().nextFloat() * 1.0f - 0.5f,
                    this.level.getRandom().nextFloat() * 0.4f + 0.4f,
                    this.level.getRandom().nextFloat() * 1.0f - 0.5f,
                    this.level.getRandom().nextFloat() * 0.8f + 0.4f);
        }
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float radius = -0.25F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + extraZ);

            double currentSpeed = this.getDeltaMovement().length();
            if(currentSpeed > 0.000001D &&
                this.level.random.nextFloat() < 0.0085D &&
                passenger.getUUID().equals(getOwnerUUID()))
            {
                addFriendship(1);
            }
        }
    }

    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.animationSpeed);
        float f1 = this.animationPosition;
        return (double) this.getBbHeight() - 0.2D + (double) (0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f);
    }

    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageModifier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void playBlockFallSound() {
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return BzSounds.BEEHEMOTH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return BzSounds.BEEHEMOTH_DEATH;
    }

    public void tick() {
        super.tick();
        stopWandering = isLeashed();

        // Become queen if friendship is maxed out.
        if(!isQueen() && getFriendship() >= 1000) {
            setQueen(true);
        }
        // Become untamed if bee is no longer a friend
        else if(getFriendship() < 0 && isTame()) {
            ejectPassengers();
            this.setTame(false);
            this.setOwnerUUID(null);
            spawnMadParticles();
        }
    }

    private BlockPos getGroundPosition(BlockPos radialPos) {
        while (radialPos.getY() > 1 && level.isEmptyBlock(radialPos)) {
            radialPos = radialPos.below();
        }
        if (radialPos.getY() <= 1) {
            return new BlockPos(radialPos.getX(), level.getSeaLevel(), radialPos.getZ());
        }
        return radialPos;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return null;
    }

    @Override
    public void setLeashedTo(Entity pEntity, boolean pSendAttachNotification) {
        super.setLeashedTo(pEntity, pSendAttachNotification);
        stopWandering = true;
    }

    @Override
    public boolean isFlying() {
        return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
    }

    static class MoveHelperController extends MoveControl {
        private final BeehemothEntity beehemothEntity;

        public MoveHelperController(BeehemothEntity beehemothEntity) {
            super(beehemothEntity);
            this.beehemothEntity = beehemothEntity;
        }

        public void tick() {
            if (this.operation == Operation.STRAFE) {
                Vec3 vector3d = new Vec3(this.wantedX - beehemothEntity.getX(), this.wantedY - beehemothEntity.getY(), this.wantedZ - beehemothEntity.getZ());
                double d0 = vector3d.length();
                beehemothEntity.setDeltaMovement(beehemothEntity.getDeltaMovement().add(0, vector3d.scale(this.speedModifier * 0.05D / d0).y(), 0));
                float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float f1 = (float) this.speedModifier * f;
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;

                this.mob.setSpeed(f1);
                this.mob.setZza(this.strafeForwards);
                this.mob.setXxa(this.strafeRight);
                this.operation = MoveControl.Operation.WAIT;
            } else if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - beehemothEntity.getX(), this.wantedY - beehemothEntity.getY(), this.wantedZ - beehemothEntity.getZ());
                double d0 = vector3d.length();
                if (d0 < beehemothEntity.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    beehemothEntity.setDeltaMovement(beehemothEntity.getDeltaMovement().scale(0.5D));
                } else {
                    double localSpeed = this.speedModifier;
                    if (beehemothEntity.isVehicle()) {
                        localSpeed *= 1.5D;
                    }
                    beehemothEntity.setDeltaMovement(beehemothEntity.getDeltaMovement().add(vector3d.scale(localSpeed * 0.005D / d0)));
                    if (beehemothEntity.getTarget() == null) {
                        double d2 = this.wantedX - beehemothEntity.getX();
                        double d1 = this.wantedZ - beehemothEntity.getZ();
                        float newRot = (float)(-Mth.atan2(d2, d1) * (180F / (float) Math.PI));
                        beehemothEntity.setYRot(this.rotlerp(beehemothEntity.getYRot(), newRot, 10.0F));
                    } else {
                        double d2 = beehemothEntity.getTarget().getX() - beehemothEntity.getX();
                        double d1 = beehemothEntity.getTarget().getZ() - beehemothEntity.getZ();
                        float newRot = (float)(-Mth.atan2(d1, d2) * (180F / (float) Math.PI));
                        beehemothEntity.setYRot(this.rotlerp(beehemothEntity.getYRot(), newRot, 10.0F));
                    }
                }

            }
        }
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(vec3, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public class DirectPathNavigator extends GroundPathNavigation {

        private final Mob mob;

        public DirectPathNavigator(Mob mob, Level world) {
            super(mob, world);
            this.mob = mob;
        }

        public void tick() {
            ++this.tick;
        }

        public boolean moveTo(double x, double y, double z, double speedIn) {
            mob.getMoveControl().setWantedPosition(x, y, z, speedIn);
            return true;
        }

        public boolean moveTo(Entity entityIn, double speedIn) {
            mob.getMoveControl().setWantedPosition(entityIn.getX(), entityIn.getY(), entityIn.getZ(), speedIn);
            return true;
        }
    }


    static class RandomFlyGoal extends Goal {
        private final BeehemothEntity parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(BeehemothEntity mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl movementcontroller = this.parentEntity.getMoveControl();
            if (parentEntity.stopWandering || parentEntity.hasItemTarget) {
                return false;
            }
            if (!movementcontroller.hasWanted() || target == null) {
                target = getBlockInViewBeehemoth();
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && !parentEntity.stopWandering && !parentEntity.hasItemTarget && parentEntity.distanceToSqr(Vec3.atCenterOf(target)) > 2.4D && parentEntity.getMoveControl().hasWanted() && !parentEntity.horizontalCollision;
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewBeehemoth();
            }
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 2.5F) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewBeehemoth() {
            float radius = 1 + parentEntity.getRandom().nextInt(5);
            float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.yBodyRot;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (parentEntity.getRandom().nextFloat() * neg);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos(parentEntity.getX() + extraX, parentEntity.getY() + 2, parentEntity.getZ() + extraZ);
            BlockPos ground = parentEntity.getGroundPosition(radialPos);
            BlockPos newPos = ground.above(1 + parentEntity.getRandom().nextInt(6));
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6) {
                return newPos;
            }
            return null;
        }
    }
}
