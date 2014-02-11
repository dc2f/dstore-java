package com.dc2f.dstore.storage;

import javax.annotation.Nonnull;

public class Property {
	/**
	 * default property name for a "name" property of a node.
	 */
	@Nonnull public static final String PROPERTY_NAME = "name";
	public static enum PropertyType {
		@Nonnull LONG(Long.class),
		@Nonnull DOUBLE(Double.class),
		@Nonnull STRING(String.class),
		@Nonnull BOOLEAN(Boolean.class),
		@Nonnull BYTE(byte[].class);
		
		
		private Class<?> valueClass;

		private PropertyType(Class<?> valueClass) {
			this.valueClass = valueClass;
		}
		
		protected Class<?> getValueClass() {
			return valueClass;
		}
	}
	
	private PropertyType type;
	private @Nonnull Object objValue;
	
	public Property(@Nonnull Object value) throws IllegalStateException {
		setObjValue(value);
		objValue = value;
	}
	
	public PropertyType getPropertyType() {
		return type;
	}
	
	private void assertType(PropertyType expectedType) {
		if (this.type != expectedType
				|| !expectedType.getValueClass().isAssignableFrom(objValue.getClass())) {
			throw new IllegalStateException("Requested {" + expectedType
					+ "} but has {" + this.type + "} - of type: {" + objValue.getClass() + "}");
		}
	}
	
	public long getLong() {
		assertType(PropertyType.LONG);
		return (Long) objValue;
	}
	
//	public void setLong(long value) {
//		setObjValue(value);
//	}
	
	private void setObjValue(@Nonnull Object value) {
		for (PropertyType type : PropertyType.values()) {
			if (type.getValueClass().isAssignableFrom(value.getClass())) {
				this.type = type;
				this.objValue = value;
				return;
			}
		}
		throw new IllegalStateException("Unable to find correct object type for value {" + value + "}");
	}

	public double getDouble() {
		assertType(PropertyType.DOUBLE);
		return (Double) objValue;
	}
	
	public @Nonnull String getString() {
		assertType(PropertyType.STRING);
		return (String) objValue;
	}
	
	public boolean getBoolean() {
		assertType(PropertyType.BOOLEAN);
		return ((Boolean) objValue).booleanValue();
	}
	
//	public void setString(String value) {
//		setObjValue(value);
//	}

//	/**
//	 * 
//	 * @param value value object, must be of one of the types of {@link PropertyType}.
//	 * @throws IllegalStateException if an invalid type was passed as value.
//	 */
//	public void setObjectValue(Object value) throws IllegalStateException {
//		setObjValue(value);
//	}
	
	/**
	 * @return the current value as object, is one of the {@link PropertyType}.
	 */
	@Nonnull
	public Object getObjectValue() {
		return objValue;
	}
	
	@Override
	public String toString() {
		return objValue.toString();
	}
}
