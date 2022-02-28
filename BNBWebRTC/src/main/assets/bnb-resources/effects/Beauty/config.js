'use strict';

const ringcentral_makeup = require('./ringcentral_makeup.js');

Object.assign(globalThis, ringcentral_makeup.m);
/* Feel free to add your custom code below */

function setBackgroundMedia(args_json) {
    var args = JSON.parse(args_json);
    Background.texture(args.path).rotation(args.orientation);
}

function enableFRX() {
}

function disableFRX(){
}

function setBackgroundTexture(file){
    Background.contentMode("fill");
    Background.texture(file)
    
}

function setBackgroundVideo(file) {
    Background.contentMode("fill");
    Background.texture(file);
}

function initBackground() {
}

function deleteBackground(params) {
    Background.clear();
}

function initBlurBackground() {
}

function setBlurRadius(radius) {
    radius = (radius < 1) ? 1 : (radius > 4) ? 4 : radius;
    var processedRadius = radius / 10;
    Background.blur(processedRadius);
}

function deleteBlurBackground(params) {
    Background.blur(0);
}

function initTransparentBG(params) {
    Background.transparency(1);
}

function deleteTransparentBG(params) {
    Background.transparency(0);
}

var beautifyingProps = {
    morph_cheeks_str: 0.0,
    morph_eyes_str: 0.0,
    morph_nose_str: 0.0,

    skin_soft_str: 0.0,
    softlight_alpha: 0.0,
    softlight_tex: 0,

    eyes_coloring_str: 0.0,

    teeth_whitening_str: 0.0
};

function onDataUpdate(param) {
    value = JSON.parse(param);

    if (typeof (value) == "object") {
        beautifyingProps = value;

        FaceMorph.face(beautifyingProps.morph_cheeks_str);
        FaceMorph.nose(beautifyingProps.morph_nose_str);

        Skin.softening(beautifyingProps.skin_soft_str);
        Softlight.strength(beautifyingProps.softlight_alpha);
        Teeth.whitening(beautifyingProps.teeth_whitening_str);
    } else {
        FaceMorph.face(value);
        FaceMorph.nose(value);

        Skin.softening(value);
        Softlight.strength(value);
        Teeth.whitening(value);
    }
}

function test() {
    Background.blur(6);
    /* base beauty */
    Softlight.strength(1);
    Skin.softening(1);
    Makeup.lashes("0 0 1 1")
    Teeth.whitening(1);
    /* advance beauty */
    Lips.matt("0.39 0.14 0.99 0.8");
    Hair.color("0.8 0.14 0.99 0.8");
    Eyes.color("0.0 1.0 0.99 0.8");
}

// test()
