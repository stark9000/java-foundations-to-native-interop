/*
 * This app only uses ONE native method - Module 9 covered the full
 * range of JNI mechanics (arrays, Strings, exceptions, callbacks) using
 * a separate NativeMath class that isn't part of this packaged app at
 * all. This file is trimmed down to exactly what's actually shipped,
 * on purpose: a real deployment bundles the native code it uses, not
 * a teaching library's full surface area.
 */
#include <jni.h>
#include <stdlib.h>

#include "NativeSignalGenerator.h"

JNIEXPORT jdouble JNICALL Java_NativeSignalGenerator_currentSystemLoad
  (JNIEnv *env, jclass clazz) {

    double loadAverages[1];

    if (getloadavg(loadAverages, 1) == -1) {
        return -1.0;
    }

    return (jdouble) loadAverages[0];
}
