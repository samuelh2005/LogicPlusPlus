package me.samuelh2005.logic_plus_plus.content.value_source;

import com.mojang.serialization.MapCodec;

import dev.architectury.networking.NetworkManager;
import me.samuelh2005.logic_plus_plus.network.OpenValueSourceScreenS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ValueSourceBlock extends BaseEntityBlock {
    public static final MapCodec<ValueSourceBlock> CODEC = simpleCodec(ValueSourceBlock::new);

    public ValueSourceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ValueSourceBlockEntity(pos, state);
    }

    // BaseEntityBlock defaults to INVISIBLE, so this is required
    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player,
            BlockHitResult blockHitResult) {
        // Send a packet to the client to open the screen
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof ValueSourceBlockEntity valueSource)) {
            return InteractionResult.FAIL;
        }

        NetworkManager.sendToPlayer((ServerPlayer) player, new OpenValueSourceScreenS2C(blockPos, valueSource.getValue()));
        return InteractionResult.SUCCESS;
    }

    // ---- events: the block only forwards, the block entity does the work ----



    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ValueSourceBlockEntity counter) {
            return Math.min(counter.getValue(), 15);
        }
        return 0;
    }
}