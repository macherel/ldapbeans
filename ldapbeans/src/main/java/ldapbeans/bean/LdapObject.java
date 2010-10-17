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

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;

import ldapbeans.util.pool.LdapContextPool;

public class LdapObject {
    private boolean m_IsNew;
    private final LdapContextPool m_Pool;
    private Attributes m_Attributes;
    private String m_Dn;

    /**
     * Constructor
     * 
     * @param p_Pool
     *            Pool of LdapConntext
     * @param p_Dn
     *            Dn of the LdapObject
     * @param p_Attributes
     *            Attribute of the LdapObject. If <code>null</code>, LdapObject
     *            will be created with new {@link BasicAttributes}
     */
    public LdapObject(LdapContextPool p_Pool, String p_Dn,
	    Attributes p_Attributes) {
	m_IsNew = false;
	m_Pool = p_Pool;
	m_Dn = p_Dn;
	if (p_Attributes == null) {
	    m_Attributes = new BasicAttributes();
	    m_IsNew = true;
	} else {
	    m_Attributes = p_Attributes;
	    m_IsNew = false;
	}
    }

    /**
     * Return the DN of the LdapObject
     * 
     * @return The DN of the LdapObject
     */
    public String getDn() {
	return m_Dn;
    }

    /**
     * Return the attributes of the LdapObject
     * 
     * @return The Attributes of the LdapObject
     */
    public Attributes getAttributes() {
	return m_Attributes;
    }

    /**
     * Modify Attributes of the LdapObject
     * 
     * @param p_Attributes
     *            The new attributes
     */
    public void setAttributes(Attributes p_Attributes) {
	m_Attributes = p_Attributes;
    }

    /**
     * Return <code>true</code> if LdapObject does not exist in the directory,
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if LdapObject does not exist in the directory,
     *         <code>false</code> otherwise.
     */
    public boolean isNew() {
	return m_IsNew;
    }

    /**
     * Save LdapObject in the directory
     * 
     * @throws NamingException
     *             If an error occurs
     */
    public void store() throws NamingException {
	LdapContext context = null;
	try {
	    context = m_Pool.acquire();
	    if (m_IsNew) {
		context.createSubcontext(getDn(), getAttributes());
		m_IsNew = false;
	    } else {
		context.modifyAttributes(getDn(),
			LdapContext.REPLACE_ATTRIBUTE, getAttributes());
	    }
	} finally {
	    m_Pool.release(context);
	}
    }

    /**
     * Restore LdapObject from directory
     * 
     * @throws NamingException
     *             If an error occurs
     */
    public void restore() throws NamingException {
	LdapContext context = null;
	try {
	    context = m_Pool.acquire();
	    m_Attributes = context.getAttributes(m_Dn);
	    m_IsNew = false;
	} finally {
	    m_Pool.release(context);
	}
    }

    /**
     * Move the LdapObject to new DN
     * 
     * @param p_Dn
     *            The new DN of the LdapObject
     * @throws NamingException
     *             If an error occurs
     */
    public void move(String p_Dn) throws NamingException {
	LdapContext context = null;
	try {
	    context = m_Pool.acquire();
	    context.rename(getDn(), p_Dn);
	    m_Dn = p_Dn;
	    // Some attributes may have change when moving.
	    // We have to restore attributes
	    restore();
	} finally {
	    m_Pool.release(context);
	}
    }

    /**
     * Remove LdapObject from directory. LdapObject will be mark as new after
     * this operation.
     * 
     * @throws NamingException
     *             If an error occurs
     */
    public void remove() throws NamingException {
	LdapContext context = null;
	try {
	    context = m_Pool.acquire();
	    context.destroySubcontext(getDn());
	    m_IsNew = true;
	} finally {
	    m_Pool.release(context);
	}
    }
}
