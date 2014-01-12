package com.dc2f.dstore.hierachynodestore.nodetype;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;

/**
 * Defines one node type. Each node in the data store can be of exactly one (currently) node type.
 * TODO: maybe we should also extend {@link WorkingTreeNode}?
 */
public interface NodeTypeDefinition {
	
	@Nonnull Collection<PropertyDefinition> listPropertyDefinitions();
	/**
	 * creates a new property definition.
	 * @return a new property definition object which was added to the current working tree.
	 */
	@Nonnull PropertyDefinition addPropertyDefinition(@Nonnull String name);
	
	
	/**
	 * @return a list of all node types which can be added to an instance of this node type.
	 */
	@Nonnull Collection<NodeTypeDefinition> listAllowedChildNodeTypes();
}
