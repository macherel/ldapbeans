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
import ldapbeans.util.pool.exception.NotPooledObjectException;
import ldapbeans.util.pool.exception.NotValidObjectException;

public interface Pool<T> {

    /**
     * Return an instance of object contains in the pool
     * 
     * @return An instance of object contains in the pool
     * @throws NoMoreObjectInPoolException
     *             If there is no more object in the pool
     * @throws NotValidObjectException
     *             If there is no valid object in the pool
     */
    T acquire() throws NoMoreObjectInPoolException, NotValidObjectException;

    /**
     * Put new Object in the pool
     * 
     * @param p_Object
     *            The object that return in the pool
     * @throws NotPooledObjectException
     *             If the object does not come from the pool
     */
    void release(T p_Object) throws NotPooledObjectException;

    /**
     * Validate an object from the pool
     * 
     * @param p_Object
     *            The object to validate
     * @throws NotValidObjectException
     *             if the object is not valid.
     */
    void validate(T p_Object) throws NotValidObjectException;

    /**
     * Return the number of managed object by the pool
     * 
     * @return the number of available object in the pool
     */
    int size();

    /**
     * Return the number of available object in the pool
     * 
     * @return the number of available object in the pool
     */
    int getAvailableSize();

    /**
     * Return the number of used object in the pool
     * 
     * @return the number of used object in the pool
     */
    int getUsedSize();

}
