package me.samuelh2005.logic_plus_plus.foundation;

import dev.architectury.networking.NetworkManager;
import me.samuelh2005.logic_plus_plus.network.OpenValueSourceScreenS2C;
import me.samuelh2005.logic_plus_plus.network.SubmitValueSourceScreenC2S;

public class ModNetworking {
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SubmitValueSourceScreenC2S.TYPE,
                SubmitValueSourceScreenC2S.CODEC,
                (payload, context) -> context.queue(() -> SubmitValueSourceScreenC2S.handle(payload, context))
        );
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                OpenValueSourceScreenS2C.TYPE,
                OpenValueSourceScreenS2C.CODEC,
                (payload, context) -> context.queue(() -> OpenValueSourceScreenS2C.handle(payload, context))
        );
        // NetworkManager.registerS2CPayloadType(
        //         OpenValueSourceScreenS2C.TYPE,
        //         OpenValueSourceScreenS2C.CODEC
        // );
    }
}
