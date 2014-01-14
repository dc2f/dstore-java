package com.dc2f.dstore.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;

public class WorkingTreeMockHelper {
	
	private int id = 0;
	
	public WorkingTreeNode getWorkingTreeNode(
			final HashMap<String, Property> properties) {
		return getWorkingTreeNode(properties, Arrays.asList(new WorkingTreeNode[0]));
	}
	
	public WorkingTreeNode getWorkingTreeNode(List<WorkingTreeNode> children) {
		return getWorkingTreeNode(new HashMap<String, Property>(), children);
	}
	
	public WorkingTreeNode getWorkingTreeNode(
			final Map<String, Property> properties, final Collection<WorkingTreeNode> children) {
		StorageId id = getNextId();
		WorkingTreeNode node = EasyMock.createMock(WorkingTreeNode.class);
		EasyMock.expect(node.getProperties()).andReturn(properties).anyTimes();
		EasyMock.expect(node.getProperty(EasyMock.anyObject(String.class))).andAnswer(new IAnswer<Property>() {
			@Override
			public Property answer() throws Throwable {
				return properties.get(EasyMock.getCurrentArguments()[0]);
			}
		}).anyTimes();
		EasyMock.expect(node.getChildren()).andReturn(children).anyTimes();
		EasyMock.expect(node.getChildrenCount()).andReturn(children.size()).anyTimes();
		EasyMock.expect(node.getStorageId()).andReturn(id).anyTimes();
		EasyMock.replay(node);
		return node;
	}

	private synchronized StorageId getNextId() {
		StorageId id = new StorageId() {
			
			final String id = WorkingTreeMockHelper.this.id++ + "";
			
			@Override
			public String getIdString() {
				return id;
			}
		};
		return id;
	}

}
