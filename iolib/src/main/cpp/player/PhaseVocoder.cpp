#include <iostream>
#include <cmath>
#include <cstring>
#include "PhaseVocoder.h"

#define PI 3.14159265358979323846

PhaseVocoder::PhaseVocoder(int fftSize, float hopFactor) {
    mFftSize = fftSize;
    mHopFactor = hopFactor;
    mHopSize = (int) floor(mFftSize * mHopFactor);

    mHann = new float[mFftSize];
    for (int i = 0; i < mFftSize; i++) {
        mHann[i] = 0.5 * (1 - cos(2.0 * PI * i / (mFftSize - 1)));
    }

    mPhase = new float[mFftSize];
    mMagnitude = new float[mFftSize];
    mPhaseIncrement = new float[mFftSize];
    mDeltaPhase = new float[mFftSize];
    memset(mPhase, 0, mFftSize * sizeof(float));
    memset(mMagnitude, 0, mFftSize * sizeof(float));
}

PhaseVocoder::~PhaseVocoder() {
    delete[] mHann;
    delete[] mPhase;
    delete[] mMagnitude;
    delete[] mPhaseIncrement;
    delete[] mDeltaPhase;
}

void PhaseVocoder::process(float *input, float *output, int numSamples, float rate) {

}

void PhaseVocoder::realFFT(float *input, float *outputMagnitude, float *outputPhase) {
    float *temp = new float[2 * mFftSize];
    for (int i = 0; i < mFftSize; i++) {
        temp[2 * i] = input[i];
        temp[2 * i + 1] = 0.0;
    }
    computeFFT(temp);
    for (int i = 0; i < mFftSize / 2; i++) {
        outputMagnitude[i] = sqrt(temp[2 * i] * temp[2 * i] + temp[2 * i + 1] * temp[2 * i + 1]);
        outputPhase[i] = atan2(temp[2 * i + 1], temp[2 * i]);
    }
    delete[] temp;
}

void PhaseVocoder::inverseRealFFT(float *inputMagnitude, float *inputPhase, float *output) {
    float *temp = new float[2 * mFftSize];
    memset(temp, 0, 2 * mFftSize * sizeof(float));
    for (int i = 0; i < mFftSize / 2; i++) {
        temp[2 * i] = inputMagnitude[i] * cos(inputPhase[i]);
        temp[2 * i + 1] = inputMagnitude[i] * sin(inputPhase[i]);
        temp[2 * (mFftSize - i - 1)] = temp[2 * i];
        temp[2 * (mFftSize - i - 1) + 1] = -temp[2 * i + 1];
    }
    computeInverseFFT(temp);
    for (int i = 0; i < mFftSize; i++) {
        output[i] = temp[2 * i] / mFftSize;
    }
    delete[] temp;
}

void PhaseVocoder::computeFFT(float *data) {
    for (int i = 0, j = 0; i < mFftSize; i++) {
        if (j > i) {
            std::swap(data[2 * i], data[2 * j]);
            std::swap(data[2 * i + 1], data[2 * j + 1]);
        }
        int m = mFftSize >> 1;
        while (m >= 2 && j >= m) {
            j -= m;
            m >>= 1;
        }
        j += m;
    }
    for (int length = 2; length <= mFftSize; length <<= 1) {
        float angle = -2.0 * PI / length;
        float wReal = cos(angle);
        float wImaginary = sin(angle);
        for (int i = 0; i < mFftSize; i += length) {
            float wr = 1.0, wi = 0.0;
            for (int j = 0; j < length / 2; j++) {
                float re = data[2 * (i + j)];
                float im = data[2 * (i + j) + 1];
                float tempRe = wr * data[2 * (i + j + length / 2)] -
                               wi * data[2 * (i + j + length / 2) + 1];
                float tempIm = wr * data[2 * (i + j + length / 2) + 1] +
                               wi * data[2 * (i + j + length / 2)];
                data[2 * (i + j)] = re + tempRe;
                data[2 * (i + j) + 1] = im + tempIm;
                float temp = wr * wReal - wi * wImaginary;
                wi = wr * wImaginary + wi * wReal;
                wr = temp;
            }
        }
    }
}

void PhaseVocoder::computeInverseFFT(float *data) {
    std::reverse(data + 1, data + 2 * mFftSize);
    computeFFT(data);
    for (int i = 0; i < mFftSize; i++) {
        data[2 * i] /= mFftSize;
        data[2 * i + 1] /= mFftSize;
    }
}
