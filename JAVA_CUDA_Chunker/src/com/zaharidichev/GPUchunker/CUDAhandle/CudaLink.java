package com.zaharidichev.GPUchunker.CUDAhandle;

import java.util.LinkedList;

import com.zaharidichev.GPUchunker.types.Chunk;
import com.zaharidichev.GPUchunker.types.Sha1Hash;
import com.zaharidichev.GPUchunker.utils.ResourceManager;

public class CudaLink {

	native int createChunksOnGPU(int sizeOfData, byte[] data,
			int[] breakpoints, int sizeOfBreakpoints, byte[] hashes,
			int sizeOfHashes, int min, int max, int primDiv, int secDiv);

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

		this.createChunksOnGPU(sizeOfData, data, breakpoints, sizeOfBpArray,
				hashArray, sizeOhHashArray, min, max, primDiv, secDiv);

		for (int var = 1; var < breakpoints.length; ++var) {

			byte[] chunkHashBytes = new byte[sizeOfHashFigure];

			System.arraycopy(hashArray, (var - 1) * sizeOfHashFigure,
					chunkHashBytes, 0, sizeOfHashFigure);
			long start = breakpoints[var - 1] + offset;
			long end = breakpoints[var] + offset;
			chunksToReturn.add(new Chunk(start, end, new Sha1Hash(
					chunkHashBytes)));

		}

		return chunksToReturn;
	}

	private static CudaLink instance = null;

	public static CudaLink getInstance() {
		if (instance == null) {
			instance = new CudaLink();
		}
		return instance;
	}

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

	private native int getSizeOfGPUBuffer();

	public int getBufferSize() {
		return getSizeOfGPUBuffer();
	}

}
