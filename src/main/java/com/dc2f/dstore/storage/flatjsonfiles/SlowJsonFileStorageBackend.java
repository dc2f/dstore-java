package com.dc2f.dstore.storage.flatjsonfiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc2f.dstore.hierachynodestore.ChildQueryAdapter;
import com.dc2f.dstore.hierachynodestore.StorageAdapter;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;
import com.dc2f.dstore.storage.StoredFlatNode;
import com.dc2f.dstore.storage.simple.SimpleStringStorageId;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class SlowJsonFileStorageBackend implements StorageBackend {
	
	private final static String BRANCH_STORAGE_ID = "branchstorage";
	private Logger logger = LoggerFactory.getLogger(SlowJsonFileStorageBackend.class);
	
	private static final String FILE_TYPE_MISC = "misc";
	private static final String FILE_TYPE_CHILDREN = "children";
	private static final String FILE_TYPE_PROPERTIES = "properties";
	private static final String FILE_TYPE_NODE = "node";
	private static final String FILE_TYPE_COMMIT = "commit";

	private File flatStoreDir;

	public SlowJsonFileStorageBackend(File path) {
		flatStoreDir = new File(path, "flatstore");
		flatStoreDir.mkdirs();
		if (!flatStoreDir.exists()) {
			throw new RuntimeException("target directory does not exist. " + flatStoreDir.getAbsolutePath());
		}
	}

	@Override
	public @Nonnull StorageId generateStorageId() {
		return SimpleStringStorageId.generateRandom();
	}

	@Override
	public StorageId storageIdFromString(String idString) {
		return new SimpleStringStorageId(idString);
	}

	@Override
	public StoredCommit readCommit(StorageId id) {
		try {
			JSONObject commitInfo = readFile(id, FILE_TYPE_COMMIT);
			if (commitInfo == null) {
				return null;
			}
			StorageId storageId = readStorageId(commitInfo.getString("storageId"));
			StorageId[] parents = readStorageIdArray(commitInfo.optJSONArray("parents"));
			StorageId rootNode = readStorageId(commitInfo.getString("rootNode"));
			return new StoredCommit(storageId, parents, rootNode);
		} catch (JSONException e) {
			logger.error("Error while reading commit.", e);
		}
		return null;
	}

	@Override
	public void writeCommit(StoredCommit commit) {
		try {
			JSONObject commitInfo = new JSONObject();
			commitInfo.put("storageId", commit.getId());
			commitInfo.put("parents", storageIdArrayToJsonArray(commit.getParents()));
			commitInfo.put("rootNode", storeStorageId(commit.getRootNode()));
			writeFile(commit.getId(), commitInfo, FILE_TYPE_COMMIT);
		} catch(JSONException e) {
			logger.error("error writing commit", e);
		}
	}

	@Override
	public StoredCommit readBranch(String name) {
		SimpleStringStorageId branchesStorageId = new SimpleStringStorageId(BRANCH_STORAGE_ID);
		JSONObject branches = readFile(branchesStorageId, FILE_TYPE_MISC);
		try {
			StorageId commitId = readStorageId(branches.getString(name));
			return readCommit(commitId);
		} catch (JSONException e) {
			logger.error("Error while loading branch.", e);
			return null;
		}
	}

	@Override
	public void writeBranch(String name, StoredCommit commit) {
		SimpleStringStorageId branchesStorageId = new SimpleStringStorageId(BRANCH_STORAGE_ID);
		JSONObject branches = readFile(branchesStorageId, FILE_TYPE_MISC);
		if (branches == null) {
			branches = new JSONObject();
		}
		try {
			branches.put(name, storeStorageId(commit.getId()));
			writeFile(branchesStorageId, branches, FILE_TYPE_MISC);
		} catch (JSONException e) {
			logger.error("Error writing branch.", e);
		}
	}

	@Override
	public StoredFlatNode readNode(StorageId id) {
		System.out.println("Reading node {" + id + "}");
		JSONObject obj = readFile(id, FILE_TYPE_NODE);
		StorageId children = readStorageId(obj.optString("children", null));
		StorageId properties = readStorageId(obj.optString("properties", null));
		return new StoredFlatNode(id, children, properties);
	}
	
	private StorageId readStorageId(String string) {
		if (string == null) {
			return null;
		}
		return new SimpleStringStorageId(string);
	}

	private String storeStorageId(StorageId id) {
		if (id == null) {
			return null;
		}
		return id.getIdString();
	}

	@Override
	public StoredFlatNode writeNode(StoredFlatNode node) {
		try {
			System.out.println("Writing node {" + node.getStorageId() + "}");
			JSONObject obj = new JSONObject();
			obj.put("children", storeStorageId(node.getChildren()));
			obj.put("properties", storeStorageId(node.getProperties()));
			writeFile(node.getStorageId(), obj, FILE_TYPE_NODE);
			StoredFlatNode newNode = new StoredFlatNode(node);
			return newNode;
		} catch (JSONException e) {
			logger.error("Error writing node.", e);
			return null;
		}
	}

	@Override
	public StorageId[] readChildren(StorageId childrenStorageId) {
		if (childrenStorageId == null) {
			return null;
		}
		JSONObject tmp = readFile(childrenStorageId, FILE_TYPE_CHILDREN);
		
		try {
			JSONArray arr = tmp.getJSONArray("children");
			return readStorageIdArray(arr);
		} catch(JSONException e) {
			logger.error("Error while reading children.", e);
			return null;
		}
	}

	private StorageId[] readStorageIdArray(JSONArray arr) throws JSONException {
		if (arr == null) {
			return null;
		}
		StorageId[] children = new StorageId[arr.length()];
		for (int i = 0 ; i < children.length ; i++) {
			children[i] = new SimpleStringStorageId(arr.getString(i));
		}
		return children;
	}

	@Override
	public StorageId writeChildren(StorageId[] children) {
		JSONObject tmp = new JSONObject();
		try {
			tmp.put("children", storageIdArrayToJsonArray(children));
			StorageId childrenStorageId = generateStorageId();
			writeFile(childrenStorageId, tmp, FILE_TYPE_CHILDREN);
			return childrenStorageId;
		} catch (JSONException e) {
			logger.error("Error while storing children.", e);
		}
		return null;
	}
	

	@Override
	public StorageId writeProperties(Map<String, Property> properties) {
		JSONObject tmp = new JSONObject();
		for (Map.Entry<String, Property> entry : properties.entrySet()) {
			Property property = entry.getValue();
			try {
				switch (property.getPropertyType()) {
				case STRING:
					tmp.put(entry.getKey(), property.getString());
					break;
				case DOUBLE:
					tmp.put(entry.getKey(), property.getDouble());
					break;
				case LONG:
					tmp.put(entry.getKey(), property.getLong());
					break;
				}
			} catch (JSONException e) {
				logger.error("Error while storing property", e);
				throw new RuntimeException("Error while storing property", e);
			}
		}
		StorageId propertyId = generateStorageId();
		writeFile(propertyId, tmp, FILE_TYPE_PROPERTIES);
		return propertyId;
	}

	@SuppressWarnings("null")
	@Override @Nonnull
	public Map<String, Property> readProperties(StorageId propertiesStorageId) {
		JSONObject tmp = readFile(propertiesStorageId, FILE_TYPE_PROPERTIES);
		Map<String, Property> props = new HashMap<String, Property>();
		for (String key : JSONObject.getNames(tmp)) {
			try {
				Object value = tmp.get(key);
				Property prop = new Property(value);
				props.put(key, prop);
			} catch (JSONException e) {
				logger.error("This should never happen, as we only iterate through existing properties.", e);
			}
		}
		return Collections.unmodifiableMap(props);
	}
	
	
	private JSONArray storageIdArrayToJsonArray(
			StorageId[] children) {
		if (children == null) {
			return null;
		}
		JSONArray ret = new JSONArray();
		for (StorageId child : children) {
			ret.put(child.getIdString());
		}
		return ret;
	}

	private void writeFile(StorageId store, JSONObject json, String type) {
		try {
			FileWriter output = new FileWriter(getFileForStorageId(store, type));
			output.write(json.toString());
			output.close();
		} catch (IOException e) {
			logger.error("Error while storing json into file.", e);
			return;
		}
	}

	private File getFileForStorageId(StorageId storageId, String type) {
		return new File(flatStoreDir, type + "." + storageId.getIdString() + ".txt");
	}

	private JSONObject readFile(StorageId storageId, String type) {
		try {
			File file = getFileForStorageId(storageId, type);
			if(!file.exists()) {
				return null;
			}
			
			String content = Files.toString(file, Charsets.UTF_8);
			return new JSONObject(content);
		} catch (IOException | JSONException e) {
			logger.error("Error while reading json file.", e);
			throw new RuntimeException("Error while reading json file.", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends StorageAdapter> T getAdapter(Class<T> adapterInterface) {
		if (adapterInterface.isAssignableFrom(ChildQueryAdapter.class)) {
			// FIXME this should be cached, only one instance is necessary.
			return (T) new SlowChildQueryAdapter(this);
		}
		return null;
	}

}
