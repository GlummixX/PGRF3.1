package shape.utils;

import java.util.Arrays;

public enum Mode {
    Fill, Lines, Dots;

    public Mode next() {
        Mode[] array = Mode.values();
        int i = Arrays.asList(array).indexOf(this);
        return array[(i + 1) % array.length];
    }
}