package com.dc2f.dstore.test.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dc2f.dstore.storage.pgsql.PgStorageBackend;

/**
 * Test storage provider for the postgresql storage.
 */
public class PgTestStorageProvider implements TestStorageProvider<PgStorageBackend> {
	
	/**
	 * Map of storages to their database name.
	 */
	private Map<PgStorageBackend, String> databases = new HashMap<>();
	
	private String host = System.getProperty("com.dc2f.dstore.test.pghost", "localhost");
	private int port = Integer.parseInt(System.getProperty("com.dc2f.dstore.test.port", "5432"));
	private String user = System.getProperty("com.dc2f.dstore.test.pguser", "dstore-test");
	private String password = System.getProperty("com.dc2f.dstore.test.pgpassword", "test");

	/**
	 * Runs the given sql on the template 1 database in its own, fresh connection.
	 */
	private void runOnTemplate1(String statement) {
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://" + host + ":" + port + "/template1";
			
			try(Connection conn = DriverManager.getConnection(url, user, password)) {
				try(Statement stmt = conn.createStatement()) {
					stmt.executeUpdate(statement);
				}
			};
		} catch (ClassNotFoundException|SQLException e) {
			throw new RuntimeException("Error while executing \"" + statement + "\" on database template1.", e);			
		}
	}

	@Override
	public PgStorageBackend createStorageBackend() {
		String dbName = "dstore-test-" + UUID.randomUUID();
		runOnTemplate1("CREATE DATABASE \"" + dbName + "\" WITH ENCODING = 'UTF8'");
		PgStorageBackend storage = new PgStorageBackend(host, port, dbName, user, password);
		databases.put(storage, dbName);
		return storage;
	}

	@Override
	public void destroyStorageBackend(PgStorageBackend backend) {
		try {
			backend.close();
			String dbName = databases.get(backend);
			runOnTemplate1("DROP DATABASE \"" + dbName + "\"");
		} catch (IOException e) {
			throw new RuntimeException("Error while closing backend.", e);
		}
	}
}