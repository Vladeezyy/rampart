package dev.vladeezyy.rampart.fabric;

import dev.vladeezyy.rampart.network.AnimationMessage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class RampartFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(AnimationMessage.TYPE, (message, context) -> message.apply(context.player().level()));
    }
}
