#include <bnb/glsl.frag>

BNB_IN(0)
vec2 var_uv;

BNB_DECLARE_SAMPLER_2D(0, 1, s);

void main()
{
    float THRESHOLD = light_streak_threshold.x;
    vec3 c = BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), var_uv).xyz;
    float intensity = dot(c, vec3(0.299, 0.587, 0.114));
    bnb_FragColor = vec4(intensity * step(THRESHOLD, intensity));
}