package com.dc2f.dstore.hierachynodestore;

import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;

public interface WorkingTreeNode {
	WorkingTreeNode addChild(String childName);
	
	Iterable<String> getChildrenNames();

	String getName();

	Property getProperty(String name);
	
	Iterable<WorkingTreeNode> getChild(String childName);

	StorageId getStorageId();
}
