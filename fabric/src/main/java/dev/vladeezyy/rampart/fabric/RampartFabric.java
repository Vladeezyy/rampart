package dev.vladeezyy.rampart.fabric;

import dev.vladeezyy.rampart.network.AnimationMessage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class RampartFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(AnimationMessage.TYPE, AnimationMessage.CODEC);
    }
}
