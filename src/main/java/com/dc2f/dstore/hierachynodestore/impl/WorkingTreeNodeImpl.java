package com.dc2f.dstore.hierachynodestore.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dc2f.dstore.hierachynodestore.ChildQueryAdapter;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredFlatNode;

public class WorkingTreeNodeImpl implements WorkingTreeNode {

	private @Nonnull WorkingTreeImpl workingTreeImpl;
	@Nullable StoredFlatNode storedNode;
	
	boolean changedChildren = false;
	boolean changedProperties = false;
	
	/**
	 * Mapping between a child's name and their node ids.
	 */
	private StorageId[] storedChildren = null;
	private List<WorkingTreeNode> createdChildren;
	
	boolean isNew = false;
	private WorkingTreeNodeImpl parentNode;
	private Map<String, Property> loadedProperties;

	public WorkingTreeNodeImpl(@Nonnull WorkingTreeImpl workingTreeImpl,
			@Nullable StoredFlatNode flatNode, WorkingTreeNodeImpl parentNode) {
		this.workingTreeImpl = workingTreeImpl;
		this.parentNode = parentNode;
//		if (flatNode == null) {
//			throw new IllegalArgumentException("flatNode must not be null.");
//		}
		this.storedNode = flatNode;
	}
	
	@Nonnull StorageId[] getStoredChildren() {
		StorageId[] ret = storedChildren;
		StoredFlatNode tmpStoredNode = storedNode;
		if (ret == null && tmpStoredNode != null) {
			ret = workingTreeImpl.storageBackend.readChildren(tmpStoredNode.getChildren());
		}
		if (ret == null) {
			ret = new StorageId[0];
		}
		storedChildren = ret;
		return ret;
	}
	
	@Override
	public Iterable<WorkingTreeNode> getChildrenByProperty(@Nonnull String propertyName, Object value) {
		ChildQueryAdapter queryAdapter = workingTreeImpl.storageBackend.getAdapter(ChildQueryAdapter.class);
		
		StorageId storageId = this.getStorageId();
		List<WorkingTreeNode> ret = new ArrayList<>();
		if (storageId != null) {
			// only load children from query adapter, if this is not a new node..
			Iterable<StorageId> flatChildrenIds = queryAdapter.getChildren(storageId, propertyName, value);
			for (StorageId flatChildId : flatChildrenIds) {
				ret.add(workingTreeImpl.getNodeByStorageId(flatChildId, this));
			}
		}
		if (createdChildren != null) {
			for (WorkingTreeNode node : createdChildren) {
				Property propertyValue = node.getProperty(propertyName);
				if (propertyValue != null && value.equals(propertyValue.getObjectValue())) {
					ret.add(node);
				}
			}
		}
		return ret;
		
//		return null;
		
//		List<WorkingTreeNode> ret = new ArrayList<>();
//		List<WorkingTreeNode> myChildren = loadChildren();
//		for (WorkingTreeNode child : myChildren) {
//			if (child.getName().equals(childName)) {
//				ret.add(child);
//			}
//		}
//		return ret;
		
//		List<WorkingTreeNode> childList = children.get(childName);
//		if (childList == null) {
//			StorageId[] storedChildList = getStoredChildren().get(childName);
//			if (storedChildList != null) {
//				childList = new ArrayList<>(storedChildList.length);
//				for (StorageId storedId : storedChildList) {
//					WorkingTreeNode child = workingTreeImpl.getNodeByStorageId(storedId, this);
//					childList.add(child);
//				}
//			} else {
//				childList = new ArrayList<>();
//			}
//			children.put(childName, childList);
//		}
//		return childList;
	}
	
//	@Nonnull
//	List<WorkingTreeNode> loadChildren() {
//		List<WorkingTreeNode> ret = loadedChildren;
//		if (ret == null) {
//			StorageId[] storedChildrenId = getStoredChildren();
//			ret = new ArrayList<>(storedChildrenId.length);
//			for (StorageId childId : storedChildren) {
//				WorkingTreeNode child = workingTreeImpl.getNodeByStorageId(childId, this);
//				ret.add(child);
//			}
//			loadedChildren = ret;
//		}
//		return ret;
//	}
	
