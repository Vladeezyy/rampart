package dev.vladeezyy.rampart;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rampart: the entity model and animation classes of Citadel (Alexthe666, LGPL-3.0) that our ports of Alex's mods
 * need, for NeoForge and Fabric on every Minecraft version we support.
 */
public final class Rampart {
    public static final String MOD_ID = "rampart";
    public static final Logger LOG = LoggerFactory.getLogger("Rampart");

    private Rampart() {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
