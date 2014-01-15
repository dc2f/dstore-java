package com.dc2f.dstore.hierachynodestore.impl.nodetype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeDefinition;
import com.dc2f.dstore.hierachynodestore.nodetype.PropertyDefinition;
import com.dc2f.dstore.storage.Property;

import static com.dc2f.utils.NullUtils.assertNotNull;

public class NodeTypeDefinitionImpl implements NodeTypeDefinition {
	public static final @Nonnull String PROPERTY_NAME = ":name";
	
	private @Nonnull WorkingTreeNode node;

	public NodeTypeDefinitionImpl(@Nonnull WorkingTreeNode node) {
		// TODO assert node type?
		this.node = node;
	}

	@Override
	@Nonnull
	public Collection<PropertyDefinition> listPropertyDefinitions() {
		List<PropertyDefinition> ret = new ArrayList<>();
		for (WorkingTreeNode child : node.getChildren()) {
			ret.add(new PropertyDefinitionImpl(assertNotNull(child)));
		}
		return assertNotNull(Collections.unmodifiableCollection(ret));
	}

	@Override
	@Nonnull
	public PropertyDefinition addPropertyDefinition(@Nonnull String name) {
		WorkingTreeNode propertyNode = node.addChild();
		propertyNode.setProperty(PROPERTY_NAME, new Property(name));
		propertyNode.setProperty(WorkingTree.NAME_NODETYPE, new Property(NodeTypeDefinition.NODETYPE_NAME_PROPERTYDEFINITION));
		return new PropertyDefinitionImpl(propertyNode);
	}

	@Override
	@Nonnull
	public Collection<NodeTypeDefinition> listAllowedChildNodeTypes() {
		// FIXME implement me :)
		return new ArrayList<>();
	}


}
