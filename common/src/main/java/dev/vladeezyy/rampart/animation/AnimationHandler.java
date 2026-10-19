package dev.vladeezyy.rampart.animation;

import dev.vladeezyy.rampart.network.AnimationMessage;
import dev.vladeezyy.rampart.platform.Platform;
import net.minecraft.world.entity.Entity;

/**
 * Plays the animations of an {@link IAnimatedEntity} on the server and tells the clients.
 *
 * <p>From Citadel's AnimationHandler (iLexiconn, Alexthe666, LGPL-3.0). Differences: the packet goes only to the players
 * that track the entity (Citadel sent it to every player on the server, in every dimension), and there are no
 * animation start / tick events.
 */
public enum AnimationHandler {
    INSTANCE;

    /** Starts {@code animation} on the server and sends it to the players that see the entity */
    public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
        if (entity.level().isClientSide) {
            return;
        }
        entity.setAnimation(animation);
        int index = indexOf(entity.getAnimations(), animation);
        Platform.INSTANCE.sendToTracking(entity, new AnimationMessage(entity.getId(), index));
    }

    /** Advances the current animation by one tick; call from the entity's tick on both sides */
    public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
        Animation animation = entity.getAnimation();
        if (animation == null) {
            entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            return;
        }
        if (animation == IAnimatedEntity.NO_ANIMATION) {
            return;
        }
        if (entity.getAnimationTick() == 0) {
            this.sendAnimationMessage(entity, animation);
        }
        if (entity.getAnimationTick() < animation.getDuration()) {
            entity.setAnimationTick(entity.getAnimationTick() + 1);
        }
        if (entity.getAnimationTick() == animation.getDuration()) {
            entity.setAnimationTick(0);
            entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
        }
    }

    private static int indexOf(Animation[] animations, Animation animation) {
        for (int i = 0; i < animations.length; i++) {
            if (animations[i] == animation) {
                return i;
            }
        }
        return -1;
    }
}
