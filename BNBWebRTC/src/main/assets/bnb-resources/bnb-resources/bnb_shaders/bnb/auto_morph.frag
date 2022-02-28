#include <bnb/glsl.frag>

BNB_IN(0)
vec3 translation;

void main()
{
    bnb_FragColor = vec4(translation, 1.);
}