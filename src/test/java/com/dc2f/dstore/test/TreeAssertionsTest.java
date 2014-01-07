package com.dc2f.dstore.test;

import static com.dc2f.dstore.test.TreeAssertions.assertTree;
import static com.dc2f.dstore.test.TreeAssertions.node;
import static com.dc2f.dstore.test.TreeAssertions.properties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.Property;
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
	public void testTreeAssertionPropertyValueWrong() {
		ExpectedNode expected = node(properties("name", "A"));
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("name", new Property("B"));
		WorkingTreeNode node = new WorkingTreeMockHelper().getWorkingTreeNode(properties);
		assertTree("Node must only contain one property", expected, node);
	}
	
	@Test(expected=AssertionError.class)
	public void testTreeAssertionPropertyNameWrong() {
		ExpectedNode expected = node(properties("name", "A"));
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("content", new Property("A"));
		WorkingTreeNode node = new WorkingTreeMockHelper().getWorkingTreeNode(properties);
		assertTree("Node must only contain one property", expected, node);
	}
	
	@Test(expected=AssertionError.class)
	public void testTreeAssertionPropertyNameAndValueWrong() {
		ExpectedNode expected = node(properties("name", "A"));
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("content", new Property("B"));
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
	
	@Test
	public void testTreeAssertionMatchingChildren() {
		WorkingTreeMockHelper helper = new WorkingTreeMockHelper();
		ExpectedNode expected = node(properties(), node(properties("name", "A")));
		WorkingTreeNode child = getChild(helper, "A");
		List<WorkingTreeNode> children = Arrays.asList(new WorkingTreeNode[]{child});
		WorkingTreeNode node = helper.getWorkingTreeNode(children);
		assertTree(expected, node);
	}
	
	@Test(expected=AssertionError.class)
	public void testTreeAssertionMatchingWrongChildrenCount() {
		WorkingTreeMockHelper helper = new WorkingTreeMockHelper();
		ExpectedNode expected = node(properties(), node(properties("name", "A")));
		List<WorkingTreeNode> children = Arrays.asList(new WorkingTreeNode[]{});
		WorkingTreeNode node = helper.getWorkingTreeNode(children);
		assertTree(expected, node);
	}
	
	@Test(expected=AssertionError.class)
	public void testTreeAssertionMatchingTooManyChildren() {
		WorkingTreeMockHelper helper = new WorkingTreeMockHelper();
		ExpectedNode expected = node(properties(), node(properties("name", "A")));
		WorkingTreeNode childA = getChild(helper, "A");
		WorkingTreeNode childB = getChild(helper, "B");
		List<WorkingTreeNode> children = Arrays.asList(new WorkingTreeNode[]{childA, childB});
		WorkingTreeNode node = helper.getWorkingTreeNode(children);
		assertTree(expected, node);
	}
	
	@Test
	public void testTreeAssertionMatchingMultipleChildren() {
		WorkingTreeMockHelper helper = new WorkingTreeMockHelper();
		ExpectedNode expected = node(properties(), node(properties("name", "A")), node(properties("name", "B")), node(properties("name", "C")));
		WorkingTreeNode childA = getChild(helper, "A");
		WorkingTreeNode childB = getChild(helper, "B");
		WorkingTreeNode childC = getChild(helper, "C");
		List<WorkingTreeNode> children = Arrays.asList(new WorkingTreeNode[]{childA, childB, childC});
		WorkingTreeNode node = helper.getWorkingTreeNode(children);
		assertTree(expected, node);
	}
	
	@Test
	public void testTreeAssertionMatchingRecursiveChildren() {
		WorkingTreeMockHelper helper = new WorkingTreeMockHelper();
		ExpectedNode expected = node(properties(), node(properties("name", "A"), node(properties("name", "B"), node(properties("name", "C")))));
		WorkingTreeNode childC = getChild(helper, "C");
		WorkingTreeNode childB = getChild(helper, "B", childC);
		WorkingTreeNode childA = getChild(helper, "A", childB);
		List<WorkingTreeNode> children = Arrays.asList(new WorkingTreeNode[]{childA});
		WorkingTreeNode node = helper.getWorkingTreeNode(children);
		assertTree(expected, node);
	}
	
	@Test(expected=AssertionError.class)
	public void testTreeAssertionMatchingRecursiveChildrenWrongName() {
		WorkingTreeMockHelper helper = new WorkingTreeMockHelper();
		ExpectedNode expected = node(properties(), node(properties("name", "A"), node(properties("name", "B"), node(properties("name", "C")))));
		WorkingTreeNode childC = getChild(helper, "D");
		WorkingTreeNode childB = getChild(helper, "B", childC);
		WorkingTreeNode childA = getChild(helper, "A", childB);
		List<WorkingTreeNode> children = Arrays.asList(new WorkingTreeNode[]{childA});
		WorkingTreeNode node = helper.getWorkingTreeNode(children);
		assertTree(expected, node);
	}

	private WorkingTreeNode getChild(WorkingTreeMockHelper helper, String name, WorkingTreeNode ... children) {
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("name", new Property(name));
		WorkingTreeNode child = helper.getWorkingTreeNode(properties, Arrays.asList(children));
		return child;
	}


}
