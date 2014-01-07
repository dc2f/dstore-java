package com.dc2f.dstore.storage.pgsql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Utility for handling SQL statements correctly.
 */
public class SQL {
	
	private static Logger LOG = LoggerFactory.getLogger(SQL.class);
	
	private ComboPooledDataSource datasource;
	
	public SQL(ComboPooledDataSource datasource) {
		this.datasource = datasource;
	}
	
	/**
	 * Executes the given script.
	 * To extract single statements the script is simply split by semicolon (";").
	 * 
	 * TODO: Implement some mechanism to allow non statement ending semicolons (eg. needed in functions in plpgsql).
	 * 
	 * @param stream The script to execute
	 */
	public void executeScript(final InputStream stream) {
		try (InputStream tmp = stream) {
			final String script = CharStreams.toString(new InputStreamReader(tmp, Charsets.UTF_8));
			
			withConnection(new ConnectionExecutor<Void>() {
				@Override
				public Void run(Connection conn) throws SQLException {
					for(String stmt : script.split(";")) {
						conn.createStatement().execute(stmt);
					}
					return null;
				}
			});
		} catch (IOException e) {
			
		}
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
			LOG.error("Error while executing sql", e);
			SQLException next = e;
			while((next = next.getNextException()) != null) {
				LOG.error("Next exception", next);
			}
			
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
			public T run(Connection conn) throws SQLException {
				try(PreparedStatement stmt = conn.prepareStatement(statement)) {
					return executor.run(stmt);
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