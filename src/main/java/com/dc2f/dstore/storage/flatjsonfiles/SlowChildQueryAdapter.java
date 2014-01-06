package com.dc2f.dstore.storage.flatjsonfiles;

import java.util.ArrayList;
import java.util.Map;

import com.dc2f.dstore.hierachynodestore.ChildQueryAdapter;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredFlatNode;

public class SlowChildQueryAdapter implements ChildQueryAdapter {

	private StorageBackend storageBackend;

	public SlowChildQueryAdapter(
			StorageBackend storageBackend) {
		this.storageBackend = storageBackend;
	}

	@Override
	public Iterable<StorageId> getChildren(StorageId parent, String property, Object value) {
		StoredFlatNode parentNode = storageBackend.readNode(parent);
		if (parentNode == null) {
			throw new IllegalArgumentException("Unable to find parent node by id {" + parent + "}");
		}
		StorageId[] children = storageBackend.readChildren(parentNode.getChildren());
		ArrayList<StorageId> ret = new ArrayList<StorageId>();
		if (children == null) {
			return ret;
		}
		for (StorageId childId : children) {
			StoredFlatNode childNode = storageBackend.readNode(childId);
			Map<String, Property> props = storageBackend.readProperties(childNode.getProperties());
			Property prop = props.get(property);
			if (prop != null) {
				if (prop.getObjectValue().equals(value)) {
					ret.add(childId);
				}
			}
//			childNode.getProperties()
		}
		return ret;
	}

	@Override
	public void createIndex(String property) {
		// TODO Auto-generated method stub
		
	}

}
