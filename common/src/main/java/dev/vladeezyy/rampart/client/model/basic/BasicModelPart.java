package dev.vladeezyy.rampart.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import dev.vladeezyy.rampart.client.model.VanillaParts;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * @since 1.9.0
 * Duplicate of ModelPart class which is not final
 */
//FIXME::Extend ModelPart class with AT?
public class BasicModelPart {
    public float textureWidth = 64.0F;
    public float textureHeight = 32.0F;
    public int textureOffsetX;
    public int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public boolean mirror;
    public boolean showModel = true;
    private final ObjectList<BasicModelPart.ModelBox> cubeList = new ObjectArrayList<>();
    private final ObjectList<BasicModelPart> childModels = new ObjectArrayList<>();
    private ModelPart vanillaPart;

    public BasicModelPart(BasicEntityModel model) {
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }

    public BasicModelPart(BasicEntityModel model, int texOffX, int texOffY) {
        this(model.textureWidth, model.textureHeight, texOffX, texOffY);
    }

    public BasicModelPart(int textureWidthIn, int textureHeightIn, int textureOffsetXIn, int textureOffsetYIn) {
        this.setTextureSize(textureWidthIn, textureHeightIn);
        this.setTextureOffset(textureOffsetXIn, textureOffsetYIn);
    }

    private BasicModelPart() {
    }

    public BasicModelPart getModelAngleCopy() {
        BasicModelPart BasicModelPart = new BasicModelPart();
        BasicModelPart.copyModelAngles(this);
        return BasicModelPart;
    }

    public void copyModelAngles(BasicModelPart BasicModelPartIn) {
        this.rotateAngleX = BasicModelPartIn.rotateAngleX;
        this.rotateAngleY = BasicModelPartIn.rotateAngleY;
        this.rotateAngleZ = BasicModelPartIn.rotateAngleZ;
        this.rotationPointX = BasicModelPartIn.rotationPointX;
        this.rotationPointY = BasicModelPartIn.rotationPointY;
        this.rotationPointZ = BasicModelPartIn.rotationPointZ;
    }

    /**
     * Sets the current box's rotation points and rotation angles to another box.
     */
    public void addChild(BasicModelPart renderer) {
        this.childModels.add(renderer);
    }

