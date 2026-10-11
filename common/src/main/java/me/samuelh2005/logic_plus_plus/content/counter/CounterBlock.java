package me.samuelh2005.logic_plus_plus.content.counter;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class CounterBlock extends BaseEntityBlock {
    public static final MapCodec<CounterBlock> CODEC = simpleCodec(CounterBlock::new);
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CounterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CounterBlockEntity(pos, state);
    }

    // BaseEntityBlock defaults to INVISIBLE, so this is required
    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }


    private static boolean getSidedSignal(Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        Direction facing = state.getValue(FACING);

        // Rotate the relative side by however far the block is turned from NORTH
        Direction absolute = side;
        for (Direction d = Direction.NORTH; d != facing; d = d.getClockWise()) {
            absolute = absolute.getClockWise();
        }

        return level.getControlInputSignal(pos.relative(absolute), absolute, false) > 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        // Only run on the server side
        if (level.isClientSide) return;

        boolean clock = getSidedSignal(level, pos, Direction.EAST);
        boolean mode = getSidedSignal(level, pos, Direction.WEST);

        boolean triggered = state.getValue(TRIGGERED);

        if (clock && !triggered) {
            // Rising edge: count once or reset and remember we're powered
            level.setBlock(pos, state.setValue(TRIGGERED, true), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(pos) instanceof CounterBlockEntity counter) {
                if (mode) {
                    // If mode is true, load value from the behind input.
                    counter.setCount(0); // TODO: Load value from behind input instead of resetting to 0
                } else {
                    // If mode is false, increment the counter.
                    counter.setCount(counter.getCount() + 1);
                }
            }
        } else if (!clock && triggered) {
            // Falling edge: reset so the next pulse can count
            level.setBlock(pos, state.setValue(TRIGGERED, false), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CounterBlockEntity counter) {
            return counter.getCount();
        }
        return 0;
    }
}