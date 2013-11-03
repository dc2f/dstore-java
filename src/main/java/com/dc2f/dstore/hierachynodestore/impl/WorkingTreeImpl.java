package com.dc2f.dstore.hierachynodestore.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dc2f.dstore.hierachynodestore.Commit;
import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;
import com.dc2f.dstore.storage.StoredFlatNode;

public class WorkingTreeImpl implements WorkingTree {

	private HierarchicalNodeStore hierarchicalNodeStore;
	private StoredCommit headCommit;
	private String branchName;
	StorageBackend storageBackend;
	private Map<StorageId, WorkingTreeNodeImpl> loadedNodes = new HashMap<>();
	private List<WorkingTreeNodeImpl> changedNodes = new ArrayList<>();

	public WorkingTreeImpl(HierarchicalNodeStore hierarchicalNodeStore,
			StorageBackend storageBackend, StoredCommit headCommit, String branchName) {
		this.hierarchicalNodeStore = hierarchicalNodeStore;
		this.storageBackend = storageBackend;
		this.headCommit = headCommit;
		this.branchName = branchName;
	}

	@Override
	public WorkingTreeNode getRootNode() {
		StorageId rootNodeId = headCommit.getRootNode();
		return getNodeByStorageId(rootNodeId);
//		return new WorkingTreeNodeImpl(this, storageBackend.readNode(rootNodeId));
	}
	
	public WorkingTreeNode getNodeByStorageId(StorageId nodeStorageId) {
		// TODO shouldn't we add caching right here?
		WorkingTreeNodeImpl ret = loadedNodes.get(nodeStorageId);
		if (ret == null) {
			StoredFlatNode flatStoredNode = storageBackend.readNode(nodeStorageId);
			if (flatStoredNode == null) {
				throw new RuntimeException("Unable to find node with storage id {" + nodeStorageId + "}");
			}
			ret = new WorkingTreeNodeImpl(this, flatStoredNode);
			loadedNodes.put(nodeStorageId, ret);
		}
		return ret;
	}

	public void notifyNodeChanged(WorkingTreeNodeImpl workingTreeNodeImpl) {
		changedNodes.add(workingTreeNodeImpl);
	}

	@Override
	public Commit commit(String message) {
		if (message == null) {
			message = "";
		}
		Set<WorkingTreeNodeImpl> nodesToUpdate = findNodesToUpdate();
		// give the ones to update a new id before storing, otherwise child ids won't match
		for (WorkingTreeNodeImpl node : nodesToUpdate) {
			
			if (!node.isNew()) {
				node.createMutableStoredNode(storageBackend.generateUniqueId());
//				node.node.setStorageId();
			} else {
				// this should be done differently.. new nodes should always have a mutable stored node..
				node.createMutableStoredNode(node.node.getStorageId());
			}
		}

		// write all changed nodes to storage.
		for (WorkingTreeNodeImpl node : nodesToUpdate) {
			if (node.changedChildren) {
				Map<String, StorageId[]> children = new HashMap<>();
				Iterable<String> childNames = node.getChildrenNames();
				for (String childName : childNames) {
					List<WorkingTreeNode> childList = node.children.get(childName);
					StorageId[] childStorageIds;
					// if children weren't loaded yet, we don't need to load/store them.. just reuse the array
					if (childList != null) {
						childStorageIds = new StorageId[childList.size()];
						int i = 0;
						for (WorkingTreeNode childNode : childList) {
							WorkingTreeNodeImpl childNodeImpl = (WorkingTreeNodeImpl) childNode;
							childStorageIds[i++] = childNodeImpl.getStorageId();
						}
					} else {
						childStorageIds = node.getStoredChildren().get(childName);
					}
					children.put(childName, childStorageIds);
				}
				node.mutableStoredNode.setChildren(
						storageBackend.writeChildren(children));
			}
//			node.node = new StoredFlatNode(node.mutableStoredNode);
			WorkingTreeNodeImpl parent = node.getParent();
			// parent must always be mutable right here?!
			if (parent != null && parent.mutableStoredNode != null) {
				node.mutableStoredNode.setParentId(parent.mutableStoredNode.getStorageId());
			}
			node.node = storageBackend.writeNode(node.mutableStoredNode);
			node.mutableStoredNode = null;
			node.changedChildren = false;
			changedNodes.remove(node);
		}
		if (!changedNodes.isEmpty()) {
			// i think if nodes are deleted/detached, changedNodes might not get empty..
//			throw new RuntimeException("changedNodes must be empty after saving everything." + changedNodes);
			changedNodes.clear();
		}
		return null;
	}

	private Set<WorkingTreeNodeImpl> findNodesToUpdate() {
		HashSet<WorkingTreeNodeImpl> toUpdate = new HashSet<>();
		
		// check all changed nodes and make sure their parents
		// are also updated and attached to root.
		for (WorkingTreeNodeImpl changedNode : changedNodes) {
			ArrayList<WorkingTreeNodeImpl> changed = new ArrayList<WorkingTreeNodeImpl>();
			
			WorkingTreeNodeImpl node = changedNode;
			boolean first = true;
			
			while (true) {
				// node has changed and is attached to root.
				if (toUpdate.contains(node)) {
					toUpdate.addAll(changed);
					break;
				}
				
				if (first) {
					first = false;
				} else {
					node.changedChildren = true;
				}
				changed.add(node);
				
				WorkingTreeNodeImpl p = node.getParent();
				if (p != null) {
					node = p;
				} else {
					// if the last node isn't the root node,
					// the node is detached from the tree and we don't need to write it
					if (node == getRootNode()) {
						toUpdate.addAll(changed);
					}
					break;
				}
			}
		}
		return toUpdate;
	}


}
