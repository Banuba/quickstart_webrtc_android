#ifndef BNB_QUAT_ROTATION
#define BNB_QUAT_ROTATION

vec3 bnb_quat_rotate(vec4 q, vec3 v)
{
    return v + 2. * cross(q.xyz, cross(q.xyz, v) + q.w * v);
}

float bnb_quat_to_side_euler(vec4 quat, float isMirroredCoeff, float initAngle)
{
    float angle = initAngle;
    vec4 q = quat;

    q.x = -q.x;
    q.y = -q.y;

    vec3 side = bnb_quat_rotate(q, vec3(1., 0., 0.));
    vec3 up = bnb_quat_rotate(q, vec3(0., 1., 0.));

    if (side.y > 0.7071 && bnb_SCREEN.x < bnb_SCREEN.y) {
        angle += 90. * isMirroredCoeff;
    } else if (side.y < -0.7071 && bnb_SCREEN.x < bnb_SCREEN.y) {
        angle += -90. * isMirroredCoeff;
    } else if (up.y < -0.7071 && bnb_SCREEN.x < bnb_SCREEN.y) {
        angle += 180.;
    }

    return angle;
}
#endif