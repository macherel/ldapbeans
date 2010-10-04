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

public class GenericKey {
    private final Object[] m_Keys;

    /**
     * Construct a key base on an array of object
     * 
     * @param p_Keys
     *            Object that constitute the key
     */
    public GenericKey(Object... p_Keys) {
	if (p_Keys == null) {
	    throw new IllegalArgumentException();
	}
	m_Keys = p_Keys;
    }

    @Override
    public int hashCode() {
	int hash = super.hashCode();
	if (m_Keys != null) {
	    for (Object obj : m_Keys) {
		hash = hash ^ 53 + obj.hashCode();
	    }
	}
	return hash;
    }

    @Override
    public boolean equals(Object p_Obj) {
	boolean equals = false;
	if ((p_Obj != null) && (p_Obj instanceof GenericKey)) {
	    Object[] keys = ((GenericKey) p_Obj).m_Keys;
	    if (keys.length == m_Keys.length) {
		equals = true;
		for (int i = 0; i < m_Keys.length; i++) {
		    if (m_Keys[i] != null) {
			if (!m_Keys[i].equals(keys[i])) {
			    equals = false;
			}
		    } else if (keys[i] != null) {
			equals = false;
		    }
		}
	    }
	}
	return equals;
    }
}
