package shape.model;

import lwjglutils.OGLBuffers;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class Cube {

    private OGLBuffers buffers;
    private int topology;

    public Cube(OGLBuffers buffers, int topology){
        this.buffers = buffers;
        this.topology = topology;
    }

    public void draw(int program){
        buffers.draw(topology, program);
    }
    public void draw(int program, int topology){
        buffers.draw(topology, program);
    }

    public static Cube createCube() {
        float[] cube = {
                // bottom (z-) face
                1, 0, 0,	0, 0, -1,
                0, 0, 0,	0, 0, -1,
                1, 1, 0,	0, 0, -1,
                0, 1, 0,	0, 0, -1,
                // top (z+) face
                1, 0, 1,	0, 0, 1,
                0, 0, 1,	0, 0, 1,
                1, 1, 1,	0, 0, 1,
                0, 1, 1,	0, 0, 1,
                // x+ face
                1, 1, 0,	1, 0, 0,
                1, 0, 0,	1, 0, 0,
                1, 1, 1,	1, 0, 0,
                1, 0, 1,	1, 0, 0,
                // x- face
                0, 1, 0,	-1, 0, 0,
                0, 0, 0,	-1, 0, 0,
                0, 1, 1,	-1, 0, 0,
                0, 0, 1,	-1, 0, 0,
                // y+ face
                1, 1, 0,	0, 1, 0,
                0, 1, 0,	0, 1, 0,
                1, 1, 1,	0, 1, 0,
                0, 1, 1,	0, 1, 0,
                // y- face
                1, 0, 0,	0, -1, 0,
                0, 0, 0,	0, -1, 0,
                1, 0, 1,	0, -1, 0,
                0, 0, 1,	0, -1, 0
        };

        int[] indexBufferData = new int[36];
        for (int i = 0; i<6; i++){
            indexBufferData[i * 6] = i * 4;
            indexBufferData[i * 6 + 1] = i * 4 + 1;
            indexBufferData[i * 6 + 2] = i * 4 + 2;
            indexBufferData[i * 6 + 3] = i * 4 + 1;
            indexBufferData[i * 6 + 4] = i * 4 + 2;
            indexBufferData[i * 6 + 5] = i * 4 + 3;
        }
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        return new Cube(new OGLBuffers(cube, attributes, indexBufferData), GL_TRIANGLES);
    }

    public static Cube createTextureCube() {
        float[] cube = {
                // bottom (z-) face
                1, 0, 0, 0, 0, -1, 1, 0,
                0, 0, 0, 0, 0, -1, 0, 0,
                1, 1, 0, 0, 0, -1, 1, 1,
                0, 1, 0, 0, 0, -1, 0, 1,
                // top (z+) face
                1, 0, 1, 0, 0, 1, 1, 0,
                0, 0, 1, 0, 0, 1, 0, 0,
                1, 1, 1, 0, 0, 1, 1, 1,
                0, 1, 1, 0, 0, 1, 0, 1,
                // x+ face
                1, 1, 0, 1, 0, 0, 1, 0,
                1, 0, 0, 1, 0, 0, 0, 0,
                1, 1, 1, 1, 0, 0, 1, 1,
                1, 0, 1, 1, 0, 0, 0, 1,
                // x- face
                0, 1, 0, -1, 0, 0, 1, 0,
                0, 0, 0, -1, 0, 0, 0, 0,
                0, 1, 1, -1, 0, 0, 1, 1,
                0, 0, 1, -1, 0, 0, 0, 1,
                // y+ face
                1, 1, 0, 0, 1, 0, 1, 0,
                0, 1, 0, 0, 1, 0, 0, 0,
                1, 1, 1, 0, 1, 0, 1, 1,
                0, 1, 1, 0, 1, 0, 0, 1,
                // y- face
                1, 0, 0, 0, -1, 0, 1, 0,
                0, 0, 0, 0, -1, 0, 0, 0,
                1, 0, 1, 0, -1, 0, 1, 1,
                0, 0, 1, 0, -1, 0, 0, 1
        };

        int[] indexBufferData = new int[36];
        for (int i = 0; i < 6; i++) {
            indexBufferData[i * 6] = i * 4;
            indexBufferData[i * 6 + 1] = i * 4 + 1;
            indexBufferData[i * 6 + 2] = i * 4 + 2;
            indexBufferData[i * 6 + 3] = i * 4 + 1;
            indexBufferData[i * 6 + 4] = i * 4 + 2;
            indexBufferData[i * 6 + 5] = i * 4 + 3;
        }
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3),
                new OGLBuffers.Attrib("inTexture", 2)
        };

        return new Cube(new OGLBuffers(cube, attributes, indexBufferData), GL_TRIANGLES);
    }

    public void unbind() {
        buffers.unbind();
    }

}
