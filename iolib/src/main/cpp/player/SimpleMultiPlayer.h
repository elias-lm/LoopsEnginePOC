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

#ifndef _PLAYER_SIMIPLEMULTIPLAYER_H_
#define _PLAYER_SIMIPLEMULTIPLAYER_H_

#include <vector>

#include <oboe/Oboe.h>
#include <jni.h>

#include "OneShotSampleSource.h"
#include "SampleBuffer.h"

namespace iolib {

    typedef unsigned char byte;     // an 8-bit unsigned value

/**
 * A simple streaming player for multiple SampleBuffers.
 */
    class SimpleMultiPlayer : public oboe::AudioStreamCallback {
    public:
        SimpleMultiPlayer();

        int32_t masterIndex = -1;  //index of the master sample
        int32_t masterFrame = 0;   //current frame of the master sample
        int32_t masterMaxFrames = 0; //max frames of the master sample
        std::map<int, bool> sampleState;

        // Inherited from oboe::AudioStreamCallback
        oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData,
                                              int32_t numFrames) override;

        virtual void onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error) override;

        virtual void onErrorBeforeClose(oboe::AudioStream *oboeStream, oboe::Result error) override;

        void setupAudioStream(jint channelCount, jint sampleRate, jint framesPerBurst);

        void teardownAudioStream();

        bool openStream();

        bool startStream();

        int getSampleRate() { return mSampleRate; }

        // Wave Sample Loading...
        /**
         * Adds the SampleSource/Samplebuffer pair to the list of source channels.
         * Transfers ownership of those objects so that they can be deleted/unloaded.
         * The indexes associated with each source channel is the order in which they
         * are added.
         */
        void addSampleSource(SampleSource *source, SampleBuffer *buffer);

        /**
         * Deallocates and deletes all added source/buffer (see addSampleSource()).
         */
        void unloadSampleData();

        void triggerDown(int32_t index);

        void triggerUp(int32_t index);

        void resetAll();

        bool getOutputReset() { return mOutputReset; }

        void clearOutputReset() { mOutputReset = false; }

        void setPan(int index, float pan);

        float getPan(int index);

        void setGain(int index, float gain);

        float getGain(int index);

        int getCurrentFrameForIndex(int i);

        int getMaxFramesForIndex(int i);

    private:
        // Oboe Audio Stream
        std::shared_ptr<oboe::AudioStream> mAudioStream;

        // Playback Audio attributes
        int32_t mChannelCount;
        int32_t mSampleRate;

        // Sample Data
        int32_t mNumSampleBuffers;
        std::vector<SampleBuffer *> mSampleBuffers;
        std::vector<SampleSource *> mSampleSources;

        bool mOutputReset;
    };

}
#endif //_PLAYER_SIMIPLEMULTIPLAYER_H_
