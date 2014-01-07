package com.dc2f.dstore.test.hierachynodestore;

import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.pgsql.PgStorageBackend;

public class PgHierarchicalNodestoreTest extends AbstractHierarchicalNodeStoreTest {
	
	@Override
	protected StorageBackend initStorageBackend() {
		PgStorageBackend storage = new PgStorageBackend("localhost", 5432, "dstore-test", "dstore-test", "test");
		storage.dropSchema();
		storage.createSchema();
		return storage;
	}
}