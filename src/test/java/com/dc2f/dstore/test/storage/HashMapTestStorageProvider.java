package com.dc2f.dstore.test.storage;

import com.dc2f.dstore.storage.map.HashMapStorage;

/**
 * Factory for creating hash map storage backends.
 */
public class HashMapTestStorageProvider implements TestStorageProvider<HashMapStorage> {

	@Override
	public HashMapStorage createStorageBackend() {
		return new HashMapStorage();
	}

	@Override
	public void destroyStorageBackend(HashMapStorage backend) {
		// Nothing to do here
	}
}