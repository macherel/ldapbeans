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

import java.util.ArrayList;
import java.util.List;

import ldapbeans.util.pool.exception.NoMoreObjectInPoolException;
import ldapbeans.util.pool.exception.NotPooledObjectException;
import ldapbeans.util.pool.exception.NotValidObjectException;

public abstract class AbstractPool<T> implements Pool<T> {

    /** Lock to control pool access */
    private final Object m_Lock = new Object();

    /** List of available object */
    private final List<T> m_Available;

    /** List of used object */
    private final List<T> m_Used;

    /** Optimal size of the pool (maximum that the pool will retain) */
    private final int m_Size;

    /** Initial size of the pool */
    private final int m_MinSize;

    /** Maximal size of the pool */
    private final int m_MaxSize;

    /**
     * Constructor
     * 
     * @param p_Size
     *            Size of the pool (maximum that the pool will retain)
     */
    public AbstractPool(int p_Size) {
	this(p_Size, 0, -1);
    }

    /**
     * Abstract constructor of a pool
     * 
     * @param p_Size
     *            Optimal size of the pool
     * @param p_MinSize
     *            Minimum size of the pool
     * @param p_MaxSize
     *            Maximum size of the pool
     */
    public AbstractPool(int p_Size, int p_MinSize, int p_MaxSize) {
	m_Size = p_Size;
	m_MinSize = p_MinSize;
	m_MaxSize = p_MaxSize;
	m_Available = new ArrayList<T>(m_Size);
	m_Used = new ArrayList<T>(m_Size);
	initialize();
    }

    /**
     * Initialization of the pool
     */
    protected void initialize() {
	synchronized (m_Lock) {
	    while (size() < m_MinSize) {
		m_Available.add(create());
	    }
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @see ldapbeans.util.pool.Pool#size()
     */
    public int size() {
	return getAvailableSize() + getUsedSize();
    }

    /**
     * {@inheritDoc}
     * 
     * @see ldapbeans.util.pool.Pool#getAvailableSize()
     */
    public int getAvailableSize() {
	return m_Available.size();
    }

    /**
     * {@inheritDoc}
     * 
     * @see ldapbeans.util.pool.Pool#getUsedSize()
     */
    public int getUsedSize() {
	return m_Used.size();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Pool#acquire()
     */
    public T acquire() throws NoMoreObjectInPoolException,
	    NotValidObjectException {
	T result = null;
	try {
	    synchronized (m_Lock) {
		do {
		    try {
			result = findAvailableObject();
		    } catch (Exception e) {
			result = null;
		    }
		} while ((result == null) && (m_Available.size() > 0));

		if (result == null) {
		    result = createNewObject();
		}

		if (result != null) {
		    m_Used.add(result);
		} else {
		    throw new NoMoreObjectInPoolException();
		}
	    }
	} finally {
	    // Missing object may be created
	    initialize();
	}

	return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Pool#release(Object)
     */
    public void release(T p_Object) throws NotPooledObjectException {
	synchronized (m_Lock) {
	    if (m_Used.remove(p_Object)) {
		if (m_Available.size() <= m_Size) {
		    m_Available.add(p_Object);
		}
	    } else {
		throw new NotPooledObjectException();
	    }
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @see Pool#validate(Object);
     */
    public void validate(T p_Object) throws NotValidObjectException {
	// Nothing to do
	// all object are valid by default
    }

    /**
     * Initialize object when object will be acquired
     * 
     * @param p_Object
     *            The object that is acquired
     */
    protected void initialize(T p_Object) {
	// Do nothing by default
    }

    /**
     * Create new instance of object that will be stored in the pool
     * 
     * @return new instance of object that will be stored in the pool
     */
    protected abstract T create();

    /**
     * Retrieve the first available object of the pool. The object will be
     * initialized and validated
     * 
     * @return the first available object of the pool
     * @throws NotValidObjectException
     *             If the object is not valid
     */
    private T findAvailableObject() throws NotValidObjectException {
	T result;
	if (m_Available.size() > 0) {
	    result = m_Available.remove(m_Available.size() - 1);
	    initialize(result);
	    validate(result);
	} else {
	    // There's no more object in the pool
	    result = null;
	}
	return result;
    }

    /**
     * Create a new object for the pool if maximum size is not reached
     * 
     * @return A new pooled object
     * @throws NotValidObjectException
     *             If the obejct is not valid.
     */
    private T createNewObject() throws NotValidObjectException {
	T result = null;
	int size = m_Available.size() + m_Used.size();
	if ((size < m_MaxSize) || (m_MaxSize < 0)) {
	    // there is no more available object in the pool
	    // we can create another object
	    result = create();
	    initialize(result);
	    validate(result);
	}
	return result;
    }
}
