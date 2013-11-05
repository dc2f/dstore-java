package com.dc2f.dstore.storage;

public class StoredFlatNode {
	
	/**
	 * unique ID for this stored *version* of the flat node.
	 */
	protected StorageId storageId;
	protected String name;
	protected StorageId children;
	protected StorageId properties;

	public StoredFlatNode(StorageId storageId, String name, StorageId children, StorageId properties) {
		this.storageId = storageId;
		this.name = name;
		this.children = children;
		this.properties = properties;
	}

	public StoredFlatNode(StoredFlatNode orig) {
		this(orig.storageId, orig.name, orig.children, orig.properties);
	}

	public StorageId getStorageId() {
		return storageId;
	}
	
	public String getName() {
		return name;
	}
	
	public StorageId getChildren() {
		return children;
	}
	
	public StorageId getProperties() {
		return properties;
	}
	

}
