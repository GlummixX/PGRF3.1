#version 330
in float norm;
out vec4 outColor;

float random(float seed) {
    return fract(sin(dot(vec2(seed), vec2(12.9898, 78.233))) * 43758.5453) - 0.5;
}

void main() {
    vec3 noise = 1.0 + vec3(random(norm), random(2.0), random(3.0));
    vec3 color = vec3(0.282, 0.835, 0.863) * noise;
    outColor.rgb = color*norm;
    outColor.a = 1.0;
}