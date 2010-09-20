package uk.ac.rhul.cs.cl1.api;

import java.io.IOException;
import java.util.Date;

/**
 * Generic interface for classes that store stuff by IDs.
 * 
 * This is the interface that must be implemented by storage providers
 * providers used in the web interface of ClusterONE. Providers must
 * be capable of at least the following operations:
 * 
 *   - Constructing a new entity with an automatically assigned
 *     unique ID.
 *   
 *   - Replacing the contents of an entity
 *   
 *   - Retrieving an entity by ID and providing an InputStream that
 *     can be used to read the entity.
 *   
 *   - Deleting an entity by ID
 * 
 *   - Removing entities that have not been accessed since a given date.
 * 
 * @author tamas
 */
public interface EntityStore<T> {
	/**
	 * Constructs a new entity storage slot.
	 * 
	 * @param entity the new entity
	 * @return the ID of the newly created entity.
	 * 
	 * @throws IOException if there was an error while creating the
	 *                     entity.
	 */
	public String create(T entity) throws IOException;
	
	/**
	 * Replaces an existing entity.
	 * 
	 * @param id the ID of the entity
	 * @param entity the new entity
	 * 
	 * @throws IOException if there was an error while storing the
	 *                     entity.
	 * @throws EntityNotFoundException if no such entity exists
	 */
	public void replace(String id, T entity)
	throws IOException, EntityNotFoundException;
	
	/**
	 * Retrieves an entity.
	 * 
	 * @param id the ID of the entity
	 * @return the entity itself
	 * 
	 * @throws IOException if there was an error while reading the
	 *                     entity
	 * @throws EntityNotFoundException if no such entity exists
	 */
	public T get(String id) throws IOException, EntityNotFoundException;
	
	/**
	 * Deletes an entity.
	 * 
	 * @param id the ID of the entity
	 * 
	 * @throws IOException if there was an error while deleting the
	 *                     entity.
	 * @throws EntityNotFoundException if no such entity exists
	 */
	public void delete(String id) throws IOException, EntityNotFoundException;
	
	/**
	 * Runs a cleanup process on the entity store.
	 * 
	 * The cleanup process removes all the items that have not been accessed
	 * since a given date.
	 * 
	 * @param  date the date of the earliest entry that will be kept.
	 * @return the number of entries that were deleted
	 * @throws IOException if there was an error while cleaning up the
	 *                     entity store.
	 */
	public int removeOlderThan(Date date) throws IOException;
}
