package me.samuelh2005.logic_plus_plus.network;

import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenValueSourceScreenS2C(
    BlockPos pos,
    int initialValue
) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(LogicPlusPlus.MOD_ID, "open_value_source_screen");
    public static final CustomPacketPayload.Type<OpenValueSourceScreenS2C> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenValueSourceScreenS2C> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, OpenValueSourceScreenS2C::pos,
        ByteBufCodecs.INT, OpenValueSourceScreenS2C::initialValue,
        OpenValueSourceScreenS2C::new
    );

    @Override
    public Type<OpenValueSourceScreenS2C> type() {
        return TYPE;
    }
}
