package loops

import android.content.res.AssetManager
import android.media.AudioManager
import java.io.IOException

class LoopsEngine(val assetMgr: AssetManager, val audioMgr: AudioManager) {

    companion object {
        init {
            System.loadLibrary("LoopsEngine")
        }
    }

    fun setupAudioStream() {
        val sampleRateStr = audioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        val defaultSampleRate = sampleRateStr.toInt()
        val framesPerBurstStr =
            audioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        val defaultFramesPerBurst = framesPerBurstStr.toInt()
        setupAudioStreamNative(2, defaultSampleRate, defaultFramesPerBurst)
    }

    fun startAudioStream() {
        startAudioStreamNative()
    }

    fun teardownAudioStream() {
        teardownAudioStreamNative()
    }
//    fun loadWavAssets(): Boolean {
//        var allAssetsCorrect = true
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBand/115_G_Pad_1.wav"
//        ) && allAssetsCorrect
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBand/115_G_Pad_2.wav"
//        ) && allAssetsCorrect
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBand/115_G_Pad_3.wav"
//        ) && allAssetsCorrect
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBand/115_G_Bass_1.wav"
//        ) && allAssetsCorrect
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBand/115_G_Bass_1.wav"
//        ) && allAssetsCorrect
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBand/115_G_Bass_3.wav"
//        ) && allAssetsCorrect
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBandMono/115_G_Vocal_1.wav"
//        ) && allAssetsCorrect
//        allAssetsCorrect = loadWavAsset(
//            assetMgr,
//            "SpaceGypsiesBand/115_G_Vocal_2.wav"
//        ) && allAssetsCorrect
//
//        return allAssetsCorrect
//    }

    fun loadWavAssets(): Boolean {
        var allAssetsCorrect = true
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand Drums 1.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand Drums 2.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand Pad 1.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand Pad 2.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand Pad 3.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand PERC 1.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand PERC 2.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpacePiretsBand/SpacePiretsBand PERC 3.wav"
        ) && allAssetsCorrect

        return allAssetsCorrect
    }

    fun unloadWavAssets() {
        unloadWavAssetsNative()
    }

    private fun loadWavAsset(
        assetMgr: AssetManager,
        assetName: String,
    ): Boolean {
        var returnVal = false
        try {
            returnVal = loadWavAssetNative(assetMgr, assetName)
        } catch (_: IOException) {
        }

        return returnVal
    }

    private external fun setupAudioStreamNative(
        numChannels: Int,
        sampleRate: Int,
        framesPerBurst: Int
    )

    private external fun startAudioStreamNative()
    private external fun teardownAudioStreamNative()

    private external fun loadWavAssetNative(assetManager: AssetManager, fileName: String): Boolean

    private external fun unloadWavAssetsNative()

    external fun getFileNameForIndex(index: Int): String

    external fun trigger(drumIndex: Int)
    external fun stopTrigger(drumIndex: Int)

    external fun setPan(index: Int, pan: Float)
    external fun getPan(index: Int): Float

    external fun setGain(index: Int, gain: Float)
    external fun getGain(index: Int): Float

    external fun getOutputReset(): Boolean
    external fun clearOutputReset()

    external fun restartStream()

    external fun getCurrentFrameForIndex(index: Int): Int
    external fun getMaxFramesForIndex(index: Int): Int

    external fun getCurrentMasterIndex(): Int
    external fun getMasterFrame(): Int
}