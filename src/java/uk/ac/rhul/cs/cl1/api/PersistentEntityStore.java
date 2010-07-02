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
	
	private PreparedStatement createStmt = null;
	private PreparedStatement deleteStmt = null;
	private PreparedStatement getStmt = null;
	private PreparedStatement replaceStmt = null;
	private PreparedStatement updateTimestampStmt = null;
	
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
	public synchronized String create(T entity) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(entity);
		bos.close();
		
		byte[] data = bos.toByteArray();
		
		try {
			createStmt.setBinaryStream(1, new ByteArrayInputStream(data), data.length);
			createStmt.executeUpdate();
			
			ResultSet rs = createStmt.getGeneratedKeys();
			if (rs.next()) {
				return Integer.toString(rs.getInt(1));
			}
		} catch (SQLException ex) {
			throw new IOException(ex);
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
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(entity);
		bos.close();
		
		byte[] data = bos.toByteArray();
		
		try {
			replaceStmt.setBinaryStream(1, new ByteArrayInputStream(data), data.length);
			replaceStmt.setInt(2, numericId);
			if (replaceStmt.executeUpdate() == 0) {
				/* No affected rows */
				throw new EntityNotFoundException(id);
			}
		} catch (SQLException ex) {
			throw new IOException(ex);
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
	@SuppressWarnings("unchecked")
	public synchronized T get(String id) throws IOException, EntityNotFoundException {
		int numericId = parseId(id);
		ResultSet rs = null;
		ObjectInputStream ois = null;
		T result = null;
		
		try {
			updateTimestamp(numericId);
			
			getStmt.setInt(1, numericId);
			rs = getStmt.executeQuery();
			if (!rs.next())
				throw new EntityNotFoundException(id);
			
			ois = new ObjectInputStream(rs.getBinaryStream(1));
			result = (T)ois.readObject();
		} catch (SQLException ex) {
			throw new IOException(ex);
		} catch (ClassNotFoundException ex) {
			throw new IOException(ex);
		}
		
		return result;
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
			deleteStmt.setInt(1, numericId);
			deleteStmt.executeUpdate();
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
	
	/**
	 * Ensures that the necessary table exists in the database
	 * @throws SQLException when the tables cannot be created
	 */
	private void checkSchema() throws SQLException {
		PreparedStatement stmt;
		ResultSet results;
		
		stmt = getConnection().prepareStatement("SHOW TABLES LIKE ?");
		stmt.setString(1, tableName);
		results = stmt.executeQuery();
		if (!results.next()) {
			/* No such table, we have to create it */
			stmt.executeUpdate("CREATE TABLE "+tableName+
				               " (id INTEGER NOT NULL AUTO_INCREMENT," +
				               "  data LONGBLOB," +
				               "  last_accessed_at TIMESTAMP NOT NULL, " +
				               "  PRIMARY KEY (id))");
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
		Connection conn = getConnection();
		
		createStmt = conn.prepareStatement(
			"INSERT INTO " + tableName + " (data, last_accessed_at) VALUES (?, NOW())",
			Statement.RETURN_GENERATED_KEYS
		);
		deleteStmt = conn.prepareStatement(
			"DELETE FROM " + tableName + " WHERE id = ?"
		);
		getStmt = conn.prepareStatement(
			"SELECT data FROM " + tableName + " WHERE id = ?"
		);
		replaceStmt = conn.prepareStatement(
			"UPDATE " + tableName + " SET data = ?, last_accessed_at = NOW() WHERE id = ?"
		);
		updateTimestampStmt = conn.prepareStatement(
			"UPDATE " + tableName + " SET last_accessed_at = NOW() WHERE id = ?"
		);
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
	 * @param  id  the ID of the entity
	 * @throws SQLException if something went wrong with the database operations
	 */
	private synchronized void updateTimestamp(int id) throws SQLException {
		updateTimestampStmt.setInt(1, id);
		updateTimestampStmt.executeUpdate();
	}
}
