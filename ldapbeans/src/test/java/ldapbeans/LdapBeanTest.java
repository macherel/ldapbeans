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
package ldapbeans;

import javax.naming.directory.SchemaViolationException;

import junit.framework.Assert;
import ldapbeans.bean.BeanForBooleanTest;
import ldapbeans.bean.BeanForIntegerTest;
import ldapbeans.bean.LdapBean;
import ldapbeans.bean.LdapBeanHelper;
import ldapbeans.bean.LdapBeanManager;
import ldapbeans.bean.OrganizationalUnit;
import ldapbeans.bean.Person;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test a single search.
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", port = 10389) }, allowAnonymousAccess = true)
// @ApplyLdifs({
// // The added entry
// "dn: cn=Kim Wilde,ou=system\n" + "objectClass: person\n" +
// "objectClass: top\n"
// + "cn: Kim Wilde\n" + "sn: Wilde\n\n" })
@ApplyLdifFiles({ "ldapbeans.ldif" })
public class LdapBeanTest {
    public static DirectoryService service;
    public static boolean isRunInSuite;
    public static LdapServer ldapServer;
    private static LdapBeanManager s_Manager;

    /**
     * Initialize all test on {@link LdapBean}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @BeforeClass
    public static void initialize() throws Exception {
	// int port = ldapServer.getPort();
	int port = 10389;
	s_Manager = LdapBeanManager.getInstance("ldap://localhost:" + port,
		"ou=system", null, null);

	LdapBeanHelper.getInstance().scanPackage("ldapbeans.bean");
    }

    /**
     * Reset a test
     * 
     * @throws Exception
     *             If an error occurs
     */
    @After
    public void teardown() throws Exception {
	s_Manager.clearCache();
    }

    /**
     * Test the creation of a bean
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testSimpleCreate() throws Exception {
	Person person = null;
	person = s_Manager.create(Person.class, "cn=foo,ou=system");
	person.setCommonName("foo");
	// surname is mandatory
	person.setSurname("surname");
	person.store();
	Assert.assertEquals("foo", person.getCommonName());
    }

    /**
     * Test the creation of a bean whithout RdnAttribute
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testCreateWithoutRdnAttribute() throws Exception {
	Person person = null;
	person = s_Manager.create(Person.class, "cn=foo,ou=system");
	// surname is mandatory
	person.setSurname("surname");
	// Saving bean add 'foo' to 'cn' attribute because it is a part of 'rdn'
	person.store();
	// Cn is not set because, cn was added by the server
	// and the bean was not read from the server
	Assert.assertEquals(null, person.getCommonName());
	// Restoring bean is necessary to get the 'cn' attribute
	person.restore();
	Assert.assertEquals("foo", person.getCommonName());
    }

    /**
     * Test the creation of a bean whithout RdnAttribute
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testCreateWithAnotherValueForRdnAttribute() throws Exception {
	Person person = null;
	person = s_Manager.create(Person.class, "cn=foo,ou=system");
	// surname is mandatory
	person.setSurname("surname");
	person.setCommonName("bar");
	try {
	    // Saving bean add 'foo' to 'cn' attribute
	    // because it is a part of 'rdn'
	    person.store();
	    // Restoring bean is necessary to get the 'cn' attribute
	    // with 'foo' and 'bar' values
	    // Otherwise 'cn' attribute contains onloy 'bar' value.
	    person.restore();
	    Assert.assertTrue("CN has to contain \"foo\" value", person
		    .getCommonNames().contains("foo"));
	    Assert.assertTrue("CN has to contain \"bar\" value", person
		    .getCommonNames().contains("bar"));
	    Assert.assertEquals("CN have to contain only 2 value", 2, person
		    .getCommonNames().size());
	} catch (Exception e) {
	    // Common name does not correspond to dn
	}
	person.setCommonName("bar");
	try {
	    person.store();
	} catch (SchemaViolationException e) {
	    Assert.assertEquals("[LDAP: error code 67 - NOT_ALLOWED_ON_RDN:"
		    + " failed for     Modify Request\n"
		    + "        Object : 'cn=foo,ou=system'\n"
		    + "            Modification[0]\n"
		    + "                Operation :  replace\n"
		    + "                Modification\n"
		    + "    objectClass: person\n" + "    objectClass: top\n"
		    + "            Modification[1]\n"
		    + "                Operation :  replace\n"
		    + "                Modification\n" + "    sn: surname\n"
		    + "            Modification[2]\n"
		    + "                Operation :  replace\n"
		    + "                Modification\n" + "    cn: bar\n"
		    + ": ERR_62 Entry cn=foo,ou=system "
		    + "does not have the cn attributeType, "
		    + "which is part of the RDN\";]", e.getExplanation());
	}
    }

    /**
     * Test the creation of a bean whithout RdnAttribute
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testCreateBeanAlreadyExists() throws Exception {
	Person person = null;
	// Kim Wilde already exists, bean is restored from directory
	person = s_Manager.create(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertEquals("cn=Kim Wilde,ou=system", person.getDN());
	Assert.assertEquals("Kim Wilde", person.getCommonName());
	Assert.assertEquals("Wilde", person.getSurname());
    }

    /**
     * 
     * Test the removal of a bean
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testRemove() throws Exception {
	Person person = null;

	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertNotNull(person);
	person.remove();
	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertNull(person);

    }

    /**
     * Test the search of a bean with the DN
     */
    @Test
    public void testFindByDn() {
	Person person = null;

	person = s_Manager.findByDn(Person.class, "cn=foo,ou=system");
	Assert.assertNull(person + " should not exist", person);

	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertNotNull(person);
	Assert.assertEquals("cn=Kim Wilde,ou=system", person.getDN());
	Assert.assertEquals("Kim Wilde", person.getCommonName());
	Assert.assertEquals("Wilde", person.getSurname());

    }

