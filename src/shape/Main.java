package shape;

import shape.app.GridScene;
import shape.app.LightsScene;
import shape.global.LwjglWindow;
import shape.utils.SceneEnum;

public class Main {
    public static void main(String[] args) {
        int w = 1280;
        int h = 720;
        SceneEnum scene = SceneEnum.Grid;
        GridScene grid = new GridScene(w, h);
        LightsScene lights = new LightsScene(w, h);
        LwjglWindow win = new LwjglWindow(w, h, false);
        boolean run = true;
        while (run) {
            System.out.println(scene.toString());
            switch (scene) {
                case Grid -> {
                    win.setRenderer(grid);
                    if (grid.nextScene()){
                        scene = scene.next();
                    }else{
                        run = false;
                    }
                    break;
                }
                case Lights -> {
                    win.setRenderer(lights);
                    if (lights.nextScene()){
                        scene = scene.next();
                    }else{
                        run = false;
                    }
                    break;
                }
            }
        }
        win.exit();

        System.exit(0);
    }

}