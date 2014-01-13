package com.dc2f.dstore.hierachynodestore.impl.nodetype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.exception.NodeStoreException;
import com.dc2f.dstore.hierachynodestore.impl.WorkingTreeImpl;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeAccessor;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeDefinition;
import com.dc2f.dstore.storage.Property;
import com.google.common.collect.Iterables;

import static com.dc2f.utils.NullUtils.assertNotNull;

// FIXME: We currently create a new NodeTypeDefinitionImpl instance for each accessor call, maybe we should cache it internally?
public class NodeTypeAccessorImpl implements NodeTypeAccessor {
	private @Nonnull WorkingTreeImpl workingTreeImpl;
	private @Nullable WorkingTreeNode rootNode;

	public NodeTypeAccessorImpl(@Nonnull WorkingTreeImpl workingTreeImpl) {
		this.workingTreeImpl = workingTreeImpl;
	}

	@Override
	public @Nonnull Collection<NodeTypeDefinition> listNodeTypeDefinitions() {
		WorkingTreeNode child = getNodeTypeRootNode();
		
		List<NodeTypeDefinition> nodeTypeDefs = new ArrayList<>();
		for (WorkingTreeNode nodeTypeChild : child.getChildrenByNodeType(NodeTypeDefinition.NODETYPE_NAME_NODETYPEDEFINTION)) {
			nodeTypeDefs.add(new NodeTypeDefinitionImpl(assertNotNull(nodeTypeChild)));
		}
		return nodeTypeDefs;
	}

	private @Nonnull WorkingTreeNode getNodeTypeRootNode() {
		WorkingTreeNode nodeTypeRoot = rootNode;
		if (nodeTypeRoot == null) {
			WorkingTreeNode rootNode = workingTreeImpl.getRootNode();
			Iterable<WorkingTreeNode> children = rootNode.getChildrenByProperty(Property.PROPERTY_NAME, WorkingTree.NAME_NODETYPE);
			try {
				nodeTypeRoot = Iterables.getOnlyElement(children);
			} catch (IllegalArgumentException e) {
				// iterable contained too many items?
				throw new NodeStoreException("Too many root nodes with node type name.", e);
			} catch (NoSuchElementException e) {
				// returned iterable was empty
				// we have to create a new root node.
			}
			if (nodeTypeRoot == null) {
				nodeTypeRoot = rootNode.addChild(WorkingTree.NAME_NODETYPE);
			}
			rootNode = nodeTypeRoot;
		}
		return nodeTypeRoot;
	}

	@Override
	@Nonnull
	public NodeTypeDefinition addNodeTypeDefinition(@Nonnull String nodeTypeName) {
		if (getNodeTypeDefinitionByName(nodeTypeName) != null) {
			throw new IllegalArgumentException("Nodetype already exists.");
		}
		WorkingTreeNode root = getNodeTypeRootNode();
		WorkingTreeNode child = root.addChild();
		child.setProperty(Property.PROPERTY_NAME, new Property(nodeTypeName));
		child.setProperty(WorkingTree.NAME_NODETYPE, new Property(NodeTypeDefinition.NODETYPE_NAME_NODETYPEDEFINTION));
		NodeTypeDefinitionImpl nodeTypeDefinition = new NodeTypeDefinitionImpl(child);
		return nodeTypeDefinition;
	}

	@Override
	@Nullable
	public NodeTypeDefinition getNodeTypeDefinitionByName(
			@Nonnull String nodeTypeName) {
		WorkingTreeNode root = getNodeTypeRootNode();
		Iterable<WorkingTreeNode> children = root.getChildrenByProperty(Property.PROPERTY_NAME, new Property(nodeTypeName));
		try {
			WorkingTreeNode node = Iterables.getOnlyElement(children);
			return new NodeTypeDefinitionImpl(assertNotNull(node));
		} catch (IllegalArgumentException e) {
			throw new NodeStoreException("There are two node types of this name.", e);
		} catch (NoSuchElementException e) {
			// empty iterable, no such child.
			return null;
		}
	}
	
	
}
