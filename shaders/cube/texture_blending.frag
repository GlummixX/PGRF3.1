#version 330 core

in vec3 FragPos;
in vec2 texCoord;

out vec4 outColor;

uniform sampler2D textureID;
uniform sampler2D textureID2;

void main() {
    vec3 tx1 = vec3(texture(textureID, texCoord));
    vec3 tx2 = vec3(texture(textureID2, texCoord));

    vec3 result = mix(tx1, tx2, texCoord.x);
    outColor = vec4(result, 1.0);
}