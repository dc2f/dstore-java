package com.dc2f.dstore.test.hierachynodestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.test.storage.TestStorageBackends;
import com.dc2f.dstore.test.storage.TestStorageProvider;

/**
 * Base class for tests that need a single HierarchicalNodeStore to run.
 * 
 * To access the node store use {@link #getNodeStore()}.
 * The store is initialized before every test and destroyed after the test.
 */
@RunWith(Parameterized.class)
public abstract class AbstractHierarchicalNodeStoreTest {

	private HierarchicalNodeStore nodeStore;
	private StorageBackend backend;
	private TestStorageProvider<StorageBackend> backendFactory;
	
	public AbstractHierarchicalNodeStoreTest(TestStorageProvider<StorageBackend> backendFactory, String storageFactoryName) {
		this.backendFactory = backendFactory;
	}
	
	/**
	 * Get the node store for this test.
	 * @return The node store for this test.
	 */
	public synchronized HierarchicalNodeStore getNodeStore() {
		return nodeStore;
	}
	
	@Before
	public void setupNodeStore() {
		backend = backendFactory.createStorageBackend();
		nodeStore = new HierarchicalNodeStore(backend);
	}
	
	@After
	public void destroyNodeStore() {
		backendFactory.destroyStorageBackend(backend);
	}
	
	@Parameters(name="{1}")
	public static Collection<Object[]> getStorageBackendFactories() {
		List<TestStorageProvider<?>> backendFactories = TestStorageBackends.getConfiguredProviders();
		List<Object []> parameters = new ArrayList<>(backendFactories.size());
		for(TestStorageProvider<?> factory : backendFactories) {
			parameters.add(new Object [] { factory, factory.getClass().getName() });
		}
		return parameters;
	}
}
