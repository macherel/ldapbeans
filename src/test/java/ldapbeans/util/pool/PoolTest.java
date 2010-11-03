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

import org.junit.Assert;
import org.junit.Test;

public class PoolTest {

    /**
     * Test {@link NullObjectPool}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testNullObjectPool() throws Exception {
	Object o = null;
	Pool<Object> pool = new NullObjectPool();

	try {
	    o = pool.acquire();
	    Assert.fail("An error may occur when acquiring object");
	} catch (NoMoreObjectInPoolException e) {
	    // All object from pool are null
	}
	Assert.assertNull("Object in pool should be null", o);
    }

    /**
     * Test {@link UnvalidateObjectPool}.
     * 
     * @throws Exception
     *             if an errors occurs
     */
    @Test
    public void testUnvalidateObjectPool() throws Exception {
	Object o = null;
	Pool<Object> pool = new UnvalidateObjectPool();

	try {
	    o = pool.acquire();
	} catch (NotValidObjectException e) {
	    // there is no object in the pool because none of them will be
	    // validated
	}

	Assert.assertNull("Object in pool should be null", o);
    }
}
