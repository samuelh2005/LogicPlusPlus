package me.samuelh2005.logic_plus_plus.network;

import dev.architectury.networking.NetworkManager.PacketContext;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.content.value_source.ValueSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record SubmitValueSourceScreenC2S(
    BlockPos pos,
    int value
) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(LogicPlusPlus.MOD_ID, "submit_value_source_screen");
    public static final CustomPacketPayload.Type<SubmitValueSourceScreenC2S> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SubmitValueSourceScreenC2S> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, SubmitValueSourceScreenC2S::pos,
        ByteBufCodecs.INT, SubmitValueSourceScreenC2S::value,
        SubmitValueSourceScreenC2S::new
    );

    @Override
    public Type<SubmitValueSourceScreenC2S> type() {
        return TYPE;
    }

    public static void handle(SubmitValueSourceScreenC2S payload, PacketContext context) {
        Player player = context.getPlayer();
        Level level = player.level();
        if (level.isClientSide()) return;

        if (player.blockPosition().distSqr(payload.pos()) > player.blockInteractionRange()) return;

        BlockEntity be = level.getBlockEntity(payload.pos());
        if (be instanceof ValueSourceBlockEntity valueSource) {
            valueSource.setValue(payload.value());
        }
    }
}
