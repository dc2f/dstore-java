package com.dc2f.dstore.hierachynodestore.nodetype;

import javax.annotation.Nonnull;

import com.dc2f.dstore.storage.Property.PropertyType;

/**
 * definition of a property of a {@link NodeTypeDefinition}.
 */
public interface PropertyDefinition {
	@Nonnull String getName();
	@Nonnull PropertyType getType();
	
	void setRequired(boolean isRequired);
	boolean isRequired();
	
	void setIndexed(boolean isIndexed);
	boolean isIndexed();
}
