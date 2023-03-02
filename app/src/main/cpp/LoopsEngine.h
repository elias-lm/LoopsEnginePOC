//
// Created by Elias Livschitz on 27/02/2023.
//

#ifndef LOOPSENGINEPOC_LOOPSENGINE_H
#define LOOPSENGINEPOC_LOOPSENGINE_H

extern "C" {

JNIEXPORT void JNICALL
Java_loops_LoopsEngine_startEngine(JNIEnv *env, jobject thiz, jobject jAssetManager,
                                   jstring jWavFileName);
JNIEXPORT void JNICALL
Java_loops_LoopsEngine_play(JNIEnv *env, jobject thiz);

}


void MyPrintFunc(const char *str);

#endif //LOOPSENGINEPOC_LOOPSENGINE_H