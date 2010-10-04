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

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;

import ldapbeans.util.pool.LdapContextPool;

public final class LdapBeanManager {

    /** Map that contains instances per ldap url */
    private final static Map<String, LdapBeanManager> INSTANCES;

    /** Static constructor */
    static {
	INSTANCES = new TreeMap<String, LdapBeanManager>();
    }

    /**
     * Return an instance of {@link LdapBeanManager}.
     * 
     * @param p_Url
     *            Url Of Ldap directory
     * @param p_Root
     *            Root dn of the directory
     * @param p_Principal
     *            User name used to login to the directory, or <code>null</code>
     *            if no authentication
     * @param p_Password
     *            Password used to login to the directory, or <code>null</code>
     *            if no authentication
     * @return An instance of {@link LdapBeanManager}
     */
    public static LdapBeanManager getInstance(String p_Url, String p_Root,
	    String p_Principal, String p_Password) {
	LdapBeanManager instance = null;
	synchronized (INSTANCES) {
	    instance = INSTANCES.get(p_Url);
	    if (instance == null) {
		instance = new LdapBeanManager(p_Url, p_Root, p_Principal,
			p_Password);
		INSTANCES.put(p_Url, instance);
	    }
	}
	return instance;
    }

    /** Ldap helper */
    private final LdapObjectManager m_LdapObjectManager;
    /** Pool of {@link LdapContext} */
    private final LdapContextPool m_Pool;

    /**
     * Constructor
     * 
     * @param p_Url
     *            The URL of LDAP directory
     * @param p_Root
     *            Root dn of the directory
     * @param p_Principal
     *            Login for LDAP authentication
     * @param p_Password
     *            Password for LDAP authentication
     */
    private LdapBeanManager(String p_Url, String p_Root, String p_Principal,
	    String p_Password) {
	m_Pool = new LdapContextPool(10, p_Url, p_Principal, p_Password);
	m_LdapObjectManager = new LdapObjectManager(m_Pool, p_Root);
    }

    /**
     * Clear the Ldap cache
     */
    public void clearCache() {
	m_LdapObjectManager.clearCache();
    }

    /**
     * 
     * Save bean in Ldap directory
     * 
     * @param p_LdapBean
     *            The bean to save
     * @throws NamingException
     *             If an error occurs
     */
    public void store(LdapBean p_LdapBean) throws NamingException {
	p_LdapBean.store();
    }

    /**
     * Restore bean from Ldap directory
     * 
     * @param p_LdapBean
     *            The bean to restore
     * @throws NamingException
     *             If an error occurs
     */
    public void restore(LdapBean p_LdapBean) throws NamingException {
	p_LdapBean.restore();
    }

    /**
     * Move bean to new dn
     * 
     * @param p_LdapBean
     *            The bean to move
     * @param p_NewDn
     *            The target dn
     * @throws NamingException
     *             If an error occurs
     */
    public void move(LdapBean p_LdapBean, String p_NewDn)
	    throws NamingException {
	p_LdapBean.move(p_NewDn);
    }

    /**
     * Remove bean from Ldap directory
     * 
     * @param p_LdapBean
     *            The bean to remove
     * @throws NamingException
     *             If an error occurs
     */
    public void remove(LdapBean p_LdapBean) throws NamingException {
	p_LdapBean.remove();
    }

    /**
     * Create a new bean.
     * 
     * @param <T>
     *            Type of the bean
     * @param p_Class
     *            The interface that the bean have to implement
     * @param p_Dn
     *            The dn of the new bean
     * @return New bean instance
     */
    public <T extends LdapBean> T create(Class<T> p_Class, String p_Dn) {
	T bean;
	LdapObject ldapObject = m_LdapObjectManager.getLdapObjectByDn(p_Dn);
	LdapBeanHelper.getInstance().addObjectClass(p_Class,
		ldapObject.getAttributes());
	if (ldapObject.isNew()) {
	    try {
		// If object already exists, we have to restore it
		ldapObject.restore();
	    } catch (NamingException e) {
		// Object does not exist, we keep the new ldapobject
	    }
	}
	bean = createInstance(p_Class, ldapObject);
	return bean;
    }

    /**
     * Find a bean based on his dn or create it if it does not exist
     * 
     * @param p_Dn
     *            Dn of the bean to find
     * @return A bean corresponding to the dn, or create it if it does not exist
     */
    public Object getByDn(String p_Dn) {
	return getByDn(null, p_Dn);
    }

    /**
     * Find a bean based on his dn or create it if it does not exist
     * 
     * @param <T>
     *            The type of the bean
     * @param p_Class
     *            The interface that the bean have to implement
     * @param p_Dn
     *            Dn of the bean to find
     * @return A bean corresponding to the dn, or create it if it does not exist
     */
    public <T extends LdapBean> T getByDn(Class<T> p_Class, String p_Dn) {
	T bean = findByDn(p_Class, p_Dn);
	if (bean == null) {
	    bean = create(p_Class, p_Dn);
	}
	return bean;
    }

