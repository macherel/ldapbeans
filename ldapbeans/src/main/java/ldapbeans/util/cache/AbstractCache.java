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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import ldapbeans.util.cache.Cache.CacheEntry;

public abstract class AbstractCache<K, V> extends
	AbstractCollection<CacheEntry<K, V>> implements Cache<K, V> {

    /**
     * Copy all element of the cache passed in parameter to this cache
     * 
     * @param p_Cache
     *            The cache to copy
     */
    public void putAll(Cache<K, V> p_Cache) {
	for (CacheEntry<K, V> entry : p_Cache) {
	    put(entry.getKey(), entry.getValue());
	}
    }

    /**
     * Return cache entries as an array. Copy entries in parameter's array or
     * create a new one if there is not enough space
     * 
     * @param p_Array
     *            An array in witch entries will be copied
     * @return Cache entries as an array
     */
    @SuppressWarnings("unchecked")
    public final CacheEntry<K, V>[] toArray(final CacheEntry<K, V>[] p_Array) {

	int index = 0;
	CacheEntry<K, V>[] result = p_Array;
	if ((p_Array == null) || (p_Array.length < size())) {
	    result = (CacheEntry<K, V>[]) java.lang.reflect.Array.newInstance(
		    p_Array.getClass().getComponentType(), size());
	}
	for (CacheEntry<K, V> entry : this) {
	    result[index++] = entry;
	}

	if (p_Array.length > size()) {
	    p_Array[size()] = null;
	}

	return result;
    }

    @Override
    public boolean add(final CacheEntry<K, V> p_Entry) {
	put(p_Entry.getKey(), p_Entry.getValue());
	return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.AbstractCollection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(final Object p_Key) {
	Iterator<CacheEntry<K, V>> e = iterator();
	while (e.hasNext()) {
	    if (e.next().getKey().equals(p_Key)) {
		e.remove();
		return true;
	    }
	}
	return false;
    }

    /**
     * Remove entries where the key are in the collection
     * 
     * @param p_Keys
     *            Collection of keys to remove
     * @return <code>true</code> if the cache is modified, <code>false</code>
     *         otherwise
     */
    public boolean removeAll(Collection<?> p_Keys) {
	boolean modified = false;
	Iterator<CacheEntry<K, V>> e = iterator();
	while (e.hasNext()) {
	    if (p_Keys.contains(e.next().getKey())) {
		e.remove();
		modified = true;
	    }
	}
	return modified;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#size()
     */
    @Override
    public int size() {
	return keySet().size();
    }

}
