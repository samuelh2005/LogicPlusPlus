package me.samuelh2005.logic_plus_plus.network;

import dev.architectury.networking.NetworkManager.PacketContext;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.content.value_source.ValueSourceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

    public static void handle(OpenValueSourceScreenS2C payload, PacketContext context) {
        Player player = context.getPlayer();
        Level level = player.level();
        if (!level.isClientSide()) return;
        Minecraft.getInstance().setScreen(new ValueSourceScreen(
            payload.initialValue(),
            payload.pos(),
            Component.literal("Value Source")
        ));
    }
}
