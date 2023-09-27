#version 330
#define PI 3.1415926535897932384626433832795
#define a 1.0
#define b 0.5
in vec3 inPosition; // input from the vertex buffer
out vec3 color; // output from this shader to the next pipeline stage
uniform mat4 mat; // variable constant for all vertices in a single draw

void main() {
    float x = inPosition.x*2.05*PI;
    float y = inPosition.y*2.05*PI;
    vec3 out_arr = inPosition;
    out_arr.x = cos(x)*(a + b*cos(y));
    out_arr.y = sin(x)*(a + b*cos(y));
    out_arr.z = b*sin(y);
    color.xyz = ((out_arr+1.0)/2.0)*((out_arr.z+1.0)/2.0); // color + primitive light
	gl_Position = mat * vec4(out_arr, 1.0);
} 
