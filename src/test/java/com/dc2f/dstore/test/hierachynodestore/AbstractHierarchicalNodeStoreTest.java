package com.dc2f.dstore.test.hierachynodestore;

import static com.dc2f.dstore.test.TreeAssertions.assertTree;
import static com.dc2f.dstore.test.TreeAssertions.node;
import static com.dc2f.dstore.test.TreeAssertions.properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.test.TreeAssertions.ExpectedNode;

public abstract class AbstractHierarchicalNodeStoreTest {

	private HierarchicalNodeStore nodeStore;
	
	@Before
	public void setupDstore() throws IOException {
		StorageBackend storageBackend = initStorageBackend();
		nodeStore = new HierarchicalNodeStore(storageBackend);
	}

	protected abstract StorageBackend initStorageBackend();
	
	@Test
	public void testInitFreshBranch() {
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root1 = wt1.getRootNode();
		ExpectedNode onlyRoot = node(properties("name", ""));
		assertTree("wt1 must only see the root node", onlyRoot, root1);
	}
	
	@Test
	public void testAddingChilden() {
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root1 = wt1.getRootNode();
		WorkingTreeNode a = root1.addChild("A");
		WorkingTreeNode b = root1.addChild("B");
		
		ExpectedNode expectedAB = node(properties("name", ""),
				node(properties("name", "A")),
				node(properties("name", "B"))
			);
		
		assertTree("Changes are visible inside uncommited working tree wt1", expectedAB, root1);
		assertSame("Get rootNode() must always return the same root node", root1, wt1.getRootNode());
	}
	
	@Test
	public void testTwoIndependentCheckouts() {
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root1 = wt1.getRootNode();
		WorkingTreeNode a = root1.addChild("A");
		WorkingTreeNode b = root1.addChild("B");
		
		WorkingTree wt2 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root2 = wt2.getRootNode();
		ExpectedNode onlyRoot = node(properties("name", ""));
		assertTree("Changes are not visible in wt2 which was checked out before commiting wt1", onlyRoot, root2);
	}
	
	@Test
	public void testCommitNotAffectingWorkInProgress() {
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root1 = wt1.getRootNode();
		WorkingTreeNode a = root1.addChild("A");
		WorkingTreeNode b = root1.addChild("B");
		
		WorkingTree wt2 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root2 = wt2.getRootNode();
		ExpectedNode onlyRoot = node(properties("name", ""));

		wt1.commit("Commiting wt1, nothing must have changed in already existing working trees");
		ExpectedNode expectedAB = node(properties("name", ""),
				node(properties("name", "A")),
				node(properties("name", "B"))
			);
		assertTree("root1 data didn't change during commit", expectedAB, root1);
		assertSame("Get rootNode() must return the same root node as before commiting", root1, wt1.getRootNode());
	}
	
	@Test
	public void testVisibilityAfterCommit() {
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root1 = wt1.getRootNode();
		WorkingTreeNode a = root1.addChild("A");
		WorkingTreeNode b = root1.addChild("B");
		wt1.commit("Commiting wt1, nothing must have changed in already existing working trees");
		
		WorkingTree wt3 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root3 = wt3.getRootNode();
		ExpectedNode expectedAB = node(properties("name", ""),
				node(properties("name", "A")),
				node(properties("name", "B"))
			);
		assertTree("wt3 must see changes commited by wt1", expectedAB, root3);
	}
	
	@Test
	public void testSettingPropertiesAndReCommit() {
		WorkingTree wt = nodeStore.checkoutBranch("master");
		WorkingTreeNode root = wt.getRootNode();
		WorkingTreeNode a = root.addChild("A");
		WorkingTreeNode b = root.addChild("B");
		wt.commit("Commiting without properties.");
		ExpectedNode expectedAB = node(properties("name", ""),
				node(properties("name", "A")),
				node(properties("name", "B"))
			);
		assertTree("wt1 was not stored correctly", expectedAB, root);
		
		a.setProperty("CONTENT", new Property("content"));
		wt.commit("Changed content property for a.");
		
		expectedAB = node(properties("name", ""),
					node(properties("name", "A", "CONTENT", "content")),
					node(properties("name", "B"))
				);
		assertTree("wt1 was not stored correctly", expectedAB, root);
		
		a.setProperty("CONTENT", new Property("content1"));
		wt.commit("Changed content property for a again.");
		
		expectedAB = node(properties("name", ""),
				node(properties("name", "A", "CONTENT", "content1")),
				node(properties("name", "B"))
			);
	assertTree("wt1 was not stored correctly", expectedAB, root);
	}
	
	
	@Test
	public void testBinaryData() throws IllegalStateException, UnsupportedEncodingException {
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root = wt1.getRootNode();
		WorkingTreeNode binary = root.addChild("binary");
		binary.setProperty("content", new Property("Testtext".getBytes("utf8")));
		
		assertEquals("Testtext", new String((byte[]) binary.getProperty("content").getObjectValue()));
	}
}
