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
package ldapbeans.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;
import ldapbeans.exception.LdapDefinitionException;
import ldapbeans.util.scanner.ClassFilter;
import ldapbeans.util.scanner.PackageHelper;

public final class LdapBeanHelper {

    /**
     * Filter that accept classed annotated with {@link ObjectClass}
     */
    private static class ObjetClassClassFilter implements ClassFilter {

	/**
	 * {@inheritDoc}
	 */
	public boolean accept(Class<?> p_Class) {
	    boolean accept = false;
	    Annotation annotation = p_Class.getAnnotation(ObjectClass.class);
	    if (annotation != null) {
		accept = true;
	    }
	    return accept;
	}
    }

    /** The unique instance of this class */
    private final static LdapBeanHelper INSTANCE = new LdapBeanHelper();

    /**
     * Return the unique instance of this class.
     * 
     * @return The unique instance of this class.
     */
    public static LdapBeanHelper getInstance() {
	return INSTANCE;
    }

    /**
     * Collection of managed classes (it is implemented as a Set to manage
     * double)
     */
    private final Collection<Class<?>> m_ManagedClasses;

    /**
     * Default constructor. This class can not be instantiated.
     */
    private LdapBeanHelper() {
	m_ManagedClasses = new HashSet<Class<?>>();
	addManagedClass(LdapBean.class);
    }

    /**
     * Add a class that will be managed by managers
     * 
     * @param p_Class
     *            {@link Class} to add to managed classes
     */
    public void addManagedClass(Class<?> p_Class) {
	m_ManagedClasses.add(p_Class);
    }

    /**
     * Add a class that will be managed by managers
     * 
     * @param p_Classes
     *            Collection of {@link Class} that will be added to add to
     *            managed classes
     */
    public void addManagedClasses(Collection<Class<?>> p_Classes) {
	m_ManagedClasses.addAll(p_Classes);
    }

    /**
     * Add all classes found recursively in a package to managed classes.
     * 
     * @param p_PackageName
     *            The name of the package to scan;
     */
    public void scanPackage(String p_PackageName) {
	ClassFilter classFilter = new ObjetClassClassFilter();
	try {
	    Collection<Class<?>> classes = PackageHelper.getInstance()
		    .getClasses(p_PackageName, true, classFilter);
	    addManagedClasses(classes);
	} catch (ClassNotFoundException e) {
	}
    }

    /**
     * Return the LdapAttribute annotation name of the ldap object mapped by a
     * method
     * 
     * @param p_Method
     *            The method that should mapped to ldap attribute
     * @return The attribute name, or <code>null</code> if no mapping is defined
     */
    public LdapAttribute getLdapAttribute(Method p_Method) {
	LdapAttribute ldapAttribute = null;
	Annotation[] annotations = p_Method.getDeclaredAnnotations();
	for (Annotation annotation : annotations) {
	    if (annotation instanceof LdapAttribute) {
		ldapAttribute = (LdapAttribute) annotation;
	    }
	}
	return ldapAttribute;
    }

    /**
     * Check that the objectclass definition of the class match with the
     * attributes
     * 
     * @param p_Class
     *            Le class to check
     * @param p_Attributes
     *            Attributes that contains objectclass
     * @throws LdapDefinitionException
     *             If the objectclass definition of the class does not match
     *             with the attributes
     */
    public void checkObjectClass(Class<?> p_Class, Attributes p_Attributes)
	    throws LdapDefinitionException {
	if (p_Class != null) {
	    checkObjectClass(p_Attributes.get("ObjectClass"),
		    p_Class.getAnnotation(ObjectClass.class));
	    checkObjectClass(p_Class.getSuperclass(), p_Attributes);
	    for (Class<?> parent : p_Class.getInterfaces()) {
		checkObjectClass(parent, p_Attributes);
	    }
	}
    }

    /**
     * Check that the objectclass definition of the class match with the
     * attributes
     * 
     * @param p_Attribute
     *            Attribute that contains objectclass
     * @param p_Annotation
     *            Extected objectclasses
     * @throws LdapDefinitionException
     *             If the objectclass definition of the class does not match
     *             with the attributes
     */
    private void checkObjectClass(Attribute p_Attribute,
	    ObjectClass p_Annotation) throws LdapDefinitionException {
	String[] expectedObjectClasses;
	if (p_Annotation != null) {
	    expectedObjectClasses = p_Annotation.value();
	    for (String objectClass : expectedObjectClasses) {
		if (!p_Attribute.contains(objectClass)) {
		    throw new LdapDefinitionException(
			    "Ldap bean does not contains \"" + objectClass
				    + "\" ObjectClass");
		}
	    }
	}
    }

    /**
     * Add objectclass definition of the class to attributes
     * 
     * @param p_Class
     *            The class that contains objectclass definition
     * @param p_Attributes
     *            Attributes to be completed
     */
    public void addObjectClass(Class<?> p_Class, Attributes p_Attributes) {
	ObjectClass annotation;
	Attribute attribute;
	if (p_Class != null) {
	    annotation = p_Class.getAnnotation(ObjectClass.class);
	    if (annotation != null) {
		for (String objectClass : annotation.value()) {
		    attribute = p_Attributes.get("objectClass");
		    if (attribute == null) {
			attribute = new BasicAttribute("objectClass");
			p_Attributes.put(attribute);
		    }
		    attribute.add(objectClass);
		}
	    }
	    addObjectClass(p_Class.getSuperclass(), p_Attributes);
	    for (Class<?> parent : p_Class.getInterfaces()) {
		addObjectClass(parent, p_Attributes);
	    }
	}
    }

    /**
     * Find classes that match to the objectclass of {@link LdapObject}
     * 
     * @param p_Attributes
     *            {@link Attributes} on witch classes have to be found
     * @return Classes that correspond to LdapObject
     */
    public Class<?>[] getClasses(Attributes p_Attributes) {
	List<Class<?>> classes = new ArrayList<Class<?>>();

	for (Class<?> clazz : m_ManagedClasses) {
	    try {
		checkObjectClass(clazz, p_Attributes);
		// the class match to attributes
		classes.add(clazz);
	    } catch (LdapDefinitionException e) {
		// The class does not match attributes
	    }
	}

	return classes.toArray(new Class[classes.size()]);
    }

}