    /**
     * Test saving a bean in the directory
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testStore() throws Exception {
	Person person = null;

	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertNotNull(person);
	Assert.assertEquals("cn=Kim Wilde,ou=system", person.getDN());
	Assert.assertEquals("Kim Wilde", person.getCommonName());
	Assert.assertEquals("Wilde", person.getSurname());

	person.setSurname("foo");
	Assert.assertEquals("Surname should have changed.", "foo",
		person.getSurname());
	Person otherPerson = s_Manager.findByDn(Person.class,
		"cn=Kim Wilde,ou=system");
	Assert.assertEquals("Surname should have changed in cache.", "foo",
		otherPerson.getSurname());
	otherPerson.restore();
	Assert.assertEquals("Wilde", person.getSurname());

	person.setSurname("foo");
	person.store();
	person.restore();
	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertEquals("foo", person.getSurname());

    }

    /**
     * 
     * Test restoring a bean from the directory
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testRestore() throws Exception {
	Person person = null;

	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertNotNull(person);
	Assert.assertEquals("cn=Kim Wilde,ou=system", person.getDN());
	Assert.assertEquals("Kim Wilde", person.getCommonName());
	Assert.assertEquals("Wilde", person.getSurname());

	person.setSurname("foo");
	Assert.assertEquals("foo", person.getSurname());
	person.restore();
	Assert.assertEquals("Wilde", person.getSurname());
    }

    /**
     * Test moving a bean in the directory
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testMove() throws Exception {
	Person person = null;

	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertNotNull(person);
	Assert.assertEquals("cn=Kim Wilde,ou=system", person.getDN());
	Assert.assertEquals("Kim Wilde", person.getCommonName());
	Assert.assertEquals("Wilde", person.getSurname());

	person.move("cn=foo,ou=system");

	person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
	Assert.assertNull(person + " should have moved.", person);

	person = s_Manager.findByDn(Person.class, "cn=foo,ou=system");
	Assert.assertNotNull(person);
	Assert.assertEquals("cn=foo,ou=system", person.getDN());
	Assert.assertEquals("foo", person.getCommonName());
	Assert.assertEquals("Wilde", person.getSurname());
    }

    /**
     * Other test for search
     */
    @Test
    public void testFind() {
	Person person = (Person) s_Manager.findByDn("cn=Kim Wilde,ou=system");
	Assert.assertNotNull("cn=Kim Wilde,ou=system should exist", person);

	OrganizationalUnit ou = (OrganizationalUnit) s_Manager
		.findByDn("ou=system");
	Assert.assertNotNull("ou=system should exist", ou);
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testBooleanGetter() throws Exception {
	BeanForBooleanTest bean = s_Manager.create(BeanForBooleanTest.class,
		"ou=foo,ou=system");
	// 1. Set string and check
	setAndCheckBooleanBean(bean, "true", true);
	setAndCheckBooleanBean(bean, "false", false);
	setAndCheckBooleanBean(bean, "1", true);
	setAndCheckBooleanBean(bean, "0", false);

	// 2. Set boolean and check
	bean.setIsOK(true);
	bean.store();
	bean.restore();
	Assert.assertEquals("true", bean.getDescription());
	Assert.assertEquals(Boolean.TRUE, bean.getIsOK());
	Assert.assertEquals(Boolean.TRUE, bean.isOKTrueFalse());
	try {
	    Assert.assertEquals(Boolean.TRUE, bean.isOK01());
	    Assert.fail("\"true\" could not be converted " +
	    		"to boolean with this method");
	} catch (IllegalArgumentException e) {
	    // "true" could not be converted to boolean with this method
	}
	Assert.assertEquals(true, bean.isOK());
	Assert.assertEquals(false, bean.isKO());
    }

    /**
     * Valuate the bean and check that it is correctly valuated
     * 
     * @param p_Bean
     *            the bean to valuate
     * @param p_NewValue
     *            The string value to set to the bean
     * @param p_ValueToCheck
     *            Boolean value to check
     * @throws Exception
     *             If an error occurs
     */
    private static void setAndCheckBooleanBean(BeanForBooleanTest p_Bean,
	    String p_NewValue, boolean p_ValueToCheck) throws Exception {
	Boolean ok = p_ValueToCheck;
	Boolean ko = !p_ValueToCheck;

	p_Bean.setDescription(p_NewValue);
	p_Bean.store();
	p_Bean.restore();
	// 1. Simple check
	Assert.assertEquals(p_NewValue, p_Bean.getDescription());
	// 2. Conversion check
	// (length > 1) => ((p_NewValue == "true") || (p_NewValue == "false"))
	if (p_NewValue.length() > 1) {
	    Assert.assertEquals(ok, p_Bean.isOKTrueFalse());
	} else {
	    try {
		p_Bean.isOKTrueFalse();
		Assert.fail("\"" + p_NewValue + "\" could not be converted "
			+ "to boolean with this method");
	    } catch (IllegalArgumentException e) {
		// p_NewValue could not be converted to boolean with this method
	    }
	}
	// (length == 1) => ((p_NewValue == "0") || (p_NewValue == "1"))
	if (p_NewValue.length() == 1) {
	    Assert.assertEquals(ok, p_Bean.isOK01());
	} else {
	    try {
		p_Bean.isOK01();
		Assert.fail("\"" + p_NewValue + "\" could not be converted "
			+ "to boolean with this method");
	    } catch (IllegalArgumentException e) {
		// p_NewValue could not be converted to boolean with this method
	    }
	}
	// "O", "1", "true" and "false" are accepted by this method,
	// so there is no particular problem
	Assert.assertEquals(ok, p_Bean.getIsOK());
	Assert.assertEquals(ok.booleanValue(), p_Bean.isOK());
	Assert.assertEquals(ko.booleanValue(), p_Bean.isKO());
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testIntegerGetter() throws Exception {
	BeanForIntegerTest bean = s_Manager.create(BeanForIntegerTest.class,
		"ou=foo,ou=system");
	bean.setDescription("42");
	bean.store();
	bean.restore();
	Assert.assertEquals("42", bean.getDescription());
	Assert.assertEquals(42, bean.getCount());

	bean.setCount(24);
	bean.store();
	bean.restore();
	Assert.assertEquals("24", bean.getDescription());
	Assert.assertEquals(24, bean.getCount());
    }

}
