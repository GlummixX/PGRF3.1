#version 330
in vec3 inPosition;
in vec3 inColor;
out vec3 color;
uniform mat4 mat;

void main() {
    color = inColor;
	gl_Position = mat * vec4(inPosition, 1.0);
}