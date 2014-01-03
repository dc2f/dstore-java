package com.dc2f.dstore.storage.flatjsonfiles;

import java.util.ArrayList;

import com.dc2f.dstore.hierachynodestore.ChildQueryAdapter;
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
		StorageId[] children = storageBackend.readChildren(parentNode.getChildren());
		ArrayList<StorageId> ret = new ArrayList<StorageId>();
		if (children == null) {
			return ret;
		}
		for (StorageId childId : children) {
			StoredFlatNode childNode = storageBackend.readNode(childId);
			if (childNode.getName().equals(value)) {
				ret.add(childId);
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
