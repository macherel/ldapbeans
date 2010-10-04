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

public final class CacheFactory {
    public static enum CacheType {
	SIMPLE, LRU, COMMITABLE
    }

    /** Unique instance of the CacheActory */
    private final static CacheFactory INSTANCE = new CacheFactory();

    /**
     * Return the unique instance of the cache factory
     * 
     * @return the unique instance of this class
     */
    public static CacheFactory getInstance() {
	return INSTANCE;
    }

    /**
     * Constructor that disallow to instanciate this class.
     */
    private CacheFactory() {
	// Nothing to do
    }

    /**
     * Create a new cache. The implementation depending of the type.
     * 
     * @param <K>
     *            Type of keys
     * @param <V>
     *            Type of values
     * @param p_Type
     *            The kind of cache
     * @return A new cache
     */
    public <K, V> Cache<K, V> createCache(CacheType p_Type) {
	Cache<K, V> cache;
	switch (p_Type) {
	case SIMPLE:
	    cache = new SimpleCache<K, V>();
	    break;
	case LRU:
	    cache = new LRUCacheImpl<K, V>();
	    break;
	case COMMITABLE:
	    cache = new CommitableCacheImpl<K, V>();
	    break;
	default:
	    cache = new SimpleCache<K, V>();
	    break;
	}
	return cache;
    }
}
