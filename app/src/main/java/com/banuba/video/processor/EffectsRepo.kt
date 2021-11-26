package com.banuba.video.processor

import android.content.Context
import android.util.Log
import java.io.File

object EffectsRepo {
    fun loadEffects(context: Context): List<EffectInfo> {
        val effectInfoList = mutableListOf<EffectInfo>()
        val vbgDirPath = context.filesDir.path + "/banuba/bnb-resources/vbg/images"
        val vbgFiles = File(vbgDirPath)
        effectInfoList.add(EffectInfo("Beauty", EffectType.OFF, "", ""))
        effectInfoList.add(EffectInfo("Beauty", EffectType.Blur, "", ""))
        effectInfoList.add(EffectInfo("Beauty", EffectType.MP4, "", ""))
        effectInfoList.add(EffectInfo("Beauty", EffectType.GIF, "", ""))
        effectInfoList.add(EffectInfo("Beauty", EffectType.Select, "", ""))
        effectInfoList.add(EffectInfo("Beauty", EffectType.Makeup, "", ""))
        vbgFiles.listFiles()?.let {
            for (file in vbgFiles.listFiles().filter { !it.name.contains("thumbnail") }) {
                Log.e("wqs", file.absolutePath)
                effectInfoList.add(
                    EffectInfo(
                        "Beauty",
                        EffectType.VBG,
                        file.absolutePath,
                        file.absolutePath
                    )
                )
            }
        }

        return effectInfoList
    }
}

data class EffectInfo(
    val effectName: String,
    val type: EffectType,
    val filePath: String,
    val iconPath: String
)

enum class EffectType {
    OFF, Blur, VBG, MP4, GIF, Select, Makeup
}