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
package ldapbeans.util.pool;

import ldapbeans.util.pool.exception.NoMoreObjectInPoolException;
import ldapbeans.util.pool.exception.NotValidObjectException;

/**
 * Pool of object where none of them is valid.
 */
public class UnvalidateObjectPool extends AbstractPool<Object> {

    private int m_Count = 0;

    /**
     * Construct a new {@link UnvalidateObjectPool}
     */
    public UnvalidateObjectPool() {
	super(5);
    }

    @Override
    public Object acquire() throws NoMoreObjectInPoolException,
	    NotValidObjectException {
	m_Count = 0;
	return super.acquire();
    }

    @Override
    protected Object create() {
	if (m_Count++ > 1000000) {
	    throw new RuntimeException();
	}
	m_Count++;
	return new Object();
    }

    @Override
    public void validate(Object p_Object) throws NotValidObjectException {
	throw new NotValidObjectException();
    }

}
