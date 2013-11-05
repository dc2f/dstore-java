package com.dc2f.dstore.hierachynodestore;

import com.dc2f.dstore.storage.StorageId;

public interface WorkingTreeNode {
	WorkingTreeNode addChild(String childName);
	
	Iterable<String> getChildrenNames();

	String getName();

	Iterable<WorkingTreeNode> getChild(String childName);

	StorageId getStorageId();
}
