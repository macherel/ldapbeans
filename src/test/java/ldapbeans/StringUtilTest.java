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
package ldapbeans;

import java.util.Arrays;

import junit.framework.Assert;
import ldapbeans.util.StringUtil;

import org.junit.Test;

public class StringUtilTest {
    /**
     * Test StringUtil.format method
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testFormat() throws Exception {
	String str = StringUtil.format("$0$1$0$2", "0", "1", "2");
	Assert.assertEquals("0102", str);
    }

    /**
     * Test StringUtil.getRegexpGroup method
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testGetRegexpGroup() throws Exception {
	String[] groups = StringUtil.getRegexpGroup("***a|b|c***",
		"(\\w*)\\|(\\w*)\\|(\\w*)");
	System.out.println(Arrays.asList(groups));
	Assert.assertEquals(Arrays.asList(new String[] { "a", "b", "c" }),
		Arrays.asList(groups));
    }
}
