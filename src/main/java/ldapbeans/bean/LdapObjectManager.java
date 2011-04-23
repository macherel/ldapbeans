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

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import ldapbeans.config.LdapbeansConfiguration;
import ldapbeans.config.LdapbeansMessageManager;
import ldapbeans.util.cache.Cache;
import ldapbeans.util.cache.SimpleCache;
import ldapbeans.util.i18n.Logger;
import ldapbeans.util.pool.LdapContextPool;

public class LdapObjectManager {

    /** The logger for this class */
    private final static Logger LOG = Logger.getLogger();

    /** the configuration */
    private final static LdapbeansConfiguration CONFIG = LdapbeansConfiguration
	    .getInstance();
    /** Message manager instance */
    private final static LdapbeansMessageManager MESSAGE = LdapbeansMessageManager
	    .getInstance();

    /** cache of LdapObject */
    private Cache<String, LdapObject> m_Cache;

    /** Pool of LdapContext */
    private final LdapContextPool m_Pool;

    /** Root DN */
    private final String m_Root;

    /** The logger */

    /**
     * Constructor
     * 
     * @param p_Pool
     *            Pool of LdapContext
     * @param p_Root
     *            Root dn
     */
    @SuppressWarnings("unchecked")
    public LdapObjectManager(LdapContextPool p_Pool, String p_Root) {
	String className = CONFIG.getCacheImplementationClassName();
	try {
	    Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(
		    className);
	    m_Cache = (Cache<String, LdapObject>) clazz.newInstance();
	    LOG.info(MESSAGE.getCacheImplementationMessage(className));
	} catch (Exception e) {
	    LOG.error(MESSAGE.getCacheImplementationErrorMessage(className));
	    m_Cache = new SimpleCache<String, LdapObject>();
	}
	m_Pool = p_Pool;
	m_Root = p_Root;
    }

    /**
     * Clear manager cache
     */
    public void clearCache() {
	synchronized (m_Cache) {
	    m_Cache.clear();
	}
    }

    /**
     * Move {@link LdapObject} to new DN in directory.
     * 
     * @param p_LdapObject
     *            {@link LdapObject} to move.
     * @param p_Dn
     *            The new DN of the LdapObject.
     * @throws NamingException
     *             If an error occurs.
     */
    public void moveLdapObject(LdapObject p_LdapObject, String p_Dn)
	    throws NamingException {
	String oldDn = p_LdapObject.getDn();
	p_LdapObject.move(p_Dn);
	synchronized (m_Cache) {
	    m_Cache.remove(oldDn);
	    m_Cache.put(p_Dn, p_LdapObject);
	}
    }

    /**
     * Remove {@link LdapObject} from directory.
     * 
     * @param p_LdapObject
     *            {@link LdapObject} to remove.
     * @throws NamingException
     *             If an error occurs
     */
    public void removeLdapObject(LdapObject p_LdapObject)
	    throws NamingException {
	p_LdapObject.remove();
    }

    /**
     * Store {@link LdapObject} in directory.
     * 
     * @param p_LdapObject
     *            {@link LdapObject} to store.
     * @throws NamingException
     *             If an error occurs
     * @see LdapObject#store()
     */
    public void storeLdapObject(LdapObject p_LdapObject) throws NamingException {
	p_LdapObject.store();
    }

    /**
     * Restore {@link LdapObject} from directory
     * 
     * @param p_LdapObject
     *            {@link LdapObject} to restore.
     * @throws NamingException
     *             If an error occurs
     * @see LdapObject#restore()
     */
    public void restoreLdapObject(LdapObject p_LdapObject)
	    throws NamingException {
	p_LdapObject.restore();
    }

    /**
     * Retrieve {@link LdapObject} from DN
     * 
     * @param p_Dn
     *            DN of the object to retrieve
     * @return {@link LdapObject}
     */
    public LdapObject getLdapObjectByDn(String p_Dn) {
	LdapObject result;
	result = getLdapObject(p_Dn, null);
	return result;
    }

    /**
     * Retrieve {@link LdapObject} from uid
     * 
     * @param p_Uid
     *            Uid of the object to retrieve
     * @return {@link LdapObject}
     * @throws NamingException
     *             If an error occurs
     */
    public LdapObject getLdapObjectByUid(String p_Uid) throws NamingException {
	LdapObject result = searchFirst("(uid=" + p_Uid + ")");
	return result;
    }

    /**
     * Search LDAP object
     * 
     * @param p_LdapSearch
     *            The LDAP search
     * @return List of {@link LdapObject} corresponding to the LDAP search
     * @throws NamingException
     *             If an error occurs
     */
    public List<LdapObject> search(String p_LdapSearch) throws NamingException {
	List<LdapObject> result = new ArrayList<LdapObject>();
	Attributes attributes = null;
	SearchControls searchControls = new SearchControls();
	NamingEnumeration<SearchResult> namingEnumeration;
	String dn = null;
	LdapContext context = m_Pool.acquire();
	try {
	    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    namingEnumeration = context.search(m_Root, p_LdapSearch,
		    searchControls);
	    while ((namingEnumeration != null)
		    && (namingEnumeration.hasMoreElements())) {
		SearchResult searchResult = namingEnumeration.nextElement();
		if (searchResult != null) {
		    dn = searchResult.getNameInNamespace();
		    attributes = searchResult.getAttributes();
		    result.add(getLdapObject(dn, attributes));
		}
	    }
	} finally {
	    m_Pool.release(context);
	}
	return result;
    }

    /**
     * Search the first occurrence of LDAP object
     * 
     * @param p_LdapSearch
     *            The LDAP search
     * @return The first occurrence of {@link LdapObject} corresponding to the
     *         LDAP search
     * @throws NamingException
     *             If an error occurs
     */
    public LdapObject searchFirst(String p_LdapSearch) throws NamingException {
	LdapObject result = null;
	Attributes attributes = null;
	SearchControls searchControls = new SearchControls();
	NamingEnumeration<SearchResult> namingEnumeration;
	String dn = null;
	LdapContext context = m_Pool.acquire();
	try {
	    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    namingEnumeration = context.search(m_Root, p_LdapSearch,
		    searchControls);
	    if ((namingEnumeration != null)
		    && (namingEnumeration.hasMoreElements())) {
		SearchResult searchResult = namingEnumeration.nextElement();
		if (searchResult != null) {
		    dn = searchResult.getNameInNamespace();
		    attributes = searchResult.getAttributes();
		    result = getLdapObject(dn, attributes);
		}
	    }
	} finally {
	    m_Pool.release(context);
	}
	return result;
    }

    /**
     * Return {@link LdapObject} from cache
     * 
     * @param p_Dn
     *            DN of the LdapObject to find in the cache
     * @param p_Attributes
     *            Attributes of the LdapObject. It is used to create
     *            {@link LdapObject} if it was not in cache or update the
     *            {@link LdapObject} otherwise. If <code>null</code> LdapObject
     *            will be create with new {@link Attributes} and will not be
     *            update.
     * @return {@link LdapObject} from cache.
     */
    private LdapObject getLdapObject(String p_Dn, Attributes p_Attributes) {
	LdapObject ldapObject;
	synchronized (m_Cache) {
	    ldapObject = m_Cache.get(p_Dn);
	    if (ldapObject == null) {
		ldapObject = new LdapObject(m_Pool, p_Dn, p_Attributes);
		m_Cache.put(p_Dn, ldapObject);
	    } else if (p_Attributes != null) {
		ldapObject.setAttributes(p_Attributes);
	    }
	}
	return ldapObject;
    }
}
