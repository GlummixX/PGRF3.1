package shape.app;

import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;
import shape.global.AbstractRenderer;
import shape.model.Axis;
import shape.model.Cube;
import shape.utils.FpsLimiter;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static shape.model.Cube.createCube;
import static shape.model.Cube.createTextureCube;

public class LightsScene extends AbstractRenderer {
    double ox, oy;
    Camera cam = new Camera().withPosition(new Vec3D(-1.5, 0.5, 0.5));
    Mat4 proj = new Mat4PerspRH(Math.PI / 4, (double) height / width, 0.01, 1000.0);
    int shaderProgram;
    private boolean changeScene = false;
    private boolean renderDocDebug;
    private HashMap<String, Integer> gridShaders;
    private FpsLimiter limiter;
    private Cube cube;
    private Cube texture_cube;
    private Axis axis;
    private OGLTexture2D texture;
    private HashMap<String, ArrayList<String>> info;
    private boolean persp = true;
    private double speed = 0.01;
    private double zoom = 32;
    private boolean mouseButton1 = false;
    private String aciveShaderName = "Flat";
    private float time = 0;
    private Mat4 modelTransf;

    public LightsScene(int width, int height, boolean debug) {
        super(width, height);
        renderDocDebug = debug;
        callbacks();
        gridShaders = new HashMap<>();

        info = new HashMap<>();
        info.put("scene", new ArrayList<>(List.of("[TAB] Scene: Lights", "")));
        info.put("projection", new ArrayList<>(List.of("[P] Projection:", "")));
        info.put("shader", new ArrayList<>(List.of("[R] Shader:", "Flat")));
        info.put("speed", new ArrayList<>(List.of("Speed:", "0.01", " Zoom:", "32")));

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
                        case GLFW_KEY_TAB -> {
                            changeScene = true;
                            glfwSetWindowShouldClose(window, true);
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
                    zoom += zoom * dy * 0.1;
                    info.get("speed").set(3, String.format("%.0f", zoom));
                    proj = new Mat4OrthoRH(width / zoom, height / zoom, 0.01, 1000.0);
                }
            }
        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    cam = cam.addAzimuth((double) Math.PI * (ox - x) / width).addZenith((double) Math.PI * (oy - y) / width);
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
                    cam = cam.addAzimuth((double) Math.PI * (ox - x) / width).addZenith((double) Math.PI * (oy - y) / width);
                    ox = x;
                    oy = y;
                }
            }

        };
    }

    @Override
    public void init() {
        super.init();
        changeScene = false;
        GL.createCapabilities();
        limiter = new FpsLimiter();
        glClearColor(0.4f, 0.4f, 0.5f, 1.0f);

        gridShaders.put("Flat", ShaderUtils.loadProgram("/grid/flat"));
        gridShaders.put("Light Phong", ShaderUtils.loadProgram("/cube/light_basic"));
        gridShaders.put("Textured Phong", ShaderUtils.loadProgram("/cube/light_texture"));
        shaderProgram = gridShaders.get("Flat");

        try {
            texture = new OGLTexture2D("textures/bricks.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        axis = new Axis();
        cube = createCube();
        texture_cube = createTextureCube();
        modelTransf = new Mat4Identity();
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void display() {
        //shared across scenes
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        time = (time + 0.01F) % (float) Math.PI;

        glUseProgram(shaderProgram);

        glPolygonMode(GL_FRONT, GL_FILL);
        glPolygonMode(GL_BACK, GL_FILL);

        glUniformMatrix4fv(0, false, ToFloatArray.convert(cam.getViewMatrix().mul(proj)));
        if (aciveShaderName.equals("Flat")) {
            cube.draw(shaderProgram);
        } else if (aciveShaderName.equals("Light Phong")) {
            glUniformMatrix4fv(1, false, ToFloatArray.convert(modelTransf));
            glUniform3fv(glGetUniformLocation(shaderProgram, "viewPos"), ToFloatArray.convert(cam.getPosition()));
            cube.draw(shaderProgram);
        } else if (aciveShaderName.equals("Textured Phong")) {
            texture.bind(shaderProgram, "textureID", 0);
            glUniformMatrix4fv(1, false, ToFloatArray.convert(modelTransf));
            glUniform3fv(glGetUniformLocation(shaderProgram, "viewPos"), ToFloatArray.convert(cam.getPosition()));
            texture_cube.draw(shaderProgram);
        }

        axis.draw(cam.getViewMatrix().mul(proj));

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

    public boolean nextScene(){
        return changeScene;
    }

    @Override
    public void dispose(){
        for (int s: gridShaders.values()) {
            glDeleteProgram(s);
        }
        cube.unbind();
    }

}