package com.dc2f.dstore.test.storage;

import com.dc2f.dstore.storage.StorageBackend;

/**
 * A TestStorageProvider provides a storage backend for use during tests.
 * The provider can create new storage backends and also destroy them correctly after the test is run.
 * 
 * @param T The storage backend type this provider is for.
 */
public interface TestStorageProvider<T extends StorageBackend> {

	/**
	 * Creates an empty storage backend usable for testing.
	 */
	T createStorageBackend();
	
	/**
	 * Destroys the storage backend and releases all resources.
	 * @param backend The backend to destroy.
	 */
	void destroyStorageBackend(T backend);
}
