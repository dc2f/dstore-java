package com.dc2f.dstore.storage.simple;

import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * storage id backed by a simple UUID string.
 */
public class SimpleStringStorageId extends WrappedStorageId<String> {

	public SimpleStringStorageId(String id) {
		super(id);
	}
	
	public static @Nonnull SimpleStringStorageId generateRandom() {
		return new SimpleStringStorageId(UUID.randomUUID().toString());
	}
}
