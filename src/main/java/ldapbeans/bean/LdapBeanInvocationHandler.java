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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import ldapbeans.annotation.LdapAttribute;

class LdapBeanInvocationHandler implements InvocationHandler {

    /** Ldap object (DN + attributes) */
    private final LdapObject m_LdapObject;
    private final LdapObjectManager m_LdapObjectManager;

    /**
     * Constructor
     * 
     * @param p_LdapObject
     *            Datas of the bean
     * @param p_LdapObjectManager
     *            Manager of the {@link LdapObject}
     */
    LdapBeanInvocationHandler(LdapObject p_LdapObject,
	    LdapObjectManager p_LdapObjectManager) {
	m_LdapObject = p_LdapObject;
	m_LdapObjectManager = p_LdapObjectManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see InvocationHandler#invoke(Object, Method, Object[])
     */
    public Object invoke(Object p_Proxy, Method p_Method, Object[] p_Params)
	    throws Throwable {
	Object result = null;
	LdapBeanHelper ldapBeanHelper = LdapBeanHelper.getInstance();
	LdapAttribute ldapAttribute = ldapBeanHelper.getLdapAttribute(p_Method);

	// Object's methods
	if (p_Method.equals(Object.class.getMethod("toString"))) {
	    result = m_LdapObject.getDn();
	}
	// LdapBean's methods
	else if (p_Method.equals(LdapBean.class.getMethod("getDN"))) {
	    result = m_LdapObject.getDn();
	} else if (p_Method.equals(LdapBean.class.getMethod("store"))) {
	    m_LdapObjectManager.storeLdapObject(m_LdapObject);
	} else if (p_Method.equals(LdapBean.class.getMethod("restore"))) {
	    m_LdapObjectManager.restoreLdapObject(m_LdapObject);
	} else if (p_Method.equals(LdapBean.class.getMethod("move",
		String.class))) {
	    m_LdapObjectManager.moveLdapObject(m_LdapObject,
		    (String) p_Params[0]);
	} else if (p_Method.equals(LdapBean.class.getMethod("remove"))) {
	    m_LdapObjectManager.removeLdapObject(m_LdapObject);
	}
	// Others
	else if ((ldapAttribute != null) && (ldapAttribute.value() != null)) {
	    result = invokeGetterOrSetter(p_Method, p_Params, ldapAttribute);
	} else {
	    throw new UnsupportedOperationException();
	}
	return result;
    }

    /**
     * Method that will be invoke each time the method does not correspond to a
     * known method.
     * 
     * @param p_Method
     *            the <code>Method</code> instance corresponding to the
     *            interface method invoked on the proxy instance. The declaring
     *            class of the <code>Method</code> object will be the interface
     *            that the method was declared in, which may be a superinterface
     *            of the proxy interface that the proxy class inherits the
     *            method through.
     * 
     * @param p_Params
     *            an array of objects containing the values of the arguments
     *            passed in the method invocation on the proxy instance, or
     *            <code>null</code> if interface method takes no arguments.
     *            Arguments of primitive types are wrapped in instances of the
     *            appropriate primitive wrapper class, such as
     *            <code>java.lang.Integer</code> or
     *            <code>java.lang.Boolean</code>.
     * @param p_LdapAttribute
     *            Annotation describing the LDAP attribute in witch data is
     *            store
     * @return The value the LDAP attribute if the method is a getter
     * @throws NamingException
     *             If an error occurs
     */
    private Object invokeGetterOrSetter(Method p_Method, Object[] p_Params,
	    LdapAttribute p_LdapAttribute) throws NamingException {
	Class<?> returnType = p_Method.getReturnType();
	Object result = null;
	if (void.class.equals(returnType) && (p_Params.length == 1)) {
	    // It must be a setter
	    invokeSetter(p_Method, p_LdapAttribute, p_Params[0]);
	} else if ((!void.class.equals(returnType)) && (p_Params == null)) {
	    // It must be a getter
	    result = invokeGetter(p_Method, p_LdapAttribute, returnType);
	} else {
	    throw new UnsupportedOperationException();
	}
	return result;
    }

    /**
     * Method that will be invoked each time a setter is called
     * 
     * @param p_Method
     *            the <code>Method</code> instance corresponding to the
     *            interface method invoked on the proxy instance. The declaring
     *            class of the <code>Method</code> object will be the interface
     *            that the method was declared in, which may be a superinterface
     *            of the proxy interface that the proxy class inherits the
     *            method through.
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_Param
     *            Value to be stored
     */
    private void invokeSetter(Method p_Method, LdapAttribute p_LdapAttribute,
	    Object p_Param) {
	if (p_Method.getName().startsWith("set")) {
	    // It must be a setter
	    invokeSimpleSetter(p_LdapAttribute, p_Param);
	} else if (p_Method.getName().startsWith("add")) {
	    // It must be a adder
	    invokeSimpleAdder(p_LdapAttribute, p_Param);
	} else {
	    throw new UnsupportedOperationException();
	}
    }

    /**
     * Method that will be invoked each time a setter is called
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_Param
     *            Value to be stored
     */
    private void invokeSimpleSetter(LdapAttribute p_LdapAttribute,
	    Object p_Param) {
	Attributes attributes = m_LdapObject.getAttributes();
	Attribute attribute = attributes.get(p_LdapAttribute.value());
	if (p_Param == null) {
	    attributes.remove(p_LdapAttribute.value());
	} else {
	    if (attribute != null) {
		attribute.clear();
	    }
	    invokeSimpleAdder(p_LdapAttribute, p_Param);
	}
    }

    /**
     * Method that will be invoked each time an adder or a setter is called
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_Param
     *            Value to be added
     */
    private void invokeSimpleAdder(LdapAttribute p_LdapAttribute, Object p_Param) {
	Class<?> paramType;
	if (p_Param != null) {
	    paramType = p_Param.getClass();
	    if (isBoolean(paramType)) {
		invokeBooleanAdder(p_LdapAttribute, (Boolean) p_Param);
	    } else if (isNumber(paramType)) {
		invokeNumberAdder(p_LdapAttribute, (Number) p_Param);
	    } else {
		invokeDefaultAdder(p_LdapAttribute, p_Param);
	    }
	} else {
	    throw new IllegalArgumentException("The parameter cannot be null");
	}
    }

    /**
     * Method that will be invoked each time a setter or an adder is called for
     * boolean object
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_Param
     *            Value to be added
     */
    private void invokeNumberAdder(LdapAttribute p_LdapAttribute, Number p_Param) {
	invokeDefaultAdder(p_LdapAttribute, String.valueOf(p_Param));
    }

    /**
     * Method that will be invoked each time a setter or an adder is called for
     * boolean object
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_Param
     *            Value to be added
     */
    private void invokeBooleanAdder(LdapAttribute p_LdapAttribute,
	    Boolean p_Param) {
	if (Boolean.TRUE.equals(p_Param)) {
	    invokeDefaultAdder(p_LdapAttribute, p_LdapAttribute.trueValue()[0]);
	} else if (Boolean.FALSE.equals(p_Param)) {
	    invokeDefaultAdder(p_LdapAttribute, p_LdapAttribute.falseValue()[0]);
	}
    }

    /**
     * Method that will be invoked each time a setter or an adder is called for
     * default object
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_Param
     *            Value to be added
     */
    private void invokeDefaultAdder(LdapAttribute p_LdapAttribute,
	    Object p_Param) {
	Attributes attributes = m_LdapObject.getAttributes();
	Attribute attribute = attributes.get(p_LdapAttribute.value());
	if (attribute == null) {
	    attribute = new BasicAttribute(p_LdapAttribute.value());
	    attributes.put(attribute);
	}
	if (p_Param.getClass().isArray()) {
	    p_Param = Arrays.asList((Object[]) p_Param);
	}
	if (p_Param instanceof Collection<?>) {
	    for (Object object : (Collection<?>) p_Param) {
		attribute.add(object);
	    }
	} else {
	    attribute.add(p_Param);
	}
    }

    /**
     * Method that will be invoked each time a getter is called.
     * 
     * @param p_Method
     *            the <code>Method</code> instance corresponding to the
     *            interface method invoked on the proxy instance. The declaring
     *            class of the <code>Method</code> object will be the interface
     *            that the method was declared in, which may be a superinterface
     *            of the proxy interface that the proxy class inherits the
     *            method through.
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_ReturnType
     *            Type of the result.
     * @return the result of the getter
     * @throws NamingException
     *             If an error occurs
     */
    private Object invokeGetter(Method p_Method, LdapAttribute p_LdapAttribute,
	    Class<?> p_ReturnType) throws NamingException {
	Object result;
	if (p_Method.getName().startsWith("get")) {
	    // It must be a getter
	    result = invokeSimpleGetter(p_LdapAttribute, p_ReturnType);
	} else if ((p_Method.getName().startsWith("is"))
		&& (isBoolean(p_ReturnType))) {
	    // It must be a getter
	    result = invokeBooleanGetter(p_LdapAttribute, p_ReturnType);
	} else {
	    throw new UnsupportedOperationException();
	}
	return result;
    }

    /**
     * Method that will be invoked each time a getter is called.
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_ReturnType
     *            Type of the result.
     * @return the result of the getter
     * @throws NamingException
     *             If an error occurs
     */
    private Object invokeSimpleGetter(LdapAttribute p_LdapAttribute,
	    Class<?> p_ReturnType) throws NamingException {
	Object result;
	if (isBoolean(p_ReturnType)) {
	    result = invokeBooleanGetter(p_LdapAttribute, p_ReturnType);
	} else if (isNumber(p_ReturnType)) {
	    result = invokeNumberGetter(p_LdapAttribute, p_ReturnType);
	} else {
	    result = invokeDefaultGetter(p_LdapAttribute, p_ReturnType);
	}
	return result;
    }

    /**
     * Method that will be invoked each time a getter is called.
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_ReturnType
     *            Type of the result.
     * @return the result of the getter
     * @throws NamingException
     *             If an error occurs
     */
    private Number invokeNumberGetter(LdapAttribute p_LdapAttribute,
	    Class<?> p_ReturnType) throws NamingException {
	Number result;
	Object object = invokeDefaultGetter(p_LdapAttribute, p_ReturnType);
	if (object != null) {
	    if (isByte(p_ReturnType)) {
		result = Byte.valueOf(object.toString());
	    } else if (isShort(p_ReturnType)) {
		result = Short.valueOf(object.toString());
	    } else if (isInteger(p_ReturnType)) {
		result = Integer.valueOf(object.toString());
	    } else if (isLong(p_ReturnType)) {
		result = Long.valueOf(object.toString());
	    } else if (isFloat(p_ReturnType)) {
		result = Float.valueOf(object.toString());
	    } else if (isDouble(p_ReturnType)) {
		result = Double.valueOf(object.toString());
	    } else {
		throw new IllegalArgumentException("Cannot convert " + object
			+ " to " + p_ReturnType);
	    }
	} else {
	    result = null;
	}
	return result;
    }

    /**
     * Method that will be invoked each time a getter is called.
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_ReturnType
     *            Type of the result.
     * @return the result of the getter
     * @throws NamingException
     *             If an error occurs
     */
    private Boolean invokeBooleanGetter(LdapAttribute p_LdapAttribute,
	    Class<?> p_ReturnType) throws NamingException {
	Boolean result = null;
	Object object = invokeDefaultGetter(p_LdapAttribute, p_ReturnType);
	if (object != null) {
	    for (String value : p_LdapAttribute.trueValue()) {
		if (value.equalsIgnoreCase(object.toString())) {
		    result = Boolean.TRUE;
		}
	    }
	    if (result == null) {
		for (String value : p_LdapAttribute.falseValue()) {
		    if (value.equalsIgnoreCase(object.toString())) {
			result = Boolean.FALSE;
		    }
		}
	    }
	    if (result == null) {
		// value cannot be transformed into boolean
		throw new IllegalArgumentException(object
			+ " cannot be converted into boolean");
	    }
	}
	return result;
    }

    /**
     * Method that will be invoked each time a getter is called.
     * 
     * @param p_LdapAttribute
     *            Ldap attribute of the bean
     * @param p_ReturnType
     *            Type of the result.
     * @return the result of the getter
     * @throws NamingException
     *             If an error occurs
     */
    private Object invokeDefaultGetter(LdapAttribute p_LdapAttribute,
	    Class<?> p_ReturnType) throws NamingException {
	Attributes attributes = m_LdapObject.getAttributes();
	Object result = null;
	Attribute attribute;
	attribute = attributes.get(p_LdapAttribute.value());
	if (attribute != null) {
	    if (true == Iterable.class.isAssignableFrom(p_ReturnType)) {
		result = getCollection(p_ReturnType, attribute);
	    } else if (true == p_ReturnType.isArray()) {
		result = getArray(p_ReturnType, attribute);
	    } else {
		result = attribute.get();
	    }
	}
	return result;
    }

    /**
     * Convert Attribute to array
     * 
     * @param p_ReturnType
     *            Type of the result
     * @param p_Attribute
     *            The attribute to convert
     * @return new array corresponding to the attribute
     * @throws NamingException
     *             If an error occurs
     */
    private Object[] getArray(Class<?> p_ReturnType, Attribute p_Attribute)
	    throws NamingException {
	Object[] result = null;
	try {
	    result = (Object[]) Array.newInstance(
		    p_ReturnType.getComponentType(), p_Attribute.size());
	} catch (NegativeArraySizeException e) {
	    // Can not occur
	}
	NamingEnumeration<?> enumeration = p_Attribute.getAll();
	int i = 0;
	while (enumeration.hasMoreElements()) {
	    result[i++] = enumeration.nextElement();
	}
	enumeration.close();
	return result;
    }

    /**
     * Convert Attribute to Collection
     * 
     * @param p_ReturnType
     *            Type of the result
     * @param p_Attribute
     *            The attribute to convert
     * @return new Collection corresponding to the attribute
     * @throws NamingException
     *             If an error occurs
     */
    private Collection<?> getCollection(Class<?> p_ReturnType,
	    Attribute p_Attribute) throws NamingException {
	Collection<Object> result;
	NamingEnumeration<?> enumeration = p_Attribute.getAll();
	if (!p_ReturnType.isInterface()) {
	    try {
		result = (Collection<Object>) p_ReturnType.newInstance();
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	} else if (List.class.isAssignableFrom(p_ReturnType)) {
	    result = new ArrayList<Object>(p_Attribute.size());
	} else if (Queue.class.isAssignableFrom(p_ReturnType)) {
	    result = new ArrayBlockingQueue<Object>(p_Attribute.size());
	} else if (Set.class.isAssignableFrom(p_ReturnType)) {
	    result = new HashSet<Object>(p_Attribute.size());
	} else {
	    result = new ArrayList<Object>(p_Attribute.size());
	}
	result = new ArrayList<Object>(p_Attribute.size());
	while (enumeration.hasMoreElements()) {
	    result.add(enumeration.nextElement());
	}
	enumeration.close();
	return result;
    }

    private static boolean isBoolean(Class<?> p_Class) {
	return (Boolean.class.equals(p_Class) || boolean.class.equals(p_Class));
    }

    private static boolean isByte(Class<?> p_Class) {
	return (Byte.class.equals(p_Class) || byte.class.equals(p_Class));
    }

    private static boolean isChar(Class<?> p_Class) {
	return (Character.class.equals(p_Class) || char.class.equals(p_Class));
    }

    private static boolean isShort(Class<?> p_Class) {
	return (Short.class.equals(p_Class) || short.class.equals(p_Class));
    }

    private static boolean isInteger(Class<?> p_Class) {
	return (Integer.class.equals(p_Class) || int.class.equals(p_Class));
    }

    private static boolean isLong(Class<?> p_Class) {
	return (Long.class.equals(p_Class) || long.class.equals(p_Class));
    }

    private static boolean isFloat(Class<?> p_Class) {
	return (Float.class.equals(p_Class) || float.class.equals(p_Class));
    }

    private static boolean isDouble(Class<?> p_Class) {
	return (Double.class.equals(p_Class) || double.class.equals(p_Class));
    }

    private static boolean isNumber(Class<?> p_Class) {
	return Number.class.isAssignableFrom(p_Class)
		|| byte.class.equals(p_Class) || int.class.equals(p_Class)
		|| long.class.equals(p_Class) || float.class.equals(p_Class)
		|| double.class.equals(p_Class);
    }
}
