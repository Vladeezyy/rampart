package dev.vladeezyy.rampart.neoforge;

import dev.vladeezyy.rampart.Rampart;
import dev.vladeezyy.rampart.network.AnimationMessage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Rampart.MOD_ID)
public final class RampartNeoForge {
    public RampartNeoForge(IEventBus modBus) {
        modBus.addListener(RampartNeoForge::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // optional: a server without Rampart (or a client without it) still connects; the animations just do not sync
        event.registrar("1").optional().playToClient(AnimationMessage.TYPE, AnimationMessage.CODEC,
            (message, context) -> message.apply(context.player().level()));
    }
}
