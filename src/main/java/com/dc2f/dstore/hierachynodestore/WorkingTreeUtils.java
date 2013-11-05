package com.dc2f.dstore.hierachynodestore;

import com.google.common.base.Strings;

public class WorkingTreeUtils {
	private static final String LINE_SEPARATOR = "\n";
	
	private static void debugRecursiveTree(WorkingTreeNode rootNode, StringBuilder builder, int depth) {
		builder.append(Strings.repeat(" ", depth));
		builder.append("- ");
		builder.append(rootNode.getName());
		builder.append(" (");
		builder.append(rootNode.getStorageId().getIdString());
		builder.append(")");
		builder.append(LINE_SEPARATOR);
		
		for (String childName : rootNode.getChildrenNames()) {
			Iterable<WorkingTreeNode> childList = rootNode.getChild(childName);
			for (WorkingTreeNode child : childList) {
				debugRecursiveTree(child, builder, depth+1);
			}
		}
	}
	public static String debugRecursiveTree(WorkingTreeNode rootNode) {
		StringBuilder tmp = new StringBuilder();
		debugRecursiveTree(rootNode, tmp, 0);
		return tmp.toString();
	}
}
