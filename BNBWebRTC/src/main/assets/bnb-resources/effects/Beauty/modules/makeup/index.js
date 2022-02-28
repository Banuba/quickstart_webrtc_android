'use strict';

const modules_scene_index = require('../scene/index.js');

const NullMakeup = "modules/makeup/MakeupNull.png";

const Lashes = "modules/makeup/eyelashes_makeup.ktx";

const vertexShader = "modules/makeup/makeup.vert";

const fragmentShader = "modules/makeup/makeup.frag";

class Makeup {
    constructor() {
        Object.defineProperty(this, "_makeup", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: new modules_scene_index.Mesh(new modules_scene_index.FaceGeometry(), new modules_scene_index.ShaderMaterial({
                vertexShader,
                fragmentShader,
                uniforms: {
                    tex_lashes: new modules_scene_index.Image(NullMakeup),
                    tex_makeup: new modules_scene_index.Image(NullMakeup),
                    var_lashes_color: new modules_scene_index.Vector4(0, 0, 0, 1),
                },
            }))
        });
        this._makeup.visible(false);
        const onChange = () => {
            let isCorrectionNeeded = [
                this._makeup.material.uniforms.var_lashes_color.value(),
            ].some(([, , , a]) => a > 0);
            if (isCorrectionNeeded)
                modules_scene_index.enable("EYES_CORRECTION", this);
            else
                modules_scene_index.disable("EYES_CORRECTION", this);
        };
        this._makeup.material.uniforms.var_lashes_color.subscribe(onChange);
        modules_scene_index.add(this._makeup);
    }
    lashes(value) {
        this._makeup.visible(true);
        if (isUrl(value)) {
            this._makeup.material.uniforms.tex_lashes.load(value);
            return;
        }
        if (this._makeup.material.uniforms.tex_lashes.filename === NullMakeup) {
            this._makeup.material.uniforms.tex_lashes.load(Lashes);
        }
        this._makeup.material.uniforms.var_lashes_color.value(value);
    }
    /** Removes the eyes color, resets any settings applied */
    clear() {
        this.lashes("0 0 0 0");
        this.lashes(NullMakeup);
        this._makeup.visible(false);
    }
}
function isUrl(str) {
    return /^\S+\.\w+$/.test(str);
}

exports.Makeup = Makeup;
