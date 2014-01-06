package com.dc2f.dstore.storage.pgsql;

import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.dc2f.dstore.hierachynodestore.StorageAdapter;
import com.dc2f.dstore.storage.Property;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.storage.StorageId;
import com.dc2f.dstore.storage.StoredCommit;
import com.dc2f.dstore.storage.StoredFlatNode;
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
		} catch (PropertyVetoException e) {
			throw new RuntimeException("Error initializing the datasource", e);
		}
	}
	
	@Override
	public synchronized StorageId generateStorageId() {
		storageId++;
		if(storageId == null || storageId % storageIdIncrement == 0) {
			storageId = sql.withStatement("SELECT nextval('storage_id_seq')", new StatementExecutor<Long>() {
				@Override
				public Long run(PreparedStatement stmt) throws SQLException {
					stmt.execute();
					try(ResultSet rs = stmt.getResultSet()) {
						rs.next();
						return rs.getLong(0);						
					}
				}
			});
		}
		return new WrappedStorageId<Long>(storageId);
	}

	@Override
	public StorageId storageIdFromString(String idString) {
		// FIXME: not implemented
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	private WrappedStorageId<Long> id(StorageId id) {
		return (WrappedStorageId<Long>) id;
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
					stmt.setLong(1, id(id).getWrappedId());
					stmt.execute();
					
					Long id, rootNode;
					String message;
					List<WrappedStorageId<Long>> parents = new LinkedList<>();
					
					try(ResultSet rs = stmt.getResultSet()) {
						if(!rs.next()) {
							return null;
						}
						
						id = rs.getLong("id");
						rootNode = rs.getLong("rootNodeId");
						message = rs.getString("message");
						do {
							Long parentId = rs.getLong("parentId");
							if(parentId != null) {
								parents.add(new WrappedStorageId<Long>(parentId));
							}
						} while(rs.next());
					}
					
					return new StoredCommit(
							new WrappedStorageId<Long>(id), 
							parents.toArray(new WrappedStorageId[parents.size()]), 
							new WrappedStorageId<Long>(rootNode)
						);
				}
			}
		);
	}
	

	@Override
	public void writeCommit(StoredCommit commit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StoredCommit readBranch(final String name) {
		Long commitId = sql.withStatement("SELECT commitId FROM Branch WHERE name = ?", new StatementExecutor<Long>() {
			@Override
			public Long run(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, name);
				stmt.execute();
				try(ResultSet rs = stmt.getResultSet()) {
					rs.next();
					return rs.getLong("commitId");
				}
			}
		});
		return readCommit(new WrappedStorageId<Long>(commitId));
	}

	@Override
	public void writeBranch(final String name, final StoredCommit commit) {
		sql.withStatement("INSERT INTO Branch (id, name, commitId) VALUES (?, ?, ?)", new StatementExecutor<Void>() {
			@Override
			public Void run(PreparedStatement stmt) throws SQLException {
				stmt.setLong(1, id(generateStorageId()).getWrappedId());
				stmt.setString(2, name);
				stmt.setLong(3, id(commit.getId()).getWrappedId());
				stmt.executeUpdate();
				return null;
			}
		});
	}

	@Override
	public StoredFlatNode readNode(final StorageId id) {
		return sql.withStatement(
				"SELECT "
				+ "n.id, n.propertiesId, n.childrenId "
				+ "FROM Node n"
				+ "WHERE id = ?", 
				
			new StatementExecutor<StoredFlatNode>() {
				@Override
				public StoredFlatNode run(PreparedStatement stmt) throws SQLException {
					stmt.setLong(1, id(id).getWrappedId());
					stmt.execute();
					
					try(ResultSet rs = stmt.getResultSet()) {
						if(!rs.next()) {
							return null;
						}
						
						return new StoredFlatNode(
								new WrappedStorageId<Long>(rs.getLong("id")),
								new WrappedStorageId<Long>(rs.getLong("childrenId")),
								new WrappedStorageId<Long>(rs.getLong("propertiesId"))
							);
					}
				}
			}
		);
	}

	@Override
	public StoredFlatNode writeNode(StoredFlatNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StorageId[] readChildren(StorageId childrenStorageId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StorageId writeChildren(StorageId[] children) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StorageId writeProperties(Map<String, Property> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Nonnull
	public Map<String, Property> readProperties(StorageId propertiesStorageId) {
		// TODO Auto-generated method stub
		return new HashMap<>();
	}

	@Override
	public <T extends StorageAdapter> T getAdapter(Class<T> adapterInterface) {
		// TODO Auto-generated method stub
		return null;
	}

}
