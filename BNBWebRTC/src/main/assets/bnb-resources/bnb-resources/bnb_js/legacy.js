
/**
 * Legacy Banuba SDK scripting support. 
 * https://docs.banuba.com/face-ar-sdk/effect_constructor/reference/config_js
 */

var getFRXVersion = bnb.RenderInfo.getFrxVersion
var getPlatform = bnb.RenderInfo.getPlatform

var meshfxApi = bnb.meshfxApi

function getCurrentTimeNs()
{
    return meshfxApi.getCurrentTimeNs()
}

function print(msg)
{
    return meshfxApi.print(msg)
}

function meshfxMsg(tag, instanceId, index, param)
{
    return meshfxApi.meshfxMsg(tag, instanceId, index, param)
}

function meshfxReset()
{
    return meshfxApi.meshfxReset()
}

function playVideo(layerId, isLooped, speedFactor)
{
    return meshfxApi.playVideo(layerId, isLooped, speedFactor)
}


function playVideoRange(layerId, startTime, stopTime, isLooped, speedFactor)
{
    return meshfxApi.playVideoRange(layerId, startTime, stopTime, isLooped, speedFactor)
}

function pauseVideo(layerId)
{
    return meshfxApi.pauseVideo(layerId)
}

function stopVideo(layerId)
{
    return meshfxApi.stopVideo(layerId)
}

function seekVideo(layerId, time)
{
    // unimplemented
}

function getCurrentVideoTime(layerId)
{
    // unimplemented
    return 0
}

function getVideoDuration(layerId)
{
    // unimplemented
    return 0
}

function setVideoVolume(layerId, volume)
{
    // unimplemented
}

function getVideoVolume(layerId)
{
    return 0.0
}

function recordStart(maximumDuration)
{
    // unimplemented
}

function recordStop()
{
    // unimplemented
}

function playSound(filename, isLooped, speedFactor)
{
    return meshfxApi.playSound(filename, isLooped, speedFactor)
}

function playSoundRange(filename, startTime, stopTime, isLooped, speedFactor)
{
    return meshfxApi.playSoundRange(filename, startTime, stopTime, speedFactor)
}

function stopSound(filename)
{
    return meshfxApi.stopSound(filename)
}

function pauseSound(filename)
{
    return meshfxApi.pauseSound(filename)
}

function seekSound(filename, time)
{
    // unimplemented
}

function getCurrentSoundTime(filename)
{
    // unimplemented
    return 0
}

function getSoundDuration(filename)
{
    return 0
}

function setSoundVolume(filename, volume) {
    meshfxApi.setSoundVolume(filename, volume)
}

function showRecordButton()
{
    // unimplemented
}

function hideRecordButton()
{
    // umimplemented
}

function showHint(hint)
{
    // unimplemented
}

function hideHint()
{
    // unimplemented
}

function drawingAreaWidth()
{
    return meshfxApi.drawingAreaWidth()
}

function drawingAreaHeight()
{
    return meshfxApi.drawingAreaHeight()
}


function visibleAreaWidth()
{
    return meshfxApi.visibleAreaWidth()
}


function visibleAreaHeight()
{
    return meshfxApi.visibleAreaHeight()
}


function effectEvent(name, params)
{
    return meshfxApi.effectEvent(name, params)
}

function setRecognizerFeatures(features)
{
    return meshfxApi.setRecognizerFeatures(features)
}

function isMouthOpen()
{
    return meshfxApi.isMouthOpen()    
}

function isSmile()
{
    return meshfxApi.isSmile()
}

function isEyebrowUp()
{
    return meshfxApi.isEyebrowUp()
}

function isDisgust()
{
    return meshfxApi.isEyebrowUp()
}

function getEyesStatus()
{
    return meshfxApi.getEyesStatus()
}


function getNamedState(name)
{
    return meshfxApi.getNamedState(name)
}


function isMirrored()
{
    return meshfxApi.isMirrored()
}


function getRotationVector()
{
    return meshfxApi.getRotationVector()
}

function modelview()
{
    return meshfxApi.modelview()
}

exports = {
    getFRXVersion,
    getPlatform,
    getCurrentTimeNs,
    print,
    meshfxMsg,
    meshfxReset,
    playVideo,
    playVideoRange,
    pauseVideo,
    stopVideo,
    seekVideo,
    getCurrentVideoTime,
    getVideoDuration,
    setVideoVolume,
    getVideoVolume,
    recordStart,
    recordStop,
    playSound,
    playSoundRange,
    stopSound,
    pauseSound,
    seekSound,
    getCurrentSoundTime,
    getSoundDuration,
    setSoundVolume,
    showRecordButton,
    hideRecordButton,
    showHint,
    hideHint,
    drawingAreaWidth,
    drawingAreaHeight,
    visibleAreaWidth,
    visibleAreaHeight,
    effectEvent,
    setRecognizerFeatures,
    isMouthOpen,
    isSmile,
    isEyebrowUp,
    isDisgust,
    getEyesStatus,
    getNamedState,
    isMirrored,
    getRotationVector,
    modelview
}
