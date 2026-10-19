package dev.vladeezyy.rampart.selftest;

import dev.vladeezyy.rampart.Rampart;
import dev.vladeezyy.rampart.animation.Animation;
import dev.vladeezyy.rampart.animation.AnimationHandler;
import dev.vladeezyy.rampart.animation.IAnimatedEntity;
import dev.vladeezyy.rampart.network.AnimationMessage;
import dev.vladeezyy.rampart.platform.Platform;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Functional checks on a dev server ({@code tools/selftest.sh}), only with -Drampart.selftest=true (the source set is
 * not in the jar). Prints "[Rampart selftest] PASS n/n" or the failed checks, then stops the server.
 */
public final class SelfTest {
  private static final String PREFIX = "[Rampart selftest] ";
  private static final boolean ENABLED = Boolean.getBoolean("rampart.selftest");
  private static final List<String> failures = new ArrayList<>();
  private static int checks;
  private static int tick = -1;
  private static AnimatedPig pig;

  /** A pig that plays Citadel-style animations, like the mobs of the mods that use Rampart */
  static final class AnimatedPig extends Pig implements IAnimatedEntity {
    static final Animation BITE = Animation.create(5);
    static final Animation ROAR = Animation.create(12);
    private Animation animation = NO_ANIMATION;
    private int animationTick;

    AnimatedPig(Level level) {
      super(EntityType.PIG, level);
    }

    @Override
    public void tick() {
      super.tick();
      AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override public int getAnimationTick() { return this.animationTick; }
    @Override public void setAnimationTick(int tick) { this.animationTick = tick; }
    @Override public Animation getAnimation() { return this.animation; }
    @Override public void setAnimation(Animation animation) { this.animation = animation; }
    @Override public Animation[] getAnimations() { return new Animation[]{BITE, ROAR}; }
  }

  static void check(String name, BooleanSupplier test) {
    boolean result;
    try {
      result = test.getAsBoolean();
    } catch (RuntimeException e) {
      Rampart.LOG.error(PREFIX + "exception in {}", name, e);
      result = false;
    }
    checks++;
    if (result) {
      Rampart.LOG.info(PREFIX + "ok   {}", name);
    } else {
      failures.add(name);
      Rampart.LOG.error(PREFIX + "FAIL {}", name);
    }
  }

  /** Server started (wired by the loader's test hooks) */
  public static void serverStarted(MinecraftServer server) {
    if (!ENABLED) {
      return;
    }
    ServerLevel level = server.overworld();
    BlockPos spawn = level.getSharedSpawnPos();
    level.setChunkForced(spawn.getX() >> 4, spawn.getZ() >> 4, true);
    check("platform of this loader is loaded", () -> Platform.INSTANCE.name().equals(System.getProperty("rampart.loader", Platform.INSTANCE.name())));
    check("animation packet survives encode / decode", () -> {
      RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());
      AnimationMessage.CODEC.encode(buf, new AnimationMessage(123456, 1));
      AnimationMessage back = AnimationMessage.CODEC.decode(buf);
      return back.entityId() == 123456 && back.index() == 1 && !buf.isReadable();
    });
    check("animation packet with no animation (-1)", () -> {
      RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());
      AnimationMessage.CODEC.encode(buf, new AnimationMessage(7, -1));
      return AnimationMessage.CODEC.decode(buf).index() == -1;
    });
    pig = new AnimatedPig(level);
    pig.moveTo(spawn.getX() + 0.5, level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, spawn.getX(), spawn.getZ()), spawn.getZ() + 0.5);
    level.addFreshEntity(pig);
    check("an animation starts on the server", () -> {
      AnimationHandler.INSTANCE.sendAnimationMessage(pig, AnimatedPig.ROAR);
      return pig.getAnimation() == AnimatedPig.ROAR && pig.getAnimationTick() == 0;
    });
    check("the packet applies to the entity on the receiving side", () -> {
      AnimatedPig other = new AnimatedPig(level);
      new AnimationMessage(pig.getId(), 0).apply(level);
      boolean bite = pig.getAnimation() == AnimatedPig.BITE;
      new AnimationMessage(pig.getId(), -1).apply(level);
      boolean none = pig.getAnimation() == IAnimatedEntity.NO_ANIMATION;
      new AnimationMessage(pig.getId(), 99).apply(level); // out of range: no crash, no animation
      AnimationHandler.INSTANCE.sendAnimationMessage(pig, AnimatedPig.ROAR);
      return bite && none && other.getAnimation() == IAnimatedEntity.NO_ANIMATION;
    });
    tick = 0;
  }

  /** After every server tick (wired by the loader's test hooks) */
  public static void serverTick(MinecraftServer server) {
    if (tick < 0) {
      return;
    }
    tick++;
    if (tick == 6) {
      check("the animation ticks with the entity", () -> pig.getAnimation() == AnimatedPig.ROAR && pig.getAnimationTick() > 0);
    }
    if (tick == 20) {
      check("the animation ends after its duration", () -> pig.getAnimation() == IAnimatedEntity.NO_ANIMATION && pig.getAnimationTick() == 0);
    }
    if (tick == 25) {
      int passed = checks - failures.size();
      if (failures.isEmpty()) {
        Rampart.LOG.info(PREFIX + "PASS {}/{}", passed, checks);
      } else {
        Rampart.LOG.error(PREFIX + "FAILED {}/{}: {}", passed, checks, failures);
      }
      tick = -1;
      pig.discard();
      server.halt(false);
    }
  }
}
