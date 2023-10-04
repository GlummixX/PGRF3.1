#version 330
#define PI 3.1415926535897932384626433832795

in vec3 inPosition; // input from the vertex buffer
out vec3 color;
uniform mat4 matMVP; // matrix

vec2 rsValue(in vec2 pos){
    return vec2(sqrt(pos.x*pos.x+pos.y*pos.y), atan(pos.y, pos.x));
}

vec2 posValue(in vec2 rs){
    return vec2(rs.x*cos(rs.y), rs.x*sin(rs.y));
}

void main() {
    vec2 cyl = inPosition.xy*2.0*PI;
    vec2 pos = posValue(cyl);
    color = vec3(pos.xy, cyl.x);
    // transformation MVP
    gl_Position = matMVP * vec4(pos.xy*0.1, cyl.x*cyl.y*0.1 ,1.0);
}