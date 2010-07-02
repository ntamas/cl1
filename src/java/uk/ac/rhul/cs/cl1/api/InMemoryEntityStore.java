package uk.ac.rhul.cs.cl1.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple entity storage class that stores all the entities in memory.
 * 
 * It is meant for testing purposes only.
 * 
 * @author tamas
 */
public class InMemoryEntityStore<T> implements EntityStore<T> {
	/** The internal storage area */
	List<T> storage = new ArrayList<T>();
	
	/**
	 * Constructs a new entity.
	 * 
	 * @param  entity  the initial entity to be stored.
	 * @throws IOException if there was an error while creating the
	 *                     entity.
	 * @return the ID of the newly created entity.
	 */
	public String create(T entity) {
		storage.add(entity);
		return Integer.toString(storage.size());
	}
	
	/**
	 * Deletes an entity.
	 * 
	 * @param id the ID of the entity
	 * 
	 * @throws EntityNotFoundException if no such entity exists
	 */
	public void delete(String id) throws EntityNotFoundException {
		T entity = storage.set(getDatasetIndex(id), null);
		
		if (entity == null)
			throw new EntityNotFoundException(id);
	}

	/**
	 * Retrieves an entity.
	 * 
	 * @param id the ID of the entity
	 * @return the entity
	 * 
	 * @throws EntityNotFoundException if no such entity exists
	 */
	public T get(String id) throws EntityNotFoundException {
		T entity = storage.get(getDatasetIndex(id));
		
		if (entity == null)
			throw new EntityNotFoundException(id);
			
		return entity;
	}
	
	/**
	 * Replaces an existing entity.
	 * 
	 * @param id the ID of the entity
	 * @param contents the new contents
	 * 
	 * @throws EntityNotFoundException if no such entity exists
	 */
	public void replace(String id, T contents) throws EntityNotFoundException {
		int index = getDatasetIndex(id);
		T entity = storage.set(index, contents);
		
		if (entity == null) {
			storage.set(index, null);
			throw new EntityNotFoundException(id);
		}
	}
	
	/**
	 * Gets the numeric index of an entity from its string ID.
	 * 
	 * @param id  the string ID
	 * @return the numeric index in the list
	 */
	private int getDatasetIndex(String id) throws EntityNotFoundException {
		int index;
		
		try {
			index = Integer.parseInt(id);
			if (index < 1 || index > storage.size()) {
				throw new EntityNotFoundException(id);
			}
			return index - 1;
		} catch (NumberFormatException ex) {
			throw new EntityNotFoundException(id);
		}
	}
}
