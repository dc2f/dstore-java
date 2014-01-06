package com.dc2f.dstore.test.hierachynodestore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;

import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.flatjsonfiles.SlowJsonFileStorageBackend;
import com.dc2f.utils.FileUtils;

public class JsonHierarchicalNodeStoreTest extends AbstractHierarchicalNodeStoreTest {

	private File jsonStorageFolder;
	
	protected StorageBackend initStorageBackend() {
		try {
			jsonStorageFolder = Files.createTempDirectory("dstore-json-test-storage").toFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		StorageBackend storageBackend = new SlowJsonFileStorageBackend(jsonStorageFolder);
		return storageBackend;
	}
	
	@After
	public void destroyDstore() throws IOException {
		if(jsonStorageFolder != null && jsonStorageFolder.exists()) {
			FileUtils.removeRecursive(jsonStorageFolder.toPath());
		}
	}
}
