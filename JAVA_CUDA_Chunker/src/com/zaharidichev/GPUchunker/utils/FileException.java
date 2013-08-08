package com.zaharidichev.GPUchunker.utils;

/**
 * A simple wrapper around the native {@link Exception} class in java. Used for
 * throwing exceptions that are associated with problems when reading a
 * particular file from disk
 * 
 * @author Zahari Dichev <zaharidichev@gmail.com>
 * 
 */
public class FileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileException(String message) {
		super(message);
	}

}
