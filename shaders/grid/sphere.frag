#version 330
in vec3 normal;
out vec4 outColor; // output from the fragment shader
void main() {
    vec3 nNormal = normalize(normal);
    // diffuse component of light on the position (0,1,1)
    float f = dot(normalize(vec3(0.0,0.0,1.0)),nNormal);
    f = max(f,0.0);
    outColor.rgb = vec3(0.5+f);
    // alpha (opacity) must be 1.0
    outColor.a = 1.0;
}