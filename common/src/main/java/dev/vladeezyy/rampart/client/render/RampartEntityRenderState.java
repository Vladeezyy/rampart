package dev.vladeezyy.rampart.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/** Render state of a non-living entity drawn with a Citadel-style model */
public class RampartEntityRenderState extends EntityRenderState implements RampartState {
    public final RampartPose pose = new RampartPose();

    @Override
    public RampartPose rampart$pose() {
        return this.pose;
    }
}
