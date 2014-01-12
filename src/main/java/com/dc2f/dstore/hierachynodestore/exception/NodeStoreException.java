package com.dc2f.dstore.hierachynodestore.exception;

/**
 * generic exception from your node store.
 */
public class NodeStoreException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	
	public NodeStoreException(String message) {
		this(message, null);
	}
	public NodeStoreException(String message, Throwable cause) {
		super(message, cause);
	}
}
