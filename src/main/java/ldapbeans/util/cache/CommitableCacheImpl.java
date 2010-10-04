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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommitableCacheImpl<K, V> extends AbstractCache<K, V> implements
	CommitableCache<K, V> {
    private final Cache<K, V> m_PrimaryCache = new SimpleCache<K, V>();
    private final Cache<K, V> m_SecondaryCache = new SimpleCache<K, V>();
    private final List<K> m_RemovedKeys = new ArrayList<K>();

    /**
     * {@inheritDoc}
     * 
     * @see Cache#get(Object);
     */
    public V get(K p_Key) {
	V tmp;
	V value = m_SecondaryCache.get(p_Key);
	if (null == value) {
	    tmp = m_PrimaryCache.get(p_Key);
	    if (null != tmp) {
		// try
		{
		    // FIXME: value = (V) tmp.clone();
		    m_SecondaryCache.put(p_Key, value);
		}
		// catch (CloneNotSupportedException e)
		{
		    // e.printStackTrace();
		}
	    }
	}
	return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#put(K, V)
     */
    public void put(K p_Key, V p_Value) {
	m_SecondaryCache.put(p_Key, p_Value);
	m_RemovedKeys.remove(p_Key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#containsKey(K)
     */
    public boolean containsKey(K p_Key) {
	boolean contains = false;
	if (true == m_SecondaryCache.containsKey(p_Key)) {
	    contains = true;
	} else if (true == m_PrimaryCache.containsKey(p_Key)) {
	    contains = true;
	} else {
	    contains = false;
	}
	return contains;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#remove(K)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object p_Key) {
	if (m_SecondaryCache.remove(p_Key)) {
	    m_RemovedKeys.add((K) p_Key);
	    return true;
	}
	return m_PrimaryCache.remove(p_Key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#clear()
     */
    @Override
    public void clear() {
	m_PrimaryCache.clear();
	m_SecondaryCache.clear();
	m_RemovedKeys.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#iterator()
     */
    @SuppressWarnings("unchecked")
    public Iterator<CacheEntry<K, V>> iterator() {
	return new AggregateIterator<CacheEntry<K, V>>(
		m_PrimaryCache.iterator(), m_SecondaryCache.iterator());
    }

    /**
     * {@inheritDoc}
     * 
     * @see CommitableCache#commit()
     */
    public void commit() {
	m_PrimaryCache.removeAll(m_RemovedKeys);
	m_PrimaryCache.putAll(m_SecondaryCache);
	m_SecondaryCache.clear();
	m_RemovedKeys.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see CommitableCache#rollback()
     */
    public void rollback() {
	m_SecondaryCache.clear();
	m_RemovedKeys.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#size()
     */
    @Override
    public int size() {
	return m_PrimaryCache.size() + m_SecondaryCache.size();
    }
}
