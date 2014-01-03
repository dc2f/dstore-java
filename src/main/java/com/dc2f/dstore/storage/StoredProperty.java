package com.dc2f.dstore.storage;

/**
 * A property which can be stored into a node.
 * Reason for this beeing a stored property: <br>
 * On this layer we only have: String, long, double (blob?) - But further above we might support other data types?
 * e.g. Date<br><br>
 * Every possible type currently has it's own constructor and it's own set/get method.. which is kind of ugly
 * ... and we always have a String, long and double.. which is probably quite memory intensive..
 * 
 * @deprecated use {@link Property} instead.
 */
@Deprecated
public class StoredProperty {
	private String stringValue;
	private long longValue;
	private double doubleValue;

	public StoredProperty(String stringValue) {
		this.stringValue = stringValue;
	}
	public StoredProperty(long longValue) {
		this.longValue = longValue;
	}
	public StoredProperty(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	
	
	public String getStringValue() {
		return stringValue;
	}
	
	public long getLongValue() {
		return longValue;
	}
	
	public double getDoubleValue() {
		return doubleValue;
	}
}
