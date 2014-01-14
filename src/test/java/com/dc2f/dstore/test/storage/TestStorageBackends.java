package com.dc2f.dstore.test.storage;

import java.util.Arrays;
import java.util.List;

import com.dc2f.dstore.storage.pgsql.PgStorageBackend;

/**
 * Utility class to get all configured storage backend factories.
 */
public class TestStorageBackends {

	/**
	 * Gets all configured storage backend factories.
	 */
	public static List<TestStorageProvider<?>> getConfiguredFactories() {
		// must be configurable
		return Arrays.asList(new TestStorageProvider<?> [] { 
				new HashMapTestStorageProvider(),
				new SlowJsonTestStorageProvider(),
				new PgTestStorageProvider()
		});
	}
}
