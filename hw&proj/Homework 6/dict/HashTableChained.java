/* HashTableChained.java */

package dict;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * HashTableChained implements a Dictionary as a hash table with chaining.
 * All objects used as keys must have a valid hashCode() method, which is
 * used to determine which bucket of the hash table an entry is stored in.
 * Each object's hashCode() is presumed to return an int between
 * Integer.MIN_VALUE and Integer.MAX_VALUE.  The HashTableChained class
 * implements only the compression function, which maps the hash code to
 * a bucket in the table's range.
 * <p>
 * DO NOT CHANGE ANY PROTOTYPES IN THIS FILE.
 **/

public class HashTableChained implements Dictionary {

    /**
     * Place any data fields here.
     **/
    private double loadFactor = 0;
    private int size = 0;
    private int arrSize = 101;
    private LinkedList[] arr;
    private int p = 10000079;
    private int scalingFactor = 7;
    private int shift = 91;


    /**
     * Construct a new empty hash table intended to hold roughly sizeEstimate
     * entries.  (The precise number of buckets is up to you, but we recommend
     * you use a prime number, and shoot for a load factor between 0.5 and 1.)
     **/

    public HashTableChained(int sizeEstimate) {
        // Your solution here.
        while (!isPrime(sizeEstimate)) {
            sizeEstimate++;
        }
        arrSize = sizeEstimate;
        arr = new LinkedList[arrSize];
    }

    /**
     * Construct a new empty hash table with a default size.  Say, a prime in
     * the neighborhood of 100.
     **/

    public HashTableChained() {
        // Your solution here.
        arr = new LinkedList[arrSize];
    }

    /**
     * Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
     * to a value in the range 0...(size of hash table) - 1.
     * <p>
     * This function should have package protection (so we can test it), and
     * should be used by insert, find, and remove.
     **/

    int compFunction(int code) {
        // Replace the following line with your solution.
        // h(i) = ((ai + b) mod p) mod N,
        code = (scalingFactor * code + shift) % p % arrSize;
        if (code < 0) {
            code = code + arrSize;
        }
        return code;
    }

    /**
     * Returns the number of entries stored in the dictionary.  Entries with
     * the same key (or even the same key and value) each still count as
     * a separate entry.
     *
     * @return number of entries in the dictionary.
     **/

    public int size() {
        // Replace the following line with your solution.
        return size;
    }

    /**
     * Tests if the dictionary is empty.
     *
     * @return true if the dictionary has no entries; false otherwise.
     **/

    public boolean isEmpty() {
        // Replace the following line with your solution.
        return size == 0;
    }

    /**
     * Create a new Entry object referencing the input key and associated value,
     * and insert the entry into the dictionary.  Return a reference to the new
     * entry.  Multiple entries with the same key (or even the same key and
     * value) can coexist in the dictionary.
     * <p>
     * This method should run in O(1) time if the number of collisions is small.
     *
     * @param key   the key by which the entry can be retrieved.
     * @param value an arbitrary object.
     * @return an entry containing the key and value.
     **/

    public Entry insert(Object key, Object value) {
        // Replace the following line with your solution.
        int index = compFunction(key.hashCode());
        LinkedList chain = arr[index];
        if (chain == null) {
            chain = new LinkedList();
            arr[index] = chain;
        }
        Entry entry = new Entry();
        entry.key = key;
        entry.value = value;
        chain.add(entry);
        size++;
        return entry;
    }

    /**
     * Search for an entry with the specified key.  If such an entry is found,
     * return it; otherwise return null.  If several entries have the specified
     * key, choose one arbitrarily and return it.
     * <p>
     * This method should run in O(1) time if the number of collisions is small.
     *
     * @param key the search key.
     * @return an entry containing the key and an associated value, or null if
     * no entry contains the specified key.
     **/

    public Entry find(Object key) {
        // Replace the following line with your solution.
        int index = compFunction(key.hashCode());
        LinkedList chain = arr[index];
        for (int i = 0; i < chain.size(); i++) {
            if (((Entry) chain.get(i)).key().equals(key)) {
                return (Entry) chain.get(i);
            }
        }
        return null;
    }

    /**
     * Remove an entry with the specified key.  If such an entry is found,
     * remove it from the table and return it; otherwise return null.
     * If several entries have the specified key, choose one arbitrarily, then
     * remove and return it.
     * <p>
     * This method should run in O(1) time if the number of collisions is small.
     *
     * @param key the search key.
     * @return an entry containing the key and an associated value, or null if
     * no entry contains the specified key.
     */

    public Entry remove(Object key) {
        // Replace the following line with your solution.

        int index = compFunction(key.hashCode());
        LinkedList chain = arr[index];
        for (int i = 0; i < chain.size(); i++) {
            if (((Entry) chain.get(i)).key().equals(key)) {
                Entry entry = (Entry) chain.get(i);
                chain.remove(i);
                size--;
                return entry;
            }
        }
        return null;
    }

    /**
     * Remove all entries from the dictionary.
     */
    public void makeEmpty() {
        // Your solution here.
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                arr[i].clear();
            }
        }
    }


    private boolean isPrime(int n) {
        if (n < 2) {
            return false;
        } else {
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
