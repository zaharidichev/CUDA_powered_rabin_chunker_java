/* DO NOT EDIT THIS FILE - it is machine generated */

#include <stdio.h>
#include "cudaLinker_CudaLink.h"
/* Header for class Main */



JNIEXPORT jint JNICALL Java_com_zaharidichev_GPUchunker_CUDAhandle_CudaLink_createChunksOnGPU (JNIEnv *env, jobject obj, jint sizeOfData, jbyteArray dataToChunk, jintArray breakPoints, jint sizeOfBreakPoints, jbyteArray hashesToCompute, jint sizeOfHashes, jint min, jint max, jint prim, jint sec) {


    //printf("%d, %d\n", prim, sec);
    // we need to extract the arguments  to JNI datatypes
    jbyte *data = (*env)->GetByteArrayElements(env, dataToChunk, 0); 
    jbyte *hashes = (*env)->GetByteArrayElements(env, hashesToCompute, 0);
    jint *breakpoints = (*env)->GetIntArrayElements(env, breakPoints, 0);

    //call kernel
    findBreakPoints(sizeOfData, data, breakpoints,sizeOfBreakPoints, hashes, sizeOfHashes,max,min,prim,sec);


    // now all we need is the results from that, that we pump back into the JVM
    (*env)->ReleaseIntArrayElements(env, breakPoints, breakpoints, 0);
    (*env)->ReleaseByteArrayElements(env, hashesToCompute, hashes, 0);
    //printf("Going back\n");
    return 1;

}



JNIEXPORT jint JNICALL Java_com_zaharidichev_GPUchunker_CUDAhandle_CudaLink_getSizeOfGPUBuffer (JNIEnv *env, jobject obj) { 
    //simply call the routine provided into the combiled library to find out the buffer size 
    jint sizeOfBuffer = getBufferSize();
    return sizeOfBuffer;
}
