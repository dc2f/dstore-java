package com.dc2f.dstore.hierachynodestore;

import com.dc2f.dstore.hierachynodestore.impl.WorkingTreeImpl;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StoredCommit;
import com.dc2f.dstore.storage.StoredFlatNode;
import com.dc2f.dstore.storage.simple.SimpleUUIDStorageId;

/**
 * creates a heirarchical node store based on the flat node storage backend.
 */
public class HierarchicalNodeStore {
	private StorageBackend storageBackend;
	
	private final static String ROOT_NODE_NAME = "";
	private final static String MASTER_BRANCH_NAME = "master";


	public HierarchicalNodeStore(StorageBackend storageBackend) {
		this.storageBackend = storageBackend;
		initializeRootCommit();
	}
	
	private void initializeRootCommit() {
		SimpleUUIDStorageId rootCommitId = storageBackend.getDefaultRootCommitId();
		StoredCommit rootCommit = storageBackend.readCommit(rootCommitId);
		if (rootCommit == null) {
			StoredFlatNode storedRootNode = new StoredFlatNode(storageBackend.generateUniqueId(), ROOT_NODE_NAME, null, null, null);
			rootCommit = new StoredCommit(rootCommitId, null, storedRootNode.getStorageId());
			storageBackend.writeNode(storedRootNode);
			storageBackend.writeCommit(rootCommit);
			storageBackend.writeBranch(MASTER_BRANCH_NAME, rootCommit);
		}
	}
	
	public WorkingTree checkoutBranch(String branchName) {
		StoredCommit commit = storageBackend.readBranch(branchName);
		if (commit != null) {
			return new WorkingTreeImpl(this, storageBackend, commit, branchName);
		}
		return null;
	}
}
