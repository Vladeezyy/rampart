package dev.vladeezyy.rampart.network;

import dev.vladeezyy.rampart.Rampart;
import dev.vladeezyy.rampart.animation.Animation;
import dev.vladeezyy.rampart.animation.IAnimatedEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Server → client: an entity started an animation (index into its {@link IAnimatedEntity#getAnimations()}, -1 = none) */
public record AnimationMessage(int entityId, int index) implements CustomPacketPayload {
    public static final Type<AnimationMessage> TYPE = new Type<>(Rampart.id("animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AnimationMessage> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, AnimationMessage::entityId,
        ByteBufCodecs.VAR_INT, AnimationMessage::index,
        AnimationMessage::new);

    @Override
    public Type<AnimationMessage> type() {
        return TYPE;
    }

    /** Runs on the client thread with the client's level */
    public void apply(Level level) {
        Entity entity = level.getEntity(this.entityId);
        if (entity instanceof IAnimatedEntity animated) {
            Animation[] animations = animated.getAnimations();
            animated.setAnimation(this.index >= 0 && this.index < animations.length ? animations[this.index] : IAnimatedEntity.NO_ANIMATION);
            animated.setAnimationTick(0);
        }
    }
}
