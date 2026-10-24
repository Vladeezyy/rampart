package dev.vladeezyy.rampart.client;

import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * Citadel's base of item client extensions (NeoForge only), subclassed by the mods that give an item its own renderer.
 * Citadel's own default renderer draws Citadel's items, so it is not part of Rampart: subclasses override
 * {@link #getCustomRenderer()}.
 */
public class CitadelItemRenderProperties implements IClientItemExtensions {
}
