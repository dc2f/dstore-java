package com.dc2f.dstore.storage;

public class MutableStoredFlatNode extends StoredFlatNode {

	public MutableStoredFlatNode(StorageId newStorageId, StoredFlatNode orig) {
		super(orig);
		this.storageId = newStorageId;
	}
	
	
//	public void setStorageId(StorageId storageId) {
//		this.storageId = storageId;
//	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setParentId(StorageId parentId) {
		this.parentId = parentId;
	}
	
	public void setChildren(StorageId children) {
		this.children = children;
	}
	
	public void setProperties(StorageId properties) {
		this.properties = properties;
	}

}
