package dev.vladeezyy.rampart.client.model.basic;

import dev.vladeezyy.rampart.client.render.RampartPose;
import dev.vladeezyy.rampart.client.render.RampartState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Citadel's entity model on the 26.x render-state API.
 *
 * <p>Models keep Citadel's entity-based {@link #setupAnim(Entity, float, float, float, float, float)}: the renderer puts
 * the entity and the animation values in a {@link RampartState} ({@link RampartPose}), and
 * {@link #setupAnim(EntityRenderState)} - called by the game right before drawing - passes them on and copies the pose
 * of the boxes into the vanilla parts that {@code renderToBuffer} draws. A state that is not a {@code RampartState}
 * only syncs the current pose (a model posed by hand).
 */
public abstract class BasicEntityModel<T extends Entity> extends EntityModel<EntityRenderState> {
    public int textureWidth = 64;
    public int textureHeight = 32;
    /** 1.21.1's EntityModel fields that Citadel models read, set from the living render state before setupAnim */
    public boolean young;
    public float attackTime;
    public boolean riding;
    private final Map<String, ModelPart> rootChildren;
    private boolean vanillaBuilt;

    protected BasicEntityModel() {
        this(RenderTypes::entityCutout);
    }

    protected BasicEntityModel(Function<Identifier, RenderType> renderType) {
        this(renderType, new LinkedHashMap<>());
    }

    private BasicEntityModel(Function<Identifier, RenderType> renderType, Map<String, ModelPart> rootChildren) {
        // ModelPart keeps the map it is given: the parts are added on first render, once the model's constructor is done
        super(new ModelPart(new ArrayList<>(), rootChildren), renderType);
        this.rootChildren = rootChildren;
    }

    /** The top-level boxes; their children are drawn through them */
    public abstract Iterable<BasicModelPart> parts();

    public abstract void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setupAnim(EntityRenderState state) {
        if (state instanceof RampartState holder) {
            RampartPose pose = holder.rampart$pose();
            // what 1.21.1's LivingEntityRenderer.render set on the model
            if (pose.entity instanceof net.minecraft.world.entity.LivingEntity living) {
                this.young = living.isBaby();
                this.attackTime = living.getSwingAnimation(pose.partialTick);
                this.riding = living.isPassenger();
            }
            if (pose.entity != null) {
                T entity = (T) pose.entity;
                this.prepareMobModel(entity, pose.limbSwing, pose.limbSwingAmount, pose.partialTick);
                this.setupAnim(entity, pose.limbSwing, pose.limbSwingAmount, pose.ageInTicks, pose.netHeadYaw, pose.headPitch);
            }
        }
        this.syncPose();
    }

    /**
     * Copies the current pose of the boxes into the vanilla parts. {@code setupAnim(state)} does it; call it yourself
     * before {@code renderToBuffer} on a model posed by hand.
     */
    public void syncPose() {
        if (!this.vanillaBuilt) {
            Map<String, ModelPart> children = this.rootChildren;
            children.clear();
            for (BasicModelPart part : this.parts()) {
                children.put("part" + children.size(), part.vanillaPart());
            }
            this.vanillaBuilt = true;
        }
        for (BasicModelPart part : this.parts()) {
            part.syncVanillaPose();
        }
    }

    /** Builds the vanilla parts again on the next render (after adding boxes or children once the model was drawn) */
    public void rebuildVanillaParts() {
        for (BasicModelPart part : this.parts()) {
            part.invalidateVanillaPart();
        }
        this.vanillaBuilt = false;
    }
}
