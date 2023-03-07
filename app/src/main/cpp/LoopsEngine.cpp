/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include <android/log.h>

// parselib includes
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>

#include <player/OneShotSampleSource.h>
#include <player/SimpleMultiPlayer.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <list>

static const char *TAG = "DrumPlayerJNI";

// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

using namespace iolib;
using namespace parselib;

static SimpleMultiPlayer sDTPlayer;
static std::string sources[8];
static int sourcesIndex = 0;
/**
 * Native (JNI) implementation of DrumPlayer.setupAudioStreamNative()
 */
JNIEXPORT void JNICALL Java_loops_LoopsEngine_setupAudioStreamNative(
        JNIEnv *env, jobject, jint numChannels, jint sampleRate, jint framesPerBurst) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "init()");

    // we know in this case that the sample buffers are all 1-channel, 41K
    sDTPlayer.setupAudioStream(numChannels, sampleRate, framesPerBurst);
}

JNIEXPORT void JNICALL
Java_loops_LoopsEngine_startAudioStreamNative(
        JNIEnv *env, jobject thiz) {
    sDTPlayer.startStream();
}

/**
 * Native (JNI) implementation of DrumPlayer.teardownAudioStreamNative()
 */
JNIEXPORT void JNICALL
Java_loops_LoopsEngine_teardownAudioStreamNative(JNIEnv *, jobject) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "deinit()");

    // we know in this case that the sample buffers are all 1-channel, 44.1K
    sDTPlayer.teardownAudioStream();
}

/**
 * Native (JNI) implementation of DrumPlayer.allocSampleDataNative()
 */
/**
 * Native (JNI) implementation of DrumPlayer.loadWavAssetNative()
 */
JNIEXPORT jboolean JNICALL
Java_loops_LoopsEngine_loadWavAssetNative(JNIEnv *env, jobject thiz,
                                          jobject jAssetManager,
                                          jstring jWavFileName) {

    const char *wavFileName = env->GetStringUTFChars(jWavFileName, nullptr);
    AAssetManager *assetManager = AAssetManager_fromJava(env, jAssetManager);
    AAsset *asset = AAssetManager_open(assetManager, wavFileName, AASSET_MODE_BUFFER);

    size_t bufferSize = AAsset_getLength(asset);
    auto *buffer = new unsigned char[bufferSize + 1];
    size_t readBytes = AAsset_read(asset, buffer, bufferSize);
    buffer[readBytes + 1] = 0;

//    parselib::MemInputStream stream(buffer, readBytes);
    auto *stream = new parselib::MemInputStream(buffer, readBytes);

//    parselib::WavStreamReader reader(&stream);
    auto *reader = new parselib::WavStreamReader(stream);
    reader->parse();
    jboolean isFormatValid = reader->getNumChannels() == 1;

    auto *sampleBuffer = new iolib::SampleBuffer();
    sampleBuffer->loadSampleData(reader);
//    sampleBuffer->changeBPM(0.4f);
    auto *source = new iolib::OneShotSampleSource(sampleBuffer, 0);

    sDTPlayer.addSampleSource(source, sampleBuffer);
    delete[] buffer;
    AAsset_close(asset);

    sources[sourcesIndex++] = wavFileName;

    return isFormatValid;
}

JNIEXPORT jstring JNICALL
Java_loops_LoopsEngine_getFileNameForIndex(JNIEnv *env, jobject obj, jint index) {
    return env->NewStringUTF(sources[index].c_str());
}

/**
 * Native (JNI) implementation of DrumPlayer.unloadWavAssetsNative()
 */
JNIEXPORT void JNICALL
Java_loops_LoopsEngine_unloadWavAssetsNative(JNIEnv *env, jobject) {
    sDTPlayer.unloadSampleData();
}

/**
 * Native (JNI) implementation of DrumPlayer.trigger()
 */
JNIEXPORT void JNICALL
Java_loops_LoopsEngine_trigger(JNIEnv *env, jobject, jint index) {
    sDTPlayer.triggerDown(index);
}

/**
 * Native (JNI) implementation of DrumPlayer.trigger()
 */
JNIEXPORT void JNICALL
Java_loops_LoopsEngine_stopTrigger(JNIEnv *env, jobject, jint index) {
    sDTPlayer.triggerUp(index);
}

/**
 * Native (JNI) implementation of DrumPlayer.getOutputReset()
 */
JNIEXPORT jboolean JNICALL
Java_loops_LoopsEngine_getOutputReset(JNIEnv *, jobject) {
    return sDTPlayer.getOutputReset();
}

/**
 * Native (JNI) implementation of DrumPlayer.clearOutputReset()
 */
JNIEXPORT void JNICALL
Java_loops_LoopsEngine_clearOutputReset(JNIEnv *, jobject) {
    sDTPlayer.clearOutputReset();
}

/**
 * Native (JNI) implementation of DrumPlayer.restartStream()
 */
JNIEXPORT void JNICALL
Java_loops_LoopsEngine_restartStream(JNIEnv *, jobject) {
    sDTPlayer.resetAll();
    if (sDTPlayer.openStream() && sDTPlayer.startStream()) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "openStream successful");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "openStream failed");
    }
}

JNIEXPORT void JNICALL Java_loops_LoopsEngine_setPan(
        JNIEnv *env, jobject thiz, jint index, jfloat pan) {
    sDTPlayer.setPan(index, pan);
}

JNIEXPORT jfloat JNICALL Java_loops_LoopsEngine_getPan(
        JNIEnv *env, jobject thiz, jint index) {
    return sDTPlayer.getPan(index);
}

JNIEXPORT void JNICALL Java_loops_LoopsEngine_setGain(
        JNIEnv *env, jobject thiz, jint index, jfloat gain) {
    sDTPlayer.setGain(index, gain);
}

JNIEXPORT jfloat JNICALL Java_loops_LoopsEngine_getGain(
        JNIEnv *env, jobject thiz, jint index) {
    return sDTPlayer.getGain(index);
}

JNIEXPORT int JNICALL Java_loops_LoopsEngine_getCurrentFrameForIndex(
        JNIEnv *env, jobject thiz, jint index) {
    return sDTPlayer.getCurrentFrameForIndex(index);
}

JNIEXPORT jint JNICALL
Java_loops_LoopsEngine_getMaxFramesForIndex(JNIEnv *env, jobject thiz, jint index) {
    return sDTPlayer.getMaxFramesForIndex(index);
}

JNIEXPORT jint JNICALL
Java_loops_LoopsEngine_getCurrentMasterIndex(JNIEnv *env, jobject thiz) {
    return sDTPlayer.masterIndex;
}

JNIEXPORT jint JNICALL
Java_loops_LoopsEngine_getMasterFrame(JNIEnv *env, jobject thiz) {
    return sDTPlayer.masterFrame;
}


#ifdef __cplusplus
}
#endif


void MyPrintFunc(const char *str) {
    char *newStr = new char[strlen(str) + 1];
    strcpy(newStr, str);

    __android_log_print(
            ANDROID_LOG_ERROR,
            "MYTAG *** ",
            "%s", newStr);
}
