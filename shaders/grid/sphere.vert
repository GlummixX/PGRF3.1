#version 330
#define PI 3.1415926535897932384626433832795
#define scale 1.0
in vec2 inPosition; // input from the vertex buffer
out vec3 normal;
uniform mat4 matMVP; // matrix

float explicitFunction(in vec2 pos){
    float distance = sqrt(pos.x*pos.x+pos.y*pos.y);
    return cos(PI*scale*distance);
}

vec3 normalCalculation(in vec2 pos){
    vec3 normal;
    float distance = sqrt(pos.x*pos.x+pos.y*pos.y);
    normal.x = scale*PI*sin(scale*PI*distance)/distance*pos.x;
    normal.y = scale*PI*sin(scale*PI*distance)/distance*pos.y;
    normal.z = 1.0;
    return normal;
}

void main() {
    // [0;1] to [-1;1]
    vec2 position = 2.0 * (inPosition.xy - vec2(0.5));
    normal = normalCalculation(position.xy);
    // z = f(x,y)
    float resultZ = explicitFunction(position.xy);
    // transformation MVP
    gl_Position = matMVP * vec4(inPosition.xy, resultZ, 1.0);
}