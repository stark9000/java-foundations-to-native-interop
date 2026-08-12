/*
 * Implements every native method declared across NativeMath.java and
 * NativeSignalGenerator.java. One shared library can back native
 * methods from any number of Java classes - the generated function
 * name (Java_<Class>_<method>) is what ties each C function to its
 * specific Java declaration, not which .c file it happens to live in.
 */
#include <jni.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

#include "NativeMath.h"
#include "NativeSignalGenerator.h"

/* ---------- NativeMath.add : the simplest possible native method ---------- */

JNIEXPORT jint JNICALL Java_NativeMath_add
  (JNIEnv *env, jclass clazz, jint a, jint b) {
    /* No JNIEnv calls needed at all here - primitives pass by value,
       exactly like any ordinary C function call. */
    return a + b;
}

/* ---------- NativeMath.rms : arrays cross the boundary by COPY (usually) ---------- */

JNIEXPORT jdouble JNICALL Java_NativeMath_rms
  (JNIEnv *env, jclass clazz, jdoubleArray samples) {

    jsize length = (*env)->GetArrayLength(env, samples);
    if (length == 0) {
        return 0.0;
    }

    /* GetDoubleArrayElements may return a direct pointer into the JVM's
       actual array storage, OR a freshly-allocated copy - the JVM
       decides, and native code is never told which. Either way, the
       pointer is only valid until ReleaseDoubleArrayElements is called. */
    jdouble *elements = (*env)->GetDoubleArrayElements(env, samples, NULL);
    if (elements == NULL) {
        return 0.0; /* allocation failed - GetDoubleArrayElements already threw OutOfMemoryError */
    }

    double sumOfSquares = 0.0;
    for (jsize i = 0; i < length; i++) {
        sumOfSquares += elements[i] * elements[i];
    }

    /* JNI_ABORT: we only READ elements, never wrote to them, so there's
       nothing to copy back - JNI_ABORT tells the JVM to skip that copy-back
       step entirely (a real, measurable difference for large arrays). */
    (*env)->ReleaseDoubleArrayElements(env, samples, elements, JNI_ABORT);

    return sqrt(sumOfSquares / length);
}

/* ---------- NativeMath.greet : jstring is opaque - never a raw char* ---------- */

JNIEXPORT jstring JNICALL Java_NativeMath_greet
  (JNIEnv *env, jclass clazz, jstring name) {

    /* A jstring cannot be read directly - Java Strings are UTF-16
       internally, not null-terminated C strings. GetStringUTFChars
       converts to a modified-UTF-8 C string for us to actually use. */
    const char *nameChars = (*env)->GetStringUTFChars(env, name, NULL);
    if (nameChars == NULL) {
        return NULL; /* GetStringUTFChars already threw OutOfMemoryError */
    }

    char buffer[256];
    snprintf(buffer, sizeof(buffer), "Hello from native code, %s!", nameChars);

    /* MUST release what GetStringUTFChars gave us - this is native
       memory the JVM allocated on our behalf, not garbage-collected. */
    (*env)->ReleaseStringUTFChars(env, name, nameChars);

    /* NewStringUTF allocates a brand new Java String from our C buffer -
       the return trip, going the opposite direction. */
    return (*env)->NewStringUTF(env, buffer);
}

/* ---------- NativeMath.divide : throwing a Java exception FROM native code ---------- */

JNIEXPORT jint JNICALL Java_NativeMath_divide
  (JNIEnv *env, jclass clazz, jint a, jint b) {

    if (b == 0) {
        /* ThrowNew queues an exception on the CURRENT thread - it does
           NOT immediately transfer control like a C++ throw would.
           Execution continues to the next line, so returning a real
           value (even a meaningless one) afterward is required. */
        jclass exceptionClass = (*env)->FindClass(env, "java/lang/ArithmeticException");
        (*env)->ThrowNew(env, exceptionClass, "division by zero (thrown from native code)");
        return 0; /* never actually seen by Java - the pending exception fires first */
    }

    return a / b;
}

/* ---------- NativeMath.processWithProgress : calling BACK into Java ---------- */

JNIEXPORT void JNICALL Java_NativeMath_processWithProgress
  (JNIEnv *env, jclass clazz, jdoubleArray samples, jobject callback) {

    jsize length = (*env)->GetArrayLength(env, samples);
    jdouble *elements = (*env)->GetDoubleArrayElements(env, samples, NULL);
    if (elements == NULL) {
        return;
    }

    /* To call a method on a Java object from native code, we need its
       Class, then the specific method's ID, looked up by name AND JNI
       type signature - "(ID)V" means "takes an int and a double,
       returns void," matching ProgressCallback.onProgress exactly. */
    jclass callbackClass = (*env)->GetObjectClass(env, callback);
    jmethodID onProgress = (*env)->GetMethodID(env, callbackClass, "onProgress", "(ID)V");

    if (onProgress != NULL) {
        for (jsize i = 0; i < length; i++) {
            /* This is a real call INTO Java bytecode, from C, mid-loop -
               executing entirely inside this single native method call. */
            (*env)->CallVoidMethod(env, callback, onProgress, (jint) i, (jdouble) elements[i]);
        }
    }

    (*env)->ReleaseDoubleArrayElements(env, samples, elements, JNI_ABORT);
}

/* ---------- NativeSignalGenerator.currentSystemLoad : a real OS-level reading ---------- */

JNIEXPORT jdouble JNICALL Java_NativeSignalGenerator_currentSystemLoad
  (JNIEnv *env, jclass clazz) {

    double loadAverages[1];

    /* getloadavg() is a POSIX libc call - available on Linux and macOS,
       not on Windows. A Windows implementation of this exact Java method
       would call a completely different native API (e.g. reading
       performance counters via PDH, or GetSystemTimes for CPU usage) -
       the JNI mechanics either side of this function stay identical;
       only this platform-specific body changes. */
    if (getloadavg(loadAverages, 1) == -1) {
        return -1.0; /* couldn't read load average - caller treats negative as "unavailable" */
    }

    return (jdouble) loadAverages[0];
}
