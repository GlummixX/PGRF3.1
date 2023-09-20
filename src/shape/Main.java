package shape;

import shape.global.LwjglWindow;
import shape.app.Renderer;

public class Main {
    public static void main(String[] args) {
        int w = 1280;
        int h = 720;
        new LwjglWindow(w, h, new Renderer(w, h), false);
        System.exit(0);
    }

}