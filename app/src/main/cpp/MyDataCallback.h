//
// Created by Elias Livschitz on 21/02/2023.
//

#ifndef BASECOMPOSEMULTIPLATFORM_MYDATACALLBACK_H
#define BASECOMPOSEMULTIPLATFORM_MYDATACALLBACK_H
#include <oboe/Oboe.h>
#include <oboe/AudioStreamCallback.h>


class MyDataCallback : public oboe::AudioStreamDataCallback {
    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override;
};

#endif //BASECOMPOSEMULTIPLATFORM_MYDATACALLBACK_H
