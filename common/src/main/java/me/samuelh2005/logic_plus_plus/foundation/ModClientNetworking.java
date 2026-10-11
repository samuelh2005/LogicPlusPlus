package me.samuelh2005.logic_plus_plus.foundation;

import dev.architectury.networking.NetworkManager;
import me.samuelh2005.logic_plus_plus.content.value_source.ValueSourceScreen;
import me.samuelh2005.logic_plus_plus.network.OpenValueSourceScreenS2C;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ModClientNetworking {
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                OpenValueSourceScreenS2C.TYPE,
                OpenValueSourceScreenS2C.CODEC,
                (payload, context) -> context.queue(() -> {
                    Player player = context.getPlayer();
                    Level level = player.level();
                    if (!level.isClientSide()) return;
                    Minecraft.getInstance().setScreen(new ValueSourceScreen(
                        payload.initialValue(),
                        payload.pos(),
                        Component.literal("Value Source")
                    ));
                })
        );
    }
}
