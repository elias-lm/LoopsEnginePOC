//
// Created by Elias Livschitz on 05/03/2023.
//

#ifndef LOOPSENGINEPOC_PHASEVOCODER_H
#define LOOPSENGINEPOC_PHASEVOCODER_H

class PhaseVocoder {

public:
    PhaseVocoder(int fftSize = 1024, float hopFactor = 0.5);

    ~PhaseVocoder();

    void process(float *input, float *output, int numSamples, float rate);

private:
    int mFftSize;
    float mHopFactor;
    int mHopSize;
    float *mHann;
    float *mPhase;
    float *mMagnitude;
    float *mPhaseIncrement;
    float *mDeltaPhase;

    void realFFT(float *input, float *outputMagnitude, float *outputPhase);

    void inverseRealFFT(float *inputMagnitude, float *inputPhase, float *output);

    void computeFFT(float *data);

    void computeInverseFFT(float *data);
};


#endif //LOOPSENGINEPOC_PHASEVOCODER_H
