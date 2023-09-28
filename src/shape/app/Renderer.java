package shape.app;

import lwjglutils.OGLModelOBJ;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;
import shape.global.AbstractRenderer;
import shape.model.Grid;
import shape.utils.FpsLimiter;
import transforms.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static shape.model.Grid.gridList;
import static shape.model.Grid.gridStrip;

enum Mode {
    Fill,
    Lines,
    Dots;

    public Mode next() {
        Mode[] array = Mode.values();
        int i = Arrays.asList(array).indexOf(this);
        return array[(i + 1) % 3];
    }
}

public class Renderer extends AbstractRenderer {
    double ox, oy;
    Camera cam = new Camera().withPosition(new Vec3D(-1.0, -1.0, 1.0));
    Mat4 proj = new Mat4PerspRH(Math.PI / 4, (double) height / width, 0.01, 1000.0);
    private boolean renderDocDebug = false;
    int shaderProgram, locMat, objShader;
    private HashMap<String, Integer> gridShaders;
    private FpsLimiter limiter;
    private Grid gridList;
    private Grid gridStrip;
    private Grid grid;
    private HashMap<String, ArrayList<String>> info;
    private boolean list = true;
    private boolean persp = true;
    private double speed = 0.01;
    private double zoom = 32;
    private boolean mouseButton1 = false;
    private Mode mode = Mode.Fill;
    private String aciveShaderName = "Flat";
    private float time = 0;
    private OGLModelOBJ model;
    private Mat4 modelTransf;

    public Renderer(int width, int height) {
        super(width, height);
        callbacks();
        gridShaders = new HashMap<>();

        info = new HashMap<>();
        info.put("mode", new ArrayList<>(List.of("Mode:", "")));
        info.put("grid", new ArrayList<>(List.of("Grid:", "")));
        info.put("projection", new ArrayList<>(List.of("Projection:", "")));
        info.put("shader", new ArrayList<>(List.of("Grid shader:", "Flat")));
        info.put("speed", new ArrayList<>(List.of("Speed:", "0.01", " Zoom:", "32")));

        info.get("mode").set(1, mode.toString());
        info.get("grid").set(1, list ? "List" : "Strip");
        info.get("projection").set(1, persp ? "Persp" : "Ortho");
    }

    private void callbacks() {
        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                    switch (key) {
                        case GLFW_KEY_M -> {
                            mode = mode.next();
                            info.get("mode").set(1, mode.toString());
                        }
                        case GLFW_KEY_G -> {
                            if (list) {
                                grid = gridStrip;
                                list = false;
                            } else {
                                grid = gridList;
                                list = true;
                            }
                            info.get("grid").set(1, list ? "List" : "Strip");
                        }
                        case GLFW_KEY_P -> {
                            if (persp) {
                                proj = new Mat4OrthoRH(width / zoom, height / zoom, 0.01, 1000.0);
                                persp = false;
                            } else {
                                proj = new Mat4PerspRH(Math.PI / 4, (double) height / width, 0.01, 1000.0);
                                persp = true;
                            }
                            info.get("projection").set(1, persp ? "Persp" : "Ortho");
                        }
                        case GLFW_KEY_R -> {
                            List<String> l = new ArrayList<>(gridShaders.keySet());
                            aciveShaderName = l.get(((l.indexOf(aciveShaderName) + 1) % l.size()));
                            shaderProgram = gridShaders.get(aciveShaderName);
                            info.get("shader").set(1, aciveShaderName);
                        }
                        case GLFW_KEY_W -> cam = cam.forward(speed);
                        case GLFW_KEY_D -> cam = cam.right(speed);
                        case GLFW_KEY_S -> cam = cam.backward(speed);
                        case GLFW_KEY_A -> cam = cam.left(speed);
                        case GLFW_KEY_LEFT_CONTROL -> cam = cam.down(speed);
                        case GLFW_KEY_LEFT_SHIFT -> cam = cam.up(speed);
                        case GLFW_KEY_SPACE -> cam = cam.withFirstPerson(!cam.getFirstPerson());
                        case GLFW_KEY_KP_ADD -> cam = cam.mulRadius(0.9f);
                        case GLFW_KEY_KP_SUBTRACT -> cam = cam.mulRadius(1.1f);
                    }
                }
            }
        };

        glfwScrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                if (persp) {
                    speed = Math.max(Math.min(speed + dy * 0.02, 1.0), 0.01);
                    info.get("speed").set(1, String.format("%.2f", speed));
                } else {
                    zoom += dy;
                    info.get("speed").set(3, String.format("%.0f", zoom));
                    proj = new Mat4OrthoRH(width / zoom, height / zoom, 0.01, 1000.0);
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

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    mouseButton1 = true;
                    DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                    DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                    glfwGetCursorPos(window, xBuffer, yBuffer);
                    ox = xBuffer.get(0);
                    oy = yBuffer.get(0);
                }

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
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

        gridShaders.put("Flat", ShaderUtils.loadProgram("/grid/flat"));
        gridShaders.put("Torus", ShaderUtils.loadProgram("/grid/torus"));
        gridShaders.put("Sphere", ShaderUtils.loadProgram("/grid/sphere"));
        gridShaders.put("Sea", ShaderUtils.loadProgram("/grid/sea"));
        objShader = ShaderUtils.loadProgram("/ducky");
        shaderProgram = gridShaders.get("Flat");

        model = new OGLModelOBJ("/obj/ducky.obj");
        modelTransf = new Mat4Scale(0.05).mul(new Mat4RotX(1.5)).mul(new Mat4Transl(new Vec3D(0.5, 0.5, 0)));

        gridList = gridList(100);
        gridStrip = gridStrip(100);
        grid = gridList;
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glUseProgram(shaderProgram);
        time = (time + 0.01F) % (float) Math.PI;

        glUniformMatrix4fv(0, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)));

        if (aciveShaderName.equals("Sea")) {
            glUniform1f(1, time);
        }

        switch (mode) {
            case Fill -> {
                glPolygonMode(GL_FRONT, GL_FILL);
                glPolygonMode(GL_BACK, GL_FILL);
                grid.draw(shaderProgram);
            }
            case Lines -> {
                glPolygonMode(GL_FRONT, GL_LINE);
                glPolygonMode(GL_BACK, GL_LINE);
                grid.draw(shaderProgram);
            }
            case Dots -> grid.draw(shaderProgram, GL_POINTS);
        }

        if (aciveShaderName.equals("Sea")) {
            glUseProgram(objShader);
            glUniformMatrix4fv(0, false,
                    ToFloatArray.convert(modelTransf.mul(cam.getViewMatrix().mul(proj))));
            glUniform1f(1, time);
            model.getBuffers().draw(model.getTopology(), objShader);
        }

        if (!renderDocDebug) {
            text();
            textRenderer.addStr2D(width - 120, height - 3, " (c) Matěj Kolář UHK");
            textRenderer.addStr2D(width - 50, 15, "FPS: " + limiter.getCurrentFps());
        }
        limiter.limit();
    }

    private void text() {
        int y = 15;
        for (ArrayList<String> entry : info.values()) {
            textRenderer.addStr2D(5, y, String.join(" ", entry));
            y += 15;
        }
    }

}