package com.zaharidichev.GPUchunker.types;

import java.util.Arrays;

/**
 * This is a class which encapsulates an array of bytes that is the hash of the
 * contents of a chunk
 * 
 * @author zahari
 * 
 */
public class Sha1Hash {

	private byte[] hashBytes; // the bytes

	/**
	 * Constructor that creates the object with the specified byte arrays
	 * contents for the hash
	 * 
	 * @param hash
	 */
	public Sha1Hash(byte[] hash) {
		this.hashBytes = hash;
	}

	/**
	 * Getter that retrieves the raw array of bytes
	 * 
	 * @return {@link Byte} array
	 */
	public byte[] getBytes() {
		return this.hashBytes;
	}

	/**
	 * Setter for the bytes of the chunk
	 * 
	 * @param bytes
	 */
	public void setBytes(byte[] bytes) {
		this.hashBytes = bytes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int j = 0; j < hashBytes.length; j++) {
			sb.append(String.format("%02x", hashBytes[j]));
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(hashBytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Sha1Hash other = (Sha1Hash) obj;
		if (!Arrays.equals(hashBytes, other.hashBytes))
			return false;
		return true;
	}

}
