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

import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;

@ObjectClass("organizationalUnit")
public interface OrganizationalUnit {

    /**
     * Return the organizational unit of the bean
     * 
     * @return The organizational unit of the bean
     */
    @LdapAttribute("ou")
    String getOu();

    /**
     * Set the organizational unit of the bean
     * 
     * @param p_Ou
     *            The organizational unit of the bean
     */
    @LdapAttribute("ou")
    void setOu(String p_Ou);

    /**
     * Return the description of the bean
     * 
     * @return The description of the bean
     */
    @LdapAttribute("description")
    String getDescription();

    /**
     * Set the description of the bean
     * 
     * @param p_Description
     *            The description of the bean
     */
    @LdapAttribute("description")
    void setDescription(String p_Description);

}
