package com.dc2f.dstore.hierachynodestore;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;

public interface WorkingTreeNode {
	
	/**
	 * Create a child with the given name to this node.
	 * @param childName - name of the child to create.
	 * @return the created child for this node
	 */
	@Nonnull
	WorkingTreeNode addChild(@Nonnull String childName);
	
	@Nonnull
	WorkingTreeNode addChild();
	
	
	@Nonnull
	Iterable<WorkingTreeNode> getChildren();
	int getChildrenCount();

	@Nullable
	Property getProperty(@Nonnull String name);
	
	void setProperty(@Nonnull String name, @Nonnull Property value);
	
	@Nonnull
	Map<String, Property> getProperties();	
	
	Iterable<WorkingTreeNode> getChildrenByProperty(@Nonnull String propertyName, Object value);

	/**
	 * @return the storage id of the node. might return null, if it was not yet committed.
	 */
	@Nullable StorageId getStorageId();
}
