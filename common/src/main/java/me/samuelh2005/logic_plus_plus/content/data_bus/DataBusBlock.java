package me.samuelh2005.logic_plus_plus.content.data_bus;

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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class DataBusBlock extends BaseEntityBlock {
    private static final int MAX_NETWORK = 256;

    public static final MapCodec<DataBusBlock> CODEC = simpleCodec(DataBusBlock::new);
    public static final EnumProperty<DataBusConnection> NORTH = EnumProperty.create("north", DataBusConnection.class);
    public static final EnumProperty<DataBusConnection> EAST = EnumProperty.create("east", DataBusConnection.class);
    public static final EnumProperty<DataBusConnection> SOUTH = EnumProperty.create("south", DataBusConnection.class);
    public static final EnumProperty<DataBusConnection> WEST = EnumProperty.create("west", DataBusConnection.class);
    public static final EnumProperty<DataBusConnection> UP = EnumProperty.create("up", DataBusConnection.class);
    public static final EnumProperty<DataBusConnection> DOWN = EnumProperty.create("down", DataBusConnection.class);

    public DataBusBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(NORTH, DataBusConnection.NONE)
            .setValue(EAST, DataBusConnection.NONE)
            .setValue(SOUTH, DataBusConnection.NONE)
            .setValue(WEST, DataBusConnection.NONE)
            .setValue(UP, DataBusConnection.NONE)
            .setValue(DOWN, DataBusConnection.NONE)
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
        return new DataBusBlockEntity(pos, state);
    }

    // BaseEntityBlock defaults to INVISIBLE, so this is required
    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    private static DataBusConnection resolve(DataBusConnection current, Direction dir, BlockState neighbor) {
        // The player's choice sticks until they toggle it again
        if (current == DataBusConnection.DISABLED) return DataBusConnection.DISABLED;

        if (!neighbor.is(ModTags.DIGITAL_BLOCKS)) return DataBusConnection.NONE;

        // The neighbouring bus has switched off its side facing us, so don't draw an arm toward it
        if (neighbor.is(ModBlocks.DATA_BUS.get())
                && neighbor.getValue(getPropertyForDirection(dir.getOpposite())) == DataBusConnection.DISABLED) {
            return DataBusConnection.NONE;
        }

        return DataBusConnection.CONNECTED;
    }

    private static EnumProperty<DataBusConnection> getPropertyForDirection(Direction dir) {
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
            BlockState neighborState = level.getBlockState(pos.relative(dir));
            state = state.setValue(getPropertyForDirection(dir),
                    resolve(DataBusConnection.NONE, dir, neighborState));
        }

        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                    LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        EnumProperty<DataBusConnection> prop = getPropertyForDirection(dir);
        return state.setValue(prop, resolve(state.getValue(prop), dir, neighborState));
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
                DataBusConnection side = currentState.getValue(getPropertyForDirection(dir));
                if (side != DataBusConnection.CONNECTED) continue;   // NONE or DISABLED: don't traverse or read

                BlockPos next = current.relative(dir);
                BlockState nextState = level.getBlockState(next);

                if (nextState.is(ModBlocks.DATA_BUS.get())) {
                    DataBusConnection other = nextState.getValue(getPropertyForDirection(dir.getOpposite()));
                    if (!side.isConnected(other)) continue;
                    if (network.size() < MAX_NETWORK && network.add(next)) queue.add(next);
                } else if (nextState.is(ModTags.DIGITAL_BLOCKS)) {
                    ValueSource src = ValueSourceProvider.find(level, next, dir.getOpposite());
                    if (src != null) value = Math.max(value, src.value());
                }
            }
        }

        for (BlockPos p : network) {
            if (level.getBlockEntity(p) instanceof DataBusBlockEntity bus && bus.getValue() != value) {
                bus.setValue(value);

                BlockState busState = level.getBlockState(p);
                for (Direction dir : Direction.values()) {
                    if (busState.getValue(getPropertyForDirection(dir)) != DataBusConnection.CONNECTED) continue;
                    BlockPos n = p.relative(dir);
                    if (!level.getBlockState(n).is(ModBlocks.DATA_BUS.get())) {
                        level.neighborChanged(n, this, p);
                    }
                }
            }
        }
    }
}