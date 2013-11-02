package com.dc2f.dstore.hierachynodestore.impl;

import java.util.ArrayList;
import java.util.List;

import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;

public class WorkingTreeImpl implements WorkingTree {

	private HierarchicalNodeStore hierarchicalNodeStore;
	private StoredCommit headCommit;
	private String branchName;
	StorageBackend storageBackend;
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
		return new WorkingTreeNodeImpl(this, storageBackend.readNode(rootNodeId));
	}

	public void notifyNodeChanged(WorkingTreeNodeImpl workingTreeNodeImpl) {
		changedNodes.add(workingTreeNodeImpl);
	}

}
