package shape.utils;

import java.util.Arrays;

public enum SceneEnum {
    Grid,
    Lights;

    public SceneEnum next() {
        SceneEnum[] array = SceneEnum.values();
        int i = Arrays.asList(array).indexOf(this);
        return array[(i + 1) % array.length];
    }
}