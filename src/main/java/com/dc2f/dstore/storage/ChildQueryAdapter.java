package com.dc2f.dstore.storage;

public interface ChildQueryAdapter extends StorageAdapter {
	public Iterable<StoredFlatNode> getChildren(String property, Object value);
	public void createIndex(String property);
}
