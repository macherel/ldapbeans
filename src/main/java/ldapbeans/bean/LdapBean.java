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

import ldapbeans.annotation.ObjectClass;

@ObjectClass({ "top" })
public interface LdapBean {
    /**
     * Return the distinguish name of the bean
     * 
     * @return The distinguish name of the bean
     */
    String getDN();

    /**
     * Save bean in Ldap directory
     * 
     * @throws NamingException
     *             If an error occurs
     */
    void store() throws NamingException;

    /**
     * Restore bean from Ldap directory
     * 
     * @throws NamingException
     *             If an error occurs
     */
    void restore() throws NamingException;

    /**
     * Move bean to new dn
     * 
     * @param p_NewDn
     *            The target dn
     * @throws NamingException
     *             If an error occurs
     */
    void move(String p_NewDn) throws NamingException;

    /**
     * Remove bean from Ldap directory
     * 
     * @throws NamingException
     *             If an error occurs
     */
    void remove() throws NamingException;
}
