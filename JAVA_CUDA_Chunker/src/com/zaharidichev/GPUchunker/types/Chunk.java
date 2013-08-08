package com.zaharidichev.GPUchunker.types;

/**
 * This class represents a chunk. A chunk of data is the main building entity of
 * a file and. Each of the chunks is located at a position that is Effectively
 * an offset from the beginning of the file. Additionally every chunk has length
 * and is associated with a {@link Sha1Hash} which is the hash of the data that
 * is within the boundaries of the chunks
 * 
 * @author Zahari Dichev <zaharidichev@gmail.com>
 * 
 */

public class Chunk {

	private long start; // the start of the chunk
	private long end; // the end
	private Sha1Hash hash; // the object containing the hash

	/**
	 * The main constructor which takes the arguments needed to initilize a
	 * chunk object
	 * 
	 * @param start
	 *            the start of the chunk in relation to the beginning of the
	 *            file
	 * @param end
	 *            the end of the chunk in relation to the beginning of the file
	 * @param hash
	 *            the {@link Sha1Hash} object containing the bytes that are the
	 *            SHA1 message digest of the data within the boundaries of the
	 *            chunk
	 */
	public Chunk(long start, long end, Sha1Hash hash) {
		this.start = start;
		this.end = end;
		this.hash = hash;
	}

	/**
	 * Getter for the start of the chunk
	 * 
	 * @return {@link Long}
	 */
	public long getStart() {
		return this.start;
	}

	/**
	 * Getter for the end of the chunk
	 * 
	 * @return {@link Long}
	 */
	public long getEnd() {
		return this.end;
	}

	/**
	 * Getter for the size of the chunk
	 * 
	 * @return {@link Long}
	 */
	public long getSize() {
		return this.end - this.start;
	}

	/**
	 * Getter for the {@link Sha1Hash} hash of the chunk
	 * 
	 * @return a {@link Sha1Hash} object
	 */
	public Sha1Hash getHash() {
		return this.hash;
	}

	@Override
	public int hashCode() {
		return this.hash.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		if (end != other.end)
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (start != other.start)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + this.start + " - " + this.end + "] [" + this.getSize()
				+ "] [" + this.hash.toString() + "]";

	}

}
