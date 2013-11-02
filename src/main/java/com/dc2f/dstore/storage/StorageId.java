package com.dc2f.dstore.storage;

import java.io.Serializable;

/**
 * Unique Id for Nodes, Properties or Children within a storage. Using interface
 * to make it easy to exchange it for another ID system (like storing system
 * prefix&id to save storage space.)
 */
public interface StorageId extends Serializable {
	String getIdString();
}
