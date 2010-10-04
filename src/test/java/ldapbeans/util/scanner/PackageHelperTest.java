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
package ldapbeans.util.scanner;

import java.lang.annotation.Annotation;
import java.util.Collection;

import ldapbeans.annotation.ObjectClass;

import org.junit.Assert;
import org.junit.Test;

public class PackageHelperTest {

    /**
     * Test the {@link PackageHelper}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testPackageHelper() throws Exception {
	Collection<Class<?>> classes = null;
	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test", false, null);
	Assert.assertEquals(3, classes.size());

	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test", true, null);
	Assert.assertEquals(7, classes.size());

	ClassFilter classFilter = new ClassFilter() {

	    public boolean accept(Class<?> p_Class) {
		boolean accept = false;
		Annotation annotation = p_Class
			.getAnnotation(ObjectClass.class);
		if (annotation != null) {
		    accept = true;
		}
		return accept;
	    }
	};

	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test", false, classFilter);
	Assert.assertEquals(1, classes.size());

	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test", true, classFilter);
	Assert.assertEquals(2, classes.size());

	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test.foo", false, classFilter);
	Assert.assertEquals(0, classes.size());

	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test.foo", true, classFilter);
	Assert.assertEquals(1, classes.size());

	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test.bar", false, classFilter);
	Assert.assertEquals(0, classes.size());

	classes = PackageHelper.getInstance().getClasses(
		"ldapbeans.util.scanner.test.bar", true, classFilter);
	Assert.assertEquals(0, classes.size());

	classes = PackageHelper.getInstance().getClasses("org.junit", true,
		classFilter);
    }
}
