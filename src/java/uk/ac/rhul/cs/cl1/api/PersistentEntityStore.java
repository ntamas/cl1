package uk.ac.rhul.cs.cl1.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * Entity storage class that stores all the entities in a JDBC
 * @link{DataSource}.
 * 
 * The entity class being used must be serializable.
 * 
 * @author tamas
 */
public class PersistentEntityStore<T> implements EntityStore<T> {
	private DataSource dataSource;
	private String tableName;
	
	/**
	 * Constructs a new persistent entity store using the given connection.
	 * 
	 * @param dataSource the data source to be used
	 * @param tableName  the name of the table storing the entities
	 * 
	 * @throws SQLException when there was an error while setting up the database
	 */
	public PersistentEntityStore(DataSource dataSource, String tableName) throws SQLException {
		this.dataSource = dataSource;
		this.tableName = tableName;
		
		initStatements();
		checkSchema();
	}
	
	/**
	 * Constructs a new entity.
	 * 
	 * This method is synchronized to avoid problems when accessing createStmt
	 * from multiple threads.
	 * 
	 * @param  entity  the initial entity to be stored.
	 * @throws IOException if there was an error while creating the
	 *                     entity. This exception may wrap an
	 *                     associated {@link SQLException} that is the
	 *                     original cause of why the operation failed.
	 * @return the ID of the newly created entity.
	 */
	public String create(T entity) throws IOException {
		try {
			return createSQL(entity);
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
	
	private String createSQL(T entity) throws IOException, SQLException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(entity);
		bos.close();
		
		byte[] data = bos.toByteArray();
		
		/* Well, nested try-finally hurts the eyes, but since Java does not have a
		 * proper RAII pattern implementation, this is still as good as it gets.
		 */
		final Connection conn = getConnection();
		try {
			final PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO " + tableName + " (data, last_accessed_at) VALUES (?, NOW())",
				Statement.RETURN_GENERATED_KEYS
			);
			try {
				stmt.setBinaryStream(1, new ByteArrayInputStream(data), data.length);
				stmt.executeUpdate();
				
				final ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					return Integer.toString(rs.getInt(1));
				}
			} finally {
				stmt.close();
			}
		} finally {
			conn.close();
		}
		
		throw new IOException("failed to retrieve the ID of the new entity from the database");
	}

	/**
	 * Replaces an existing entity.
	 * 
	 * @param id the ID of the entity
	 * @param contents the new contents
	 * 
	 * @throws EntityNotFoundException if no such entity exists
	 * @throws IOException if there was an error while communicating with the database
	 */
	public void replace(String id, T entity) throws IOException,
			EntityNotFoundException {
		int numericId = parseId(id);
		
		try {
			replaceSQL(numericId, entity);
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
	
	private void replaceSQL(int id, T entity)
	throws IOException, EntityNotFoundException, SQLException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(entity);
		bos.close();
		byte[] data = bos.toByteArray();
		
		Connection conn = getConnection();		
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"UPDATE " + tableName + " SET data = ?, last_accessed_at = NOW() WHERE id = ?"
			);
			
			try {
				stmt.setBinaryStream(1, new ByteArrayInputStream(data), data.length);
				stmt.setInt(2, id);
				if (stmt.executeUpdate() == 0) {
					/* No affected rows */
					throw new EntityNotFoundException(Integer.toString(id));
				}
			} finally {
				stmt.close();
			}
		} finally {
			conn.close();
		}
	}
	
	/**
	 * Retrieves an entity by ID.
	 * 
	 * This method is synchronized to avoid problems when accessing getStmt
	 * from multiple threads.
	 * 
	 * @param id the ID of the entity
	 * @return the entity
	 * 
	 * @throws EntityNotFoundException if no such entity exists
	 */
	public T get(String id) throws IOException, EntityNotFoundException {
		int numericId = parseId(id);
		
		try {
			return getSQL(numericId);
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private T getSQL(int id)
	throws IOException, EntityNotFoundException, SQLException {
		ObjectInputStream ois = null;
		
		Connection conn = getConnection();
		
		try {
			updateTimestamp(id, conn);
			
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT data FROM " + tableName + " WHERE id = ?"
			);
			try {
				stmt.setInt(1, id);
				ResultSet rs = stmt.executeQuery();
				try {
					if (!rs.next())
						throw new EntityNotFoundException(Integer.toString(id));
					
					ois = new ObjectInputStream(rs.getBinaryStream(1));
				} finally {
					rs.close();
				}
			} finally {
				stmt.close();
			}
		} finally {
			conn.close();
		}
		
		try {
			return (T)ois.readObject();
		} catch (ClassNotFoundException ex) {
			throw new IOException(ex);
		}
	}
	
	/**
	 * Deletes an entity from the data source by its ID.
	 * 
	 * This method is synchronized to avoid problems when accessing deleteStmt
	 * from multiple threads.
	 * 
	 * @param id the ID of the entity
	 * @throws EntityNotFoundException if no such entity exists
	 * @throws IOException if there was an error while connecting the database
	 */
	public void delete(String id) throws IOException, EntityNotFoundException {
		int numericId = parseId(id);
		try {
			deleteSQL(numericId);
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
	
	private void deleteSQL(int id) throws IOException, EntityNotFoundException, SQLException {
		Connection conn = getConnection();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"DELETE FROM " + tableName + " WHERE id = ?"
			);
			try {
				stmt.setInt(1, id);
				stmt.executeUpdate();
			} finally {
				stmt.close();
			}
		} finally {
			conn.close();
		}
	}
	
	/**
	 * Ensures that the necessary table exists in the database
	 * @throws SQLException when the tables cannot be created
	 */
	private void checkSchema() throws SQLException {
		Connection conn = getConnection();
		
		try {
			PreparedStatement stmt = getConnection().prepareStatement("SHOW TABLES LIKE ?");
			try {
				stmt.setString(1, tableName);
				ResultSet results = stmt.executeQuery();
				try {
					if (!results.next()) {
						/* No such table, we have to create it */
						stmt.executeUpdate("CREATE TABLE "+tableName+
							               " (id INTEGER NOT NULL AUTO_INCREMENT," +
							               "  data LONGBLOB," +
							               "  last_accessed_at TIMESTAMP NOT NULL, " +
							               "  PRIMARY KEY (id))");
					}
				} finally {
					results.close();
				}
			} finally {
				stmt.close();
			}
		} finally {
			conn.close();
		}
	}
	
	/**
	 * Gets a connection to the data source
	 */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	/**
	 * Initializes the prepared statements used by this entity store
	 */
	private void initStatements() throws SQLException {
	}
	
	/**
	 * Parses a string ID into a numeric ID
	 * 
	 * This method takes care of converting string IDs into numeric ones as this
	 * entity store uses numeric IDs only.
	 * 
	 * @param   id  the string ID to be parsed
	 * @return  the corresponding numeric ID
	 * @throws  EntityNotFoundException  if the ID is not numeric
	 */
	private int parseId(String id) throws EntityNotFoundException {
		try {
			return Integer.parseInt(id);
		} catch (NumberFormatException ex) {
			throw new EntityNotFoundException(id);
		}
	}
	
	/**
	 * Updates the timestamp of a given entity by the current time
	 * 
	 * @param  id    the ID of the entity
	 * @param  conn  an existing database connection to use
	 * @throws SQLException if something went wrong with the database operations
	 */
	private void updateTimestamp(int id, Connection conn) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(
			"UPDATE " + tableName + " SET last_accessed_at = NOW() WHERE id = ?"
		);
		try {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} finally {
			stmt.close();
		}
	}
}
