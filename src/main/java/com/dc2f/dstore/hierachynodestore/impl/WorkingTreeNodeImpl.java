package com.dc2f.dstore.hierachynodestore.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.MutableStoredFlatNode;
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
	private Map<String, StorageId[]> storedChildren = null;
	
	/**
	 * storage for all <strong>loaded</strong> children.
	 * if a child was removed value is an empty list. if child was not yet loaded, the key won't exist.
	 * (yep, for SNS (Same Name Siblings, aka multi value children) can only be loaded all children together)
	 * 
	 * funny side note: value should never be null. whatever happens.
	 */
	Map<String, List<WorkingTreeNode>> children = new HashMap<String, List<WorkingTreeNode>>();
	private boolean isNew = false;
	MutableStoredFlatNode mutableStoredNode;

	public WorkingTreeNodeImpl(WorkingTreeImpl workingTreeImpl,
			StoredFlatNode flatNode) {
		this.workingTreeImpl = workingTreeImpl;
		if (flatNode == null) {
			throw new IllegalArgumentException("flatNode must not be null.");
		}
		this.node = flatNode;
	}
	
	Map<String, StorageId[]> getStoredChildren() {
		if (storedChildren == null) {
			storedChildren = workingTreeImpl.storageBackend.readChildren(node.getChildren());
		}
		if (storedChildren == null) {
			storedChildren = new HashMap<>();
		}
		return storedChildren;
	}
	
	@Override
	public List<WorkingTreeNode> getChild(String childName) {
		List<WorkingTreeNode> childList = children.get(childName);
		if (childList == null) {
			StorageId[] storedChildList = getStoredChildren().get(childName);
			if (storedChildList != null) {
				childList = new ArrayList<>(storedChildList.length);
				for (StorageId storedId : storedChildList) {
					WorkingTreeNode child = workingTreeImpl.getNodeByStorageId(storedId);
					childList.add(child);
				}
			} else {
				childList = new ArrayList<>();
			}
			children.put(childName, childList);
		}
		return childList;
	}
	
	@Override
	public WorkingTreeNode addChild(String childName) {
		StoredFlatNode childNode = new StoredFlatNode(workingTreeImpl.storageBackend.generateUniqueId(),
				childName, node.getStorageId(), null, null);
		WorkingTreeNodeImpl child = new WorkingTreeNodeImpl(workingTreeImpl, childNode);
		child.isNew  = true;
		List<WorkingTreeNode> childList = getChild(childName);
		if (childList == null) {
			childList = new ArrayList<>();
			children.put(childName, childList);
		}
		childList.add(child);
		changedChildren = true;
		workingTreeImpl.notifyNodeChanged(this);
		

		return child;
	}

	@Override
	public Iterable<String> getChildrenNames() {
		Set<String> storedNames = getStoredChildren().keySet();
		HashSet<String> mergedNames = new HashSet<>(storedNames.size());
		mergedNames.addAll(storedNames);
		for (Map.Entry<String, List<WorkingTreeNode>> entry : children.entrySet()) {
			if (entry.getValue().size() == 0) {
				// children were removed.
				mergedNames.remove(entry.getKey());
			} else {
				mergedNames.add(entry.getKey());
			}
		}
		return mergedNames;
	}

	@Override
	public String getName() {
		return node.getName();
	}

	WorkingTreeNodeImpl getParent() {
		StorageId parentId = node.getParentId();
		if (parentId == null) {
			return null;
		}
		return (WorkingTreeNodeImpl) workingTreeImpl.getNodeByStorageId(parentId);
	}

	public boolean isNew() {
		return isNew;
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