    public BasicModelPart setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public BasicModelPart addBox(String partName, float x, float y, float z, int width, int height, int depth, float delta, int texX, int texY) {
        this.setTextureOffset(texX, texY);
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, (float)width, (float)height, (float)depth, delta, delta, delta, this.mirror, false);
        return this;
    }

    public BasicModelPart addBox(float x, float y, float z, float width, float height, float depth) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, this.mirror, false);
        return this;
    }

    public BasicModelPart addBox(float x, float y, float z, float width, float height, float depth, boolean mirrorIn) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, mirrorIn, false);
        return this;
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float delta) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, this.mirror, false);
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, this.mirror, false);
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float delta, boolean mirrorIn) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, mirrorIn, false);
    }

    private void addBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, boolean p_228305_13_) {
        this.cubeList.add(new BasicModelPart.ModelBox(texOffX, texOffY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, mirorIn, this.textureWidth, this.textureHeight));
    }

    public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
        this.rotationPointX = rotationPointXIn;
        this.rotationPointY = rotationPointYIn;
        this.rotationPointZ = rotationPointZIn;
    }

    public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn) {
        this.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, -1);
    }

    public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int color) {
        if (this.showModel) {
            if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
                matrixStackIn.pushPose();
                this.translateRotate(matrixStackIn);
                this.doRender(matrixStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, color);

                for(BasicModelPart BasicModelPart : this.childModels) {
                    BasicModelPart.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, color);
                }

                matrixStackIn.popPose();
            }
        }
    }

    public void translateRotate(PoseStack matrixStackIn) {
        matrixStackIn.translate(this.rotationPointX / 16.0F, this.rotationPointY / 16.0F, (double)(this.rotationPointZ / 16.0F));
        if (this.rotateAngleZ != 0.0F) {
            matrixStackIn.rotate(Axis.ZP, this.rotateAngleZ);
        }

        if (this.rotateAngleY != 0.0F) {
            matrixStackIn.rotate(Axis.YP, this.rotateAngleY);
        }

        if (this.rotateAngleX != 0.0F) {
            matrixStackIn.rotate(Axis.XP, this.rotateAngleX);
        }

    }

    private void doRender(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        // one scratch vector per call instead of two allocations per vertex (Citadel)
        Vector3f scratch = new Vector3f();
        for (BasicModelPart.ModelBox box : this.cubeList) {
            for (BasicModelPart.TexturedQuad quad : box.quads) {
                normalMatrix.transform(quad.normal, scratch).normalize();
                float nx = scratch.x, ny = scratch.y, nz = scratch.z;
                for (BasicModelPart.PositionTextureVertex vertex : quad.vertexPositions) {
                    matrix.transformPosition(vertex.position.x() / 16.0F, vertex.position.y() / 16.0F, vertex.position.z() / 16.0F, scratch);
                    buffer.addVertex(scratch.x, scratch.y, scratch.z, color, vertex.textureU, vertex.textureV, packedOverlay, packedLight, nx, ny, nz);
                }
            }
        }
    }

    /**
     * Returns the model renderer with the new texture parameters.
     */
    public BasicModelPart setTextureSize(int textureWidthIn, int textureHeightIn) {
        this.textureWidth = (float)textureWidthIn;
        this.textureHeight = (float)textureHeightIn;
        return this;
    }

    /**
     * The vanilla part this box draws with on 26.x, built on first use from the boxes and children added so far.
     * Call {@link #invalidateVanillaPart()} (or the model's {@code rebuildVanillaParts()}) after adding boxes later.
     */
    public ModelPart vanillaPart() {
        if (this.vanillaPart == null) {
            this.vanillaPart = this.buildVanillaPart();
        }
        return this.vanillaPart;
    }

    protected ModelPart buildVanillaPart() {
        List<ModelPart.Cube> cubes = new ArrayList<>(this.cubeList.size());
        for (BasicModelPart.ModelBox box : this.cubeList) {
            cubes.add(box.toVanilla());
        }
        Map<String, ModelPart> children = new LinkedHashMap<>();
        for (BasicModelPart child : this.childModels) {
            children.put("child" + children.size(), child.vanillaPart());
        }
        return new ModelPart(cubes, children);
    }

    /** Forgets the vanilla part of this box and its children; the next render builds them again */
    public void invalidateVanillaPart() {
        this.vanillaPart = null;
        for (BasicModelPart child : this.childModels) {
            child.invalidateVanillaPart();
        }
    }

    /** Copies the current pose of this box and its children into their vanilla parts */
    public void syncVanillaPose() {
        VanillaParts.pose(this.vanillaPart(), this.rotationPointX, this.rotationPointY, this.rotationPointZ,
            this.rotateAngleX, this.rotateAngleY, this.rotateAngleZ, this.showModel);
        for (BasicModelPart child : this.childModels) {
            child.syncVanillaPose();
        }
    }

    public BasicModelPart.ModelBox getRandomCube(RandomSource randomIn) {
        return !this.cubeList.isEmpty() ? this.cubeList.get(randomIn.nextInt(this.cubeList.size())) : null;
    }

    public static class ModelBox {
        private final BasicModelPart.TexturedQuad[] quads;
        /** This box as a vanilla cube drawing the same quads */
        public net.minecraft.client.model.geom.ModelPart.Cube toVanilla() {
            return dev.vladeezyy.rampart.client.model.VanillaParts.cube(this.texOffX, this.texOffY, this.posX1, this.posY1, this.posZ1, this.posX2 - this.posX1, this.posY2 - this.posY1, this.posZ2 - this.posZ1,
                this.deltaX, this.deltaY, this.deltaZ, this.mirror, this.texWidth, this.texHeight, this.quads);
        }

        public final float posX1;
        public final float posY1;
        public final float posZ1;
        public final float posX2;
        public final float posY2;
        public final float posZ2;
        /** The box's parameters, for {@link #toVanilla()} */
        private final int texOffX;
        private final int texOffY;
        private final float deltaX;
        private final float deltaY;
        private final float deltaZ;
        private final boolean mirror;
        private final float texWidth;
        private final float texHeight;

        public ModelBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, float texWidth, float texHeight) {
            this.posX1 = x;
            this.texOffX = texOffX;
            this.texOffY = texOffY;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.deltaZ = deltaZ;
            this.mirror = mirorIn;
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.posY1 = y;
            this.posZ1 = z;
            this.posX2 = x + width;
            this.posY2 = y + height;
            this.posZ2 = z + depth;
            this.quads = new BasicModelPart.TexturedQuad[6];
            float f = x + width;
            float f1 = y + height;
            float f2 = z + depth;
            x = x - deltaX;
            y = y - deltaY;
            z = z - deltaZ;
            f = f + deltaX;
            f1 = f1 + deltaY;
            f2 = f2 + deltaZ;
            if (mirorIn) {
                float f3 = f;
                f = x;
                x = f3;
            }

            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex7 = new BasicModelPart.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex = new BasicModelPart.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex1 = new BasicModelPart.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex2 = new BasicModelPart.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex3 = new BasicModelPart.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex4 = new BasicModelPart.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex5 = new BasicModelPart.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
            BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex6 = new BasicModelPart.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
            float f4 = (float)texOffX;
            float f5 = (float)texOffX + depth;
            float f6 = (float)texOffX + depth + width;
            float f7 = (float)texOffX + depth + width + width;
            float f8 = (float)texOffX + depth + width + depth;
            float f9 = (float)texOffX + depth + width + depth + width;
            float f10 = (float)texOffY;
            float f11 = (float)texOffY + depth;
            float f12 = (float)texOffY + depth + height;
            this.quads[2] = new BasicModelPart.TexturedQuad(new BasicModelPart.PositionTextureVertex[]{BasicModelPart$positiontexturevertex4, BasicModelPart$positiontexturevertex3, BasicModelPart$positiontexturevertex7, BasicModelPart$positiontexturevertex}, f5, f10, f6, f11, texWidth, texHeight, mirorIn, Direction.DOWN);
            this.quads[3] = new BasicModelPart.TexturedQuad(new BasicModelPart.PositionTextureVertex[]{BasicModelPart$positiontexturevertex1, BasicModelPart$positiontexturevertex2, BasicModelPart$positiontexturevertex6, BasicModelPart$positiontexturevertex5}, f6, f11, f7, f10, texWidth, texHeight, mirorIn, Direction.UP);
            this.quads[1] = new BasicModelPart.TexturedQuad(new BasicModelPart.PositionTextureVertex[]{BasicModelPart$positiontexturevertex7, BasicModelPart$positiontexturevertex3, BasicModelPart$positiontexturevertex6, BasicModelPart$positiontexturevertex2}, f4, f11, f5, f12, texWidth, texHeight, mirorIn, Direction.WEST);
            this.quads[4] = new BasicModelPart.TexturedQuad(new BasicModelPart.PositionTextureVertex[]{BasicModelPart$positiontexturevertex, BasicModelPart$positiontexturevertex7, BasicModelPart$positiontexturevertex2, BasicModelPart$positiontexturevertex1}, f5, f11, f6, f12, texWidth, texHeight, mirorIn, Direction.NORTH);
            this.quads[0] = new BasicModelPart.TexturedQuad(new BasicModelPart.PositionTextureVertex[]{BasicModelPart$positiontexturevertex4, BasicModelPart$positiontexturevertex, BasicModelPart$positiontexturevertex1, BasicModelPart$positiontexturevertex5}, f6, f11, f8, f12, texWidth, texHeight, mirorIn, Direction.EAST);
            this.quads[5] = new BasicModelPart.TexturedQuad(new BasicModelPart.PositionTextureVertex[]{BasicModelPart$positiontexturevertex3, BasicModelPart$positiontexturevertex4, BasicModelPart$positiontexturevertex5, BasicModelPart$positiontexturevertex6}, f8, f11, f9, f12, texWidth, texHeight, mirorIn, Direction.SOUTH);
        }
    }

    static class PositionTextureVertex {
        public final Vector3f position;
        public final float textureU;
        public final float textureV;

        public PositionTextureVertex(float x, float y, float z, float texU, float texV) {
            this(new Vector3f(x, y, z), texU, texV);
        }

        public BasicModelPart.PositionTextureVertex setTextureUV(float texU, float texV) {
            return new BasicModelPart.PositionTextureVertex(this.position, texU, texV);
        }

        public PositionTextureVertex(Vector3f posIn, float texU, float texV) {
            this.position = posIn;
            this.textureU = texU;
            this.textureV = texV;
        }
    }

    static class TexturedQuad implements dev.vladeezyy.rampart.client.model.VanillaParts.Face {
        @Override public int vertexCount() { return this.vertexPositions.length; }
        @Override public Vector3f position(int vertex) { return this.vertexPositions[vertex].position; }
        @Override public float u(int vertex) { return this.vertexPositions[vertex].textureU; }
        @Override public float v(int vertex) { return this.vertexPositions[vertex].textureV; }
        @Override public Vector3f normal() { return this.normal; }

        public final BasicModelPart.PositionTextureVertex[] vertexPositions;
        public final Vector3f normal;

        public TexturedQuad(BasicModelPart.PositionTextureVertex[] positionsIn, float u1, float v1, float u2, float v2, float texWidth, float texHeight, boolean mirrorIn, Direction directionIn) {
            this.vertexPositions = positionsIn;
            float f = 0.0F / texWidth;
            float f1 = 0.0F / texHeight;
            positionsIn[0] = positionsIn[0].setTextureUV(u2 / texWidth - f, v1 / texHeight + f1);
            positionsIn[1] = positionsIn[1].setTextureUV(u1 / texWidth + f, v1 / texHeight + f1);
            positionsIn[2] = positionsIn[2].setTextureUV(u1 / texWidth + f, v2 / texHeight - f1);
            positionsIn[3] = positionsIn[3].setTextureUV(u2 / texWidth - f, v2 / texHeight - f1);
            if (mirrorIn) {
                int i = positionsIn.length;

                for(int j = 0; j < i / 2; ++j) {
                    BasicModelPart.PositionTextureVertex BasicModelPart$positiontexturevertex = positionsIn[j];
                    positionsIn[j] = positionsIn[i - 1 - j];
                    positionsIn[i - 1 - j] = BasicModelPart$positiontexturevertex;
                }
            }

            this.normal = directionIn.step();
            if (mirrorIn) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }

        }
    }
}