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
package ldapbeans.util;

import junit.framework.Assert;

import org.junit.Test;

public class SynchronizedObjectTest {
    private static interface TestObject {
	/**
	 * A method for test
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	void test() throws Exception;
    }

    private static class TestObjectImpl implements TestObject {
	private boolean m_Used = false;

	/**
	 * {@inheritDoc}
	 * 
	 * @see TestObject#test()
	 */
	public void test() throws Exception {
	    if (m_Used == true) {
		throw new Exception();
	    }
	    m_Used = true;
	    synchronized (Thread.currentThread()) {
		Thread.currentThread().wait(100);
	    }
	    m_Used = false;
	}
    }

    private static class TestThread extends Thread {
	private final TestObject m_Object;
	private Exception m_Error = null;

	/**
	 * Construct a thread for test
	 * 
	 * @param p_Object
	 *            The object to test
	 */
	public TestThread(TestObject p_Object) {
	    m_Object = p_Object;
	}

	@Override
	public void run() {
	    try {
		m_Object.test();
	    } catch (Exception e) {
		m_Error = e;
	    }
	};

	/**
	 * Return an error that occurs during the execution of the test of the
	 * object.
	 * 
	 * @return The exception that occurs during the execution of the thread,
	 *         or <code>null</code> if there is no error
	 */
	public Exception getError() {
	    return m_Error;
	}
    }

    /**
     * Test Objects that have to be synchronized
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testSynchronizedObject() throws Exception {
	SynchronizedObjectFactory factory = SynchronizedObjectFactory
		.getInstance();
	TestObject obj = new TestObjectImpl();
	TestObject syncObj = factory.create(new TestObjectImpl());

	Assert.assertFalse(checkSynchronyedObject(obj));
	Assert.assertTrue(checkSynchronyedObject(syncObj));
    }

    /**
     * Check if the {@link TestObject} is synchronized
     * 
     * @param p_Object
     *            The object to test
     * @return <code>true</code> if the object may be synchronized,
     *         <code>false</code> if it is not.
     * @throws InterruptedException
     *             If an error occurs
     */
    private boolean checkSynchronyedObject(final TestObject p_Object)
	    throws InterruptedException {
	boolean error = false;
	try {
	    int nbThread = 5;
	    TestThread[] threads = new TestThread[5];
	    for (int i = 0; i < nbThread; i++) {
		threads[i] = new TestThread(p_Object);
	    }
	    for (TestThread thread : threads) {
		thread.start();
	    }
	    for (TestThread thread : threads) {
		thread.join();
	    }
	    for (TestThread thread : threads) {
		if (thread.getError() != null) {
		    error = true;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return !error;
    }
}
