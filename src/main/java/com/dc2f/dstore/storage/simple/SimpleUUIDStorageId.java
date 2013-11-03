package com.dc2f.dstore.storage.simple;

import java.util.UUID;

import com.dc2f.dstore.storage.StorageId;

/**
 * storage id backed by a simple UUID string.
 */
public class SimpleUUIDStorageId implements StorageId {
	private static final long serialVersionUID = 1L;
	
	private String uuid;

	public SimpleUUIDStorageId(String uuid) {
		this.uuid = uuid;
	}
	
	public static SimpleUUIDStorageId generateRandom() {
		return new SimpleUUIDStorageId(UUID.randomUUID().toString());
	}

	@Override
	public String getIdString() {
		return uuid;
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleUUIDStorageId) {
			return uuid.equals(((SimpleUUIDStorageId) obj).uuid);
		}
		return uuid.equals(obj);
	}
	
	@Override
	public String toString() {
		return uuid;
	}

}
