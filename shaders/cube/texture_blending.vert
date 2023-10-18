#version 330 core
in vec3 inPosition;
in vec3 inNormal;
in vec2 inTexture;

out vec3 FragPos;
out vec2 texCoord;

uniform mat4 mat;
uniform mat4 model;

void main() {
	FragPos = vec3(model * vec4(inPosition, 1.0));
	gl_Position = mat * model * vec4(inPosition, 1.0);
	texCoord = inTexture;
}