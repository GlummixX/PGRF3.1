package shape.model;

import lwjglutils.OGLBuffers;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class Grid {
    private OGLBuffers buffers;
    private int topology;

    public Grid(OGLBuffers buffers, int topology){
        this.buffers = buffers;
        this.topology = topology;
    }

    public void draw(int program){
        buffers.draw(topology, program);
    }
    public void draw(int program, int topology){
        buffers.draw(topology, program);
    }

    public static Grid gridList(int boxesPerSide) {
        float boxSize = 1F / boxesPerSide;
        float[] vertex = new float[boxesPerSide * boxesPerSide * 3];
        for (int y = 0; y < boxesPerSide; y++) {
            for (int x = 0; x < boxesPerSide; x++) {
                int idx = (y * boxesPerSide + x) * 3;
                vertex[idx] = x * boxSize;
                vertex[idx + 1] = y * boxSize;
                vertex[idx + 2] = 0F;
            }
        }

        int[] index = new int[(boxesPerSide - 1) * (boxesPerSide - 1) * 6];
        for (int x = 0; x < boxesPerSide - 2; x++) {
            for (int y = 0; y < boxesPerSide - 2; y++) {
                int i = y * boxesPerSide + x;
                index[i * 6] = i;
                index[i * 6 + 1] = (i + 1);
                index[i * 6 + 2] = (i + boxesPerSide + 1);
                index[i * 6 + 3] = (i);
                index[i * 6 + 4] = (i + boxesPerSide + 1);
                index[i * 6 + 5] = (i + boxesPerSide);
            }
        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3)};

        return new Grid(new OGLBuffers(vertex, attributes, index), GL_TRIANGLES);
    }

    public static Grid gridStrip(int boxesPerSide) {
        float boxSize = 1F / boxesPerSide;
        float[] vertex = new float[boxesPerSide * boxesPerSide * 3];
        for (int y = 0; y < boxesPerSide; y++) {
            for (int x = 0; x < boxesPerSide; x++) {
                int idx = (y * boxesPerSide + x) * 3;
                vertex[idx] = x * boxSize;
                vertex[idx + 1] = y * boxSize;
                vertex[idx + 2] = 0F;
            }
        }

        int[] index = new int[2 * boxesPerSide * (boxesPerSide - 1)];
        int r = 0;
        for (int row = 0; row < boxesPerSide - 1; row++) {
            if (row % 2 == 0) {
                // Even rows
                for (int col = 0; col < boxesPerSide; col++) {
                    index[r++] = col + row * boxesPerSide;
                    index[r++] = col + (row + 1) * boxesPerSide;
                }
            } else {
                // Odd rows (reverse order)
                for (int col = boxesPerSide - 1; col >= 0; col--) {
                    index[r++] = col + (row + 1) * boxesPerSide;
                    index[r++] = col + row * boxesPerSide;
                }
            }
        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3)};

        return new Grid(new OGLBuffers(vertex, attributes, index), GL_TRIANGLE_STRIP);
    }
}
