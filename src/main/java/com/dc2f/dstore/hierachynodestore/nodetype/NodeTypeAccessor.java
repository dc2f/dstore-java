package com.dc2f.dstore.hierachynodestore.nodetype;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.dc2f.dstore.hierachynodestore.WorkingTree;

/**
 * Convenience interface to access node types in a working tree of a repository. Allows reflection and editing
 * of node types.
 * 
 * 
 * @see WorkingTree#getNodeTypeAccessor()
 */
public interface NodeTypeAccessor {
	/**
	 * find all node type definitions in the current working tree and return them.
	 * TODO: really all, or just those within /:nodetypes/ ??
	 * @return list of all node type definnitions.
	 */
	@Nonnull Collection<NodeTypeDefinition> listNodeTypeDefinitions();
	
	
}
