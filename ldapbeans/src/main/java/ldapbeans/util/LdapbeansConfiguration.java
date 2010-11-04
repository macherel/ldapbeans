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
package ldapbeans.util;

public final class LdapbeansConfiguration {

    /** Name of the property that describe the class cache implementation */
    private final static String PROPERTY_CACHE_CLASS_IMPLEMENTATION = "ldapbeans.cache.impl";
    /**
     * Name of the property that describe the path where generated class will be
     * stored
     */
    private final static String PROPERTY_GENERATED_CLASS_PATH = "ldapbeans.generated.class.path";
    /**
     * Name of the property that have to be set if ldapbeans have to used
     * dynamic proxy
     */
    private final static String PROPERTY_USE_PROXY_BEAN = "ldapbeans.use.proxy.bean";

    /** Singleton instance of this class */
    private final static LdapbeansConfiguration INSTANCE = new LdapbeansConfiguration();

    /**
     * Return the singleton instance
     * 
     * @return The singleton instance
     */
    public static LdapbeansConfiguration getInstance() {
	return INSTANCE;
    }

    /**
     * Create a new configuration
     */
    private LdapbeansConfiguration() {
	// Nothing to do
    }

    /**
     * Return the path where the generated class will be stored or
     * <code>null</code> if generated class will not be stored on file system
     * 
     * @return The path where the generated class will be stored or
     *         <code>null</code> if generated class will not be stored on file
     *         system
     */
    public String getGeneratedClassPath() {
	return System.getProperty(PROPERTY_GENERATED_CLASS_PATH);
    }

    /**
     * Return the name of the cache class implementation
     * 
     * @return The name of the cache class implementation
     */
    public String getCacheImplementationClassName() {
	return System.getProperty(PROPERTY_CACHE_CLASS_IMPLEMENTATION);
    }

    /**
     * Return <code>true</code> if ldapbeans have to use dynamic proxy to
     * implements beans, <code>false</code> if beans classes have to be
     * generated
     * 
     * @return <code>true</code> if ldapbeans have to use dynamic proxy to
     *         implements beans, <code>false</code> if beans classes have to be
     *         generated
     */
    public boolean useProxyBean() {
	return System.getProperty(PROPERTY_USE_PROXY_BEAN) != null;
    }
}
