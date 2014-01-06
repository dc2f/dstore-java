package com.dc2f.dstore.test.hierachynodestore;

import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.map.HashMapStorage;

public class HashMapHierarchicalNodeStoreTest extends AbstractHierarchicalNodeStoreTest {


	protected StorageBackend initStorageBackend() {
		StorageBackend storageBackend = new HashMapStorage();
		return storageBackend;
	}

}
