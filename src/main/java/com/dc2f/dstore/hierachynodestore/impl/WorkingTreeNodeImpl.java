package com.dc2f.dstore.hierachynodestore.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.dc2f.dstore.hierachynodestore.ChildQueryAdapter;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.MutableStoredFlatNode;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredFlatNode;

public class WorkingTreeNodeImpl implements WorkingTreeNode {

	private WorkingTreeImpl workingTreeImpl;
	StoredFlatNode node;
	
	boolean changedChildren = false;
	boolean changedProperties = false;
	
	/**
	 * Mapping between a child's name and their node ids.
	 */
	private StorageId[] storedChildren = null;
	
	/**
	 * storage for all <strong>loaded</strong> children. as soon as we are accessing a single child,
	 * or have manipulate the children, this will be filled completely.
	 */
	List<WorkingTreeNode> children = null;
	boolean isNew = false;
	MutableStoredFlatNode mutableStoredNode;
	private WorkingTreeNodeImpl parentNode;

	public WorkingTreeNodeImpl(WorkingTreeImpl workingTreeImpl,
			StoredFlatNode flatNode, WorkingTreeNodeImpl parentNode) {
		this.workingTreeImpl = workingTreeImpl;
		this.parentNode = parentNode;
		if (flatNode == null) {
			throw new IllegalArgumentException("flatNode must not be null.");
		}
		this.node = flatNode;
	}
	
	StorageId[] getStoredChildren() {
		if (storedChildren == null) {
			storedChildren = workingTreeImpl.storageBackend.readChildren(node.getChildren());
		}
		if (storedChildren == null) {
			storedChildren = new StorageId[0];
		}
		return storedChildren;
	}
	
	@Override
	public Iterable<WorkingTreeNode> getChild(String childName) {
		ChildQueryAdapter queryAdapter = workingTreeImpl.storageBackend.getAdapter(ChildQueryAdapter.class);
		
		Iterable<StorageId> flatChildrenIds = queryAdapter.getChildren(this.getStorageId(), "name", childName);
		List<WorkingTreeNode> ret = new ArrayList<>();
		for (StorageId flatChildId : flatChildrenIds) {
			ret.add(workingTreeImpl.getNodeByStorageId(flatChildId, this));
		}
		return ret;
		
//		return null;
		
//		List<WorkingTreeNode> ret = new ArrayList<>();
//		List<WorkingTreeNode> myChildren = loadChildren();
//		for (WorkingTreeNode child : myChildren) {
//			if (child.getName().equals(childName)) {
//				ret.add(child);
//			}
//		}
//		return ret;
		
//		List<WorkingTreeNode> childList = children.get(childName);
//		if (childList == null) {
//			StorageId[] storedChildList = getStoredChildren().get(childName);
//			if (storedChildList != null) {
//				childList = new ArrayList<>(storedChildList.length);
//				for (StorageId storedId : storedChildList) {
//					WorkingTreeNode child = workingTreeImpl.getNodeByStorageId(storedId, this);
//					childList.add(child);
//				}
//			} else {
//				childList = new ArrayList<>();
//			}
//			children.put(childName, childList);
//		}
//		return childList;
	}
	
	List<WorkingTreeNode> loadChildren() {
		if (children == null) {
			StorageId[] storedChildrenId = getStoredChildren();
			List<WorkingTreeNode> ret = new ArrayList<>(storedChildrenId.length);
			for (StorageId childId : storedChildren) {
				WorkingTreeNode child = workingTreeImpl.getNodeByStorageId(childId, this);
				ret.add(child);
			}
			children = ret;
		}
		return children;
	}
	
	@Override
	public WorkingTreeNode addChild(String childName) {
		StorageId propertiesId = workingTreeImpl.storageBackend.writeProperties(
				Collections.singletonMap(Property.PROPERTY_NAME, new Property(childName)));
		StoredFlatNode childNode = new StoredFlatNode(workingTreeImpl.storageBackend.generateStorageId(),
				null, propertiesId);
		WorkingTreeNodeImpl child = new WorkingTreeNodeImpl(workingTreeImpl, childNode, this);
		child.isNew = true;
		workingTreeImpl.loadedNodes.put(child.getStorageId(), child);
		List<WorkingTreeNode> myChildren = loadChildren();
		myChildren.add(child);
		changedChildren = true;
		workingTreeImpl.notifyNodeChanged(this);
		workingTreeImpl.notifyNodeChanged(child);
		

		return child;
	}

	@Override
	public Iterable<String> getChildrenNames() {
		List<WorkingTreeNode> myChildren = loadChildren();
		Set<String> ret = new HashSet<>(myChildren.size());
		for (WorkingTreeNode child : myChildren) {
			ret.add(child.getName());
		}
		
		return ret;
	}
	
	@Nullable
	public Property getProperty(String propertyName) {
		Map<String, Property> properties = workingTreeImpl.storageBackend.readProperties(node.getProperties());
		return properties.get(propertyName);
	}
	
	@Override
	public String getName() {
		Property nameProperty = getProperty(Property.PROPERTY_NAME);
		if (nameProperty == null) {
			return null;
		}
		return nameProperty.getString();
	}

	WorkingTreeNodeImpl getParent() {
		return parentNode;
	}

	public void createMutableStoredNode(StorageId generateUniqueId) {
		mutableStoredNode = new MutableStoredFlatNode(generateUniqueId, node);
	}

	@Override
	public StorageId getStorageId() {
		if (mutableStoredNode != null) {
			return mutableStoredNode.getStorageId();
		}
		return node.getStorageId();
	}
	
	
}
