#version 330

#define objectColor vec3(0.6, 0.5, 0.5)
#define lightColor vec3(1.0, 1.0, 1.0)
#define ambientStrength 0.3
#define diffuseStrength 0.7
#define specularStrength 0.5
#define constantAtt 1.0
#define linearAtt 0.1
#define quadraticAtt 0.1
#define spotCutoff 0.98

uniform vec3 viewPos;
uniform vec3 lightPos;
uniform vec3 lightDir;

in vec3 FragPos;
in vec3 normal;

out vec4 outColor;

void main() {
    vec3 lightToFragment = FragPos - lightPos;
    vec3 ld = normalize(lightToFragment);

    float distance = length(lightToFragment);

    float spotEffect = max(dot(normalize(lightDir), normalize(-ld)), 0.0);
    vec3 ambient = lightColor * ambientStrength;

    if (spotEffect > spotCutoff) {
        // Calculate attenuation
        float attenuation = 1.0 / (constantAtt + linearAtt * distance + quadraticAtt * distance * distance);

        // Diffuse
        vec3 norm = normalize(normal);
        vec3 lightDir = normalize(lightPos - FragPos);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * lightColor;

        // Specular
        vec3 viewDir = normalize(viewPos - FragPos);
        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 128);
        vec3 specular = specularStrength * spec * lightColor;

        outColor = vec4((ambient + attenuation*(diffuse + specular)) * objectColor, 1.0);
    } else {
        outColor = vec4(ambient * objectColor, 1.0);
    }
}