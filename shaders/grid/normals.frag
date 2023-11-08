#version 330
#define objectColor vec3(0.6,0.5,0.5)
#define lightColor vec3(1.0, 1.0, 1.0)
#define lightPos vec3(0.0, 0.0, 100.0)
#define ambientStrength 0.3

in vec3 FragPos;
in vec3 normal;

out vec4 outColor;

void main() {
    //ambient
    vec3 ambient = ambientStrength * lightColor;

    //difuse
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor * 1.5;

    vec3 result = (ambient + diffuse) * objectColor;
    outColor = vec4(result, 1.0);
}