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
package ldapbeans.util.pool.exception;

@SuppressWarnings("serial")
public abstract class PoolException extends RuntimeException {

    /**
     * {@inheritDoc}
     * 
     * @see PoolException#RuntimeException()
     */
    public PoolException() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see PoolException#RuntimeException(Throwable)
     */
    public PoolException(Throwable p_Cause) {
	super(p_Cause);
    }
}
