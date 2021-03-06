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

import java.util.Collection;
import java.util.List;

import ldapbeans.annotation.ConvertAttribute;
import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;

@ObjectClass({ "person", "inetOrgPerson" })
public interface Person extends LdapBean {

    /**
     * Return the uid of the bean
     * 
     * @return The uid of the bean
     */
    @LdapAttribute("uid")
    String getUid();

    /**
     * Set the uid of the bean
     * 
     * @param p_Uid
     *            The uid of the bean
     */
    @LdapAttribute("uid")
    void setUid(String p_Uid);

    /**
     * Return the common name of the bean
     * 
     * @return The common name of the bean
     */
    @LdapAttribute("cn")
    String getCommonName();

    /**
     * Set the common name of the bean
     * 
     * @param p_CommonName
     *            The Common name of the bean
     */
    @LdapAttribute("cn")
    void setCommonName(String p_CommonName);

    /**
     * Return the common name of the bean
     * 
     * @return The common name of the bean
     */
    @LdapAttribute("cn")
    Collection<String> getCommonNames();

    /**
     * Return the surname of the bean
     * 
     * @return The surname of the bean
     */
    @LdapAttribute("sn")
    String getSurname();

    /**
     * Set the surname of the bean
     * 
     * @param p_Surname
     *            The surname of the bean
     */
    @LdapAttribute("sn")
    void setSurname(String p_Surname);

    /**
     * Return the given name of the bean
     * 
     * @return The given name of the bean
     */
    @LdapAttribute("GivenName")
    String getGivenName();

    /**
     * Return the description of the bean
     * 
     * @return The description of the bean
     */
    @LdapAttribute("description")
    List<String> getDescriptions();

    /**
     * Return the descriptions of the bean
     * 
     * @return The descriptions of the bean
     */
    @LdapAttribute("description")
    String[] getDescriptionArray();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Descriptions
     *            The descriptions of the bean
     */
    @LdapAttribute("description")
    void setDescriptions(List<String> p_Descriptions);

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Descriptions
     *            The descriptions of the bean
     */
    @LdapAttribute("description")
    void setDescriptionArray(String... p_Descriptions);

    /**
     * Return an attribute that does not exist
     * 
     * @return an attribute that does not exist
     */
    @LdapAttribute("__DOES_NOT_EXIST_ERROR__")
    Object getErreur();

    /**
     * set another Person
     * 
     * @param p_Person
     *            Another Person
     */
    @LdapAttribute("description")
    void setOtherPerson(Person p_Person);

    /**
     * set another Person
     * 
     * @param p_Person
     *            Another Person
     */
    @LdapAttribute("description")
    void setOtherPersonUsingUid(
	    @ConvertAttribute(method = "getUid") Person p_Person);

    /**
     * set another Person
     * 
     * @param p_Int
     *            An integer for test
     * @param p_Long
     *            A long for test
     * @param p_Person
     *            Another Person
     */
    @LdapAttribute(value = "description", pattern = "$2-$0-$1")
    void setComplexDescription(int p_Int, long p_Long,
	    @ConvertAttribute(method = "getUid") Person p_Person);

    /**
     * Return another Person
     * 
     * @return Another Person
     */
    @LdapAttribute("description")
    Person getOtherPersonByDn();

    /**
     * Return another Person
     * 
     * @return Another Person
     */
    @LdapAttribute(value = "description", search = "(uid=$0)")
    Person getOtherPersonBySimpleSearch();

    /**
     * Return another Person
     * 
     * @return Another Person
     */
    @LdapAttribute(value = "description", search = "(uid=$0)", pattern = "^\\W+(\\w*)\\W+$")
    Person getOtherPersonByRegexpSearch();
}
