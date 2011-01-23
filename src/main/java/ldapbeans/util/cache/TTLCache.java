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

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class TTLCache<K, V> extends LRUCacheImpl<K, V> {
    protected static class TTLCacheEntry<K, V> extends LRUCacheEntry<K, V> {
	private final long m_Timestamp = System.currentTimeMillis();

	/**
	 * Construct a {@link CacheEntry} for {@link TTLCache}
	 * 
	 * @param p_Key
	 *            The key of the entry
	 * @param p_Value
	 *            the value of the entry
	 */
	public TTLCacheEntry(K p_Key, V p_Value) {
	    super(p_Key, p_Value);
	}

	/**
	 * Return the timestamp corresponding to the moment of the creation of
	 * the entry
	 * 
	 * @return The timestamp corresponding to the moment of the creation of
	 *         the entry
	 */
	public long getTimestamp() {
	    return m_Timestamp;
	}
    }

    private final Timer m_Timer = new Timer();
    private long m_Ttl = -1;

    /**
     * Construct a cache in witch object expired
     */
    public TTLCache() {
	super();
	m_Timer.schedule(new TimerTask() {
	    @Override
	    public void run() {
		purge();
	    }
	}, 0, 100);
    }

    /**
     * Set the delay after witch entry will expired
     * 
     * @param p_Ttl
     *            The delay
     */
    public void setTtl(long p_Ttl) {
	m_Ttl = p_Ttl;
    }

    @Override
    protected CacheEntry<K, V> createCacheEntry(K p_Key, V p_Value) {
	CacheEntry<K, V> entry = new TTLCacheEntry<K, V>(p_Key, p_Value);
	return entry;
    }

    /**
     * Remove expired entries
     */
    private final void purge() {
	long timespamp = System.currentTimeMillis();
	LinkedList<CacheEntry<K, V>> cache = getInternalCache();
	if (m_Ttl >= 0) {
	    while (!cache.isEmpty()
		    && ((TTLCacheEntry<K, V>) cache.getLast()).getTimestamp()
			    + m_Ttl < timespamp) {
		cache.removeLast();
	    }
	}
    }
}
