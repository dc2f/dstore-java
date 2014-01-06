package com.dc2f.dstore.hierachynodestore;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;

public interface WorkingTreeNode {
	@Nonnull
	WorkingTreeNode addChild(String childName);
	
	
	@Nonnull
	Iterable<WorkingTreeNode> getChildren();
	int getChildrenCount();

	@Nullable
	Property getProperty(String name);
	
	void setProperty(@Nonnull String name, @Nonnull Property value);
	
	@Nonnull
	Map<String, Property> getProperties();	
	
	Iterable<WorkingTreeNode> getChild(String childName);

	StorageId getStorageId();
}
