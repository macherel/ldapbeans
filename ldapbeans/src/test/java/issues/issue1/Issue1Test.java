/*
 * Thissuesrt of ldapbeans
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
package issues.issue1;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;
import ldapbeans.bean.LdapBean;
import ldapbeans.bean.LdapBeanManager;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") }, allowAnonymousAccess = true)
@ApplyLdifFiles({ "ldapbeans.ldif" })
public class Issue1Test {
    public static DirectoryService service;
    public static boolean isRunInSuite;
    public static LdapServer ldapServer;

    private static abstract class AbstractClassLoader extends ClassLoader {

	private final ClassLoader m_ParentClassLoader;

	/**
	 * Constructor of the {@link AbstractClassLoader}
	 * 
	 * @param p_ParentClassLoader
	 *            the {@link ClassLoader} to use if the class can't be load
	 *            by this classloader
	 */
	public AbstractClassLoader(ClassLoader p_ParentClassLoader) {
	    super(p_ParentClassLoader);
	    m_ParentClassLoader = p_ParentClassLoader;
	}

	/**
	 * Indicate if the class have to be loaded by this classloader
	 * 
	 * @param p_Name
	 *            The name of the class to load
	 * @return <code>true</code> if the class have to be loaded by this
	 *         classloader, <code>false</code> otherwise
	 */
	protected abstract boolean classToLoad(String p_Name);

	/**
	 * Indicate if the class do not have to be loaded by this classloader
	 * 
	 * @param p_Name
	 *            The name of the class to load
	 * @return <code>true</code> if the class do not have to be loaded by
	 *         this classloader, <code>false</code> otherwise
	 */
	protected abstract boolean classNotToLoad(String p_Name);

	@Override
	public Class<?> loadClass(String p_Name) throws ClassNotFoundException {
	    Class<?> result;
	    if (classToLoad(p_Name)) {
		System.out.println(p_Name);
		try {
		    ClassWriter cw = new ClassWriter(0);
		    ClassAdapter ca = new ClassAdapter(cw);
		    ClassReader cr = new ClassReader(p_Name);
		    cr.accept(ca, 0);
		    byte[] datas = cw.toByteArray();
		    result = defineClass(p_Name, datas, 0, datas.length);
		    return result;
		} catch (IOException e) {
		    throw new ClassNotFoundException(p_Name, e);
		}
	    } else if (classNotToLoad(p_Name)) {
		throw new ClassNotFoundException(p_Name);
	    } else {
		return m_ParentClassLoader.loadClass(p_Name);
	    }
	}
    }

    public static class MyClassLoader1 extends AbstractClassLoader {
	/**
	 * {@inheritDoc}
	 * 
	 * @see AbstractClassLoader#AbstractClassLoader(ClassLoader)
	 */
	public MyClassLoader1(ClassLoader p_ParentClassLoader) {
	    super(p_ParentClassLoader);
	}

	@Override
	protected boolean classToLoad(String p_Name) {
	    return false;
	}

	@Override
	protected boolean classNotToLoad(String p_Name) {
	    return p_Name.startsWith("issuses");
	}
    }

    public static class MyClassLoader2 extends AbstractClassLoader {
	/**
	 * {@inheritDoc}
	 * 
	 * @see AbstractClassLoader#AbstractClassLoader(ClassLoader)
	 */
	public MyClassLoader2(ClassLoader p_ParentClassLoader) {
	    super(p_ParentClassLoader);
	}

	@Override
	protected boolean classToLoad(String p_Name) {
	    return p_Name.startsWith("issues");
	}

	@Override
	protected boolean classNotToLoad(String p_Name) {
	    return false;
	}
    }

    @ObjectClass("organizationalUnit")
    public static interface TestLdapBean extends LdapBean {
	/**
	 * Return the ou attribute of the ldap object
	 * 
	 * @return The ou attribute of the
	 */
	@LdapAttribute("ou")
	public String getOu();
    }

    public static class TestClass {

	private static LdapBeanManager s_Manager;

	/**
	 * Initialization of the test class
	 * 
	 * @param p_Port
	 *            Value of the ldap server port
	 */
	public static void init(int p_Port) {
	    s_Manager = LdapBeanManager.getInstance("ldap://localhost:"
		    + p_Port, "ou=system");
	}

	/**
	 * Simple test using LdapBeanManager
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	public void test() throws Exception {
	    TestLdapBean bean = null;
	    Object o = s_Manager.create(TestLdapBean.class, "ou=foo,ou=system");
	    System.out.println(o.getClass() + " - "
		    + o.getClass().getClassLoader());
	    System.out.println("######################");
	    bean = (TestLdapBean) o;
	    bean.store();
	}
    }

    /**
     * Test for the issue #1
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testIssue() throws Exception {
	// The test simply call the runTest method
	// A custom class loader is used so as not to load classes from the
	// issues package
	Thread currentThread = Thread.currentThread();
	ClassLoader oldClassLoader = currentThread.getContextClassLoader();
	ClassLoader newClassLoader = new MyClassLoader1(
		MyClassLoader1.class.getClassLoader());
	currentThread.setContextClassLoader(newClassLoader);
	Class<?> clazz = newClassLoader.loadClass(Issue1Test.class.getName());
	Object o = clazz.newInstance();
	Method method = o.getClass().getMethod("runTest");
	try {
	    method.invoke(o);
	    method.invoke(o);
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	    throw e;
	}
	currentThread.setContextClassLoader(oldClassLoader);
    }

    /**
     * Run the test in a separated classloader
     * 
     * @throws Exception
     *             If an error occurs
     */
    public static void runTest() throws Exception {
	// The test is conducted in a separate classloader to simulate a
	// container (i.e. J2EE Servlet, EJB, ...)
	Thread currentThread = Thread.currentThread();
	ClassLoader oldClassLoader = currentThread.getContextClassLoader();
	ClassLoader newClassLoader = new MyClassLoader2(
		MyClassLoader2.class.getClassLoader());
	currentThread.setContextClassLoader(newClassLoader);
	Class<?> clazz = newClassLoader.loadClass(TestClass.class.getName());
	// Initialize the newly loaded class
	clazz.getMethod("init", int.class).invoke(null,
		Issue1Test.ldapServer.getPort());
	Object o = clazz.newInstance();
	try {
	    @SuppressWarnings("unused")
	    TestClass testClass = (TestClass) o;
	    Assert.fail();
	} catch (ClassCastException e) {
	    // Check that is not the class loaded by the system classloader
	}
	// Run the test method
	o.getClass().getMethod("test").invoke(o);
	currentThread.setContextClassLoader(oldClassLoader);

    }
}
