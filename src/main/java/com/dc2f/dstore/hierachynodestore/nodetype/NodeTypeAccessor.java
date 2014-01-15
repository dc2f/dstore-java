package com.dc2f.dstore.hierachynodestore.nodetype;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	 * @return list of all node type definitions.
	 */
	@Nonnull Collection<NodeTypeDefinition> listNodeTypeDefinitions();
	
	/**
	 * Creates a new empty node type definition and returns it.
	 * @param nodeTypeName name of the new node type - must be unique.
	 * @return a new node type defintion.
	 * @throws IllegalArgumentException if a node with that type already exists.
	 */
	@Nonnull NodeTypeDefinition addNodeTypeDefinition(@Nonnull String nodeTypeName) throws IllegalArgumentException;
	
	/**
	 * finds the node type definition by the given name and returns it.
	 * @param nodeTypeName name of the node type
	 * @return node type definition, or null if it does not exist.
	 */
	@Nullable NodeTypeDefinition getNodeTypeDefinitionByName(@Nonnull String nodeTypeName);
}
