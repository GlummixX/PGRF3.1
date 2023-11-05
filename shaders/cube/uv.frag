#version 330 core

in vec3 FragPos;
in vec2 texCoord;

out vec4 outColor;

void main() {
    outColor = vec4(texCoord.xy, 0.0, 1.0);
}