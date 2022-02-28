#include <bnb/glsl.frag>

BNB_IN(0)
vec2 var_uv;

BNB_DECLARE_SAMPLER_2D(0, 1, tex_y);
BNB_DECLARE_SAMPLER_2D(2, 3, tex_u);
BNB_DECLARE_SAMPLER_2D(4, 5, tex_v);
BNB_DECLARE_SAMPLER_2D(6, 7, tex_uv);
BNB_DECLARE_SAMPLER_2D(8, 9, tex_rgb);

void main()
{
    vec2 uv = var_uv;

    if (bnb_rgba_camera.x < 0.5) {
        float Y = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_y), uv).x;

        if (bnb_is_i420.x < 0.5) {
#ifdef BNB_GL_ES_1
            // GL_LUMINANCE_ALPHA
            vec2 UV = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_uv), uv).xw;
#else
            vec2 UV = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_uv), uv).xy;
#endif
            bnb_FragColor = vec4(Y, UV.x, UV.y, 1.) * bnb_conversion_matrix;
        } else {
            float U = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_u), uv).x;
            float V = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_v), uv).x;

            bnb_FragColor = vec4(Y, U, V, 1.) * bnb_conversion_matrix;
        }
    } else {
        bnb_FragColor = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_rgb), uv);
    }
    //discard pixels where uv values are out of range [0, 1]
    vec2 s = step(vec2(0., 0.), uv) - step(vec2(1., 1.), uv);
    bnb_FragColor.rgb = mix(vec3(0., 0., 0.), bnb_FragColor.rgb, s.x * s.y);
    bnb_FragColor.a = 1.;
}