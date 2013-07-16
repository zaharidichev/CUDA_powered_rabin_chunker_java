package com.zaharidichev.GPUchunker.chunker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.zaharidichev.GPUchunker.CUDAhandle.CudaLink;
import com.zaharidichev.GPUchunker.types.Chunk;
import com.zaharidichev.GPUchunker.utils.FileException;
import com.zaharidichev.GPUchunker.utils.ResourceManager;

public class RabinChunker {
	private CudaLink GPUhandle;
	private int primaryDivisor;
	private int secondaryDivisor;
	private byte[] dataBuffer;
	private int minChunkSize;
	private int maxChunkSize;

	public RabinChunker(int minSize, int maxSize, int primDivisor,
			int secondaryDivisor) {

		this.GPUhandle = CudaLink.getInstance();
		this.minChunkSize = minSize;
		this.maxChunkSize = maxSize;
		this.primaryDivisor = primDivisor;
		this.secondaryDivisor = secondaryDivisor;

		this.dataBuffer = new byte[0];

	}

	@SuppressWarnings("resource")
	public LinkedList<Chunk> chunkFile(File file) throws FileException {

		LinkedList<Chunk> chunks = new LinkedList<Chunk>();
		FileInputStream stream = null;
		long sizeOfFile = 0;
		try {
			stream = new FileInputStream(file);
			sizeOfFile = stream.available();
			//System.out.println(sizeOfFile);

		} catch (IOException e) {
			throw new FileException("Could not open: " + file.getAbsolutePath());
		}

		int sizeOfBuffer = (int) Math.min(this.GPUhandle.getBufferSize(),
				sizeOfFile);
		this.dataBuffer = new byte[sizeOfBuffer];

		int numBatches = ResourceManager.getNumberOfBatches(sizeOfFile,
				this.dataBuffer.length);

		for (int bathch = 0; bathch < numBatches; bathch++) {
			long offsetIntoFile = ResourceManager.getOffsetIntoFile(
					this.dataBuffer.length, bathch);

			int sizeOfDataToRead = ResourceManager.getCurrentLengthOfBatch(
					bathch, this.dataBuffer.length, numBatches, sizeOfFile);
			try {
				//stream.read();
				stream.read(this.dataBuffer, 0, sizeOfDataToRead);
			} catch (IOException e) {
				throw new FileException("Problem reading: "
						+ file.getAbsolutePath());
			}

			if (sizeOfDataToRead != this.dataBuffer.length) {
				this.dataBuffer = ResourceManager.trimArray(this.dataBuffer,
						sizeOfDataToRead);
			}

			chunks.addAll(this.GPUhandle.createChunks(this.dataBuffer,
					this.minChunkSize, this.maxChunkSize, offsetIntoFile,this.primaryDivisor,this.secondaryDivisor));
		}

		try {
			stream.close();
		} catch (IOException e) {
			throw new FileException("Could not close: "
					+ file.getAbsolutePath());
		}
		return chunks;
	}
}
