package dev.vladeezyy.rampart.fabric.selftest;

import dev.vladeezyy.rampart.selftest.ClientScene;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class FabricTestHooksClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientTickEvents.END_CLIENT_TICK.register(mc -> {
      ClientScene.onClientTick();
    });
  }
}
