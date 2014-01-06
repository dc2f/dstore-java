package com.dc2f.dstore.storage;

import javax.annotation.Nonnull;

public class StoredFlatNode {
	
	/**
	 * unique ID for this stored *version* of the flat node.
	 */
	@Nonnull protected StorageId storageId;
	protected StorageId children;
	protected StorageId properties;

	public StoredFlatNode(@Nonnull StorageId storageId, StorageId children, StorageId properties) {
		this.storageId = storageId;
		this.children = children;
		this.properties = properties;
	}

	public StoredFlatNode(@Nonnull StoredFlatNode orig) {
		this(orig.storageId, orig.children, orig.properties);
	}

	public StorageId getStorageId() {
		return storageId;
	}
	
	public StorageId getChildren() {
		return children;
	}
	
	public StorageId getProperties() {
		return properties;
	}
	

}
