package com.dc2f.dstore.hierachynodestore;

import javax.annotation.Nonnull;

import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeAccessor;


public interface WorkingTree {
	@Nonnull WorkingTreeNode getRootNode();

	@Nonnull Commit commit(String message);
	
	@Nonnull NodeTypeAccessor getNodeTypeAccessor();
}
