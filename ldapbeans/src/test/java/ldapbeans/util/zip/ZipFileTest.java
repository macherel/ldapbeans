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
package ldapbeans.util.zip;

import java.io.File;
import java.net.URLDecoder;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Test;

public class ZipFileTest {

    private final static String[] TEST_ZIP_FILE_ENTRIES = {
	    "test_zip_file.zip", "test_zip_file.zip!tmp",
	    "test_zip_file.zip!tmp/zipEntry.txt" };

    /**
     * Test the {@link ZipFileProxy}
     * 
     * @throws Exception
     *             If an error occurs
     */
    @Test
    public void testZipFile() throws Exception {
	ClassLoader cld = Thread.currentThread().getContextClassLoader();
	String fileName = cld.getResource("test_zip_file.zip").getFile();
	fileName = URLDecoder.decode(fileName, "UTF-8");
	File zipFile = new ZipFileProxy(new ZipFile(fileName));

	checkZipFile(0, zipFile);
    }

    /**
     * Check a ZipFileProxy and his entries
     * 
     * @param p_Index
     *            Index of the entry in the ZipFile
     * @param p_File
     *            The file to check
     * @return The index of the next File
     * @throws Exception
     *             If an error occurs
     */
    private int checkZipFile(int p_Index, File p_File) throws Exception {
	int index = p_Index;
	if (!p_File.getCanonicalPath().endsWith(TEST_ZIP_FILE_ENTRIES[index])) {
	    Assert.fail("Entry " + TEST_ZIP_FILE_ENTRIES[index]
		    + " is expected.");
	}
	if (p_File.isDirectory()) {
	    File[] children = p_File.listFiles();
	    for (File child : children) {
		index = checkZipFile(++index, child);
	    }
	}
	return index;
    }
}
