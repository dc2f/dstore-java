package com.dc2f.dstore.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;

/**
 * Functions for asserting working trees.
 */
public class TreeAssertions {

	/**
	 * Asserts a working tree node to equal the expected node.
	 * 
	 * @param expected The expected node.
	 * @param node The node to assert.
	 * @return A new ExpectedNode as a copy of the current ExpectedNode but filled in with all StorageIds of the asserted node.
	 */
	public static ExpectedNode assertTree(ExpectedNode expected, WorkingTreeNode node) {
		return assertTree(null, expected, node);
	}
	
	/**
	 * Asserts a working tree node to equal the expected node.
	 * 
	 * @param message The assertion message.
	 * @param expected The expected node.
	 * @param node The node to assert.
	 * @return The asserted tree including all ids.
	 */
	public static ExpectedNode assertTree(String message, ExpectedNode expected, WorkingTreeNode node) {
		String messagePrefix = message == null ? "" : (message + "; ");
		
		assertNotNull("Given WorkingTreeNode must not be null.", node);
		
		// check the properties
		assertEquals(messagePrefix + "Expected properties count equals actual properties count for node " + node.getStorageId(), 
				(long) (expected.properties.length / 2), node.getProperties().size());
		
		for(int i=0; i < expected.properties.length; i+=2) {
			String key = (String) expected.properties[i];
			Object expectedValue = (Object) expected.properties[i+1];
			
			Property actual = node.getProperty(key);
			assertNotNull(actual);
			assertEquals(messagePrefix + "check value of property " + key + " for node " + node.getStorageId(), expectedValue, actual.getObjectValue());
		}
		
		// check children
		assertEquals(messagePrefix + "Expected children count equals actual children count for node " + node.getStorageId(),
				expected.children.size(), node.getChildrenCount());

		Iterator<ExpectedNode> expectedChildren = expected.children.iterator();
		Iterator<WorkingTreeNode> actualChildren = node.getChildren().iterator();
		
		List<ExpectedNode> childrenWithId = new ArrayList<>(node.getChildrenCount());
		for(int i=0; i<node.getChildrenCount(); i++) {
			ExpectedNode assertedChild = assertTree(message, expectedChildren.next(), actualChildren.next());
			childrenWithId.add(assertedChild);
		}

		return new ExpectedNode(node.getStorageId(), expected.properties, childrenWithId);
	}
	
	/**
	 * The expected version of a working tree node.
	 */
	public static class ExpectedNode {
		
		public ExpectedNode(StorageId storageId, Object[] properties, List<ExpectedNode> children) {
			this.storageId = storageId;
			this.properties = properties;
			this.children = new ArrayList<>(children);
		}

		/**
		 * The expected storage id
		 */
		public final StorageId storageId;
		
		/**
		 * The expected properties in key value format.
		 * Every odd parameter is a key and every even a value.
		 */
		public final Object [] properties;
		
		/**
		 * The expected children of the node.
		 */
		public final List<ExpectedNode> children;
	}
	
	/**
	 * Creates an expected node with properties but without children.
	 * 
	 * @param properties 
	 *		The expected properties in key value format.
	 *		Every odd parameter is a key and every even a value.
	 */
	public static ExpectedNode node(Object[] properties) {
		return new ExpectedNode(null, properties, new LinkedList<ExpectedNode>());
	}
	
	/**
	 * Creates an expected node with properties and children.
	 * 
	 * @param properties 
	 *		The expected properties in key value format.
	 *		Every odd parameter is a key and every even a value.
	 * @param children
	 * 		The child nodes of the node.
	 */
	public static ExpectedNode node(Object[] properties, ExpectedNode... children) {
		return new ExpectedNode(null, properties, Arrays.asList(children));
	}
	
	/**
	 * Creates a properties object to use in combination with the {@link #node(Object[], List)} function.
	 * 
	 * @param properties 
	 * 				The expected properties in key value format.
	 * 				Every odd parameter is a key and every even a value.
	 * @return Object property array.
	 */
	public static Object [] properties(Object... properties) {
		return properties;
	}
}
