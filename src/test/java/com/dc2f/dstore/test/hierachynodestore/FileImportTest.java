package com.dc2f.dstore.test.hierachynodestore;

import static com.dc2f.dstore.test.TreeAssertions.assertTree;
import static com.dc2f.dstore.test.TreeAssertions.node;
import static com.dc2f.dstore.test.TreeAssertions.properties;

import java.io.File;

import org.junit.Test;

import com.dc2f.dstore.folderimport.FolderImporter;
import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.WorkingTreeUtils;
import com.dc2f.dstore.storage.map.HashMapStorage;
import com.dc2f.dstore.test.TreeAssertions.ExpectedNode;

/**
 * Test for testing the file importer.
 */
public class FileImportTest {
	
	/**
	 * Tests the basic import of files.
	 * 
	 * FIXME: Make the test running stable
	 * The FolderImporter doesn't provide a stable import order of children on the same level.
	 * This means that we need to make the assertTree function more flexible and not rely on the provided order of nodes.
	 */
	@Test
	public void testBasicImport() {
		HierarchicalNodeStore nodeStore = new HierarchicalNodeStore(new HashMapStorage());
		FolderImporter importer = new FolderImporter(nodeStore);
		importer.startImport(new File("./test-data/fileimport"));
		
		WorkingTreeNode rootNode = nodeStore.checkoutBranch("master").getRootNode();
		System.out.println(WorkingTreeUtils.debugRecursiveTree(rootNode));
		
		ExpectedNode expected = 
			node(properties("name", ""),
				node(properties(
					"name", "root.json",
					"rootKey1", "rootValue1",
					"rootKey2", "rootValue2"
				)),
				node(properties("name", "a"),
					node(properties(
						"name", "a1.json",
						"a1Key1", "a1Value1",
						"a1Key2", "a1Value2"
					)),
					node(properties(
						"name", "a2.json",
						"a2Key1", "a2Value1",
						"a2Key2", "a2Value2"
					))
				),
				node(properties("name", "b"),
					node(properties(
							"name", "b1.txt",
							"data", "b1content"
						)),
					node(properties("name", "c"),
						node(properties(
							"name", "c1.json",
							"c1Key1", "c1Value1",
							"c1Key2", "c1Value2"
						)),
						node(properties(
							"name", "c2.json",
							"c2Key1", "c2Value1",
							"c2Key2", "c2Value2"
						)),
						node(properties(
							"name", "c3.json",
							"c3Key1", "c3Value1",
							"c3Key2", "c3Value2",
							"c3Key3", "c3Value3"
						))
					)
				)
			);
		
		assertTree(expected, rootNode);
	}
}
