package com.dc2f.dstore.storage.simple;


/**
 * storage id backed by a simple UUID string.
 */
public class SimpleStringStorageId extends WrappedStorageId<String> {

	public SimpleStringStorageId(String id) {
		super(id);
	}
	
}
