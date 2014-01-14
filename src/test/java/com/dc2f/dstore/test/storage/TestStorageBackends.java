package com.dc2f.dstore.test.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to get all configured storage backend factories.
 */
public class TestStorageBackends {

	/**
	 * Gets all configured storage backend factories.
	 */
	public static List<TestStorageProvider<?>> getConfiguredProviders() {
		String configuredProviders = System.getProperty("com.dc2f.dstore.test.storageProviders");
		
		if(configuredProviders == null) {
			return Arrays.asList(new TestStorageProvider<?> [] { new HashMapTestStorageProvider() });
		} else {
			List<TestStorageProvider<?>> providers = new ArrayList<>();
			for(String configuredProvider : configuredProviders.split(",")) {
				try {
					providers.add((TestStorageProvider<?>) Class.forName(configuredProvider).newInstance());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Test storage provider class \"" + configuredProvider + "\" not found.", e);
				} catch (IllegalAccessException|InstantiationException e) {
					throw new RuntimeException("Can't initialize test storage provider \"" + configuredProvider + "\".", e);
				}
			}
			return providers;
		}
	}
}
