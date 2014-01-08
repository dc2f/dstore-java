package com.dc2f.dstore.folderimport;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.Property;
import com.google.common.base.Charsets;

/**
 * simple importer which imports a directory structure from the file system into a hierarchical node store.
 */
public class FolderImporter {
	
	private HierarchicalNodeStore nodeStore;

	private static Logger LOG = LoggerFactory.getLogger(FolderImporter.class);
	
	public FolderImporter(HierarchicalNodeStore nodeStore) {
		this.nodeStore = nodeStore;
	}
	
	public void startImport(File rootFolder) {
		try {
			WorkingTree masterBranch = nodeStore.checkoutBranch("master");
			final WorkingTreeNode rootNode = masterBranch.getRootNode();
			final Path rootPath = rootFolder.getAbsoluteFile().toPath();

			Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
				
				private WorkingTreeNode getNode(Path path) {
					Path relativePath = rootPath.relativize(path);
					WorkingTreeNode node = rootNode;
					for (Path pathPart : relativePath) {
						String name = pathPart.toString();
						if ("".equals(name)) {
							continue;
						}
						Iterable<WorkingTreeNode> children = node.getChildrenByProperty("name", name);
						Iterator<WorkingTreeNode> childrenIterator = children.iterator();
						if (childrenIterator.hasNext()) {
							node = childrenIterator.next();
						} else {
							node = node.addChild(name);
						}
						
					}
					return node;
				}
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					WorkingTreeNode fileNode = getNode(file);
					
					if (file.toString().endsWith(".json")) {
						try {
							JSONObject jsonObject = new JSONObject(com.google.common.io.Files.toString(file.toFile(), Charsets.UTF_8));
							String[] names = JSONObject.getNames(jsonObject);
							for (String propertyName : names) {
								assert propertyName != null;
								fileNode.setProperty(propertyName, new Property(jsonObject.get(propertyName)));
							}
						} catch (JSONException e) {
							throw new RuntimeException("unable to read json of file " + file.toString(), e);
						}
					} else {
						fileNode.setProperty("data", new Property(com.google.common.io.Files.toString(file.toFile(), Charsets.UTF_8)));
					}
					return FileVisitResult.CONTINUE;
				}
			});
			
			masterBranch.commit(null);
			LOG.debug("Imported folder {}", rootFolder.getName());
		} catch (IOException e) {
			throw new RuntimeException("Error while importing path.", e);
		}
	}
}
