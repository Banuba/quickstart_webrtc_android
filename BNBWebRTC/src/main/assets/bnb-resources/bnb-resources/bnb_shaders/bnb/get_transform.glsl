
#ifndef BNB_GET_TRANSFORN_GLSL
#define BNB_GET_TRANSFORN_GLSL
#ifdef BNB_GL_ES_1

mat4 bnb_get_transform()
{
    float frame = (bnb_ANIM.x + 0.5) / bnb_ANIM.y;
    float db = 1. / (bnb_ANIM.z * 3.);
    mat4 m = bnb_get_bone((float(attrib_bones[0]) * 3. + 0.5) * db, db, frame);
    #ifndef BNB_1_BONE
    if (attrib_weights[1] > 0.) {
        m = m * attrib_weights[0]
            + bnb_get_bone((float(attrib_bones[1]) * 3. + 0.5) * db, db, frame) * attrib_weights[1];

        if (attrib_weights[2] > 0.) {
            m += bnb_get_bone((float(attrib_bones[2]) * 3. + 0.5) * db, db, frame) * attrib_weights[2];

            if (attrib_weights[3] > 0.) {
                m += bnb_get_bone((float(attrib_bones[3]) * 3. + 0.5) * db, db, frame) * attrib_weights[3];
            }
        }
    }
    #endif // BNB_1_BONE
    return m;
}

#else
mat4 bnb_get_transform()
{
    int y = int(bnb_ANIMKEY);
    mat4 m = bnb_get_bone(attrib_bones[0], y);
    #ifndef BNB_1_BONE
    if (attrib_weights[1] > 0.) {
        m = m * attrib_weights[0] + bnb_get_bone(attrib_bones[1], y) * attrib_weights[1];

        if (attrib_weights[2] > 0.) {
            m += bnb_get_bone(attrib_bones[2], y) * attrib_weights[2];

            if (attrib_weights[3] > 0.) {
                m += bnb_get_bone(attrib_bones[3], y) * attrib_weights[3];
            }
        }
    }
    #endif // BNB_1_BONE
    return m;
}
#endif     // BNB_GL_ES_1

#endif // BNB_GET_TRANSFORN_GLSL