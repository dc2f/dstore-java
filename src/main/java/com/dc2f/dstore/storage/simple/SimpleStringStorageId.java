package com.dc2f.dstore.storage.simple;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.dc2f.dstore.storage.StorageId;

/**
 * storage id backed by a simple UUID string.
 */
public class SimpleStringStorageId implements StorageId {
	
	private String id;

	public SimpleStringStorageId(String id) {
		this.id = id;
	}
	
	public static @Nonnull SimpleStringStorageId generateRandom() {
		return new SimpleStringStorageId(UUID.randomUUID().toString());
	}

	@Override
	public String getIdString() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleStringStorageId) {
			return id.equals(((SimpleStringStorageId) obj).id);
		}
		return id.equals(obj);
	}
	
	@Override
	public String toString() {
		return id;
	}
}
