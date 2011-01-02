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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    /** Default constructor */
    private StringUtil() {
	// Nothing to do
    }

    /**
     * Replace $i (i start at 0) in string with corresponding parameter
     * 
     * @param p_UnformatedString
     *            The String to format
     * @param p_Parameters
     *            replacement parameters
     * @return The formated String
     */
    public static String format(String p_UnformatedString,
	    Object... p_Parameters) {
	String result = p_UnformatedString;

	if (p_Parameters != null) {

	    for (int i = 0; i < p_Parameters.length; i++) {
		result = result.replace("$" + i,
			String.valueOf(p_Parameters[i]));
	    }
	}

	return result;
    }

    /**
     * Return group of the regexp as an array
     * 
     * @param p_Source
     *            String to witch the regexp will applied
     * @param p_Regexp
     *            Regexp that defines groups
     * @return groups of the regexp applied to the source string
     */
    public static String[] getRegexpGroup(String p_Source, String p_Regexp) {
	String[] result;
	Pattern pattern = Pattern.compile(p_Regexp);
	Matcher matcher = pattern.matcher(p_Source);
	if (matcher.find()) {
	    int groupCount = matcher.groupCount();
	    result = new String[groupCount];
	    for (int i = 0; i < groupCount; i++) {
		result[i] = matcher.group(i + 1);
	    }
	} else {
	    result = null;
	}
	return result;
    }
}
