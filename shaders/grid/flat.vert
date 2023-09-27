#version 330
in vec3 inPosition; // input from the vertex buffer
out vec3 color; // output from this shader to the next pipeline stage
uniform mat4 mat; // variable constant for all vertices in a single draw

void main() {
    color.xyz = vec3(0.5);
	gl_Position = mat * vec4(inPosition, 1.0);
}
