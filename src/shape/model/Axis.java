package shape.model;

import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import transforms.Mat4;

import static org.lwjgl.opengl.GL20.*;

public class Axis {
    private OGLBuffers bufferX;
    private OGLBuffers bufferY;
    private OGLBuffers bufferZ;
    private int program;

    public Axis() {
        program = ShaderUtils.loadProgram("/axis");
        float[] lineX = {0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0};
        float[] lineY = {0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
        float[] lineZ = {0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1};

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inColor", 3),
        };

        int[] idx = {0, 1};

        bufferX = new OGLBuffers(lineX, attributes, idx);
        bufferY = new OGLBuffers(lineY, attributes, idx);
        bufferZ = new OGLBuffers(lineZ, attributes, idx);
    }

    public void draw(Mat4 tr) {
        glUseProgram(program);
        glUniformMatrix4fv(0, false, ToFloatArray.convert(tr));
        bufferX.draw(GL_LINES, program);
        bufferY.draw(GL_LINES, program);
        bufferZ.draw(GL_LINES, program);
    }

    public void unbind() {
        bufferX.unbind();
        bufferY.unbind();
        bufferZ.unbind();
        glDeleteProgram(program);
    }
}
