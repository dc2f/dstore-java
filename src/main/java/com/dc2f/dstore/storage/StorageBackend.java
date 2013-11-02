package com.dc2f.dstore.storage;

import java.util.Map;

import com.dc2f.dstore.storage.simple.SimpleUUIDStorageId;

public interface StorageBackend {
	
	/**
	 * generates a new unique id.
	 */
	public StorageId generateUniqueId();
	
	public StoredCommit readCommit(StorageId id);
	
	public void writeCommit(StoredCommit commit);
	
	StoredCommit readBranch(String name);
	
	void writeBranch(String name, StoredCommit commit);
	
	StoredFlatNode readNode(StorageId id);
	
	void writeNode(StoredFlatNode node);

	public SimpleUUIDStorageId getDefaultRootCommitId();
	
	Map<String, StorageId[]> readChildren(StorageId childrenStorageId);

}
