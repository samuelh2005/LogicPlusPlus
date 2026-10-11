package me.samuelh2005.logic_plus_plus.content.digital_cable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public class DigitalCableBlock extends BaseEntityBlock {
    private static final int MAX_NETWORK = 256;

    public static final MapCodec<DigitalCableBlock> CODEC = simpleCodec(DigitalCableBlock::new);
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
        Direction.NORTH, NORTH,
        Direction.EAST, EAST,
        Direction.SOUTH, SOUTH,
        Direction.WEST, WEST
    ));

    public DigitalCableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(NORTH, RedstoneSide.NONE)
            .setValue(EAST, RedstoneSide.NONE)
            .setValue(SOUTH, RedstoneSide.NONE)
            .setValue(WEST, RedstoneSide.NONE)

        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, SOUTH, WEST});
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

    private static RedstoneSide connection(BlockState neighbor) {
        return neighbor.is(ModTags.DIGITAL_BLOCKS) ? RedstoneSide.SIDE : RedstoneSide.NONE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = defaultBlockState();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir),
                    connection(level.getBlockState(pos.relative(dir))));
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                    LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir.getAxis().isHorizontal()) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(dir), connection(neighborState));
        }
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
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos next = current.relative(dir);
                BlockState nextState = level.getBlockState(next);
                if (nextState.is(ModBlocks.DIGITAL_CABLE.get())) {
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
                // Tell only non-cable neighbours (gates etc.); cables are already handled by this flood fill
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos n = p.relative(dir);
                    if (!level.getBlockState(n).is(ModBlocks.DIGITAL_CABLE.get())) {
                        level.neighborChanged(n, this, p);
                    }
                }
            }
        }
    }
}