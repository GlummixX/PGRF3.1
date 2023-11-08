#version 330
#define PI 3.1415926535897932384626433832795
in vec3 inPosition;

out vec3 FragPos;
out vec3 normal;
out vec3 n1o;
out vec3 n2o;

uniform mat4 mat;

float generate(in vec3 pos) {
    float kx = 2 * PI / 0.05;
    float ky = 2 * PI / 0.1;
    return 0.02 * sin(kx * pos.x + ky * pos.y);
}

void main() {
    vec3 out_arr = inPosition;
    out_arr.z = generate(out_arr);

    vec3 n1 = inPosition+vec3(0.005,0.0,0.0);
    n1.z = generate(n1);
    vec3 n2 = inPosition+vec3(0.0,0.005,0.0);
    n2.z = generate(n2);
    n1o = n1;
    n2o = n2;
    vec3 tangent = n1 - out_arr;
    vec3 bitangent = n2 - out_arr;

    normal = normalize(cross(tangent, bitangent));

	FragPos = out_arr;
	gl_Position = mat * vec4(out_arr, 1.0);
}