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

import java.util.Iterator;

public class AggregateIterator<E> implements Iterator<E> {
    private final Iterator<E>[] m_Iterators;
    private int m_Index = 0;

    /**
     * Construct an iterator based on other iterators
     * 
     * @param p_Iterators
     *            Array of iterators that will be aggregate
     */
    public AggregateIterator(Iterator<E>... p_Iterators) {
	m_Iterators = p_Iterators;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
	boolean hasNext;
	if (m_Index < m_Iterators.length) {
	    hasNext = m_Iterators[m_Index].hasNext();
	    if (!hasNext) {
		m_Index++;
		hasNext = hasNext();
	    }
	} else {
	    hasNext = false;
	}
	return hasNext;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#next()
     */
    public E next() {
	hasNext();
	return m_Iterators[m_Index].next();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
	hasNext();
	m_Iterators[m_Index].remove();
    }
}
