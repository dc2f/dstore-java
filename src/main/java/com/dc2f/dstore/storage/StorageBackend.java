package com.dc2f.dstore.storage;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dc2f.dstore.hierachynodestore.StorageAdapter;



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
	@Nonnull public StorageId generateStorageId();
	
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
	
	@Nullable StorageId[] readChildren(StorageId childrenStorageId);
	
	StorageId writeChildren(StorageId[] children);
	
	StorageId writeProperties(Map<String, Property> properties);

	@Nonnull
	Map<String, Property> readProperties(StorageId propertiesStorageId);
	
	
	<T extends StorageAdapter>T getAdapter(Class<T> adapterInterface);

}
