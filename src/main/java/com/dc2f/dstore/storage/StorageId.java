package com.dc2f.dstore.storage;


/**
 * Unique Id for Nodes, Properties or Children within a storage. Using interface
 * to make it easy to exchange it for another ID system (like storing system
 * prefix&id to save storage space.)
 * 
 * Storage ids must always be created by the storage backend and are not
 * interchangeable between storage implementations.
 * 
 * StorageIds MUST have a useful .hashCode and .equals implementation.
 * TODO: We should probably define them to be Serializable, because it should be usable as a cache key!
 */
public interface StorageId {
	
	/**
	 * Converts the storage id into a global unique string representation.
	 */
	String getIdString();
}
