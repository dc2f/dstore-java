package com.dc2f.dstore.storage;

import java.util.Map;

/**
 * Backend for storing flat node data.
 * 
 * A storage backend only needs to be able to deal with storage ids
 * created by its own methods. So it is safe to cast input storage
 * ids to its own StorageId implementation.
 */
public interface StorageBackend {
	
	/**
	 * generates a new unique id.
	 */
	public StorageId generateStorageId();
	
	/**
	 * Creates a storage id from the given global id string for this store.
	 */
	public StorageId storageIdFromString(String idString);
	
	public StoredCommit readCommit(StorageId id);
	
	public void writeCommit(StoredCommit commit);
	
	StoredCommit readBranch(String name);
	
	void writeBranch(String name, StoredCommit commit);
	
	StoredFlatNode readNode(StorageId id);
	
	StoredFlatNode writeNode(StoredFlatNode node);
	
	Map<String, StorageId[]> readChildren(StorageId childrenStorageId);
	
	StorageId writeChildren(Map<String, StorageId[]> children);
	
	<T extends StorageAdapter>T getAdapter(Class<T> adapterInterface);

}
