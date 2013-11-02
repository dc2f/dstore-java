package com.dc2f.dstore.hierachynodestore;

import java.util.List;

public interface WorkingTreeNode {
	WorkingTreeNode addChild(String childName);
	
	Iterable<String> getChildrenNames();

	String getName();

	List<WorkingTreeNode> getChild(String childName);
}
