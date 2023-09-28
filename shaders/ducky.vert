#version 330
#define PI 3.1415926535897932384626433832795
#define wavelength 1.0
#define amplitude 2.0
#define waveSpeed 2.0

in vec3 inPosition; // input from the vertex buffer
in vec2 inTexCoord; // input from the vertex buffer
//in vec3 inNormal; // input from the vertex buffer
out vec3 vertColor; // output from this shader to the next pipleline stage
uniform mat4 mat; // variable constant for all vertices in a single draw
uniform float time;

mat4 translate(vec3 delta)
{
    return mat4(
        vec4(1.0, 0.0, 0.0, 0.0),
        vec4(0.0, 1.0, 0.0, 0.0),
        vec4(0.0, 0.0, 1.0, 0.0),
        vec4(delta, 1.0));
}

float generate(in vec3 pos) {
    float kx = 2 * PI / wavelength;
    float ky = 2 * PI / wavelength;
    return amplitude * sin(kx * pos.x + ky * pos.y - waveSpeed * time);
}

void main() {
    float z = generate(vec3(0.5,0.5,0))+0.25;
    gl_Position = (mat*translate(vec3(0.0,z,0.0))) * vec4(inPosition*0.005, 1.0);
	//vertColor = inNormal * 0.5 + 0.5;
	vertColor = vec3(inTexCoord , 0.5);
	//vertColor = inPosition*0.01;
}