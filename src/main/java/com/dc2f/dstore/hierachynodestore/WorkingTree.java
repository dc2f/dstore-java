package com.dc2f.dstore.hierachynodestore;


public interface WorkingTree {
	WorkingTreeNode getRootNode();

	Commit commit(String message);
}
