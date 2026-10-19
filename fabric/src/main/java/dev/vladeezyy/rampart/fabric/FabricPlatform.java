package dev.vladeezyy.rampart.fabric;

import dev.vladeezyy.rampart.platform.Platform;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class FabricPlatform implements Platform {
    @Override
    public String name() {
        return "fabric";
    }

    @Override
    public void sendToTracking(Entity entity, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, payload);
        }
        if (entity instanceof ServerPlayer self) {
            ServerPlayNetworking.send(self, payload);
        }
    }
}
