#version 330
out vec4 outColor;
void main() {
    float originalZ = gl_FragCoord.z / gl_FragCoord.w;
    outColor = vec4(originalZ, originalZ, originalZ, 1.0);
}