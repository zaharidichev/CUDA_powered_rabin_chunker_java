package com.zaharidichev.GPUchunker.entryPoint;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import com.zaharidichev.GPUchunker.chunker.RabinChunker;
import com.zaharidichev.GPUchunker.types.Chunk;
import com.zaharidichev.GPUchunker.utils.FileException;

/**
 * Just testing how the system works with a 700Mb file...
 * 
 * @author zahari
 * 
 */
public class starter {

	public static void main(String[] args) throws NoSuchAlgorithmException,
			FileException {

		File f = new File("/home/zahari/Desktop/2.6_kernels_merged.dat");

		RabinChunker chunker = new RabinChunker(32768, 131072, 512, 256);

		// chunk and print resulting chunks
		for (Chunk c : chunker.chunkFile(f)) {
			System.out.println(c);
		}

	}

}
