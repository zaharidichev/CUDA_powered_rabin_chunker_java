package com.zaharidichev.GPUchunker.utils;

public class ResourceManager {

	public static final int SIZE_OF_HASH = 20;

	public static int getSizeOfHash() {
		return SIZE_OF_HASH;
	}

	public static int getNumberOfBatches(long sizeOfFile, long sizeOfGPUBuffer) {

		int numBatches = (int) (sizeOfFile / sizeOfGPUBuffer);

		return (sizeOfFile % sizeOfGPUBuffer != 0) ? ++numBatches : numBatches;

	}

	public static int getSizeOfBreakpointsArray(int dataLn, int minThreshold) {
		
		//System.out.println(dataLn + " " + minThreshold);
		return (dataLn % minThreshold == 0) ? (dataLn / minThreshold) + 1: (dataLn / minThreshold) + 2;

		
/*		return (dataLn % minThreshold == 0) ? dataLn / minThreshold
				: (dataLn / minThreshold) + 1;*/

	}

	public static int getSizeOfHashArray(int sizeOfBpArray) {
		return (SIZE_OF_HASH * sizeOfBpArray);
	}

	public static long getOffsetIntoFile(int bufferSize, int currentBatch) {
		return bufferSize * currentBatch;
	}

	public static int getCurrentLengthOfBatch(int itteration, int bufferSize,
			int numBatches, long dataLn) {
		int lengthOfBatch = bufferSize;
		if (itteration == numBatches - 1 && dataLn % bufferSize != 0) {
			lengthOfBatch = (int) (dataLn % bufferSize);
		}
		return lengthOfBatch;

	}

	public static byte[] trimArray(byte[] arrayToTrim, int length) {
		byte[] result = new byte[length];
		System.arraycopy(arrayToTrim, 0, result, 0, length);
		return result;

	}

}