    /**
     * Find a bean based on his dn
     * 
     * @param p_Dn
     *            Dn of the bean to find
     * @return A bean corresponding to the dn, or <code>null</code> if not found
     */
    public Object findByDn(String p_Dn) {
	return findByDn(null, p_Dn);
    }

    /**
     * Find a bean based on his dn
     * 
     * @param <T>
     *            The type of the bean
     * @param p_Class
     *            The interface that the bean have to implement
     * @param p_Dn
     *            Dn of the bean to find
     * @return A bean corresponding to the dn, or <code>null</code> if not found
     */
    public <T extends LdapBean> T findByDn(Class<T> p_Class, String p_Dn) {
	LdapObject ldapObject;
	// ldapObject can't be null, but it's attributes can
	ldapObject = m_LdapObjectManager.getLdapObjectByDn(p_Dn);
	if (ldapObject.isNew()) {
	    try {
		// If ldapobject was not in cache, we have to check that it
		// exists in directory
		ldapObject.restore();
	    } catch (NamingException e) {
		// Object does not exist in directory, bean will not be created
		ldapObject = null;
	    }
	}
	// Else, ldap object was extract from cache, attributes was not updated
	return createInstance(p_Class, ldapObject);
    }

    /**
     * Find a bean based on his uid
     * 
     * @param p_Uid
     *            Uid of the bean to find
     * @return A bean corresponding to the uid, or <code>null</code> if not
     *         found
     */
    public Object findByUid(String p_Uid) {
	return findByUid(null, p_Uid);
    }

    /**
     * Find a bean based on his uid
     * 
     * @param <T>
     *            The type of the bean
     * @param p_Class
     *            The interface that the bean have to implement
     * @param p_Uid
     *            Uid of the bean to find
     * @return A bean corresponding to the uid, or <code>null</code> if not
     *         found
     */
    public <T extends LdapBean> T findByUid(Class<T> p_Class, String p_Uid) {
	LdapObject ldapObject;
	try {
	    // ldapObject can be null if uid was not found
	    ldapObject = m_LdapObjectManager.getLdapObjectByUid(p_Uid);
	} catch (NamingException e) {
	    ldapObject = null;
	}
	return createInstance(p_Class, ldapObject);
    }

    /**
     * Find a list of beans
     * 
     * @param p_LdapSearch
     *            The LDAP search
     * @return A list of beans corresponding to the LDAP search
     */
    public List<?> search(String p_LdapSearch) {
	return search(null, p_LdapSearch);
    }

    /**
     * Find a list of beans
     * 
     * @param <T>
     *            The type of the bean
     * @param p_Class
     *            The interface that the bean have to implement
     * @param p_LdapSearch
     *            The LDAP search
     * @return A list of beans corresponding to the LDAP search
     */
    public <T extends LdapBean> List<T> search(Class<T> p_Class,
	    String p_LdapSearch) {
	List<T> result = new ArrayList<T>();
	List<LdapObject> ldapObjects;
	try {
	    ldapObjects = m_LdapObjectManager.search(p_LdapSearch);
	    for (LdapObject ldapObject : ldapObjects) {
		result.add(createInstance(p_Class, ldapObject));
	    }
	} catch (NamingException e) {
	    // Nothing to do
	}
	return result;
    }

    /**
     * Create new instance of {@link LdapBean}
     * 
     * @param <T>
     *            Type of the bean
     * @param p_Class
     *            The interface that the bean have to implement, or
     *            <code>null</code> if interfaces have to be found from
     *            LdapObject
     * @param p_LdapObject
     *            LdapObject of the LdapBean
     * @return new {@link LdapBean} based on LdapObject
     */
    @SuppressWarnings("unchecked")
    private <T extends LdapBean> T createInstance(Class<T> p_Class,
	    LdapObject p_LdapObject) {
	T bean;
	Class<?>[] classes;
	Attributes attributes;
	LdapBeanHelper ldapBeanHelper;
	if ((p_LdapObject != null) && (p_LdapObject.getAttributes() != null)) {
	    if (p_Class != null) {
		// create interfaces array that LdapBean will implements
		classes = new Class[] { p_Class, LdapBean.class };
	    } else {
		// p_Class is null, we have to find interfaces that LdapBean
		// will implements
		ldapBeanHelper = LdapBeanHelper.getInstance();
		attributes = p_LdapObject.getAttributes();
		classes = ldapBeanHelper.getClasses(attributes);
	    }
	    // Create a dynamic implementation (a proxy) of the bean
	    bean = (T) Proxy.newProxyInstance(LdapBeanInvocationHandler.class
		    .getClassLoader(), classes, new LdapBeanInvocationHandler(
		    p_LdapObject, m_LdapObjectManager));
	} else {
	    bean = null;
	}
	return bean;
    }

}
