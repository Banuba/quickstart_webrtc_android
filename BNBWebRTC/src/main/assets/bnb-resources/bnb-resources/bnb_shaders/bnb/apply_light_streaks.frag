#include <bnb/glsl.frag>

BNB_DECLARE_SAMPLER_2D(0, 1, s);
BNB_IN(0)
vec2 var_uv;

void main()
{
    float streak = dot(BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), var_uv), vec4(1.));
    if (streak < 1. / 255.)
        discard;
    vec3 color = light_streaks_color.rgb;
    bnb_FragColor = vec4(color * streak, streak);
}