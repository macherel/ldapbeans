/*
 * Thissuesrt of ldapbeans
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
package issues.issue2;

import java.net.ConnectException;

import javax.naming.CommunicationException;

import ldapbeans.bean.LdapBeanManager;
import ldapbeans.util.pool.exception.PooledObjectCreationExeption;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", port = 2002) }, allowAnonymousAccess = true)
@ApplyLdifFiles({ "ldapbeans.ldif" })
public class Issue2Test {
    public static DirectoryService service;
    public static boolean isRunInSuite;
    public static LdapServer ldapServer;

    @Before
    public void init() {
	ldapServer.stop();
    }

    @After
    public void shutdown() throws Exception {
	ldapServer.start();
    }

    /**
     * Test
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testIssue() throws Exception {
	try {
	    LdapBeanManager.getInstance(
		    "ldap://localhost:" + ldapServer.getPort(), "ou=system")
		    .findByDn("ou=system");
	    Assert.fail("ldap server should be stopped");
	} catch (PooledObjectCreationExeption e) {
	    // can't connect to the ldap server
	    CommunicationException comEx = (CommunicationException) e
		    .getCause();
	    ConnectException conEx = (ConnectException) comEx.getCause();
	    Assert.assertEquals(conEx.getMessage(), "Connection refused");
	}
    }
}
