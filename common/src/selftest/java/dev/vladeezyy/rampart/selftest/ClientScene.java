package dev.vladeezyy.rampart.selftest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.vladeezyy.rampart.Rampart;
import dev.vladeezyy.rampart.client.model.AdvancedEntityModel;
import dev.vladeezyy.rampart.client.model.AdvancedModelBox;
import dev.vladeezyy.rampart.client.model.TabulaModelRenderUtils;
import dev.vladeezyy.rampart.client.model.basic.BasicModelPart;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Client checks, inert unless {@code -Drampart.scene=main} ({@code tools/scenetest.sh}): on the title screen, renders a
 * small model into a recording buffer and compares every vertex with Citadel's original per-vertex math, then quits.
 * Logs {@code [scene] CHECK OK/FAIL} and {@code [scene] DONE}.
 */
public final class ClientScene {
  private static final boolean ENABLED = System.getProperty("rampart.scene", "").equals("main");
  private static boolean done;
  private static int ticks;
  private static int checks, failures;

  static void check(String name, boolean ok, Object detail) {
    checks++;
    if (!ok) {
      failures++;
    }
    Rampart.LOG.info("[scene] CHECK " + (ok ? "OK " : "FAIL ") + name + (ok ? "" : " (" + detail + ")"));
  }

  /** A two-part model like the mobs' models: a body with a rotated, scaled head as its child */
  static final class TestModel extends AdvancedEntityModel<Entity> {
    final AdvancedModelBox body, head;

    TestModel() {
      this.texWidth = 64;
      this.texHeight = 32;
      this.body = new AdvancedModelBox(this, "body");
      this.body.setTextureOffset(0, 0);
      this.body.setRotationPoint(0, 16, 0);
      this.body.addBox(-4, -3, -6, 8, 6, 12, 0.25F);
      this.head = new AdvancedModelBox(this, "head");
      this.head.setTextureOffset(32, 0);
      this.head.setRotationPoint(0, -1, -6);
      this.head.addBox(-3, -3, -5, 6, 6, 5, 0);
      this.body.addChild(this.head);
      this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
      return List.of(this.body, this.head);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
      return List.of(this.body);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
  }

  /** Records the vertices instead of drawing them */
  static final class Recorder implements VertexConsumer {
    final List<float[]> vertices = new ArrayList<>();

    @Override
    public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
      this.vertices.add(new float[]{x, y, z, u, v, nx, ny, nz});
    }

    @Override public VertexConsumer addVertex(float x, float y, float z) { throw new UnsupportedOperationException(); }
    @Override public VertexConsumer setColor(int r, int g, int b, int a) { return this; }
    @Override public VertexConsumer setUv(float u, float v) { return this; }
    @Override public VertexConsumer setUv1(int u, int v) { return this; }
    @Override public VertexConsumer setUv2(int u, int v) { return this; }
    @Override public VertexConsumer setNormal(float x, float y, float z) { return this; }
  }

  private static void run(Minecraft mc) {
    TestModel model = new TestModel();
    model.body.rotateAngleY = 0.6F;
    model.head.rotateAngleX = -0.4F;
    model.head.rotateAngleZ = 0.2F;
    model.head.setScale(1.2F, 0.9F, 1.1F);
    PoseStack pose = new PoseStack();
    pose.translate(1.5, -0.25, 3);
    pose.mulPose(new org.joml.Quaternionf().rotateXYZ(0.3F, -1.1F, 0.05F));
    Recorder recorder = new Recorder();
    model.renderToBuffer(pose, recorder, 0xF000F0, 0, -1);
    check("both boxes drawn (2 boxes x 6 faces x 4 vertices)", recorder.vertices.size() == 48, recorder.vertices.size());

    // the body's 24 vertices again with Citadel's math: new Vector4f(pos / 16, 1).mul(pose) and a new normal per quad
    PoseStack again = new PoseStack();
    again.translate(1.5, -0.25, 3);
    again.mulPose(new org.joml.Quaternionf().rotateXYZ(0.3F, -1.1F, 0.05F));
    model.body.translateAndRotate(again);
    Matrix4f matrix = again.last().pose();
    org.joml.Matrix3f normalMatrix = again.last().normal();
    float worst = 0;
    int i = 0;
    for (TabulaModelRenderUtils.ModelBox box : model.body.cubeList) {
      for (TabulaModelRenderUtils.TexturedQuad quad : box.quads) {
        Vector3f normal = new Vector3f(quad.normal).mul(normalMatrix).normalize();
        for (TabulaModelRenderUtils.PositionTextureVertex vertex : quad.vertexPositions) {
          Vector4f expected = new Vector4f(vertex.position.x() / 16F, vertex.position.y() / 16F, vertex.position.z() / 16F, 1).mul(matrix);
          float[] got = recorder.vertices.get(i++);
          worst = Math.max(worst, Math.abs(expected.x - got[0]) + Math.abs(expected.y - got[1]) + Math.abs(expected.z - got[2])
            + Math.abs(normal.x - got[5]) + Math.abs(normal.y - got[6]) + Math.abs(normal.z - got[7])
            + Math.abs(vertex.textureU - got[3]) + Math.abs(vertex.textureV - got[4]));
        }
      }
    }
    check("body vertices match Citadel's math (positions, normals, UV)", i == 24 && worst < 1e-5F, "worst difference " + worst);
    boolean finite = recorder.vertices.stream().allMatch(v -> Float.isFinite(v[0]) && Float.isFinite(v[1]) && Float.isFinite(v[2]));
    check("all vertices finite", finite, "");
    float[] last = recorder.vertices.get(47);
    float length = (float) Math.sqrt(last[5] * last[5] + last[6] * last[6] + last[7] * last[7]);
    check("head normals are unit length", Math.abs(length - 1) < 1e-4F, length);

    // the speed: 5000 renders of the model
    long start = System.nanoTime();
    for (int n = 0; n < 5000; n++) {
      recorder.vertices.clear();
      model.renderToBuffer(pose, recorder, 0xF000F0, 0, -1);
    }
    Rampart.LOG.info("[scene] PERF 5000 renders of a 2-box model: " + (System.nanoTime() - start) / 1_000_000 + " ms");
  }

  public static void onClientTick() {
    if (!ENABLED || done) {
      return;
    }
    Minecraft mc = Minecraft.getInstance();
    // any first screen (title or the accessibility onboarding of a fresh game directory) once the game has loaded
    if (mc.screen != null && ++ticks > 40) {
      done = true;
      try {
        run(mc);
      } catch (RuntimeException | LinkageError e) {
        check("model checks ran", false, e);
        Rampart.LOG.error("[scene] model checks crashed", e);
      }
      Rampart.LOG.info("[scene] DONE " + (checks - failures) + "/" + checks + (failures == 0 ? " passed" : " (" + failures + " failed)"));
      mc.stop();
    }
  }
}
