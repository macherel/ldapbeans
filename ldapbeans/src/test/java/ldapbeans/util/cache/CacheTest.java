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

import static ldapbeans.util.cache.CacheFactory.CacheType.LRU;
import static ldapbeans.util.cache.CacheFactory.CacheType.SIMPLE;
import junit.framework.Assert;

import org.junit.Test;

public class CacheTest {
    /**
     * Initialize the cache
     * 
     * @param p_Cache
     *            Cache to initialize
     * @param p_Keys
     *            Initial keys
     * @param p_Values
     *            Initial values
     */
    private void initCache(Cache<String, String> p_Cache, String[] p_Keys,
	    String[] p_Values) {
	p_Cache.clear();
	for (int i = 0; i < p_Keys.length; i++) {
	    p_Keys[i] = "k" + i;
	    p_Values[i] = "v" + i;
	    p_Cache.put(p_Keys[i], p_Values[i]);
	}
    }

    /**
     * Check LRUCache
     * 
     * @param p_Cache
     *            the cache to check
     * @param p_ExistingKeys
     *            Array of existing keys in the cache
     * @param p_DeprecatedKeys
     *            Array of key that does not exist anymore in the cache
     */
    private void checkLRUCache(LRUCache<String, String> p_Cache,
	    String[] p_ExistingKeys, String[] p_DeprecatedKeys) {
	for (String key : p_ExistingKeys) {
	    Assert.assertTrue(p_Cache.containsKey(key));
	    Assert.assertNotNull(p_Cache.get(key));
	}
	for (String key : p_DeprecatedKeys) {
	    Assert.assertFalse(p_Cache.containsKey(key));
	    Assert.assertNull(p_Cache.get(key));
	}
    }

    /**
     * Test the {@link CacheFactory}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testCacheFactory() throws Exception {
	CacheFactory cacheFactory = CacheFactory.getInstance();
	Cache<String, String> cache;
	// Test for SimpleCache
	cache = cacheFactory.createCache(SIMPLE);
	if (!(cache instanceof SimpleCache)) {
	    Assert.fail("cache is not a simple cache");
	}
	// Test for LRUCache
	cache = cacheFactory.createCache(LRU);
	if (!(cache instanceof LRUCache)) {
	    Assert.fail("cache is not a LRU cache");
	}
    }

    /**
     * Test the {@link SimpleCache}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testSimpleCache() throws Exception {
	int size = 10;
	Cache<String, String> cache = new SimpleCache<String, String>();
	String[] keys = new String[size];
	String[] values = new String[size];
	initCache(cache, keys, values);
	for (int i = 0; i < size; i++) {
	    cache.put(keys[i], values[i]);
	}
	for (int i = 0; i < keys.length; i++) {
	    Assert.assertEquals("v" + i, cache.get(keys[i]));
	}
    }

    /**
     * Test the {@link LRUCache}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testLRUCache() throws Exception {
	int size = 10;
	int cacheSize = 5;
	LRUCache<String, String> cache = new LRUCacheImpl<String, String>();
	cache.setMaxSize(cacheSize);
	String[] keys = new String[size];
	String[] values = new String[size];

	initCache(cache, keys, values);
	checkLRUCache(cache, new String[] { "k5", "k6", "k7", "k8", "k9" },
		new String[] { "k0", "k1", "k2", "k3", "k4" });
	initCache(cache, keys, values);
	cache.get(keys[5]);
	checkLRUCache(cache, new String[] { "k5", "k6", "k7", "k8", "k9" },
		new String[] { "k0", "k1", "k2", "k3", "k4" });
	initCache(cache, keys, values);
	cache.put(keys[0], values[0]);
	checkLRUCache(cache, new String[] { "k0", "k6", "k7", "k8", "k9" },
		new String[] { "k5", "k1", "k2", "k3", "k4" });
	initCache(cache, keys, values);
	cache.get(keys[5]);
	cache.put(keys[0], values[0]);
	checkLRUCache(cache, new String[] { "k0", "k5", "k7", "k8", "k9" },
		new String[] { "k6", "k1", "k2", "k3", "k4" });
    }

    /**
     * Test the {@link TTLCache}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testTtlCache() throws Exception {
	long ref = System.currentTimeMillis();
	TTLCache<String, String> cache = new TTLCache<String, String>();
	cache.setTtl(20000);
	for (int i = 0; i < 10; i++) {
	    cache.put("key" + i, "value" + i);
	    Thread.sleep(2000);
	}
	Thread.sleep(2500);
	Assert.assertEquals(8, cache.size());
    }
}
