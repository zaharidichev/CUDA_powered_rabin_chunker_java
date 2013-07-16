package com.zaharidichev.GPUchunker.types;

import java.util.Arrays;

public class Sha1Hash {

	private byte[] hashBytes;

	public Sha1Hash(byte[] hash) {
		this.hashBytes = hash;
	}

	public byte[] getBytes() {
		return this.hashBytes;
	}

	public void setBytes(byte[] bytes) {
		this.hashBytes = bytes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (int j = 0; j < hashBytes.length; j++) {
			sb.append(String.format("%02x", hashBytes[j]) );
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
