package com.dc2f.dstore.storage.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Utility for handling SQL statements correctly.
 */
public class SQL {
	
	private ComboPooledDataSource datasource;
	
	public SQL(ComboPooledDataSource datasource) {
		this.datasource = datasource;
	}
	
	/**
	 * Execute the given executor with a new connection from the pool, 
	 * which will be closed properly in the end.
	 * 
	 * SQLExecptions will be wrapped into RuntimeExceptions.
	 * 
	 * @param executor The code to execute with a connection
	 */
	<T> T withConnection(ConnectionExecutor<T> executor) {
		try {
			try(Connection conn = datasource.getConnection()) {
				return executor.run(conn);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error while executing sql", e);
		}
	}
	
	/**
	 * Prepare the given statement in a new connection and handle exceptions 
	 * as well as statement closing correctly.
	 * 
	 * @param statement The sql statement to execute.
	 * @param executor The code to execute with the statement.
	 * @return
	 */
	<T> T withStatement(final String statement, final StatementExecutor<T> executor) {
		return withConnection(new ConnectionExecutor<T>() {
			@Override
			public T run(Connection conn) {
				try(PreparedStatement stmt = conn.prepareStatement(statement)) {
					return executor.run(stmt);
				} catch(SQLException e) {
					throw new RuntimeException("Error while executing sql", e);
				}
			}
		});
	}
}

interface ConnectionExecutor<T> {
	T run(Connection conn) throws SQLException;
}

interface StatementExecutor<T> {
	T run(PreparedStatement stmt) throws SQLException;
}