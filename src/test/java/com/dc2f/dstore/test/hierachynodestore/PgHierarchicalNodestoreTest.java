package com.dc2f.dstore.test.hierachynodestore;

import org.junit.Ignore;

import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.pgsql.PgStorageBackend;

@Ignore
public class PgHierarchicalNodestoreTest extends AbstractHierarchicalNodeStoreTest {
	
	
	
	@Override
	protected StorageBackend initStorageBackend() {
		String host = System.getProperty("com.dc2f.dstore.test.pghost", "localhost");
		int port = Integer.parseInt(System.getProperty("com.dc2f.dstore.test.port", "5432"));
		String user = System.getProperty("com.dc2f.dstore.test.pguser", "dstore-test");
		String database = System.getProperty("com.dc2f.dstore.test.pgdatabase", "dstore-test");
		String password = System.getProperty("com.dc2f.dstore.test.pgpassword", "test");
		PgStorageBackend storage = new PgStorageBackend(host, port, database, user, password);
		storage.dropSchema();
		storage.createSchema();
		return storage;
	}
}