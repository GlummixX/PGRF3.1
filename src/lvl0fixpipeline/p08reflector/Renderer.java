package lvl0fixpipeline.p08reflector;

import lvl0fixpipeline.global.AbstractRenderer;
import lwjglutils.OGLTextRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.DoubleBuffer;

import static lvl0fixpipeline.global.GluUtils.gluLookAt;
import static lvl0fixpipeline.global.GluUtils.gluPerspective;
import static lvl0fixpipeline.global.GlutUtils.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Shows the definition of a reflector light source
 *
 * @author PGRF FIM UHK
 * @version 3.1
 * @since 2020-01-20
 */
public class Renderer extends AbstractRenderer {
    private float mouseX, mouseY, directX, directY;
    private boolean mouseButton1 = false;
    private boolean mouseButton3 = false;
    private boolean per = true, flat = false, light = false, spot = false;
    private int wire = 0;
    private float kA = 0.5f, kD = 0.5f, kS = 0.5f, kH = 1;
    private float step = 0.01f;

    public Renderer() {
        super();

        /*used default glfwWindowSizeCallback see AbstractRenderer*/

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_RELEASE) {
                    //do nothing
                }
                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_P:
                            per = !per;
                            break;
                        case GLFW_KEY_T:
                            flat = !flat;
                            break;
                        case GLFW_KEY_L:
                            light = !light;
                            break;
                        case GLFW_KEY_I:
                            wire = (wire + 1);
                            break;
                        case GLFW_KEY_O:
                            spot = !spot;
                            break;
                    }
                }
                switch (key) {
                    case GLFW_KEY_Q:
                        kA = kA < 1 ? kA + step : 1;
                        break;
                    case GLFW_KEY_A:
                        kA = kA > 0 ? kA - step : 0;
                        break;
                    case GLFW_KEY_W:
                        kS = kS < 1 ? kS + step : 1;
                        break;
                    case GLFW_KEY_S:
                        kS = kS > 0 ? kS - step : 0;
                        break;
                    case GLFW_KEY_E:
                        kD = kD < 1 ? kD + step : 1;
                        break;
                    case GLFW_KEY_D:
                        kD = kD > 0 ? kD - step : 0;
                        break;
                    case GLFW_KEY_R:
                        kH = kH < 100 ? kH * (1 + step) : 100;
                        break;
                    case GLFW_KEY_F:
                        kH = kH > 0 ? kH * (1 - step) : 1;
                        break;
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);

                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
                mouseButton3 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_3) == GLFW_PRESS;

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    mouseX = (float) xBuffer.get(0);
                    mouseY = (float) yBuffer.get(0);
                }
                if (button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS) {
                    directX = (float) xBuffer.get(0);
                    directY = (float) yBuffer.get(0);
                }
            }

        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    mouseX = (float) x;
                    mouseY = (float) y;
                }
                if (mouseButton3) {
                    directX = (float) x;
                    directY = (float) y;
                }
            }
        };

        glfwScrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                //do nothing
            }
        };
    }

    @Override
    public void init() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        textRenderer = new OGLTextRenderer(width, height);

        glEnable(GL_DEPTH_TEST);

        glClearColor(0f, 0f, 0f, 1f);

        setLight();
    }

    private void setLight() {
        // light source setting - specular component
        float[] light_spec = new float[]{1, 1, 1, 1};
        // light source setting - diffuse component
        float[] light_dif = new float[]{1, 1, 1, 1};
        // light source setting - ambient component
        float[] light_amb = new float[]{1, 1, 1, 1};

        glLightfv(GL_LIGHT0, GL_AMBIENT, light_amb);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, light_dif);
        glLightfv(GL_LIGHT0, GL_SPECULAR, light_spec);
        glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, kH);
    }

    private void setMaterial(int mode) {
        // surface material setting - specular reflection
        float[] mat_spec = new float[]{0.1f, 0.1f, 0.1f, 1};
        // surface material setting - diffuse reflection
        float[] mat_dif = new float[]{0.1f, 0.1f, 0.1f, 1};
        // surface material setting - ambient reflection
        float[] mat_amb = new float[]{0.1f, 0.1f, 0.1f, 1};

        // surface material setting - emission
        float[] mat_emis = new float[]{0.1f, 0.1f, 0.1f, 1};

        int index = mode % 3;

        mat_dif[index] = kD;
        mat_spec[index] = kS;
        mat_amb[index] = kD;

        glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, mat_amb);
        glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, mat_dif);
        glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, mat_spec);
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, kH);
        glMaterialfv(GL_FRONT, GL_EMISSION, mat_emis);
    }

    private void drawScene() {
        setMaterial(2);
        glFrontFace(GL_CCW);
        glLoadIdentity();
        glTranslatef(-50, 0, 0);
        glBegin(GL_QUADS);
        for (int i = -100; i < 100; i++) {
            for (int j = -100; j < 100; j++) {
                glNormal3f(1, 0, 0);
                glVertex3f(0, i, j);
                glVertex3f(0, i + 1, j);
                glVertex3f(0, i + 1, j + 1);
                glVertex3f(0, i, j + 1);
            }
        }
        glEnd();

        glEnable(GL_NORMALIZE);
        glFrontFace(GL_CCW);
        glLoadIdentity();
        glTranslatef(0, 20, 0);
        setMaterial(1);
        glutSolidSphere(5, 30, 30);

        glLoadIdentity();
        glTranslatef(0, -20, 0);
        setMaterial(0);
        glutSolidCube(5);
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // povoleni a nastaveni odstraneni odvracenych ploch
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        //glCullFace(GL_FRONT);
        //glCullFace(GL_FRONT_AND_BACK);

        glEnable(GL_DEPTH_TEST);
        setLight();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        if (per)
            gluPerspective(45, width / (float) height, 0.1f, 200.0f);
        else
            glOrtho(-20 * width / (float) height,
                    20 * width / (float) height,
                    -20, 20, 0.1f, 200.0f);

        gluLookAt(50, 0, 0, 0, 0, 0, 0, 0, 1);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // nastaveni pozice svetla
        float[] light_position;
        if (!light) {
            // bod v prostoru
            light_position = new float[]{25, mouseX - width / 2f, height / 2f - mouseY, 1.0f};
        } else {
            // smer - umisteni v nekonecnu
            light_position = new float[]{25, mouseX - width / 2f, height / 2f - mouseY, 0.0f};
        }
        glLightfv(GL_LIGHT0, GL_POSITION, light_position);

        //smer reflektoru
        float[] light_direction = {-1, directX / (float) width - 0.5f, 0.5f - directY / (float) height, 0.0f};

        glFrontFace(GL_CCW);
        glPushMatrix();
        glLoadIdentity();

        glColor3f(1.0f, 1.0f, 0.0f);
        // cara znazornujici smer reflektoru
        if (spot) {
            glBegin(GL_LINES);
            glVertex3f(25, mouseX - width / 2f, height / 2f - mouseY);
            glVertex3f(15, light_direction[1] * 10 + mouseX - width / 2f, light_direction[2] * 10 + height / 2f - mouseY);
            glEnd();
        }

        glTranslatef(25, mouseX - width / 2f, height / 2f - mouseY);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glColor3f(1.0f, 1.0f, 0.0f);
        // koule znazornujici bodovy zdroj svetla
        glutSolidSphere(1, 10, 10);
        glPopMatrix();


        // zapnuti svetla a nastaveni modelu stinovani
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        if (flat)
            glShadeModel(GL_FLAT);
        else
            glShadeModel(GL_SMOOTH);

        // uhel svetelneho kuzele
        if (spot)
            glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 20);
        else
            glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 180);

        // exponent pri vypoctu ubytku osvetleni

        glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 1f);


        //light_direction =  new float[]{-25,0,0,0.0f};

        glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light_direction);

        //  glEnable( GL_POLYGON_OFFSET_FILL );
        // zpusob vykresleni privracenych a odvracenych ploch
        wire = wire % 2;
        switch (wire) {
            case 0:
                glPolygonMode(GL_FRONT, GL_FILL);
                break;
            case 1:
                glPolygonMode(GL_FRONT, GL_LINE);
                break;
        }
        glPolygonMode(GL_BACK, GL_POINT); // GL_LINE,GL_POINT,GL_FILL

        drawScene();

        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);
        float[] color = {1.0f, 1.0f, 1.0f};
        glColor3fv(color);
        glDisable(GL_DEPTH_TEST);
        String text = this.getClass().getName() + ": [lmb] move, ";

        if (per)
            text += ", [P]ersp ";
        else
            text += ", [p]ersp ";

        if (flat)
            text += ", Fla[T] ";
        else
            text += ", Fla[t] ";

        if (light)
            text += ", [L]ight infinity position";
        else
            text += ", [l]ight infinity position";

        if (spot)
            text += ", sp[O]t light";
        else
            text += ", sp[o]t light";

        switch (wire) {
            case 0:
                text += ", sol[i]d";
                break;
            case 1:
                text += ", w[i]re";
                break;
        }
        String textInfo = "x " + directX + " y " + directY +
                ", kA = " + kA + ", kS = " + kS + ", kD = " + kD + ", h = " + kH;

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        text += String.format(" [lmb] light position (%3.1f, %3.1f, %3.1f, %3.1f)", light_position[0], light_position[1], light_position[2], light_position[3]);
        text += String.format(" [mmb] light direction (%3.1f, %3.1f, %3.1f, %3.1f)", light_direction[0], light_direction[1], light_direction[2], light_direction[3]);
        //create and draw text
        textRenderer.clear();
        textRenderer.addStr2D(3, 20, text);
        textRenderer.addStr2D(3, 40, textInfo);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
        textRenderer.draw();
    }

}
