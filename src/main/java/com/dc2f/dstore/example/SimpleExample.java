package com.dc2f.dstore.example;

import java.io.File;

import com.dc2f.dstore.hierachynodestore.Commit;
import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.WorkingTreeUtils;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.flatjsonfiles.SlowJsonFileStorageBackend;
import com.dc2f.dstore.storage.map.HashMapStorage;

public class SimpleExample {
	public static void main(String[] args) {
		HierarchicalNodeStore nodeStore = new HierarchicalNodeStore(new HashMapStorage());
		writeToStore(nodeStore);
		
//		SlowJsonFileStorageBackend persistentNodeStore = new SlowJsonFileStorageBackend(new File("tmpwd"));
//		writeToStore(new HierarchicalNodeStore(persistentNodeStore));
	}
	
	public static void writeToStore(HierarchicalNodeStore nodeStore) {
		
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		
		WorkingTreeNode rootNode1 = wt1.getRootNode();
		WorkingTreeNode a = rootNode1.addChild("A");
		WorkingTreeNode b = rootNode1.addChild("B");
		
		WorkingTreeNode a1 = a.addChild("A1");
		a.addChild("A2");
		
		WorkingTreeNode b1 = b.addChild("B1");
		b.addChild("B2");
		
		analyze("created some nodes in wt1", wt1);
		
		
		WorkingTree wt2 = nodeStore.checkoutBranch("master");
		analyze("new wt2 must see only original root node", wt2);
		
		System.out.println("(1) commit wt1");
		Commit c1 = wt1.commit("");
		analyze("root node id of wt1 must have changed", wt1);
		
		System.out.println("(2) commit wt1 a second time without changing anything");
		Commit c2 = wt1.commit("");
		
		analyze("nothing has to be changed by the second commit", wt1);
		
		analyze("wt2 must not see any changes", wt2);
		
		b1.addChild("B11");
		b1.setProperty("testproperty", new Property("Hello World"));
		Commit c3 = wt1.commit("");
		
		analyze("(3) B1, B, and root node must have changed after adding B11", wt1);
		
		WorkingTree wt3 = nodeStore.checkoutBranch("master");
		
		analyze("even a new wt3 of master must not see any changes", wt3);
		
		WorkingTree wtC1 = nodeStore.checkoutCommit(c1);
		WorkingTree wtC2 = nodeStore.checkoutCommit(c2);
		WorkingTree wtC3 = nodeStore.checkoutCommit(c3);
		
		analyze("new wtC1 of commit1 must be the same as before in (1)", wtC1);
		analyze("new wtC2 of commit2 must be the same as before in (2)", wtC2);
		analyze("new wtC2 of commit3 must be the same as before in (3)", wtC3);

		
//		a1.addChild("A11");
//		wt1.commit("");
//		
//		analyze("Committed A11", wt1);
	}

	private static void analyze(String message, WorkingTree wt) {
		System.out.println();
		System.out.println(message);
		System.out.println(WorkingTreeUtils.debugRecursiveTree(wt.getRootNode()));
	}
}
