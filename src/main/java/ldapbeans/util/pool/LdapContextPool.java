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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import ldapbeans.util.pool.exception.NotValidObjectException;

public class LdapContextPool extends AbstractPool<LdapContext> {

    private final String m_Url;
    private final String m_Principal;
    private final String m_Password;
    private String m_TestObject = null;

    /**
     * Construct a {@link Pool} of {@link LdapContext}
     * 
     * @param p_Size
     *            Size of the pool
     * @param p_Url
     *            Url of the LDAP directory
     * @param p_Principal
     *            User name used to login to the directory, or <code>null</code>
     *            if no authentication
     * @param p_Password
     *            Password used to login to the directory, or <code>null</code>
     *            if no authentication
     */
    public LdapContextPool(int p_Size, String p_Url, String p_Principal,
	    String p_Password) {
	super(p_Size);
	m_Url = p_Url;
	m_Principal = p_Principal;
	m_Password = p_Password;
	initialize();
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPool#create()
     */
    @Override
    protected LdapContext create() {
	LdapContext context = null;
	Hashtable<String, String> environment = new Hashtable<String, String>();
	try {
	    environment.put(DirContext.INITIAL_CONTEXT_FACTORY,
		    "com.sun.jndi.ldap.LdapCtxFactory");
	    environment.put(DirContext.PROVIDER_URL, m_Url);
	    environment.put("com.sun.jndi.ldap.connect.pool", "false");
	    environment.put(Context.REFERRAL, "follow");
	    if ((m_Principal != null) && (m_Password != null)) {
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, m_Principal);
		environment.put(Context.SECURITY_CREDENTIALS, m_Password);
	    }

	    context = new InitialLdapContext(environment, null);
	} catch (NamingException e) {
	    e.printStackTrace();
	}
	return context;
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPool#initialize(Object)
     */
    @Override
    protected void initialize(LdapContext p_Object) {

    }

    @Override
    public void validate(LdapContext p_LdapContext)
	    throws NotValidObjectException {
	try {
	    if ((m_TestObject != null)
		    && ((null == p_LdapContext) || (null == p_LdapContext
			    .getAttributes(m_TestObject)))) {
		throw new NotValidObjectException();
	    }
	} catch (Throwable t) {
	    throw new NotValidObjectException();
	}
    }

    /**
     * Change the LDAP object to test when the context will be validate
     * 
     * @param p_Dn
     *            DN of the object to test
     */
    public void setTestObject(String p_Dn) {
	m_TestObject = p_Dn;
    }

}
