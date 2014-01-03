package com.dc2f.dstore.hierachynodestore;

import com.dc2f.dstore.storage.StoredFlatNode;

public interface ChildQueryAdapter extends StorageAdapter {
	public Iterable<StoredFlatNode> getChildren(String property, Object value);
	public void createIndex(String property);
}
