package dev.vladeezyy.rampart.client.render;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;

/**
 * What a Citadel model's {@code setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)}
 * needs, carried in the render state ({@link RampartState}) to the moment the game draws the model.
 *
 * <p>The state keeps a reference to the entity: Citadel models read the entity's own animation fields. The renderer
 * reuses one state, so the reference is replaced on the next frame.
 */
public final class RampartPose {
    public Entity entity;
    public float partialTick;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;

    /** The values 1.21.1's {@code LivingEntityRenderer.render} passed to the model, from a filled living state */
    public RampartPose fromLiving(LivingEntityRenderState state, Entity entity, float partialTick) {
        this.entity = entity;
        this.partialTick = partialTick;
        // 1.21.1 tripled the walk position of babies and capped the speed at 1; 26.x leaves both to the model
        this.limbSwing = state.isBaby ? state.walkAnimationPos * 3.0F : state.walkAnimationPos;
        this.limbSwingAmount = Math.min(state.walkAnimationSpeed, 1.0F);
        this.ageInTicks = state.ageInTicks;
        this.netHeadYaw = state.yRot;
        this.headPitch = state.xRot;
        return this;
    }

    public RampartPose set(Entity entity, float partialTick, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.entity = entity;
        this.partialTick = partialTick;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        return this;
    }

    public void copyFrom(RampartPose other) {
        this.set(other.entity, other.partialTick, other.limbSwing, other.limbSwingAmount, other.ageInTicks, other.netHeadYaw, other.headPitch);
    }
}
