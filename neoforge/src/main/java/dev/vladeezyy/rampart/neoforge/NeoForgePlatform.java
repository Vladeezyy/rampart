package dev.vladeezyy.rampart.neoforge;

import dev.vladeezyy.rampart.platform.Platform;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NeoForgePlatform implements Platform {
    @Override
    public String name() {
        return "neoforge";
    }

    @Override
    public void sendToTracking(Entity entity, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload);
    }
}
