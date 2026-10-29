package dev.vladeezyy.rampart.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.EnumSet;

/**
 * Builds vanilla {@link ModelPart} cubes from Citadel's boxes (26.x).
 *
 * <p>Since 1.21.2 a {@code Model} renders only its vanilla {@code ModelPart} tree ({@code renderToBuffer} is final), so
 * every Rampart box keeps a mirror {@code ModelPart} and copies its pose into it before drawing. The cube's polygons are
 * replaced with Citadel's own quads, so positions, UV and normals are exactly Citadel's, whatever vanilla's cube code
 * does differently.
 *
 * <p>The cube is still built with the box's real parameters (texture offset and size, inflation, mirror): mods that
 * render cubes from those parameters instead of the polygons (Sodium's entity renderer) must get the same box.
 */
public final class VanillaParts {
    private static final EnumSet<Direction> ALL_FACES = EnumSet.allOf(Direction.class);

    private VanillaParts() {}

    /** One face of a box: four corners (in pixels) with their UV, and the face normal */
    public interface Face {
        int vertexCount();

        Vector3f position(int vertex);

        float u(int vertex);

        float v(int vertex);

        Vector3f normal();
    }

    /** A vanilla cube with the parameters of a Citadel box, drawing exactly {@code faces} */
    public static ModelPart.Cube cube(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth,
                                      float deltaX, float deltaY, float deltaZ, boolean mirror, float texWidth, float texHeight, Face[] faces) {
        ModelPart.Cube cube = new ModelPart.Cube(texOffX, texOffY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, mirror, texWidth, texHeight, ALL_FACES);
        if (cube.polygons.length != faces.length) {
            throw new IllegalStateException("vanilla cube has " + cube.polygons.length + " faces, Citadel's box " + faces.length);
        }
        for (int i = 0; i < faces.length; i++) {
            Face face = faces[i];
            ModelPart.Vertex[] vertices = new ModelPart.Vertex[face.vertexCount()];
            for (int j = 0; j < vertices.length; j++) {
                Vector3f p = face.position(j);
                vertices[j] = new ModelPart.Vertex(p.x(), p.y(), p.z(), face.u(j), face.v(j));
            }
            cube.polygons[i] = new ModelPart.Polygon(vertices, new Vector3f(face.normal()));
        }
        return cube;
    }

    /** Copies a Citadel pose into a vanilla part (same order of operations: translate, rotate Z-Y-X, scale) */
    public static void pose(ModelPart part, float x, float y, float z, float xRot, float yRot, float zRot, boolean visible) {
        part.x = x;
        part.y = y;
        part.z = z;
        part.xRot = xRot;
        part.yRot = yRot;
        part.zRot = zRot;
        part.visible = visible;
    }
}
