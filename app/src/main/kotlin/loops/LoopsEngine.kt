package loops

import android.content.res.AssetManager
import java.io.IOException

class LoopsEngine(val assetMgr: AssetManager) {

    companion object {
        init {
            System.loadLibrary("LoopsEngine")
        }
    }

    fun setupAudioStream() {
        setupAudioStreamNative(1)
    }

    fun startAudioStream() {
        startAudioStreamNative()
    }

    fun teardownAudioStream() {
        teardownAudioStreamNative()
    }

    fun loadWavAssets(): Boolean {
        var allAssetsCorrect = true
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBand/115_G_Bass_1.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBand/115_G_Bass_2.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBandMono/115_G_Bass_3.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBandMono/115_G_Pad_1.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBandMono/115_G_Pad_2.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBandMono/115_G_Pad_3.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBandMono/115_G_Vocal_1.wav"
        ) && allAssetsCorrect
        allAssetsCorrect = loadWavAsset(
            assetMgr,
            "SpaceGypsiesBandMono/115_G_Vocal_2.wav"
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

    private external fun setupAudioStreamNative(numChannels: Int)
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
}