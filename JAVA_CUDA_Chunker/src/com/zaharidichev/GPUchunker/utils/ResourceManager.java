package com.zaharidichev.GPUchunker.utils;

import com.zaharidichev.GPUchunker.types.Chunk;

/**
 * This class contains a collection of static functions that are used to
 * determine key values in the chunking systems. Examples are the length of the
 * hash array, the length of the breakpoints array and so on.
 * 
 * @author Zahari Dichev <zaharidichev@gmail.com>
 * 
 */

public class ResourceManager {

	public static final int SIZE_OF_HASH = 20; // for now the size of the hash
												// value is 20 bytes

	/**
	 * Retrieves the length of the hash in bytes
	 * 
	 * @return {@link Integer}
	 */
	public static int getSizeOfHash() {
		return SIZE_OF_HASH;
	}

	/**
	 * Given a size of a file and the size of the GPU buffer available on the
	 * CUDA enabled device, this function retrieves the number of batches that
	 * need to be dispatched (the number of kernel invocations ) in order to
	 * fingerprint the whole file
	 * 
	 * @param sizeOfFile
	 *            the size of the file
	 * @param sizeOfGPUBuffer
	 *            the size of the GPU buffer
	 * @return {@link Integer} the numebr of batches that are needed
	 */
	public static int getNumberOfBatches(long sizeOfFile, long sizeOfGPUBuffer) {

		int numBatches = (int) (sizeOfFile / sizeOfGPUBuffer);

		return (sizeOfFile % sizeOfGPUBuffer != 0) ? ++numBatches : numBatches;

	}

	/**
	 * Given a minimum size of a chunk, this function calculates the length for
	 * the array of breakpoints that is needed to hold the results.
	 * 
	 * @param dataLn
	 *            the length of the data for a particular batch
	 * @param minThreshold
	 *            the minimum threshold that indicated the minimum size of a
	 *            {@link Chunk}
	 * @return the length of the array of breakpoints needed
	 */
	public static int getSizeOfBreakpointsArray(int dataLn, int minThreshold) {

		// System.out.println(dataLn + " " + minThreshold);
		return (dataLn % minThreshold == 0) ? (dataLn / minThreshold) + 1
				: (dataLn / minThreshold) + 2;

		/*
		 * return (dataLn % minThreshold == 0) ? dataLn / minThreshold : (dataLn
		 * / minThreshold) + 1;
		 */

	}

	/**
	 * Given the length of the breakpoints, and therefore the maximum number of
	 * chunks that can be produced in a batch, this function calculates the size
	 * of the array that is needed to hold the hashes of those chunks
	 * 
	 * @param sizeOfBpArraythe
	 *            size of the breakpoints array
	 * @return the length of the array of bytes that will be needed to hold the
	 *         hashes of the chunks
	 */
	public static int getSizeOfHashArray(int sizeOfBpArray) {
		return (SIZE_OF_HASH * sizeOfBpArray);
	}

	/**
	 * GIven the buffer size and the current batch, this function calculates the
	 * offset of every chunk relative to the file. This is needed since the cuda
	 * kernel is deigned to work with absolute values of positions within the
	 * data, without considering their relative placement in the file. This is
	 * needed for efficiency purposes.
	 * 
	 * @param bufferSize
	 *            the size of the buffer on the GPU device
	 * @param currentBatch
	 *            the current iteration of the fingerpritngin process
	 * @return the offset into the file
	 */
	public static long getOffsetIntoFile(int bufferSize, int currentBatch) {
		return bufferSize * currentBatch;
	}

	/**
	 * This function calculates the length of the current batch. It is useful
	 * when making multiple invocations of a kernel in order to fingeprint a
	 * large file whose size is not a perfect multiple of the size of the GPU
	 * buffer. In this case the last batch will be of a size that is the size of
	 * the file % the size of the GPUbuffer.
	 * 
	 * @param itteration
	 *            the current iteration
	 * @param bufferSize
	 *            the size of the buffer
	 * @param numBatches
	 *            the number of batches needed to fingeprrint the whole file
	 * @param dataLn
	 *            the length of the whole file
	 * @return the size of the current batch that needs to be dispatched
	 */
	public static int getCurrentLengthOfBatch(int itteration, int bufferSize,
			int numBatches, long dataLn) {
		int lengthOfBatch = bufferSize;
		if (itteration == numBatches - 1 && dataLn % bufferSize != 0) {
			lengthOfBatch = (int) (dataLn % bufferSize);
		}
		return lengthOfBatch;

	}

	/**
	 * SImple function that trims an array of bytes to a smaller size.
	 * Effectively this function causes data truncation and discards all the
	 * data after the specified size.
	 * 
	 * @param arrayToTrim
	 *            the array the needs to be trimmer
	 * @param length
	 *            the length to which it needs to be trimmer
	 * @return the new array of bytes
	 */
	public static byte[] trimArray(byte[] arrayToTrim, int length) {
		byte[] result = new byte[length];
		System.arraycopy(arrayToTrim, 0, result, 0, length);
		return result;

	}

}
