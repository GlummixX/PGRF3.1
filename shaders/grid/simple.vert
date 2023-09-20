#version 150
in vec3 inPosition; // input from the vertex buffer
out vec3 vertColor; // output from this shader to the next pipeline stage
uniform mat4 mat; // variable constant for all vertices in a single draw
void main() {
	gl_Position = mat * vec4(inPosition, 1.0);
	vertColor = vec3(1.0,1.0,1.0);
} 
