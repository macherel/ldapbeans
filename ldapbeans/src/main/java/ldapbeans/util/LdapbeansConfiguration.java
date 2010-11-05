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

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public final class LdapbeansConfiguration implements
	LdapbeansConfigurationMBean {

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

    /** Static constructor */
    static {
	MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	try {
	    ObjectName name = new ObjectName("ldapbeans:type=Configuration");
	    mbs.registerMBean(INSTANCE, name);
	} catch (Exception e) {
	    e.printStackTrace();
	    // Nothing to do
	}
    }

    /** Path where generated class will be written */
    private String m_GeneratedClassPath;

    /** The name of the class the will be used for cache */
    private final String m_CacheImplementationClassName;

    /** Flag that indicate if Proxy will be used when creating beans */
    private boolean m_UseProxyBean;

    /**
     * Create a new configuration
     */
    private LdapbeansConfiguration() {
	m_GeneratedClassPath = System
		.getProperty(PROPERTY_GENERATED_CLASS_PATH);
	m_CacheImplementationClassName = System
		.getProperty(PROPERTY_CACHE_CLASS_IMPLEMENTATION);
	m_UseProxyBean = System.getProperty(PROPERTY_USE_PROXY_BEAN) != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ldapbeans.util.LdapbeansConfigurationMBean#getGeneratedClassPath()
     */
    public String getGeneratedClassPath() {
	return m_GeneratedClassPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ldapbeans.util.LdapbeansConfigurationMBean#setGeneratedClassPath(java
     * .lang.String)
     */
    public void setGeneratedClassPath(String p_GeneratedClassPath) {
	m_GeneratedClassPath = p_GeneratedClassPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ldapbeans.util.LdapbeansConfigurationMBean#getCacheImplementationClassName
     * ()
     */
    public String getCacheImplementationClassName() {
	return m_CacheImplementationClassName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ldapbeans.util.LdapbeansConfigurationMBean#useProxyBean()
     */
    public boolean useProxyBean() {
	return m_UseProxyBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ldapbeans.util.LdapbeansConfigurationMBean#setUseProxyBean(boolean)
     */
    public void setUseProxyBean(boolean p_UseProxyBean) {
	m_UseProxyBean = p_UseProxyBean;
    }
}
