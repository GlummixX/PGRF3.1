#version 330
#define lightColor vec3(1.0, 1.0, 1.0)
#define lightPos vec3(0.0, 5.0, 100.0)
#define ambientStrength 0.3
#define specularStrength 0.5

in vec3 FragPos;
in vec3 normal;
in vec2 texCoord;

out vec4 outColor;

uniform vec3 viewPos;
uniform sampler2D textureID;

void main() {
    vec3 objectColor = texture(textureID, texCoord).rgb;
    //ambient
    vec3 ambient = ambientStrength * lightColor;

    //difuse
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    //specular
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 256);
    vec3 specular = specularStrength * spec * lightColor;

    vec3 result = (ambient + diffuse + specular) * objectColor;
    outColor = vec4(result, 1.0);
}