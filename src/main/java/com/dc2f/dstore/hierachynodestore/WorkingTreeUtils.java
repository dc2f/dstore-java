package com.dc2f.dstore.hierachynodestore;

import java.util.Map;

import com.dc2f.dstore.storage.Property;
import com.google.common.base.Strings;

public class WorkingTreeUtils {
	private static final String LINE_SEPARATOR = "\n";
	
	private static void debugRecursiveTree(WorkingTreeNode rootNode, StringBuilder builder, int depth) {
		builder.append(Strings.repeat(" ", depth));
		builder.append("- ");
		builder.append(rootNode.getProperty(Property.PROPERTY_NAME));
		builder.append(" (");
		builder.append(rootNode.getStorageId().getIdString());
		builder.append(")");
		Map<String, Property> props = rootNode.getProperties();
		builder.append(" props=");
		builder.append(props.toString());
		builder.append(LINE_SEPARATOR);
		
		for (WorkingTreeNode child : rootNode.getChildren()) {
			debugRecursiveTree(child, builder, depth+1);
		}
	}
	public static String debugRecursiveTree(WorkingTreeNode rootNode) {
		StringBuilder tmp = new StringBuilder();
		debugRecursiveTree(rootNode, tmp, 0);
		return tmp.toString();
	}
}
