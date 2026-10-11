package me.samuelh2005.logic_plus_plus.content.digital_display;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.data.DataBlockEntity;
import me.samuelh2005.logic_plus_plus.data.DataValueInput;
import me.samuelh2005.logic_plus_plus.data.DataValueOutput;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalDisplayBlockEntity extends DataBlockEntity {
    private static final Direction BACK = Direction.SOUTH;   // data input
    private final DigitalDisplayBlockEntityDataValueInput BACK_INPUT;
    private int value;

    public DigitalDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIGITAL_DISPLAY.get(), pos, state);
        BACK_INPUT = new DigitalDisplayBlockEntityDataValueInput(this);
    }

    public int getValue() {
        return value;
    }

    @Nullable
    @Override
    public DataValueOutput getDataOutput(@Nullable Direction side) {
        return null;
    }

    @Nullable
    @Override
    public DataValueInput getDataInput(@Nullable Direction side) {
        return side == toAbsolute(BACK) ? BACK_INPUT : null;
    }

    public void onInputChanged() {
        if (level == null || level.isClientSide) return;
        int newValue = readValueAt(toAbsolute(BACK));
        if (newValue == value) return;
        value = newValue;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    // Persist to disk. The client also reads the update tag through loadAdditional,
    // so this is what actually puts the value on the client.
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("value", value);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        value = tag.getInt("value");
    }

    // Sync to client on chunk load and on sendBlockUpdated
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static class DigitalDisplayBlockEntityDataValueInput implements DataValueInput {
        private final DigitalDisplayBlockEntity blockEntity;

        public DigitalDisplayBlockEntityDataValueInput(DigitalDisplayBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public void onInputChanged() {
            blockEntity.onInputChanged();
        }
    }
}