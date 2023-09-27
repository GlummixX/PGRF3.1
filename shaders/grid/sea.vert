#version 330
#define PI 3.1415926535897932384626433832795
#define wavelength 0.5
#define amplitude 0.1
#define waveSpeed 2.0

in vec3 inPosition; // input from the vertex buffer
out float norm;
uniform mat4 mat; // matrix
uniform float time;

float generate(in vec3 pos) {
    float kx = 2 * PI / wavelength;
    float ky = 2 * PI / wavelength;
    return amplitude * sin(kx * pos.x + ky * pos.y - waveSpeed * time);
}

void main() {
    vec3 out_arr = inPosition;
    out_arr.z = generate(inPosition);
    norm = (out_arr.z+1.0)/2.0;
	gl_Position = mat * vec4(out_arr, 1.0);
}