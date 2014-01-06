package com.dc2f.dstore.test;

import static com.dc2f.dstore.test.TreeAssertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.test.TreeAssertions.ExpectedNode;


public class TreeAssertionsTest {
	@Test(expected=AssertionError.class)
	public void testTreeAssertionsNotMatchesNullNode() {
		ExpectedNode expected = node(properties("name", "A"));
		assertTree(expected, null);
	}
	
	@Test
	public void testTreeAssertionMatchingProperty() {
		ExpectedNode expected = node(properties("name", "A"));
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("name", new Property("A"));
		WorkingTreeNode node = new WorkingTreeMockHelper().getWorkingTreeNode(properties);
		assertTree("Node must only contain one property", expected, node);
	}
	
	@Test(expected=AssertionError.class)
	public void testTreeAssertionMissingProperty() {
		ExpectedNode expected = node(properties("name", "A"));
		HashMap<String, Property> properties = new HashMap<>();
		WorkingTreeNode node = new WorkingTreeMockHelper().getWorkingTreeNode(properties);
		assertTree("Node must only contain one property", expected, node);
	}
	
	@Test(expected=AssertionError.class)
	public void testTreeAssertionOnePropertyToMuch() {
		ExpectedNode expected = node(properties("name", "A"));
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("name", new Property("A"));
		properties.put("content", new Property("B"));
		WorkingTreeNode node = new WorkingTreeMockHelper().getWorkingTreeNode(properties);
		assertTree("Node must only contain one property", expected, node);
	}


}
