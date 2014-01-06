package com.dc2f.dstore.storage.simple;

import java.util.UUID;

import com.dc2f.dstore.storage.StorageId;

/**
 * A simple storage id that is based on a wrapped basic other type.
 * 
 * @param <T> The basic type used in the storage id.
 */
public class WrappedStorageId<T> implements StorageId {
	
	private T id;

	public WrappedStorageId(T id) {
		this.id = id;
	}
	
	public static SimpleStringStorageId generateRandom() {
		return new SimpleStringStorageId(UUID.randomUUID().toString());
	}
	
	public T getWrappedId() {
		return id;
	}

	@Override
	public String getIdString() {
		return String.valueOf(id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WrappedStorageId) {
			return id.equals(((WrappedStorageId<?>) obj).id);
		}
		return id.equals(obj);
	}
	
	@Override
	public String toString() {
		return String.valueOf(id);
	}
}
