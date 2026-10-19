package dev.vladeezyy.rampart.client.render;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** Render state of a living entity drawn with a Citadel-style model; extend it for renderer-specific values */
public class RampartLivingRenderState extends LivingEntityRenderState implements RampartState {
    public final RampartPose pose = new RampartPose();
    public float partialTick;

    @Override
    public RampartPose rampart$pose() {
        return this.pose;
    }
}
