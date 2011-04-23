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
public interface BeanForNumberTest extends LdapBean {

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
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setByteP(int p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    byte getByteP();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setByte(Byte p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    Byte getByte();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setShortP(short p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    short getShortP();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setShort(Short p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    Short getShort();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setInt(int p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    int getInt();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setInteger(Integer p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    Integer getInteger();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setLongP(long p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    long getLongP();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setLong(Long p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    Long getLong();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setFloatP(float p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    float getFloatP();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setFloat(Float p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    Float getFloat();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setDoubleP(double p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    double getDoubleP();

    /**
     * Set the descriptions of the bean
     * 
     * @param p_Number
     *            a Number for test
     */
    @LdapAttribute(value = "description")
    void setDouble(Double p_Number);

    /**
     * get the descriptions of the bean
     * 
     * @return a Number for test
     */
    @LdapAttribute(value = "description")
    Double getDouble();
}
