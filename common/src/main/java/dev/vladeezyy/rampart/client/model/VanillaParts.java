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

    /** A vanilla cube with the bounds {@code (x1, y1, z1)–(x2, y2, z2)} drawing exactly {@code faces} */
    public static ModelPart.Cube cube(float x1, float y1, float z1, float x2, float y2, float z2, Face[] faces) {
        ModelPart.Cube cube = new ModelPart.Cube(0, 0, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, 0, 0, 0, false, 1, 1, ALL_FACES);
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
