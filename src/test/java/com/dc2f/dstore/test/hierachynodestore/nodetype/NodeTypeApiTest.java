package com.dc2f.dstore.test.hierachynodestore.nodetype;

import static com.dc2f.dstore.test.TreeAssertions.assertTree;
import static com.dc2f.dstore.test.TreeAssertions.node;
import static com.dc2f.dstore.test.TreeAssertions.properties;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeUtils;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeAccessor;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeDefinition;
import com.dc2f.dstore.storage.Property.PropertyType;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.map.HashMapStorage;
import com.dc2f.dstore.test.TreeAssertions.ExpectedNode;

public class NodeTypeApiTest {
	private Logger logger = LoggerFactory.getLogger(NodeTypeApiTest.class);
	private HierarchicalNodeStore nodeStore;
	
	@Before
	public void setupDstore() throws IOException {
		StorageBackend storageBackend = initStorageBackend();
		nodeStore = new HierarchicalNodeStore(storageBackend);
	}

	protected StorageBackend initStorageBackend() {
		return new HashMapStorage();
	}
	
	
	@Test
	public void testSimpleNodeTypeSetup() {
		WorkingTree wt = nodeStore.checkoutBranch("master");
		NodeTypeAccessor nodeTypeAccessor = wt.getNodeTypeAccessor();
		
		NodeTypeDefinition entryNodeType = nodeTypeAccessor.addNodeTypeDefinition("com.dc2f.blog.BlogEntry");
		
		// we need a 'nodetype' property (no inheritance, grml)
		entryNodeType.addPropertyDefinition(WorkingTree.NAME_NODETYPE)
		// for now we have no special property type for this..
				.setType(PropertyType.STRING)
				.setRequired(true);
		
		entryNodeType.addPropertyDefinition("title")
			.setRequired(true)
			.setType(PropertyType.STRING);
		
		entryNodeType.addPropertyDefinition("body")
				.setRequired(true)
				.setType(PropertyType.STRING);
		
		entryNodeType.addPropertyDefinition("slug")
			.setRequired(true)
			.setType(PropertyType.STRING);
		
		wt.commit(null);
		
		logger.info("Got tree: " + WorkingTreeUtils.debugRecursiveTree(wt.getRootNode()));

		ExpectedNode expectedNode = node(properties("name", ""),
				node(properties("name", ":nodetype"),
						node(properties("name", "com.dc2f.blog.BlogEntry",
								":nodetype",
								"com.dc2f.nodetype.NodeTypeDefinition"),

								node(properties(":name", ":nodetype",
										":nodetype", "com.dc2f.nodetype.PropertyDefinition",
										":required", true,
										":type", "STRING")),

								node(properties(":name", "title",
										":nodetype", "com.dc2f.nodetype.PropertyDefinition",
										":required", true,
										":type", "STRING")),

								node(properties(":name", "body",
										":nodetype", "com.dc2f.nodetype.PropertyDefinition",
										":required", true,
										":type", "STRING")),

								node(properties(":name", "slug",
										":nodetype", "com.dc2f.nodetype.PropertyDefinition",
										":required", true,
										":type", "STRING"))

						)));
		
		assertTree(expectedNode, wt.getRootNode());
	}

}
