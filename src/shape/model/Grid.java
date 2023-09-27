package shape.model;

import lwjglutils.OGLBuffers;

public class Grid {

    public static OGLBuffers gridList(int boxesPerSide) {
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

        return new OGLBuffers(vertex, attributes, index);
    }
}
