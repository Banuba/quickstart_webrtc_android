'use strict';

require('bnb_js/console');
const modules_scene_index = require('../scene/index.js');

class FaceMorph {
    constructor() {
        Object.defineProperty(this, "_face", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: new modules_scene_index.Mesh(new modules_scene_index.FaceGeometry(), [])
        });
        Object.defineProperty(this, "_beauty", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: this._face.geometry.addMorphing("$builtin$meshes/beauty")
        });
        modules_scene_index.add(this._face);
    }

    /** Sets nose shrink strength from 0 to 1 */
    nose(weight) {
        return this._beauty.weight("nose", weight);
    }
    /** Sets face (cheeks) shrink strength from 0 to 1 */
    face(weight) {
        return this._beauty.weight("face", weight);
    }

    /** Resets all morphs */
    clear() {
        this.nose(0);
        this.face(0);
    }
}

exports.FaceMorph = FaceMorph;
