package com.dc2f.dstore.hierachynodestore.impl.nodetype;

import javax.annotation.Nonnull;

import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.exception.NodeStoreException;
import com.dc2f.dstore.hierachynodestore.nodetype.PropertyDefinition;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.Property.PropertyType;

import static com.dc2f.utils.NullUtils.assertNotNull;

public class PropertyDefinitionImpl implements PropertyDefinition {
	public static final @Nonnull String PROPERTY_NAME = NodeTypeDefinitionImpl.PROPERTY_NAME;
	public static final @Nonnull String PROPERTY_TYPE = ":type";
	public static final @Nonnull String PROPERTY_REQUIRED = ":required";
	public static final @Nonnull String PROPERTY_INDEXED = ":indexed";
	
	private WorkingTreeNode node;

	public PropertyDefinitionImpl(@Nonnull WorkingTreeNode node) {
		this.node = node;
	}

	@Override
	@Nonnull
	public String getName() {
		// a property definition must always have a name..
		return assertNotNull(node.getProperty(PROPERTY_NAME)).getString();
	}
	
	@Override
	public @Nonnull PropertyDefinition setType(@Nonnull PropertyType type) {
		node.setProperty(PROPERTY_TYPE, new Property(assertNotNull(type.name())));
		return this;
	}

	@Override
	@Nonnull
	public PropertyType getType() {
		// property definition must always have a type.
		Property typeProperty = assertNotNull(node.getProperty(PROPERTY_TYPE));
		PropertyType propertyType = PropertyType.valueOf(typeProperty.getString());
		if (propertyType == null) {
			throw new NodeStoreException("Illegal property type {" + typeProperty.getString() + "}");
		}
		return propertyType;
	}

	@Override
	public @Nonnull PropertyDefinition setRequired(boolean isRequired) {
		node.setProperty(PROPERTY_REQUIRED, new Property(new Boolean(isRequired)));
		return this;
	}

	@Override
	public boolean isRequired() {
		Property requiredProperty = node.getProperty(PROPERTY_REQUIRED);
		if (requiredProperty == null) {
			return false;
		}
		return requiredProperty.getBoolean();
	}

	@Override
	public @Nonnull PropertyDefinition setIndexed(boolean isIndexed) {
		node.setProperty(PROPERTY_INDEXED, new Property(new Boolean(isIndexed)));
		return this;
	}

	@Override
	public boolean isIndexed() {
		Property indexedProperty = node.getProperty(PROPERTY_INDEXED);
		if (indexedProperty == null) {
			return false;
		}
		return indexedProperty.getBoolean();
	}

}
