package com.dc2f.dstore.hierachynodestore;

import com.dc2f.dstore.storage.StorageId;

public interface ChildQueryAdapter extends StorageAdapter {
	public Iterable<StorageId> getChildren(StorageId parent, String property, Object value);
	public void createIndex(String property);
}
