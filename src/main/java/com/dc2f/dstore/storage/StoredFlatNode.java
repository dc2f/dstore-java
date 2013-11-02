package com.dc2f.dstore.storage;

public class StoredFlatNode {
	
	/**
	 * unique ID for this stored *version* of the flat node.
	 */
	private StorageId storageId;
	private String name;
	private StorageId parentId;
	private StorageId children;
	private StorageId properties;

	public StoredFlatNode(StorageId storageId, String name, StorageId parentId, StorageId children, StorageId properties) {
		this.storageId = storageId;
		this.name = name;
		this.parentId = parentId;
		this.children = children;
		this.properties = properties;
	}

	public StorageId getStorageId() {
		return storageId;
	}
	
	public String getName() {
		return name;
	}
	
	public StorageId getParentId() {
		return parentId;
	}
	
	public StorageId getChildren() {
		return children;
	}
	
	public StorageId getProperties() {
		return properties;
	}

}
