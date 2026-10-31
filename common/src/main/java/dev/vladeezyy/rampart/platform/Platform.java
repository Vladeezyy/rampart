package dev.vladeezyy.rampart.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;

import java.nio.file.Path;
import java.util.ServiceLoader;

/** What the common code needs from the mod loader (NeoForge or Fabric) */
public interface Platform {
    Platform INSTANCE = ServiceLoader.load(Platform.class, Platform.class.getClassLoader()).findFirst()
        .orElseThrow(() -> new IllegalStateException("No Rampart platform for this mod loader"));

    /** "neoforge" or "fabric" */
    String name();

    /** Sends a packet to every player that tracks the entity (and to the entity itself if it is a player) */
    void sendToTracking(Entity entity, CustomPacketPayload payload);

    /** The config folder of the game (Citadel's spawn biome configs live there) */
    Path configDir();
}
