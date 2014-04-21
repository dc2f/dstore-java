package com.dc2f.dstore.hierachynodestore.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

import com.dc2f.dstore.hierachynodestore.Commit;
import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.impl.nodetype.NodeTypeAccessorImpl;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeAccessor;
import com.dc2f.dstore.storage.MutableStoredFlatNode;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;
import com.dc2f.dstore.storage.StoredFlatNode;

@Slf4j
public class WorkingTreeImpl implements WorkingTree {

	/**
	 * Reference back to the hierarchical node store in case we need it(?)
	 */
	@SuppressWarnings("unused")
	private @Nonnull HierarchicalNodeStore hierarchicalNodeStore;
	private StoredCommit headCommit;
	private String branchName;
	private @Nullable NodeTypeAccessor nodeTypeAccessor;
	StorageBackend storageBackend;
	Map<StorageId, WorkingTreeNodeImpl> loadedNodes = new HashMap<>();
	private List<WorkingTreeNodeImpl> changedNodes = new ArrayList<>();

	public WorkingTreeImpl(@Nonnull HierarchicalNodeStore hierarchicalNodeStore,
			StorageBackend storageBackend, StoredCommit headCommit, String branchName) {
		this.hierarchicalNodeStore = hierarchicalNodeStore;
		this.storageBackend = storageBackend;
		this.headCommit = headCommit;
		this.branchName = branchName;
	}

	@Override
	public @Nonnull WorkingTreeNode getRootNode() {
		StorageId rootNodeId = headCommit.getRootNode();
		return getNodeByStorageId(rootNodeId, null);
//		return new WorkingTreeNodeImpl(this, storageBackend.readNode(rootNodeId));
	}
	
	public @Nonnull WorkingTreeNode getNodeByStorageId(StorageId nodeStorageId, WorkingTreeNodeImpl parentNode) {
		// TODO shouldn't we add caching right here?
		WorkingTreeNodeImpl ret = loadedNodes.get(nodeStorageId);
		if (ret == null) {
			StoredFlatNode flatStoredNode = storageBackend.readNode(nodeStorageId);
			if (flatStoredNode == null) {
				throw new RuntimeException("Unable to find node with storage id {" + nodeStorageId + "}");
			}
			ret = new WorkingTreeNodeImpl(this, flatStoredNode, parentNode);
			loadedNodes.put(nodeStorageId, ret);
		}
		return ret;
	}

	public void notifyNodeChanged(WorkingTreeNodeImpl workingTreeNodeImpl) {
		changedNodes.add(workingTreeNodeImpl);
	}

	@Override
	public @Nonnull Commit commit(String message) {
		if (message == null) {
			message = "";
		}
		WorkingTreeNode oldRootNode = getRootNode();
		Set<WorkingTreeNodeImpl> nodesToUpdate = findNodesToUpdate();
		Map<WorkingTreeNode, MutableStoredFlatNode> storedFlatNodeMappings = new HashMap<>();
		log.trace("nodesToUpdate: {} ({})", new Object[]{nodesToUpdate, changedNodes});
		// give the ones to update a new id before storing, otherwise child ids won't match
		// FIXME: move mutable stored node into this method (e.g. by using a map)
		for (WorkingTreeNodeImpl node : nodesToUpdate) {
			if (!node.isNew) {
				// storedNode must not be null, if this is an existing node.
				StoredFlatNode tmpStoredNode = node.storedNode;
				assert tmpStoredNode != null;
				storedFlatNodeMappings.put(node,
						new MutableStoredFlatNode(storageBackend.generateStorageId(), tmpStoredNode));
//				node.createMutableStoredNode(storageBackend.generateStorageId());
//				node.node.setStorageId();
			} else {
				storedFlatNodeMappings.put(node, new MutableStoredFlatNode(storageBackend.generateStorageId()));
			}
		}

		// write all changed nodes to storage.
		for (WorkingTreeNodeImpl node : nodesToUpdate) {
			MutableStoredFlatNode mutableStoredNode = storedFlatNodeMappings.get(node);
			if (node.changedChildren) {
				Iterable<WorkingTreeNode> nodeChildren = node.getChildren();
				StorageId[] childStorageIds = new StorageId[node.getChildrenCount()];
				int i = 0;
				for (WorkingTreeNode childNode : nodeChildren) {
					WorkingTreeNodeImpl childNodeImpl = (WorkingTreeNodeImpl) childNode;
					MutableStoredFlatNode tmpMutableStoredFlatNode = storedFlatNodeMappings.get(childNodeImpl);
					if (tmpMutableStoredFlatNode != null) {
						childStorageIds[i++] = tmpMutableStoredFlatNode.getStorageId();
					} else {
						childStorageIds[i++] = childNodeImpl.getStorageId();
					}
				}
				mutableStoredNode.setChildren(
						storageBackend.writeChildren(childStorageIds));
			}
			if (node.changedProperties) {
				Map<String, Property> nodeProperties = node.loadProperties();
				StorageId nodePropertiesId = storageBackend.writeProperties(nodeProperties);
				mutableStoredNode.setProperties(nodePropertiesId);
			}
//			node.node = new StoredFlatNode(node.mutableStoredNode);
//			WorkingTreeNodeImpl parent = node.getParent();
//			// parent must always be mutable right here?!
//			if (parent != null && parent.mutableStoredNode != null) {
//				node.mutableStoredNode.setParentId(parent.mutableStoredNode.getStorageId());
//			}
//			if (parent != null && parent.mutableStoredNode == null) {
//				node.mutableStoredNode.setParentId(parent.node.getStorageId());
////				throw new RuntimeException("we have to recursively change parent id, and mutableStorageNode must therefore never be null." + parent);
//			}
			StoredFlatNode oldStoredNode = node.storedNode;
			if (oldStoredNode != null) {
				// remove old storage id from cache.
				loadedNodes.remove(oldStoredNode.getStorageId());
			}
			node.storedNode = storageBackend.writeNode(mutableStoredNode);
			node.isNew = false;
			node.changedChildren = false;
			node.changedProperties = false;
			changedNodes.remove(node);
			loadedNodes.put(node.getStorageId(), node);
		}
		if (!changedNodes.isEmpty()) {
			// i think if nodes are deleted/detached, changedNodes might not get empty..
//			throw new RuntimeException("changedNodes must be empty after saving everything." + changedNodes);
			changedNodes.clear();
		}
		StoredCommit storedCommit = new StoredCommit(storageBackend.generateStorageId(), new StorageId[]{headCommit.getId()}, oldRootNode.getStorageId());
		headCommit = storedCommit;
		storageBackend.writeCommit(storedCommit);
		if (branchName != null) {
			// user has checked out a branch, so make sure to set branch to this commit.
			storageBackend.writeBranch(branchName, storedCommit);
		}
		return new CommitImpl(headCommit);
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
					log.trace("already in toUpdate. {}  ---  {}", new Object[]{node, changed});
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
						log.debug("found the root node {}", new Object[]{changed});
						toUpdate.addAll(changed);
					} else {
						log.debug("node is detached from root node. {} ({}) / root: ({})", new Object[]{node, node.getStorageId(), getRootNode().getStorageId()});
					}
					break;
				}
			}
		}
		return toUpdate;
	}

	@Override
	@Nonnull
	public NodeTypeAccessor getNodeTypeAccessor() {
		NodeTypeAccessor ret = nodeTypeAccessor;
		if (ret == null) {
			nodeTypeAccessor = ret = new NodeTypeAccessorImpl(this);
		}
		return ret;
	}


}
