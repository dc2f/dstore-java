package com.dc2f.dstore.storage;

import javax.annotation.Nonnull;

public class MutableStoredFlatNode extends StoredFlatNode {

	public MutableStoredFlatNode(@Nonnull StorageId newStorageId) {
		super(newStorageId, null, null);
	}
	public MutableStoredFlatNode(@Nonnull StorageId newStorageId, @Nonnull StoredFlatNode orig) {
		super(orig);
		this.storageId = newStorageId;
	}
	
	
//	public void setStorageId(StorageId storageId) {
//		this.storageId = storageId;
//	}

	
	public void setChildren(StorageId children) {
		this.children = children;
	}
	
	public void setProperties(StorageId properties) {
		this.properties = properties;
	}

}
