#include <bnb/glsl.frag>

BNB_IN(0)
vec3 pos_static;

void main()
{
    bnb_FragColor = vec4(pos_static, 1.);
}