package me.samuelh2005.logic_plus_plus.content.digital_cable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import com.mojang.serialization.MapCodec;

import me.samuelh2005.logic_plus_plus.foundation.ModBlocks;
import me.samuelh2005.logic_plus_plus.foundation.ModTags;
import me.samuelh2005.logic_plus_plus.systems.ValueSource;
import me.samuelh2005.logic_plus_plus.systems.ValueSourceProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class DigitalCableBlock extends BaseEntityBlock {
    private static final int MAX_NETWORK = 256;

    public static final MapCodec<DigitalCableBlock> CODEC = simpleCodec(DigitalCableBlock::new);
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public DigitalCableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, UP, DOWN});
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DigitalCableBlockEntity(pos, state);
    }

    // BaseEntityBlock defaults to INVISIBLE, so this is required
    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    private static boolean connection(BlockState neighbor) {
        return neighbor.is(ModTags.DIGITAL_BLOCKS);
    }

    private static BooleanProperty getPropertyForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = defaultBlockState();

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            state = state.setValue(getPropertyForDirection(dir), connection(neighborState));
        }

        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                    LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        state = state.setValue(getPropertyForDirection(dir), connection(neighborState));
        return state;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moved) {
        if (!level.isClientSide) level.scheduleTick(pos, this, 1);   // neighborChanged isn't called on placement
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) level.scheduleTick(pos, this, 1);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Set<BlockPos> network = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        network.add(pos);
        queue.add(pos);
        int value = 0;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            BlockState currentState = level.getBlockState(current);

            for (Direction dir : Direction.values()) {
                // This side of the cable is disconnected: don't traverse or read through it
                if (!currentState.getValue(getPropertyForDirection(dir))) continue;

                BlockPos next = current.relative(dir);
                BlockState nextState = level.getBlockState(next);

                if (nextState.is(ModBlocks.DIGITAL_CABLE.get())) {
                    // Both cables must agree the link exists
                    if (!nextState.getValue(getPropertyForDirection(dir.getOpposite()))) continue;
                    if (network.size() < MAX_NETWORK && network.add(next)) queue.add(next);
                } else if (nextState.is(ModTags.DIGITAL_BLOCKS)) {
                    ValueSource src = ValueSourceProvider.find(level, next, dir.getOpposite());
                    if (src != null) value = Math.max(value, src.value());
                }
            }
        }

        for (BlockPos p : network) {
            if (level.getBlockEntity(p) instanceof DigitalCableBlockEntity cable && cable.getValue() != value) {
                cable.setValue(value);

                // Notify connected non-cable neighbours (gates etc.); cables are handled by this flood fill
                BlockState cableState = level.getBlockState(p);
                for (Direction dir : Direction.values()) {
                    if (!cableState.getValue(getPropertyForDirection(dir))) continue;
                    BlockPos n = p.relative(dir);
                    if (!level.getBlockState(n).is(ModBlocks.DIGITAL_CABLE.get())) {
                        level.neighborChanged(n, this, p);
                    }
                }
            }
        }
    }
}