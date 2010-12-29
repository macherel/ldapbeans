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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class LRUCacheImpl<K, V> extends AbstractCache<K, V> implements
	LRUCache<K, V> {

    protected static class LRUCacheEntry<K, V> implements CacheEntry<K, V> {
	private final K m_Key;
	private final V m_Value;

	/**
	 * Construct an entry for {@link LRUCache}
	 * 
	 * @param p_Key
	 *            The key of the cache
	 * @param p_Value
	 *            The value to cache
	 */
	public LRUCacheEntry(K p_Key, V p_Value) {
	    m_Key = p_Key;
	    m_Value = p_Value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see CacheEntry#getKey()
	 */
	public K getKey() {
	    return m_Key;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see CacheEntry#getValue()
	 */
	public V getValue() {
	    return m_Value;
	}

	@Override
	public int hashCode() {
	    return m_Key.hashCode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object p_Object) {
	    boolean equals = false;
	    if (p_Object instanceof LRUCacheEntry) {
		try {
		    LRUCacheEntry<K, V> entry = (LRUCacheEntry<K, V>) p_Object;
		    equals = m_Key.equals(entry.getKey());
		} catch (Exception e) {
		    equals = false;
		}
	    }
	    return equals;
	}
    }

    private final LinkedList<CacheEntry<K, V>> m_Cache;
    private int m_MaxSize;

    /**
     * Default constructor
     */
    public LRUCacheImpl() {
	m_Cache = new LinkedList<CacheEntry<K, V>>();
	m_MaxSize = -1;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#containsKey(Object)
     */
    public boolean containsKey(K p_Key) {
	CacheEntry<K, V> entry = createCacheEntry(p_Key, null);
	return m_Cache.contains(entry);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#put(Object, Object)
     */
    public void put(K p_Key, V p_Value) {
	CacheEntry<K, V> entry = createCacheEntry(p_Key, p_Value);
	m_Cache.remove(entry);
	if ((m_Cache.size() >= m_MaxSize) && (m_MaxSize >= 0)) {
	    m_Cache.removeLast();
	}
	if ((m_Cache.size() < m_MaxSize) || (m_MaxSize < 0)) {
	    m_Cache.addFirst(entry);
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#get(Object)
     */
    public V get(K p_Key) {
	for (CacheEntry<K, V> entry : m_Cache) {
	    if (entry.getKey().equals(p_Key)) {
		put(entry.getKey(), entry.getValue());
		return entry.getValue();
	    }
	}
	return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#clear()
     */
    public void clear() {
	m_Cache.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see LRUCache#getMaxSize
     */
    public int getMaxSize() {
	return m_MaxSize;
    }

    /**
     * {@inheritDoc}
     * 
     * @see LRUCache#setMaxSize(int)
     */
    public void setMaxSize(int p_MaxSize) {
	m_MaxSize = p_MaxSize;
	if (m_MaxSize >= 0) {
	    while ((size() > m_MaxSize) && (size() > 0)) {
		m_Cache.removeLast();
	    }
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#
     */
    public Iterator<CacheEntry<K, V>> iterator() {
	return m_Cache.iterator();
    }

    @Override
    public int size() {
	return m_Cache.size();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#keySet()
     */
    public Set<K> keySet() {
	Set<K> keySet = new HashSet<K>();
	for (CacheEntry<K, V> entry : m_Cache) {
	    keySet.add(entry.getKey());
	}
	return keySet;
    }

    /**
     * Create a new entry for {@link LRUCache}
     * 
     * @param p_Key
     *            The key of the entry
     * @param p_Value
     *            The value of the entry
     * @return a new {@link CacheEntry}
     */
    protected CacheEntry<K, V> createCacheEntry(K p_Key, V p_Value) {
	return new LRUCacheEntry<K, V>(p_Key, p_Value);
    }

    /**
     * Return the internal structure of the cache
     * 
     * @return The internal structure of the cache
     */
    protected final LinkedList<CacheEntry<K, V>> getInternalCache() {
	return m_Cache;
    }
}
