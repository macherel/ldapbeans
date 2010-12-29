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
import ldapbeans.bean.BeanForNumberTest;
import ldapbeans.bean.BeanForTest;
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
	    Assert.assertTrue("CN (" + person.getCommonNames()
		    + ") has to contain \"foo\" value", person.getCommonNames()
		    .contains("foo"));
	    Assert.assertTrue("CN (" + person.getCommonNames()
		    + ") has to contain \"bar\" value", person.getCommonNames()
		    .contains("bar"));
	    Assert.assertEquals("CN (" + person.getCommonNames()
		    + ") have to contain only 2 value", 2, person
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
	    Assert.fail("\"true\" could not be converted "
		    + "to boolean with this method");
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
    public void testByte() throws Exception {
	BeanForNumberTest bean = s_Manager.create(BeanForNumberTest.class,
		"ou=foo,ou=system");
	bean.setDescription("42");
	bean.store();
	bean.restore();
	Assert.assertEquals("42", bean.getDescription());
	Assert.assertEquals(42, bean.getByteP());
	Assert.assertEquals(42, bean.getByte().byteValue());

	bean.setByteP(24);
	bean.store();
	bean.restore();
	Assert.assertEquals("24", bean.getDescription());
	Assert.assertEquals(24, bean.getByteP());
	Assert.assertEquals(24, bean.getByte().byteValue());

	bean.setByte(Byte.valueOf("22"));
	bean.store();
	bean.restore();
	Assert.assertEquals("22", bean.getDescription());
	Assert.assertEquals(22, bean.getByteP());
	Assert.assertEquals(22, bean.getByte().byteValue());
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testShort() throws Exception {
	BeanForNumberTest bean = s_Manager.create(BeanForNumberTest.class,
		"ou=foo,ou=system");
	bean.setDescription("42");
	bean.store();
	bean.restore();
	Assert.assertEquals("42", bean.getDescription());
	Assert.assertEquals(42, bean.getShortP());
	Assert.assertEquals(42, bean.getShort().byteValue());

	bean.setShortP((short) 24);
	bean.store();
	bean.restore();
	Assert.assertEquals("24", bean.getDescription());
	Assert.assertEquals(24, bean.getShortP());
	Assert.assertEquals(24, bean.getShort().byteValue());

	bean.setShort(Short.valueOf("22"));
	bean.store();
	bean.restore();
	Assert.assertEquals("22", bean.getDescription());
	Assert.assertEquals(22, bean.getShortP());
	Assert.assertEquals(22, bean.getShort().shortValue());
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testInteger() throws Exception {
	BeanForNumberTest bean = s_Manager.create(BeanForNumberTest.class,
		"ou=foo,ou=system");
	bean.setDescription("42");
	bean.store();
	bean.restore();
	Assert.assertEquals("42", bean.getDescription());
	Assert.assertEquals(42, bean.getInt());
	Assert.assertEquals(42, bean.getInteger().intValue());

	bean.setInt(24);
	bean.store();
	bean.restore();
	Assert.assertEquals("24", bean.getDescription());
	Assert.assertEquals(24, bean.getInt());
	Assert.assertEquals(24, bean.getInteger().intValue());

	bean.setInteger(Integer.valueOf("22"));
	bean.store();
	bean.restore();
	Assert.assertEquals("22", bean.getDescription());
	Assert.assertEquals(22, bean.getInt());
	Assert.assertEquals(22, bean.getInteger().intValue());
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testLong() throws Exception {
	BeanForNumberTest bean = s_Manager.create(BeanForNumberTest.class,
		"ou=foo,ou=system");
	bean.setDescription("42");
	bean.store();
	bean.restore();
	Assert.assertEquals("42", bean.getDescription());
	Assert.assertEquals(42, bean.getLongP());
	Assert.assertEquals(42, bean.getLong().longValue());

	bean.setLongP(24);
	bean.store();
	bean.restore();
	Assert.assertEquals("24", bean.getDescription());
	Assert.assertEquals(24, bean.getLongP());
	Assert.assertEquals(24, bean.getLong().longValue());

	bean.setLong(Long.valueOf("22"));
	bean.store();
	bean.restore();
	Assert.assertEquals("22", bean.getDescription());
	Assert.assertEquals(22, bean.getLongP());
	Assert.assertEquals(22, bean.getLong().longValue());
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testFloat() throws Exception {
	BeanForNumberTest bean = s_Manager.create(BeanForNumberTest.class,
		"ou=foo,ou=system");
	bean.setDescription("42");
	bean.store();
	bean.restore();
	Assert.assertEquals("42", bean.getDescription());
	Assert.assertEquals(42.0f, bean.getFloatP(), 0.01f);
	Assert.assertEquals(42.0f, bean.getFloat().floatValue(), 0.01f);

	bean.setFloatP(24.0f);
	bean.store();
	bean.restore();
	Assert.assertEquals("24.0", bean.getDescription());
	Assert.assertEquals(24.0f, bean.getFloatP(), 0.01);
	Assert.assertEquals(24.0f, bean.getFloat().floatValue(), 0.01);

	bean.setFloat(Float.valueOf("22"));
	bean.store();
	bean.restore();
	Assert.assertEquals("22.0", bean.getDescription());
	Assert.assertEquals(22.0f, bean.getFloatP(), 0.01f);
	Assert.assertEquals(22.0f, bean.getFloat().floatValue(), 0.01f);
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testDouble() throws Exception {
	BeanForNumberTest bean = s_Manager.create(BeanForNumberTest.class,
		"ou=foo,ou=system");
	bean.setDescription("42");
	bean.store();
	bean.restore();
	Assert.assertEquals("42", bean.getDescription());
	Assert.assertEquals(42.0d, bean.getDoubleP(), 0.01d);
	Assert.assertEquals(42.0d, bean.getDouble().doubleValue(), 0.01d);

	bean.setDoubleP(24.0d);
	bean.store();
	bean.restore();
	Assert.assertEquals("24.0", bean.getDescription());
	Assert.assertEquals(24.0d, bean.getDoubleP(), 0.01d);
	Assert.assertEquals(24.0d, bean.getDouble().doubleValue(), 0.01d);

	bean.setDouble(Double.valueOf("22"));
	bean.store();
	bean.restore();
	Assert.assertEquals("22.0", bean.getDescription());
	Assert.assertEquals(22.0d, bean.getDoubleP(), 0.01d);
	Assert.assertEquals(22.0d, bean.getDouble().doubleValue(), 0.01d);
    }

    /**
     * Test for getter and setter call
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testChar() throws Exception {
	BeanForTest bean = s_Manager.create(BeanForTest.class,
		"ou=foo,ou=system");
	bean.setDescription("a");
	bean.store();
	bean.restore();
	Assert.assertEquals("a", bean.getDescription());
	Assert.assertEquals('a', bean.getChar());
	Assert.assertEquals('a', bean.getCharacter().charValue());

	bean.setChar('b');
	bean.store();
	bean.restore();
	Assert.assertEquals("b", bean.getDescription());
	Assert.assertEquals('b', bean.getChar());
	Assert.assertEquals('b', bean.getCharacter().charValue());

	bean.setCharacter(Character.valueOf('c'));
	bean.store();
	bean.restore();
	Assert.assertEquals("c", bean.getDescription());
	Assert.assertEquals('c', bean.getChar());
	Assert.assertEquals('c', bean.getCharacter().charValue());
    }

    /**
     * Test for getter when it returns LdapBean type
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testLdapBeanGetterByDn() throws Exception {
	Person parent = (Person) s_Manager.findByDn("cn=parent,ou=system");
	Assert.assertNotNull("cn=parent,ou=system should exist", parent);

	Person child = parent.getOtherPersonByDn();
	Assert.assertNotNull("cn=child,ou=system should exist", child);
	Assert.assertEquals("child", child.getSurname());

	child = parent.getOtherPersonBySimpleSearch();
	Assert.assertNull(child);

	child = parent.getOtherPersonByRegexpSearch();
	Assert.assertNull(child);
    }

    /**
     * Test for getter when it returns LdapBean type
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testLdapBeanGetterBySimpleSeach() throws Exception {
	Person parent = (Person) s_Manager
		.findByDn("cn=parent_simple_search,ou=system");
	Assert.assertNotNull("cn=parent_simple_search,ou=system should exist",
		parent);

	Person child = parent.getOtherPersonByDn();
	Assert.assertNull(child);

	child = parent.getOtherPersonBySimpleSearch();
	Assert.assertNotNull("cn=child,ou=system should exist", child);
	Assert.assertEquals("child", child.getSurname());

	child = parent.getOtherPersonByRegexpSearch();
	Assert.assertNull(child);
    }

    /**
     * Test for getter when it returns LdapBean type
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testLdapBeanGetterByRegexpSearch() throws Exception {
	Person parent = (Person) s_Manager
		.findByDn("cn=parent_regexp_search,ou=system");
	Assert.assertNotNull("cn=parent_regexp_search,ou=system should exist",
		parent);

	Person child = parent.getOtherPersonByDn();
	Assert.assertNull(child);

	child = parent.getOtherPersonBySimpleSearch();
	Assert.assertNull(child);

	child = parent.getOtherPersonByRegexpSearch();
	Assert.assertNotNull("cn=child,ou=system should exist", child);
	Assert.assertEquals("child", child.getSurname());
    }

}
