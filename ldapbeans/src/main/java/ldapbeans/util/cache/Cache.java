/*
 * This file is part of ldapbeans
 *
 * Released under LGPL
 *
 * ldapbeans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ldapbeans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ldapbeans.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Bruno Macherel
 */
package ldapbeans.util.cache;

import java.util.Collection;

import ldapbeans.util.cache.Cache.CacheEntry;

public interface Cache<K, V> extends Collection<CacheEntry<K, V>> {

    /** Represent an entry of the cache */
    interface CacheEntry<K, V> {

	/**
	 * Return the key of the entry
	 * 
	 * @return The key of the entry
	 */
	public K getKey();

	/**
	 * Return the value of the entry
	 * 
	 * @return The value of the entry
	 */
	public V getValue();
    }

    /**
     * @param p_Key
     *            The key to find.
     * @return <code>true</code> if the cache contains an entry having this key,
     *         <code>false</code> otherwise.
     */
    boolean containsKey(K p_Key);

    /**
     * Add or modify an entry to the cache
     * 
     * @param p_Key
     *            The key of the new entry
     * @param p_Value
     *            The value of the new entry
     */
    void put(K p_Key, V p_Value);

    /**
     * Return an object value of the cache based on the key
     * 
     * @param p_Key
     *            The key of the entry to find
     * @return The object value corresponding to the key
     */
    V get(K p_Key);

    /**
     * Remove an entry to the cache
     * 
     * @param p_Key
     *            the key of the object to remove
     * @return <code>true</code> if an entry is removed, <code>false</code>
     *         otherwise
     */
    boolean remove(K p_Key);

    /**
     * remove all entries of the cache
     */
    void clear();

    /**
     * Add all entries of the parameter's cache to this cache
     * 
     * @param p_Cache
     *            the cache to copy
     */
    void putAll(Cache<K, V> p_Cache);
}
