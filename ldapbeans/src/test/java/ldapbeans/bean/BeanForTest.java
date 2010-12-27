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

@ObjectClass({ "organizationalUnit" })
public interface BeanForTest extends LdapBean {

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Description
     *            the description
     */
    @LdapAttribute(value = "description")
    void setDescription(String p_Description);

    /**
     * get the descriptions of the bean
     * 
     * @return the description for test
     */
    @LdapAttribute(value = "description")
    String getDescription();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Char
     *            a char for test
     */
    @LdapAttribute(value = "description")
    void setChar(char p_Char);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    char getChar();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Char
     *            a char for test
     */
    @LdapAttribute(value = "description")
    void setCharacter(Character p_Char);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    Character getCharacter();
}
