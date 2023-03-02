//
// Created by Elias Livschitz on 21/02/2023.
//

#include "OboeEngine.h"
#include "MyDataCallback.h"

OboeEngine::OboeEngine() : mDataCallback(std::make_shared<MyDataCallback>()) {
}

oboe::Result OboeEngine::start() {
    std::lock_guard<std::mutex> lock(mLock);
    oboe::Result result = oboe::Result::OK;
    int tryCount = 0;
    do {
        if (tryCount > 0) { usleep(20 * 1000); }
        result = openPlaybackStream();
        if (result == oboe::Result::OK) {
            result = mStream->requestStart();
            if (result != oboe::Result::OK) {
                mStream->close();
                mStream.reset();
            }
        }
    } while (result != oboe::Result::OK && tryCount++ < 3);
    return result;
}

oboe::Result OboeEngine::stop() {
    oboe::Result result = oboe::Result::OK;
    std::lock_guard<std::mutex> lock(mLock);
    if (mStream) {
        result = mStream->stop();
        mStream->close();
        mStream.reset();
    }
    return result;
}

oboe::Result OboeEngine::openPlaybackStream() {
    oboe::AudioStreamBuilder builder;
    oboe::Result result = builder.setSharingMode(oboe::SharingMode::Exclusive)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setFormat(oboe::AudioFormat::Float)
            ->setFormatConversionAllowed(true)
            ->setDataCallback(mDataCallback)
            ->setErrorCallback(mErrorCallback)
            ->setAudioApi(mAudioApi)
            ->setChannelCount(mChannelCount)
            ->setDeviceId(mDeviceId)
            ->openStream(mStream);
    if (result == oboe::Result::OK) {
        mChannelCount = mStream->getChannelCount();
    }
    return result;
}

void OboeEngine::setFile(char i) {

}
