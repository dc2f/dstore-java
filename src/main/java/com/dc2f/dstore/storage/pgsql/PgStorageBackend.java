package com.dc2f.dstore.storage.pgsql;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.json.JSONException;
import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.dc2f.dstore.hierachynodestore.ChildQueryAdapter;
import com.dc2f.dstore.hierachynodestore.StorageAdapter;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;
import com.dc2f.dstore.storage.StoredFlatNode;
import com.dc2f.dstore.storage.flatjsonfiles.SlowChildQueryAdapter;
import com.dc2f.dstore.storage.simple.WrappedStorageId;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Storage backend storing in a postgresql database.
 */
public class PgStorageBackend implements StorageBackend {

	/**
	 * Datasource to obtain database connections.
	 */
	private ComboPooledDataSource datasource;
	
	/**
	 * SQL helper which uses the datasource.
	 */
	private SQL sql;
	
	/**
	 * The number of local generated storage ids before hitting the database.
	 */
	private int storageIdIncrement = 5000;
	
	/**
	 * The last returned storage id.
	 */
	private Long storageId;
	
	/**
	 * The storage adapters implemented by this backend.
	 */
	private Map<Class<?>, StorageAdapter> adapters = new HashMap<>();
	
	/**
	 * Creates a new postgresql storage backend with the given postgresql configuration.
	 * 
	 * @param host The host to connect.
	 * @param port The port on which to connect.
	 * @param username The postgres username.
	 * @param password The postgres password.
	 */
	public PgStorageBackend(String host, int port, String database, String username, String password) {
		try {
			this.datasource = new ComboPooledDataSource();
			datasource.setDriverClass("org.postgresql.Driver");
			datasource.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
			datasource.setUser(username);
			datasource.setPassword(password);
			
			sql = new SQL(datasource);
			createSchemaIfNotExisting();
		} catch (PropertyVetoException e) {
			throw new RuntimeException("Error initializing the datasource", e);
		}
		
		adapters.put(ChildQueryAdapter.class, new SlowChildQueryAdapter(this));
	}
	
	/**
	 * Creates the database schema if it is not existing.
	 */
	protected void createSchemaIfNotExisting() {
		boolean existing = sql.withStatement("SELECT id FROM Node LIMIT 1", new StatementExecutor<Boolean>() {
			@Override
			public Boolean run(PreparedStatement stmt) throws SQLException {
				try {
					stmt.execute();
					return true;
				} catch (SQLException e) {
					// we have an exception when the node table doesn't exist
					return false;
				}
			}
		});
		
		if(!existing) {
			createSchema();
		}
	}
	
	/**
	 * Drops all tables and other database entities.
	 */
	public void dropSchema() {
		try {
			InputStream in = this.getClass().getClassLoader().getResource("com/dc2f/dstore/storage/pgsql/destroy_schema.sql").openStream();
			sql.executeScript(in);
		} catch (IOException e) {
			throw new RuntimeException("Error while destroying schema", e);
		}
	}
	
	/**
	 * Creates all tables and other needed database entities.
	 */
	public void createSchema() {
		try {
			InputStream in = this.getClass().getClassLoader().getResource("com/dc2f/dstore/storage/pgsql/setup_schema.sql").openStream();
			sql.executeScript(in);
		} catch (IOException e) {
			throw new RuntimeException("Error while creating schema", e);
		}
	}
	
	/**
	 * Extract the long value of a storage id.
	 * @param id The StorageId of which to extract the Long.
	 * @return The extracted Long value.
	 */
	@SuppressWarnings("unchecked")
	private Long id(StorageId id) {
		if(id == null) {
			return null;
		}
		
		return ((WrappedStorageId<Long>) id).getWrappedId();
	}
	
	/**
	 * Create a WrappedStorageId of a Long.
	 * @param id The id to wrap in a storage id.
	 * @return The wrapped storage id.
	 */
	@Nonnull
	private WrappedStorageId<Long> id(Long id) {
		return new WrappedStorageId<Long>(id);
	}
	
	@Override @Nonnull
	public synchronized StorageId generateStorageId() {
		if(storageId != null) {
			storageId++;
		}
		if(storageId == null || storageId % storageIdIncrement == 0) {
			storageId = sql.withStatement("SELECT nextval('storageId_seq') as id", new StatementExecutor<Long>() {
				@Override
				public Long run(PreparedStatement stmt) throws SQLException {
					stmt.execute();
					try(ResultSet rs = stmt.getResultSet()) {
						rs.next();
						return rs.getLong("id");						
					}
				}
			});
		}
		return id(storageId);
	}

