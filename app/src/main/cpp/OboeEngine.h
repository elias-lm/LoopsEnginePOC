//
// Created by Elias Livschitz on 21/02/2023.
//

#ifndef BASECOMPOSEMULTIPLATFORM_OBOEENGINE_H
#define BASECOMPOSEMULTIPLATFORM_OBOEENGINE_H

#include <oboe/Oboe.h>
#include "MyDataCallback.h"

class OboeEngine {

public:
    OboeEngine();

    oboe::Result start();

    oboe::Result stop();

    void setFile(char i);

private:
    oboe::Result openPlaybackStream();

    std::shared_ptr<oboe::AudioStream> mStream;
    std::shared_ptr<MyDataCallback> mDataCallback;
    std::shared_ptr<oboe::AudioStreamErrorCallback> mErrorCallback;

    int32_t mDeviceId = oboe::Unspecified;
    int32_t mChannelCount = oboe::Unspecified;
    oboe::AudioApi mAudioApi = oboe::AudioApi::Unspecified;
    std::mutex mLock;
};
#endif //BASECOMPOSEMULTIPLATFORM_OBOEENGINE_H
