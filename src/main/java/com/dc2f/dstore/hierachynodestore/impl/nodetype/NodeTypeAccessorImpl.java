package com.dc2f.dstore.hierachynodestore.impl.nodetype;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.dc2f.dstore.hierachynodestore.impl.WorkingTreeImpl;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeAccessor;
import com.dc2f.dstore.hierachynodestore.nodetype.NodeTypeDefinition;


public class NodeTypeAccessorImpl implements NodeTypeAccessor {
	@SuppressWarnings("unused")
	private @Nonnull WorkingTreeImpl workingTreeImpl;

	public NodeTypeAccessorImpl(@Nonnull WorkingTreeImpl workingTreeImpl) {
		this.workingTreeImpl = workingTreeImpl;
	}

	@Override
	public @Nonnull Collection<NodeTypeDefinition> listNodeTypeDefinitions() {
		throw new UnsupportedOperationException();
	}
}