	@Override
	public StorageId storageIdFromString(final String idString) {
		final StorageId id = generateStorageId();
		return sql.withStatement("INSERT INTO StorageIdMapping(externalId, internalId) VALUES (?, ?)", new StatementExecutor<StorageId>() {
			@Override
			public StorageId run(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, idString);
				stmt.setLong(2, id(id));
				return id;
			}
		});
	}
	
	@Override
	public StoredCommit readCommit(final StorageId id) {
		return sql.withStatement(
			"SELECT "
			+ "c.id, c.rootNodeId, ch.parentId, c.message "
			+ "FROM Commit c "
			+ "LEFT JOIN CommitHistory ch ON ch.childId = c.id "
			+ "WHERE id = ?", 
				
			new StatementExecutor<StoredCommit>() {
				@Override
				public StoredCommit run(PreparedStatement stmt) throws SQLException {
					stmt.setLong(1, id(id));
					stmt.execute();
					
					Long id, rootNode;
					// String message;
					List<WrappedStorageId<Long>> parents = new LinkedList<>();
					
					try(ResultSet rs = stmt.getResultSet()) {
						if(!rs.next()) {
							return null;
						}
						
						id = rs.getLong("id");
						rootNode = rs.getLong("rootNodeId");
						// message = rs.getString("message");
						do {
							Long parentId = rs.getLong("parentId");
							if(parentId != null) {
								parents.add(new WrappedStorageId<Long>(parentId));
							}
						} while(rs.next());
					}
					
					return new StoredCommit(
							id(id), 
							parents.toArray(new WrappedStorageId[parents.size()]), 
							id(rootNode)
						);
				}
			}
		);
	}
	
	@Override
	public void writeCommit(final StoredCommit commit) {
		sql.withStatement("INSERT INTO Commit (id, rootNodeId, message) values (?, ?, ?)", new StatementExecutor<Void>() {
			@Override
			public Void run(PreparedStatement stmt) throws SQLException {
				stmt.setLong(1, id(commit.getId()));
				stmt.setLong(2, id(commit.getRootNode()));
				stmt.setNull(3, Types.VARCHAR);
				stmt.executeUpdate();
				
				return null;
			}
		});
	}

	@Override
	public StoredCommit readBranch(final String name) {
		StorageId commitId = sql.withStatement("SELECT commitId FROM Branch WHERE name = ?", new StatementExecutor<StorageId>() {
			@Override
			public StorageId run(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, name);
				stmt.execute();
				try(ResultSet rs = stmt.getResultSet()) {
					if(!rs.next()) {
						return null;
					}
					
					return id(rs.getLong("commitId"));
				}
			}
		});
		return readCommit(commitId);
	}

	@Override
	public synchronized void writeBranch(final String name, final StoredCommit commit) {
		boolean exists = sql.withStatement("SELECT id FROM Branch WHERE name = ?", new StatementExecutor<Boolean>() {
			@Override
			public Boolean run(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, name);
				stmt.execute();
				return stmt.getResultSet().next();
			}
		});
		
		if(!exists) {
			sql.withStatement("INSERT INTO Branch (id, name, commitId) VALUES (?, ?, ?)", new StatementExecutor<Void>() {
				@Override
				public Void run(PreparedStatement stmt) throws SQLException {
					stmt.setLong(1, id(generateStorageId()));
					stmt.setString(2, name);
					stmt.setLong(3, id(commit.getId()));
					stmt.executeUpdate();
					return null;
				}
			});
		} else {
			sql.withStatement("UPDATE Branch SET commitId = ? WHERE name = ?", new StatementExecutor<Void>() {
				@Override
				public Void run(PreparedStatement stmt) throws SQLException {
					stmt.setLong(1, id(commit.getId()));
					stmt.setString(2, name);
					stmt.executeUpdate();
					return null;
				}
			});
		}
	}

	@Override
	public StoredFlatNode readNode(final StorageId id) {
		return sql.withStatement("SELECT n.id, n.propertiesId, n.childrenId FROM Node n WHERE id = ?", 
			new StatementExecutor<StoredFlatNode>() {
				@Override
				public StoredFlatNode run(PreparedStatement stmt) throws SQLException {
					stmt.setLong(1, id(id));
					stmt.execute();
					
					try(ResultSet rs = stmt.getResultSet()) {
						if(!rs.next()) {
							return null;
						}
						
						return new StoredFlatNode(id(rs.getLong("id")), id(rs.getLong("childrenId")), id(rs.getLong("propertiesId")));
					}
				}
			}
		);
	}

	@Override
	public StoredFlatNode writeNode(final StoredFlatNode node) {
		return sql.withStatement("INSERT INTO Node (id, propertiesId, childrenId) VALUES (?, ?, ?)", 
			new StatementExecutor<StoredFlatNode>() {
				@Override
				public StoredFlatNode run(PreparedStatement stmt) throws SQLException {
					stmt.setLong(1, id(node.getStorageId()));
					stmt.setLong(2, id(node.getProperties()));
					stmt.setObject(3, id(node.getChildren()));
					stmt.executeUpdate();
					
					return new StoredFlatNode(node);
				}
			}
		);
	}

	@Override
	public StorageId[] readChildren(final StorageId childrenStorageId) {
		if (childrenStorageId == null) {
			return null;
		}
		
		return sql.withStatement("SELECT childId FROM NodeChildren WHERE id = ?", new StatementExecutor<StorageId[]>() {
			@Override
			public StorageId[] run(PreparedStatement stmt) throws SQLException {
				stmt.setLong(1, id(childrenStorageId));
				stmt.execute();
				
				try(ResultSet rs = stmt.getResultSet()) {
					List<StorageId> childrenIds = new LinkedList<>();
					while(rs.next()) {
						childrenIds.add(id(rs.getLong("childId")));
					}
					return childrenIds.toArray(new StorageId[childrenIds.size()]);
				}
			}
		});
	}

	@Override
	public StorageId writeChildren(final StorageId[] children) {
		final StorageId id = generateStorageId();

		return sql.withStatement("INSERT INTO NodeChildren (id, childId) VALUES (?, ?)", new StatementExecutor<StorageId>() {
			@Override
			public StorageId run(PreparedStatement stmt) throws SQLException {
				for(StorageId child : children) {
					stmt.setLong(1, id(id));
					stmt.setLong(2, id(child));
					stmt.addBatch();
				}
				stmt.executeBatch();
				return id;
			}
		});
	}

	@Override
	public StorageId writeProperties(Map<String, Property> properties) {
		final JSONObject json = new JSONObject();
		final StorageId id = generateStorageId();
		
		try {
			for(Entry<String, Property> entry : properties.entrySet()) {
				json.put(entry.getKey(), entry.getValue().getObjectValue());
			}
		} catch (JSONException e) {
			throw new RuntimeException("Error when converting properties to json object", e);
		}
		
		return sql.withStatement("INSERT INTO Properties (id, properties) VALUES (?, ?)", new StatementExecutor<StorageId>() {
			@Override
			public StorageId run(PreparedStatement stmt) throws SQLException {
				PGobject jsonObject = new PGobject();
				jsonObject.setType("json");
				jsonObject.setValue(json.toString());
				
				stmt.setLong(1, id(id));
				stmt.setObject(2, jsonObject);
				stmt.executeUpdate();
				return id;
			}
		});
	}

	@SuppressWarnings("null")
	@Override
	@Nonnull
	public Map<String, Property> readProperties(final StorageId propertiesStorageId) {
		return sql.withStatement("SELECT properties FROM Properties WHERE id = ?", new StatementExecutor<Map<String, Property>>() {
			@Override
			public Map<String, Property> run(PreparedStatement stmt) throws SQLException {
				stmt.setLong(1, id(propertiesStorageId));
				stmt.execute();
				try(ResultSet rs = stmt.getResultSet()) {
					HashMap<String, Property> result = new HashMap<>();
					if(!rs.next()) {
						return result;
					}
					try {
						JSONObject json = new JSONObject(rs.getString("properties"));
						for(String name : JSONObject.getNames(json)) {
							result.put(name, new Property(json.get(name)));
						}
					} catch (JSONException e) {
						throw new RuntimeException("Error while reading properies", e);
					}
					
					return result;
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends StorageAdapter> T getAdapter(Class<T> adapterInterface) {
		return (T) adapters.get(adapterInterface);
	}
}