	@Override
	public Iterable<WorkingTreeNode> getChildrenByNodeType(
			@Nonnull String nodeTypeName) {
		return getChildrenByProperty(WorkingTree.NAME_NODETYPE, nodeTypeName);
	}
	
	@Override @Nonnull
	public WorkingTreeNode addChild(@Nonnull String childName) {
		WorkingTreeNode child = addChild();
		child.setProperty(Property.PROPERTY_NAME, new Property(childName));

		return child;
	}
	
	@Override
	@Nonnull
	public WorkingTreeNode addChild() {
		WorkingTreeNodeImpl child = new WorkingTreeNodeImpl(workingTreeImpl, null, this);
		child.isNew = true;
		workingTreeImpl.loadedNodes.put(child.getStorageId(), child);
		if (createdChildren == null) {
			createdChildren = new ArrayList<>();
		}
		createdChildren.add(child);
		changedChildren = true;
		workingTreeImpl.notifyNodeChanged(this);
		workingTreeImpl.notifyNodeChanged(child);
		

		return child;
	}
	
	public void setProperty(@Nonnull String propertyName, @Nonnull Property property) {
		// make sure properties are loaded.
		loadProperties();
		loadedProperties.put(propertyName, property);
		changedProperties = true;
		workingTreeImpl.notifyNodeChanged(this);
	}
	
	@Nonnull Map<String, Property> loadProperties() {
		Map<String, Property> ret = loadedProperties;
		if (ret == null) {
			StoredFlatNode tmpStoredNode = storedNode;
			if (tmpStoredNode == null) {
				ret = loadedProperties = new HashMap<>();
			} else {
				ret = loadedProperties = new HashMap<String, Property>(workingTreeImpl.storageBackend.readProperties(tmpStoredNode.getProperties()));
			}
			changedProperties = false;
		}
		return ret;
	}

//	@Override
//	public Iterable<String> getChildrenNames() {
//		List<WorkingTreeNode> myChildren = loadChildren();
//		Set<String> ret = new HashSet<>(myChildren.size());
//		for (WorkingTreeNode child : myChildren) {
//			ret.add(child.getName());
//		}
//		
//		return ret;
//	}
	
	private WorkingTreeNode getChildByStorageId(
			StorageId storageId) {
		return workingTreeImpl.getNodeByStorageId(storageId, this);
	}

	
	@Override
	@Nonnull
	public Iterable<WorkingTreeNode> getChildren() {
		final StorageId[] myStoredChildren = getStoredChildren();
		final List<WorkingTreeNode> myCreatedChildren = createdChildren;
		final int count = getChildrenCount();
		return new Iterable<WorkingTreeNode>() {
			
			@Override
			public Iterator<WorkingTreeNode> iterator() {
				return new Iterator<WorkingTreeNode>() {
					int pos = -1;

					@Override
					public boolean hasNext() {
						return count > pos+1;
					}

					@Override
					public WorkingTreeNode next() {
						pos++;
						if (myStoredChildren.length > pos) {
							return getChildByStorageId(myStoredChildren[pos]);
						} else {
							return myCreatedChildren.get(pos - myStoredChildren.length);
						}
					}


					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
	@Override
	public int getChildrenCount() {
		if (createdChildren != null) {
			return getStoredChildren().length + createdChildren.size();
		}
		return getStoredChildren().length;
//		return loadChildren().size();
	}
	
	@Nullable
	public Property getProperty(@Nonnull String propertyName) {
		return loadProperties().get(propertyName);
//		Map<String, Property> properties = workingTreeImpl.storageBackend.readProperties(storedNode.getProperties());
//		return properties.get(propertyName);
	}
	
	/**
	 * Get all properties of the node as map.
	 * @return Map of property names to property.
	 */
	@Override @Nonnull
	public Map<String, Property> getProperties() {
		Map<String, Property> ret = Collections.unmodifiableMap(loadProperties());
		if (ret == null) {
			throw new IllegalStateException();
		}
		return ret;
	}

	WorkingTreeNodeImpl getParent() {
		return parentNode;
	}

	@Override
	public StorageId getStorageId() {
		StoredFlatNode tmpStoredNode = storedNode;
		if (tmpStoredNode == null) {
			return null;
		}
		return tmpStoredNode.getStorageId();
	}
	
	
}
