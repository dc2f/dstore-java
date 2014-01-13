package com.dc2f.dstore.hierachynodestore.nodetype;

import javax.annotation.Nonnull;

import com.dc2f.dstore.storage.Property.PropertyType;

/**
 * definition of a property of a {@link NodeTypeDefinition}.
 */
public interface PropertyDefinition {
	@Nonnull String getName();
	
	@Nonnull PropertyDefinition setType(@Nonnull PropertyType type);
	@Nonnull PropertyType getType();
	
	@Nonnull PropertyDefinition setRequired(boolean isRequired);
	boolean isRequired();
	
	@Nonnull PropertyDefinition setIndexed(boolean isIndexed);
	boolean isIndexed();
}
