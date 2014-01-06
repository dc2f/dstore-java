package com.dc2f.dstore.storage;

public class StoredCommit {
	
	private StorageId id;
	private StorageId[] parents;
	private StorageId rootNode;

	public StoredCommit(StorageId id, StorageId[] parents, StorageId rootNode) {
		this.id = id;
		this.parents = parents;
		this.rootNode = rootNode;
	}

	public StorageId getId() {
		return id;
	}
	
	public StorageId[] getParents() {
		return parents;
	}
	
	public StorageId getRootNode() {
		return rootNode;
	}
}
