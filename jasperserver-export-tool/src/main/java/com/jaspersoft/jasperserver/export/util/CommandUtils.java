/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.export.util;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;

import com.jaspersoft.jasperserver.export.BaseExportImportCommand;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.ParametersImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CommandUtils {

	public static final String ARG_PREFIX = "--";
	public static final int ARG_PREFIX_LEGTH = ARG_PREFIX.length();
	
	public static final char ARG_VALUE_SEPARATOR = '=';
	public static final String VALUES_SEPARATORS = System.getProperty("path.separator") + ",";
	
	public static final String SPRING_CONFIG_ARG = "--configResources";
	public static final String SPRING_CONFIG_FILE = "ApplicationContext";

	protected static final Log log = LogFactory.getLog(CommandUtils.class);
	
		
	public static Parameters parse(String[] inArgs) {
		ParametersImpl params = new ParametersImpl();
		
		String[] args = preParse(inArgs);
		
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith(ARG_PREFIX) && arg.length() > ARG_PREFIX_LEGTH) {
				String param;
				String value;
				int sepIdx = arg.indexOf(ARG_VALUE_SEPARATOR);

				if (sepIdx >= 0) {
					param = arg.substring(ARG_PREFIX_LEGTH, sepIdx);
					value = arg.substring(sepIdx + 1);
				} else {
					param = arg.substring(ARG_PREFIX_LEGTH);
					value = null;
				}
				
				params.addParameter(param);
				if (value != null) {
					addParameterValues(params, param, value);
				}
				
				int j;
				for (j = i + 1; j < args.length; ++j) {
					String argn = args[j];
					if (argn.startsWith(ARG_PREFIX)) {
						break;
					}
					addParameterValues(params, param, argn);
				}
				i = j - 1;
			}
		}
		return params;
	}

	/* 
	 * Extra processing to support ant
	 */
	protected static String[] preParse(String[] args) {
		
		return breakParms(checkSpringConfigs(args));
	}
	
	/*
	 * To support executing import-export from ant, the --configResources
	 * option will support: 
	 * 		explicitly specify spring xml files
	 * 		specify a directory that contains the spring files
	 * 
	 * In the latter case we need to go and get the spring config files in the
	 * directory.
	 */	
	protected static String[] checkSpringConfigs(String[] args) {
		
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < args.length; i++) {

			if (args[i].indexOf(SPRING_CONFIG_ARG) >= 0) {
			
				// we have the spring resource option, does the option point 
				// to a dir or the actual files
				
				if (args[i].indexOf(SPRING_CONFIG_FILE) < 0) {
				
					list.add(getSpringFiles(args[i]));
					
				} else {
					list.add(args[i]);
				}
			} else {
				list.add(args[i]);
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	protected static String getSpringFiles(String arg) {
		// TODO: add this to support JS Pro
		return arg;
	}
	
	/* 
	 * To support executing import-export from ant, the input options  
	 * need to be further broken up in order to get handled properly.
	 * So args like this:
	 * 		"--output-dir=foo --uris=/images"	or
	 * 		"--output-dir foo --uris /images"
	 * are broken into this:
	 * 		"--output-dir=foo"  "--uris=/images"   		and
	 * 		"--output-dir"  "foo"  "--uris"  "/images"
	 */
	protected static String[] breakParms(String[] args) {

		ArrayList list = new ArrayList();

		for (int i = 0; i < args.length; i++) {

			int first = args[i].indexOf(ARG_PREFIX);
			int last = args[i].lastIndexOf(ARG_PREFIX);

			if (first < last) {
				// param string has more than one ARG_PREFIX so break it up
				for (StringTokenizer tok = new StringTokenizer(args[i]);tok.hasMoreTokens();) {
						String token = tok.nextToken();
						list.add((String) token);
				}
			} else if (first == last) {
				list.add((String) args[i]);
			} else {
				log.error("ERROR: an argument string should always have an ARG_PREFIX (--) arg=" + args[i]);
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	protected static void addParameterValues(ParametersImpl params, String param, String argn) {
		for (StringTokenizer tok = new StringTokenizer(argn, VALUES_SEPARATORS);
				tok.hasMoreTokens();) {
			String value = tok.nextToken();
			params.addParameterValue(param, value);
		}
	}
	
}
