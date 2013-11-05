package com.dc2f.dstore.storage.flatjsonfiles;

import com.dc2f.dstore.storage.ChildQueryAdapter;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StoredFlatNode;

public class SlowChildQueryAdapter implements ChildQueryAdapter {

	public SlowChildQueryAdapter(
			StorageBackend storageBackend) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Iterable<StoredFlatNode> getChildren(String property, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createIndex(String property) {
		// TODO Auto-generated method stub
		
	}

}
