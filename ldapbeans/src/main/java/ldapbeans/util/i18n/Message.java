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
package ldapbeans.util.i18n;

public class Message {

    private final String m_MessageProperty;
    private final Object[] m_Parameters;

    /**
     * Create a new message
     * 
     * @param p_MessageProperty
     *            Property of the message
     * @param p_Parameters
     *            Parameters of the message
     */
    public Message(String p_MessageProperty, Object... p_Parameters) {
	m_MessageProperty = p_MessageProperty;
	m_Parameters = p_Parameters;
    }

    /**
     * Return the entry name of the message in the properties file
     * 
     * @return The entry name of the message in the properties file
     */
    public String getMessageProperty() {
	return m_MessageProperty;
    }

    /**
     * Return the parameters of the message
     * 
     * @return The parameters of the message
     */
    public Object[] getParameters() {
	return m_Parameters;
    }

}
