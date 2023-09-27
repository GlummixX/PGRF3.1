#version 330
in vec3 color;
out vec4 outColor;
void main() {
    outColor.rgb = color;
    outColor.a = 1.0;
}