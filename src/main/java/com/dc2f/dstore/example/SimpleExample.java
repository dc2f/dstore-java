package com.dc2f.dstore.example;

import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.WorkingTreeUtils;
import com.dc2f.dstore.storage.map.HashMapStorage;

public class SimpleExample {
	public static void main(String[] args) {
		HierarchicalNodeStore nodeStore = new HierarchicalNodeStore(new HashMapStorage());
		
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		
		WorkingTreeNode rootNode1 = wt1.getRootNode();
		WorkingTreeNode a = rootNode1.addChild("A");
		
		System.out.println(WorkingTreeUtils.debugRecursiveTree(rootNode1));
	}
}
