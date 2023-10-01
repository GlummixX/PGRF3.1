#version 330
in vec3 inPosition; // input from the vertex buffer
uniform mat4 mat; // variable constant for all vertices in a single draw

void main() {
	gl_Position = mat * vec4(inPosition, 1.0);
}
