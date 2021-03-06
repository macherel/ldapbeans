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

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Set;

public class SoftCache<K, V> extends AbstractCache<K, V> {
    private static class SoftCacheEntry<K, V> implements CacheEntry<K, V> {
	private final CacheEntry<K, SoftReference<V>> m_Entry;

	/**
	 * Construct a {@link CacheEntry} for {@link SimpleCache}
	 * 
	 * @param p_Entry
	 *            An entry
	 */
	public SoftCacheEntry(CacheEntry<K, SoftReference<V>> p_Entry) {
	    m_Entry = p_Entry;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see CacheEntry#getKey()
	 */
	public K getKey() {
	    return m_Entry.getKey();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see CacheEntry#getValue()
	 */
	public V getValue() {
	    return m_Entry.getValue().get();
	}

    }

    private static class SoftCacheIterator<K, V> implements
	    Iterator<CacheEntry<K, V>> {
	private final Iterator<CacheEntry<K, SoftReference<V>>> m_Iterator;

	/**
	 * Construct a new iterator the the {@link SimpleCache}
	 * 
	 * @param p_Iterator
	 *            An iterator
	 */
	public SoftCacheIterator(
		Iterator<CacheEntry<K, SoftReference<V>>> p_Iterator) {
	    m_Iterator = p_Iterator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Iterator#hasNext()
	 */
	public boolean hasNext() {
	    return m_Iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Iterator#next()
	 */
	public CacheEntry<K, V> next() {
	    CacheEntry<K, SoftReference<V>> entry = m_Iterator.next();
	    return new SoftCacheEntry<K, V>(entry);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Iterator#remove()
	 */
	public void remove() {
	    m_Iterator.remove();
	}

    }

    private final Cache<K, SoftReference<V>> m_Cache = null;

    /**
     * {@inheritDoc}
     * 
     * @see Cache#containsKey(Object)
     */
    public boolean containsKey(K p_Key) {
	return m_Cache.containsKey(p_Key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#put(Object, Object)
     */
    public void put(K p_Key, V p_Value) {
	m_Cache.put(p_Key, new SoftReference<V>(p_Value));
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#get(Object)
     */
    public V get(K p_Key) {
	return m_Cache.get(p_Key).get();
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
     * @see Cache#iterator()
     */
    public Iterator<CacheEntry<K, V>> iterator() {
	return new SoftCacheIterator<K, V>(m_Cache.iterator());
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#keySet()
     */
    public Set<K> keySet() {
	return m_Cache.keySet();
    }

    @Override
    public int size() {
	return m_Cache.size();
    }

}
