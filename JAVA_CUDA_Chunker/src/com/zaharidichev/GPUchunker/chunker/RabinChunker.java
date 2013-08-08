package com.zaharidichev.GPUchunker.chunker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.zaharidichev.GPUchunker.CUDAhandle.CudaLink;
import com.zaharidichev.GPUchunker.types.Chunk;
import com.zaharidichev.GPUchunker.utils.FileException;
import com.zaharidichev.GPUchunker.utils.ResourceManager;

/**
 * This is the class that provides the main functionality for chunking. It uses
 * the Rabin fingerprinting algorithm and chunks files according to predefined
 * minimum and maximum thresholds. All the chunking and hashing is performed on
 * the GPU, if a CUDA - enabled device is available.
 * 
 * @author Zahari Dichev <zaharidichev@gmail.com>
 * 
 */
public class RabinChunker {
	private CudaLink GPUhandle; // the CUDALink object
	private int primaryDivisor; // the primary divisor needed for chunking
	private int secondaryDivisor; // the secondary divisor needed for chunking
	private byte[] dataBuffer; // the buffer of data that will be used to hold
								// remporary data read from disk
	private int minChunkSize; // the minimum size for a resulting chunk
	private int maxChunkSize; // the maximum size for a resulting chunk

	/**
	 * The main constructor for this class
	 * 
	 * @param minSize
	 *            the minimum size of a chunk
	 * @param maxSize
	 *            the maximum size of a chunk
	 * @param primDivisor
	 *            the primary Rabin divisor
	 * @param secondaryDivisor
	 *            . the secondary Rabin divisor
	 */
	public RabinChunker(int minSize, int maxSize, int primDivisor,
			int secondaryDivisor) {

		this.GPUhandle = CudaLink.getInstance(); // Retrieving an instance of
													// the cuda handle
		this.minChunkSize = minSize;
		this.maxChunkSize = maxSize;
		this.primaryDivisor = primDivisor;
		this.secondaryDivisor = secondaryDivisor;

		this.dataBuffer = new byte[0];

	}

	/**
	 * This is the public method which takes care of chunking a file by
	 * determining the number of batches that need to be dispatched based on the
	 * size of the GPU buffer available and the size of the data.
	 * 
	 * @param file
	 *            a {@link File} object
	 * @return a {@link LinkedList} of {@link Chunk} objects
	 * @throws FileException
	 *             in case ther e is a problem reading from disk
	 */
	@SuppressWarnings("resource")
	public LinkedList<Chunk> chunkFile(File file) throws FileException {

		LinkedList<Chunk> chunks = new LinkedList<Chunk>();
		FileInputStream stream = null;
		long sizeOfFile = 0;
		try {
			stream = new FileInputStream(file);
			sizeOfFile = stream.available(); // figuring out the size of the
												// file

		} catch (IOException e) {
			throw new FileException("Could not open: " + file.getAbsolutePath());
		}

		/*
		 * The size of the buffer on the GPU device shall be the minimum of the
		 * available buffer size and the size of the file itself
		 */
		int sizeOfBuffer = (int) Math.min(this.GPUhandle.getBufferSize(),
				sizeOfFile);
		this.dataBuffer = new byte[sizeOfBuffer]; // creating the temporary
													// buffer on the CPU

		/*
		 * Calculating how many batches are needed given the buffer size and the
		 * file size
		 */
		int numBatches = ResourceManager.getNumberOfBatches(sizeOfFile,
				this.dataBuffer.length); //

		for (int bathch = 0; bathch < numBatches; bathch++) {

			/*
			 * iterate as many times as needed in order to dispatch all the
			 * batches
			 */
			long offsetIntoFile = ResourceManager.getOffsetIntoFile(
					this.dataBuffer.length, bathch); // calcualte the current
														// offset into the file

			// and the size of the data that needs to be read from disk
			int sizeOfDataToRead = ResourceManager.getCurrentLengthOfBatch(
					bathch, this.dataBuffer.length, numBatches, sizeOfFile);
			try {
				// stream.read();
				stream.read(this.dataBuffer, 0, sizeOfDataToRead);
			} catch (IOException e) {
				throw new FileException("Problem reading: "
						+ file.getAbsolutePath());
			}

			/*
			 * if the data is less that the buffer size (meaning this is most
			 * likely the last batch), trim the host buffer array
			 */
			if (sizeOfDataToRead != this.dataBuffer.length) {
				this.dataBuffer = ResourceManager.trimArray(this.dataBuffer,
						sizeOfDataToRead);
			}
			/*
			 * now invoke the chunkiung method that will start the kernel on the
			 * GPU
			 */

			chunks.addAll(this.GPUhandle.createChunks(this.dataBuffer,
					this.minChunkSize, this.maxChunkSize, offsetIntoFile,
					this.primaryDivisor, this.secondaryDivisor));
		}

		try {
			stream.close(); // close the file at the end
		} catch (IOException e) {
			throw new FileException("Could not close: "
					+ file.getAbsolutePath());
		}
		return chunks; // and return the resulting chunks
	}
}
