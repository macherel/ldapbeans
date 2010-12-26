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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	V value = m_SecondaryCache.get(p_Key);
	if (null == value) {
	    value = m_PrimaryCache.get(p_Key);
	    if (null != value) {
		if (!(value instanceof Commitable)) {
		    value = createCopy(value);
		}
		m_SecondaryCache.put(p_Key, value);
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
	K key = (K) p_Key;
	if (m_SecondaryCache.remove(key)) {
	    m_RemovedKeys.add(key);
	    return true;
	} else {
	    if (m_PrimaryCache.containsKey(key)) {
		m_RemovedKeys.add(key);
		return true;
	    } else {
		return false;
	    }
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#clear()
     */
    @Override
    public void clear() {
	m_SecondaryCache.clear();
	m_RemovedKeys.addAll(m_PrimaryCache.keySet());
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
	for (CacheEntry<K, V> entry : m_SecondaryCache) {
	    V value = entry.getValue();
	    if (value instanceof Commitable) {
		((Commitable) value).commit();
	    }
	    m_PrimaryCache.put(entry.getKey(), value);
	}
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
	for (CacheEntry<K, V> entry : m_SecondaryCache) {
	    V value = entry.getValue();
	    if (value instanceof Commitable) {
		((Commitable) value).rollback();
	    }
	    m_PrimaryCache.put(entry.getKey(), value);
	}
	m_SecondaryCache.clear();
	m_RemovedKeys.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Cache#keySet()
     */
    public Set<K> keySet() {
	Set<K> keySet = new HashSet<K>(m_SecondaryCache.keySet());
	keySet.addAll(m_PrimaryCache.keySet());
	return keySet;
    }

    /**
     * Create a copy of the parameter's value
     * 
     * @param <V>
     *            The type of the value to copy
     * @param p_Value
     *            The value to copy
     * @return A copy of the value
     */
    @SuppressWarnings("unchecked")
    private static <V> V createCopy(V p_Value) {
	Class<V> clazz = (Class<V>) p_Value.getClass();
	// Try to find copy constructor
	try {
	    Constructor<V> constructor = clazz.getConstructor(clazz);
	    if (constructor != null) {
		constructor.setAccessible(true);
		return constructor.newInstance(p_Value);
	    }
	} catch (Exception e) {
	    throw new IllegalArgumentException(
		    "Cannot create a copy of the value " + p_Value, e);
	}
	// Try to use clone method
	try {
	    Method cloneMethod = clazz.getMethod("clone");
	    if (cloneMethod != null) {
		cloneMethod.setAccessible(true);
		return (V) cloneMethod.invoke(p_Value);
	    }
	} catch (Exception e) {
	    throw new IllegalArgumentException(
		    "Cannot create a copy of the value " + p_Value, e);
	}
	throw new IllegalArgumentException("Cannot create a copy of the value "
		+ p_Value);
    }

}
