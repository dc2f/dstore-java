package com.dc2f.dstore.storage.map;

import java.util.HashMap;
import java.util.Map;

import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;
import com.dc2f.dstore.storage.StoredFlatNode;
import com.dc2f.dstore.storage.StoredProperty;
import com.dc2f.dstore.storage.simple.SimpleUUIDStorageId;

/**
 * Storage implementation backed by a few hashmaps.
 */
public class HashMapStorage implements StorageBackend {
	
	private final static String ROOT_COMMIT_ID = "rootCommitId";
	
	Map<String, StorageId> storedBranches = new HashMap<>();
	Map<StorageId, StoredCommit> storedCommit = new HashMap<>();
	Map<StorageId, StoredFlatNode> storedNodes = new HashMap<>();
	Map<StorageId, Map<String, StorageId[]>> storedChildren = new HashMap<>();
	Map<StorageId, Map<String, StoredProperty[]>> storedProperties = new HashMap<>();

	@Override
	public StorageId generateUniqueId() {
		// TODO do we need to verify this UUID is really unique? probably not, since we can't
		// check uniquenes in a distributed environment anyway.. so don't even try to..
		return SimpleUUIDStorageId.generateRandom();
	}

	@Override
	public StoredCommit readCommit(StorageId id) {
		return storedCommit.get(id);
	}

	@Override
	public void writeCommit(StoredCommit commit) {
		storedCommit.put(commit.getId(), commit);
	}

	@Override
	public StoredCommit readBranch(String name) {
		StorageId branchHeadId = storedBranches.get(name);
		if (branchHeadId != null) {
			return storedCommit.get(branchHeadId);
		}
		return null;
	}

	@Override
	public void writeBranch(String name, StoredCommit commit) {
		// TODO make sure commit is already stored?
		storedBranches.put(name, commit.getId());
	}

	@Override
	public StoredFlatNode readNode(StorageId id) {
		return storedNodes.get(id);
	}

	@Override
	public void writeNode(StoredFlatNode node) {
		storedNodes.put(node.getStorageId(), node);
	}

	@Override
	public SimpleUUIDStorageId getDefaultRootCommitId() {
		return new SimpleUUIDStorageId(ROOT_COMMIT_ID);
	}

	@Override
	public Map<String, StorageId[]> readChildren(StorageId childrenStorageId) {
		return storedChildren.get(childrenStorageId);
	}

}
