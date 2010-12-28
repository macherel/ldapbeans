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
package ldapbeans.config;

import java.lang.reflect.Proxy;

public interface LdapbeansConfigurationMBean {

    /**
     * Return the path where the generated class will be stored or
     * <code>null</code> if generated class will not be stored on file system
     * 
     * @return The path where the generated class will be stored or
     *         <code>null</code> if generated class will not be stored on file
     *         system
     */
    String getGeneratedClassPath();

    /**
     * Set the path where generated class will be written
     * 
     * @param p_GeneratedClassPath
     *            The path where generated class will be written
     */
    void setGeneratedClassPath(String p_GeneratedClassPath);

    /**
     * Return the name of the cache class implementation
     * 
     * @return The name of the cache class implementation
     */
    String getCacheImplementationClassName();

    /**
     * Return <code>true</code> if ldapbeans have to use dynamic proxy to
     * implements beans, <code>false</code> if beans classes have to be
     * generated
     * 
     * @return <code>true</code> if ldapbeans have to use dynamic proxy to
     *         implements beans, <code>false</code> if beans classes have to be
     *         generated
     */
    boolean useProxyBean();

    /**
     * The the flag that indicate if beans will be created by using
     * {@link Proxy}
     * 
     * @param p_UseProxyBean
     *            <code>true</code> if Proxy will be used, <code>false</code>
     *            otherwise
     */
    void setUseProxyBean(boolean p_UseProxyBean);

}