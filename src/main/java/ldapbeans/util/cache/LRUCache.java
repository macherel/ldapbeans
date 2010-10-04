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

public interface LRUCache<K, V> extends Cache<K, V> {
    /**
     * Get the maximum size of the cache. If an entry is added when the cache is
     * full, oldest entry will be removed
     * 
     * @return the maximum size of the pool
     */
    public int getMaxSize();

    /**
     * Set the maximum size of the cache. If an entry is added when the cache is
     * full, oldest entry will be removed
     * 
     * @param p_MaxSize
     *            the maximum size of the pool
     */
    public void setMaxSize(int p_MaxSize);

}
