package com.dc2f.dstore.hierachynodestore.impl;

import com.dc2f.dstore.hierachynodestore.Commit;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;

public class CommitImpl implements Commit {

	private StoredCommit storedCommit;

	public CommitImpl(StoredCommit storedCommit) {
		this.storedCommit = storedCommit;
	}

	@Override
	public StorageId getStorageId() {
		return storedCommit.getId();
	}
	
	

}
