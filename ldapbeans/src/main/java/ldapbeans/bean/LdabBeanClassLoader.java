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

public class LdabBeanClassLoader extends ClassLoader {

    /**
     * Define a new class
     * 
     * @param p_ClassName
     *            The name of the new class
     * @param p_Datas
     *            The bytes that make up the class data.
     * @return The <tt>Class</tt> object that was created from the specified
     *         class data.
     */
    public Class<?> defineClass(String p_ClassName, byte[] p_Datas) {
	return defineClass(p_ClassName, p_Datas, 0, p_Datas.length);
    }
}
