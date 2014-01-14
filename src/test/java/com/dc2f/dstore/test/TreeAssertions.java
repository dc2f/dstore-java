package com.dc2f.dstore.test;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import junit.framework.AssertionFailedError;

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
	public static void assertTree(ExpectedNode expected, WorkingTreeNode node) {
		assertTree(null, expected, node);
	}
	
	/**
	 * Checks if all expected properties match the properties of the node.
	 * 
	 * @return Error message if they don't match, null if they match.
	 */
	private static String checkProperties(ExpectedNode expected, WorkingTreeNode node) {
		// check count
		if(expected.properties.length / 2 != node.getProperties().size()) {
			return "Expected " + (expected.properties.length / 2) + " children but got " + node.getProperties().size() + ".";
		}
		
		// check properties
		for(int i=0; i < expected.properties.length; i+=2) {
			String key = (String) expected.properties[i];
			Object expectedValue = (Object) expected.properties[i+1];
			
			Property actual = node.getProperty(key);
			if(actual == null) {
				return "Did not find expected property '" + key + "' on actual node.";
			}
			
			if(!Objects.equals(actual.getObjectValue(), expectedValue)) {
				return "Property '" + key + "' didn't match. "
						+ "Expected <" + expectedValue + "> but was <" + actual.getObjectValue() + ">";
			}
		}
		
		return null;
	}
	
	/**
	 * Checks the tree recursively and returns a describing error message of a failure.
	 * If the tree matches the method returns null.
	 * 
	 * @param expected The expected node.
	 * @param node The actual node.
	 * @return Error message describing the cause of failure. Null if the tree matches.
	 */
	private static String checkTreeRecursive(ExpectedNode expected, WorkingTreeNode node) {
		String propertiesError = checkProperties(expected, node);
		if(propertiesError != null) {
			return "Properties of expected node don't match actual properties. \n"
					+ propertiesError + "\n"
					+ "Expected node:\n" + expected.toString();
		}
		if(expected.children.size() != node.getChildrenCount()) {
			return "Number of children of expected node don't match actual number of children. \n"
					+ "Expected <" + expected.children.size() + "> but got <" + node.getChildrenCount() + ">. \n"
					+ "Expected node:\n" + expected.toString();
		}
		
		// set of remaining actual children, when we find a match with an expected node 
		// we remove it from the set and check all other children
		Set<WorkingTreeNode> remainingActualChildren = new HashSet<>();
		for(WorkingTreeNode actualChild : node.getChildren()) {
			remainingActualChildren.add(actualChild);
		}
		
		for(ExpectedNode expectedChild : expected.children) {
			int remainingChildrenCount = remainingActualChildren.size();
			String error = null;
			
			for(Iterator<WorkingTreeNode> it = remainingActualChildren.iterator(); it.hasNext();) {
				WorkingTreeNode actualChild = it.next();
				error = checkTreeRecursive(expectedChild, actualChild);
				if(error == null) {
					it.remove();
					break;
				}
			}
			
			// we didn't find a remaining node that matches the expected child
			if(remainingActualChildren.size() == remainingChildrenCount) {
				return error;
			}
		}
		
		return null;
	}
	
	/**
	 * Asserts a working tree node to equal the expected node.
	 * 
	 * @param message The assertion message.
	 * @param expected The expected node.
	 * @param node The node to assert.
	 */
	public static void assertTree(String message, ExpectedNode expected, WorkingTreeNode node) {
		String messagePrefix = message == null ? "" : (message + ".\n");
		
		assertNotNull(messagePrefix + "Given WorkingTreeNode must not be null.", node);
		String error = checkTreeRecursive(expected, node);
		if(error != null) {
			throw new AssertionFailedError(messagePrefix + error);
		}
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
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder("children count: ")
				.append(children.size());
			
			if(properties.length > 0) {
				b.append("\nproperties:");
			}
			
			for(int i = 0; i < properties.length; i+=2) {
				b
					.append("\n  ")
					.append(properties[i])
					.append(" = ")
					.append(properties[i+1]);
			}
			
			return b.toString();
		}
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
