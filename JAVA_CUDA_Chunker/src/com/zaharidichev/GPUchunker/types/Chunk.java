package com.zaharidichev.GPUchunker.types;


public class Chunk {

	private long start;
	private long end;
	private Sha1Hash hash;

	public Chunk(long start, long end, Sha1Hash hash) {
		this.start = start;
		this.end = end;
		this.hash = hash;
	}

	public long getStart() {
		return this.start;
	}

	public long getEnd() {
		return this.end;
	}

	public long getSize() {
		return this.end - this.start;
	}

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
