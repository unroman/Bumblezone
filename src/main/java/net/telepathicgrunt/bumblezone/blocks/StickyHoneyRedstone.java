package net.telepathicgrunt.bumblezone.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StickyHoneyRedstone extends StickyHoneyResidue {
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final Box DOWN_REAL_AABB = new Box(0.0D, 0.0D, 0.0D, 1D, 0.2D, 1D);
    protected static final Box UP_REAL_AABB = new Box(0.0D, 0.8D, 0.0D, 1D, 1D, 1D);
    protected static final Box NORTH_REAL_AABB = new Box(0.0D, 0.0D, 0.0D, 1D, 1D, 0.2D);
    protected static final Box EAST_REAL_AABB = new Box(0.8D, 0.0D, 0.0D, 1D, 1D, 1D);
    protected static final Box WEST_REAL_AABB = new Box(0.0D, 0.0D, 0.0D, 0.2D, 1D, 1D);
    protected static final Box SOUTH_REAL_AABB = new Box(0.0D, 0.0D, 0.2D, 1D, 1D, 1D);
    public static final Map<Direction, Box> FACING_TO_AABB_MAP;

    static {
        Map<Direction, Box> map = new HashMap<Direction, Box>();

        map.put(Direction.DOWN, DOWN_REAL_AABB);
        map.put(Direction.UP, UP_REAL_AABB);
        map.put(Direction.EAST, EAST_REAL_AABB);
        map.put(Direction.WEST, WEST_REAL_AABB);
        map.put(Direction.NORTH, NORTH_REAL_AABB);
        map.put(Direction.SOUTH, SOUTH_REAL_AABB);

        FACING_TO_AABB_MAP = map;
    }

    public StickyHoneyRedstone() {
        super();
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(UP, false)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(DOWN, false)
                .with(POWERED, false));
    }

    /**
     * Set up properties.
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add().add(UP, NORTH, EAST, SOUTH, WEST, DOWN, POWERED);
    }

    /**
     * Slows all entities inside the block and triggers being powered.
     */
    @Deprecated
    @Override
    public void onEntityCollision(BlockState blockstate, World world, BlockPos pos, Entity entity) {
        updateState(world, pos, blockstate, 0);
        super.onEntityCollision(blockstate, world, pos, entity);
    }

    @Override
    public int getWeakRedstonePower(BlockState blockstate, BlockView blockAccess, BlockPos pos, Direction side) {
        return blockstate.get(POWERED) ? 1 : 0;
    }

    /**
     * Remove vine's ticking with removing power instead.
     */
    @Override
    public void scheduledTick(BlockState blockstate, ServerWorld world, BlockPos pos, Random rand) {
        this.updateState(world, pos, blockstate, blockstate.get(POWERED) ? 1 : 0);
    }

    /**
     * Notifies blocks that this block is attached to of changes
     */
    protected void updateNeighbors(BlockState blockstate, World world, BlockPos pos) {
        if (blockstate.getBlock() != BzBlocks.STICKY_HONEY_REDSTONE)
            return;

        for (Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = StickyHoneyResidue.FACING_TO_PROPERTY_MAP.get(direction);
            if (blockstate.get(booleanproperty)) {
                world.updateNeighbor(pos.offset(direction), blockstate.getBlock(), pos);
            }
        }
    }


    /**
     * notify neighbor of changes when replaced
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onBlockRemoved(BlockState blockstate, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && blockstate.getBlock() != newState.getBlock()) {
            if (blockstate.get(POWERED)) {
                this.updateNeighbors(blockstate, world, pos);
            }

            super.onBlockRemoved(blockstate, world, pos, newState, isMoving);
        }
    }

    /**
     * Updates the sticky residue block when entity enters or leaves
     */
    protected void updateState(World world, BlockPos pos, BlockState oldBlockstate, int oldRedstoneStrength) {
        int newPower = this.computeRedstoneStrength(oldBlockstate, world, pos);
        boolean flag1 = newPower > 0;
        if (oldRedstoneStrength != newPower) {
            BlockState newBlockstate = this.setRedstoneStrength(oldBlockstate, newPower);
            world.setBlockState(pos, newBlockstate, 2);
            this.updateNeighbors(oldBlockstate, world, pos);
            world.checkBlockRerender(pos, oldBlockstate, newBlockstate);
        }

        if (flag1) {
            world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate(world));
        }
    }


    /**
     * Set if block is powered or not
     */
    protected BlockState setRedstoneStrength(BlockState blockstate, int strength) {
        return blockstate.with(POWERED, Boolean.valueOf(strength > 0));
    }

    /**
     * Detects if any entity is inside this block and outputs power if so
     */
    protected int computeRedstoneStrength(BlockState blockstate, World world, BlockPos pos) {

        Box axisalignedbb = getOutlineShape(blockstate, world, pos, null).getBoundingBox().offset(pos);
        List<? extends Entity> list = world.getEntitiesIncludingUngeneratedChunks(LivingEntity.class, axisalignedbb);

        if (!list.isEmpty()) {
            return 1;
        }

        return 0;
    }
}
