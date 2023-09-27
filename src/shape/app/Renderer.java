package shape.app;

import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import shape.global.AbstractRenderer;
import shape.utils.FpsLimiter;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static shape.model.Grid.gridList;

enum Mode{
    Fill,
    Lines,
    Dots;
    public Mode next(){
        Mode[] array = Mode.values();
        int i = Arrays.asList(array).indexOf(this);
        return array[(i+1)%3];
    }
}

public class Renderer extends AbstractRenderer {
    private FpsLimiter limiter;
    private OGLBuffers grid;
    private boolean mouseButton1 = false;
    private Mode mode = Mode.Fill;
    double ox, oy;
    Camera cam = new Camera().withPosition(new Vec3D(-1.0,-1.0,1.0));
    Mat4 proj = new Mat4PerspRH(Math.PI / 4, 1, 0.01, 1000.0);
    int shaderProgram, locMat;

    public Renderer(int width, int height) {
        super(width, height);
        callbacks();
    }

    private void callbacks() {
         glfwKeyCallback = new GLFWKeyCallback(){
             @Override
             public void invoke(long window, int key, int scancode, int action, int mods) {
                 if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                     glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                 if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                     switch (key) {
                         case GLFW_KEY_M -> mode = mode.next();
                         case GLFW_KEY_W -> cam = cam.forward(0.01);
                         case GLFW_KEY_D -> cam = cam.right(0.01);
                         case GLFW_KEY_S -> cam = cam.backward(0.01);
                         case GLFW_KEY_A -> cam = cam.left(0.01);
                         case GLFW_KEY_LEFT_CONTROL -> cam = cam.down(0.01);
                         case GLFW_KEY_LEFT_SHIFT -> cam = cam.up(0.01);
                         case GLFW_KEY_SPACE -> cam = cam.withFirstPerson(!cam.getFirstPerson());
                         case GLFW_KEY_R -> cam = cam.mulRadius(0.9f);
                         case GLFW_KEY_F -> cam = cam.mulRadius(1.1f);
                     }
                 }
             }
         };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                            .addZenith((double) Math.PI * (oy - y) / width);
                    ox = x;
                    oy = y;
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

                if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                    mouseButton1 = true;
                    DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                    DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                    glfwGetCursorPos(window, xBuffer, yBuffer);
                    ox = xBuffer.get(0);
                    oy = yBuffer.get(0);
                }

                if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                    mouseButton1 = false;
                    DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                    DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                    glfwGetCursorPos(window, xBuffer, yBuffer);
                    double x = xBuffer.get(0);
                    double y = yBuffer.get(0);
                    cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                            .addZenith((double) Math.PI * (oy - y) / width);
                    ox = x;
                    oy = y;
                }
            }

        };
    }

    @Override
    public void init() {
        super.init();
        GL.createCapabilities();
        limiter = new FpsLimiter(60);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        shaderProgram = ShaderUtils.loadProgram("/grid/simple");
        glUseProgram(this.shaderProgram);
        grid = gridList(100);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        // set the current shader to be used
        glUseProgram(shaderProgram);

        glUniformMatrix4fv(locMat, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)));

        // bind and draw
        switch (mode){
            case Fill -> {
                glPolygonMode(GL_FRONT, GL_FILL);
                glPolygonMode(GL_BACK, GL_FILL);
                grid.draw(GL_TRIANGLES, shaderProgram);
            }
            case Lines -> {
                glPolygonMode(GL_FRONT, GL_LINE);
                glPolygonMode(GL_BACK, GL_LINE);
                grid.draw(GL_TRIANGLES, shaderProgram);
            }
            case Dots -> {
                grid.draw(GL_POINTS, shaderProgram);
            }
        }

        textRenderer.addStr2D(width-120, height-3, " (c) Matěj Kolář UHK");
        textRenderer.addStr2D(width-50, 15, "FPS: " + limiter.getCurrentFps());
        limiter.limit();
    }

}