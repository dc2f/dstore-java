package com.dc2f.dstore.hierachynodestore;

import javax.annotation.Nonnull;

import com.dc2f.dstore.storage.StorageId;

public interface ChildQueryAdapter extends StorageAdapter {
	public Iterable<StorageId> getChildren(@Nonnull StorageId parent, String property, Object value);
	public void createIndex(String property);
}
