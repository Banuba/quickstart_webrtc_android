#include <bnb/glsl.vert>

BNB_LAYOUT_LOCATION(0)
BNB_IN vec3 attrib_original;
BNB_LAYOUT_LOCATION(1)
BNB_IN vec3 attrib_morph;

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
    vec4 npush_scale = vec4(NPUSH * float(i) / float(EXPAND_PASSES), scale * 0.5, d1 - d0, d0 + d1 - 1.);
#else
    vec4 npush_scale = vec4(NPUSH * float(i) / float(EXPAND_PASSES), scale * 0.5, (d1 - d0) * 0.5, (d0 + d1) * 0.5);
#endif

    gl_Position = bnb_MVP * vec4(attrib_morph * (1. + npush_scale.x / length(attrib_morph)), 1.);
    gl_Position.z = gl_Position.z * npush_scale.z + gl_Position.w * npush_scale.w;
    vec4 pos_no_push = bnb_MVP * vec4(attrib_morph, 1.);
    vec4 original_pos = bnb_MVP * vec4(attrib_original, 1.);
    var_c = npush_scale.y * (original_pos.xy / original_pos.w - pos_no_push.xy / pos_no_push.w);
}