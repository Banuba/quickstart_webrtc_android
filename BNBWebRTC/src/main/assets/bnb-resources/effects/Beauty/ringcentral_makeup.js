'use strict';

require('./modules/scene/index.js');
const modules_faceMorph_index = require('./modules/face-morph/index.js');
const modules_hair_index = require('./modules/hair/index.js');
const modules_eyes_index = require('./modules/eyes/index.js');
const modules_lips_index = require('./modules/lips/index.js');
const modules_makeup_index = require('./modules/makeup/index.js');
const modules_skin_index = require('./modules/skin/index.js');
const modules_softlight_index = require('./modules/softlight/index.js');
const modules_teeth_index = require('./modules/teeth/index.js');
const background = require('bnb_js/background');

function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

const background__default = /*#__PURE__*/_interopDefaultLegacy(background);

bnb.log(`\n\nRingCentral Makeup API version: ${"1.0.0-bee7b008bed8189e22af7b26e64a01d1a2693f8c"}\n`);
const Skin = new modules_skin_index.Skin();
const Eyes = new modules_eyes_index.Eyes();
const Teeth = new modules_teeth_index.Teeth();
const Lips = new modules_lips_index.Lips();
const Makeup = new modules_makeup_index.Makeup();
const Hair = new modules_hair_index.Hair();
const Softlight = new modules_softlight_index.Softlight();
const FaceMorph = new modules_faceMorph_index.FaceMorph();

const m = /*#__PURE__*/Object.freeze({
	__proto__: null,
	Background: background__default['default'],
	Skin: Skin,
	Eyes: Eyes,
	Teeth: Teeth,
	Lips: Lips,
	Makeup: Makeup,
	Hair: Hair,
	Softlight: Softlight,
	FaceMorph: FaceMorph
});

exports.m = m;
