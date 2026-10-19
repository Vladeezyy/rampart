package dev.vladeezyy.rampart.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;

/**
 * A mob renderer for Citadel-style models: fills the {@link RampartPose} of the state from the entity, the way 1.21.1's
 * {@code LivingEntityRenderer.render} called {@code setupAnim}. Override {@link #createRenderState()} when {@code S} is
 * a subclass of {@link RampartLivingRenderState}.
 */
public abstract class RampartMobRenderer<T extends Mob, S extends RampartLivingRenderState, M extends EntityModel<? super S>> extends MobRenderer<T, S, M> {
    protected RampartMobRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Override
    @SuppressWarnings("unchecked")
    public S createRenderState() {
        return (S) new RampartLivingRenderState();
    }

    @Override
    public void extractRenderState(T entity, S state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.partialTick = partialTick;
        state.pose.fromLiving(state, entity, partialTick);
    }
}
