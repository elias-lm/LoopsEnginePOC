//
// Created by Elias Livschitz on 21/02/2023.
//

#include "MyDataCallback.h"
#include <oboe/AudioStreamCallback.h>


oboe::DataCallbackResult
MyDataCallback::onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {
    return oboe::DataCallbackResult::Stop;
}
