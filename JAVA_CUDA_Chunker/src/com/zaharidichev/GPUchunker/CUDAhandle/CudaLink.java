package com.zaharidichev.GPUchunker.CUDAhandle;

import java.util.LinkedList;

import com.zaharidichev.GPUchunker.types.Chunk;
import com.zaharidichev.GPUchunker.types.Sha1Hash;
import com.zaharidichev.GPUchunker.utils.ResourceManager;

/**
 * This class serves as the main link to the CUDA library which is used to
 * dispatch computational kernels to the GPU. For this purpose, the class uses a
 * JNI interfaces that calls C functions through the natively declared methods.
 * The class is a singleton and upon its construction dynamically loads a .so
 * file which contains the CUDA routines.
 * 
 * @author Zahari Dichev <zaharidichev@gmail.com>
 * 
 */
public class CudaLink {
	/**
	 * 
	 * A natively declared method that corresponds to a method in the .so file.
	 * This method calls the C code responsible for dispatching the CUDA
	 * chunking Kernel. This particular methods works with C semantics. For
	 * example the byte arrays that are passes can be filled with data and will
	 * retain this data although the function does not return any of them (much
	 * like passing by reference in C/C++)
	 * 
	 * @param sizeOfData
	 *            the size of the batch that is to be dispatched
	 * @param data
	 *            the binary data that will be fingerprinted
	 * @param breakpoints
	 *            the array of breakpoints in which the breakpoints will be
	 *            stored
	 * @param sizeOfBreakpoints
	 *            the size of the array of breakpoints
	 * @param hashes
	 *            the array of hashes that will store all the hashes computed on
	 *            the GPU
	 * @param sizeOfHashes
	 *            the size of the array of hashes
	 * @param min
	 *            the minimum threshold for the size of a {@link Chunk}
	 * @param max
	 *            the maximum threshold for the size of a {@link Chunk}
	 * @param primDiv
	 *            the primary Rabin divisor
	 * @param secDiv
	 *            the secondary Rabin divisor
	 * @return
	 */
	private native int createChunksOnGPU(int sizeOfData, byte[] data,
			int[] breakpoints, int sizeOfBreakpoints, byte[] hashes,
			int sizeOfHashes, int min, int max, int primDiv, int secDiv);

	/**
	 * A method that calls a C routine that communicates directly with the
	 * NVidia driver API in order to determine the optimal size of the buffer
	 * that can be allocated on the device
	 * 
	 * @return {@link Integer} size of the buffer
	 */
	private native int getSizeOfGPUBuffer();

	private static CudaLink instance = null; // Reference to self.. needed for
												// singleton

	/**
	 * Static method which retrieves an instance of this class and call the
	 * constructor if an instance has not been created yet.
	 * 
	 * @return {@link CudaLink}
	 */
	public static CudaLink getInstance() {
		if (instance == null) {
			instance = new CudaLink();
		}
		return instance;
	}

	/**
	 * Private constructor that loads the .so library which contains the native
	 * calls to the CUDA API and the rest of the C code needed for
	 * fingerprinting and chunking. This library contains all the heavily
	 * mathematical operations as well as multiple routines for dispatching the
	 * kernels to the card and handling resources such as memory, registers,
	 * etc.
	 */
	private CudaLink() {

		String cudaLibraryPath = System.getProperty("java.class.path")
				+ "/../lib/cuda_rabin_chunker.so";

		try {
			System.load(cudaLibraryPath);
		} catch (Exception e) {
			System.out.println("Problem loding library: " + e);
		}
		System.out.println("[Loaded CUDA kernel handler]");

	}

	/**
	 * A publicly exposed method which communicates directly with the private
	 * native method in order to chunk an array of bytes and hash the results
	 * 
	 * @param data
	 *            the byte data
	 * @param min
	 *            the minimum threshold for a chunk
	 * @param max
	 *            the maximum threshold for a chunk
	 * @param offset
	 *            the offset of this data from the file
	 * @param primDiv
	 *            the primary rabin divisor
	 * @param secDiv
	 *            the secondary rabin divisor
	 * @return
	 */
	public LinkedList<Chunk> createChunks(byte[] data, int min, int max,
			long offset, int primDiv, int secDiv) {
		int sizeOfHashFigure = ResourceManager.getSizeOfHash();
		LinkedList<Chunk> chunksToReturn = new LinkedList<Chunk>();

		int sizeOfData = data.length;
		int sizeOfBpArray = ResourceManager.getSizeOfBreakpointsArray(
				sizeOfData, min);
		// System.out.println(sizeOfBpArray);

		int breakpoints[] = new int[sizeOfBpArray];
		int sizeOhHashArray = ResourceManager.getSizeOfHashArray(sizeOfBpArray);
		byte[] hashArray = new byte[sizeOhHashArray];

		// calling the nativem thod on the GPU
		this.createChunksOnGPU(sizeOfData, data, breakpoints, sizeOfBpArray,
				hashArray, sizeOhHashArray, min, max, primDiv, secDiv);

		// now constructing the chunk objects from the raw results stored in the
		// arrays supplied to the GPU routines
		for (int var = 1; var < breakpoints.length; ++var) {

			byte[] chunkHashBytes = new byte[sizeOfHashFigure];

			System.arraycopy(hashArray, (var - 1) * sizeOfHashFigure,
					chunkHashBytes, 0, sizeOfHashFigure);
			// to every start and end position we need to add the offset into
			// the fiel since the GPU code does not account for that..
			long start = breakpoints[var - 1] + offset;
			long end = breakpoints[var] + offset;
			chunksToReturn.add(new Chunk(start, end, new Sha1Hash(
					chunkHashBytes)));

		}

		return chunksToReturn;
	}

	public int getBufferSize() {
		return getSizeOfGPUBuffer();
	}

}
