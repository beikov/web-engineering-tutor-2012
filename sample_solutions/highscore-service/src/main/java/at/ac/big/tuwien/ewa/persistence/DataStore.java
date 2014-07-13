package at.ac.big.tuwien.ewa.persistence;

import java.util.Collections;
import java.util.List;

/**
 * This class is used to hold a list of the last 40 entries which have been submitted
 * to the Web Serivce
 * @author pl
 *
 */
public class DataStore {

	
	private static DataStore dataStore;
	private LimitedQueue<DataStoreEntry> elements;
	
	private DataStore() {	
		//Create a new limited queue with size 40
		elements = new LimitedQueue<DataStoreEntry>(40);
	}
	
	
	/**
	 * Return the entries of the list
	 * @return
	 */
	public synchronized List<DataStoreEntry> getEntries() {
		//Return a "view only" list
		return Collections.unmodifiableList(elements);
		
	}
	
	/**
	 * Add an entry to the list
	 * @param entry
	 */
	public synchronized void addEntry(DataStoreEntry entry) {
		elements.add(entry);
	}
	
	
	
	/**
	 * Get an instance of the data store
	 * @return
	 */
	public static synchronized DataStore getInstance() {
		if (dataStore == null) {
			dataStore = new DataStore();
		}
		return dataStore;
	}
	
	
	

	
	
}
