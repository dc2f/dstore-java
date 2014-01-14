package com.dc2f.dstore.test.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.dc2f.dstore.storage.flatjsonfiles.SlowJsonFileStorageBackend;
import com.dc2f.utils.FileUtils;

/**
 * Test storage provider for the slow json storage backend.
 */
public class SlowJsonTestStorageProvider implements TestStorageProvider<SlowJsonFileStorageBackend>{

	private Map<SlowJsonFileStorageBackend, File> storageFolders = new HashMap<>();
	
	@Override
	public SlowJsonFileStorageBackend createStorageBackend() {
		try {
			File storageFolder = Files.createTempDirectory("dstore-json-test-storage").toFile();
			SlowJsonFileStorageBackend backend = new SlowJsonFileStorageBackend(storageFolder);
			storageFolders.put(backend, storageFolder);
			return backend;
		} catch (IOException e) {
			throw new RuntimeException("Error while creating storage backend", e);
		}
	}

	@Override
	public void destroyStorageBackend(SlowJsonFileStorageBackend backend) {
		try {
			File storageFolder = storageFolders.get(backend);
			if(storageFolder != null && storageFolder.exists()) {
				FileUtils.removeRecursive(storageFolder.toPath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Error when destroying storage backend", e);
		}
	}

}
