#include <bnb/glsl.vert>

BNB_LAYOUT_LOCATION(0)
BNB_IN vec3 attrib_pos;

#if defined(BNB_VK_1)
BNB_LAYOUT_LOCATION(1)
BNB_IN uint attrib_n;
BNB_LAYOUT_LOCATION(2)
BNB_IN uint attrib_t;
#else
BNB_LAYOUT_LOCATION(1)
BNB_IN vec4 attrib_n;
BNB_LAYOUT_LOCATION(2)
BNB_IN vec4 attrib_t;
#endif

BNB_LAYOUT_LOCATION(3)
BNB_IN vec2 attrib_uv;

#if defined(BNB_GL_ES_3) || defined(BNB_VK_1)
BNB_LAYOUT_LOCATION(4)
BNB_IN uvec4 attrib_bones;
#else
BNB_LAYOUT_LOCATION(4)
BNB_IN vec4 attrib_bones;
#endif
BNB_LAYOUT_LOCATION(5)
BNB_IN vec4 attrib_weights;

BNB_DECLARE_SAMPLER_2D(0, 1, bnb_UVMORPH);
BNB_DECLARE_SAMPLER_2D(2, 3, bnb_STATICPOS);

BNB_CENTROID BNB_OUT(0) vec2 var_c;

void main()
{
    const int EXPAND_PASSES = 8;
    const float NPUSH = 75.;

    int i = int(gl_InstanceID);

    float scale = 1. - float(i) / float(EXPAND_PASSES + 1);
    scale = scale * scale * (3. - 2. * scale); // smoothstep fall-off
    float d0 = float(i) / float(EXPAND_PASSES + 1);
    float d1 = float(i + 1) / float(EXPAND_PASSES + 1);
#ifndef BNB_VK_1
    vec4 npush_scale = vec4(NPUSH * float(i) / float(EXPAND_PASSES), scale * 0.5 * MORPH_WEIGHT, d1 - d0, d0 + d1 - 1.);
#else
    vec4 npush_scale = vec4(NPUSH * float(i) / float(EXPAND_PASSES), scale * 0.5 * MORPH_WEIGHT, (d1 - d0) * 0.5, (d0 + d1) * 0.5);
#endif
    const float max_range = 40.;
    vec3 translation = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_UVMORPH), smoothstep(0., 1., attrib_uv)).xyz * (2. * max_range) - max_range;
    vec3 vpos = attrib_pos + translation;

    gl_Position = bnb_MVP * vec4(vpos * (1. + npush_scale.x / length(vpos)), 1.);
    gl_Position.z = gl_Position.z * npush_scale.z + gl_Position.w * npush_scale.w;

    vec4 pos_no_push = bnb_MVP * vec4(vpos, 1.);
    vec2 uv = attrib_uv;
#if defined(BNB_GL_ES_3) || defined(BNB_GL) || defined(BNB_GL_ES_1)
    uv.y = 1.0 - uv.y;
#endif
    vec3 static_pos = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_STATICPOS), uv).xyz;
    vec4 original_pos = bnb_MVP * vec4(static_pos + translation, 1.);
    var_c = npush_scale.y * (original_pos.xy / original_pos.w - pos_no_push.xy / pos_no_push.w);
}