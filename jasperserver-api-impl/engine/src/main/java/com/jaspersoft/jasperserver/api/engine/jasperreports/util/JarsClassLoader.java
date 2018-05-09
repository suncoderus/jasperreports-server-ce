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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JarsClassLoader extends ClassLoader {
	private static final Log log = LogFactory.getLog(JarsClassLoader.class);

	private final JarURLStreamHandler urlStreamHandler;

	private final JarFile[] jars;
	
	private final ProtectionDomain protectionDomain;

	public JarsClassLoader(JarFile[] jars, ClassLoader parent) {
		this(jars, parent, JarsClassLoader.class.getProtectionDomain());
	}

	public JarsClassLoader(JarFile[] jars, ClassLoader parent, ProtectionDomain protectionDomain) {
		super(parent);

		this.urlStreamHandler = new JarURLStreamHandler();
		this.jars = jars;
		this.protectionDomain = protectionDomain;
	}

	protected Class findClass(String name) throws ClassNotFoundException {
		String path = name.replace('.', '/').concat(".class");

		JarFileEntry entry = findPath(path);

		if (entry == null) {
			throw new ClassNotFoundException(name);
		}

		//TODO certificates, package

		byte[] classData;
		try {
			long size = entry.getSize();
			if (size >= 0) {
				classData = DataContainerStreamUtil.readData(entry.getInputStream(),
						(int) size);
			} else {
				classData = DataContainerStreamUtil.readData(entry.getInputStream());
			}
		} catch (IOException e) {
			log.debug(e, e);
			throw new ClassNotFoundException(name, e);
		}

		return defineClass(name, classData, 0, classData.length,
				protectionDomain);
	}

	protected JarFileEntry findPath(String path) {
		JarFileEntry entry = null;
		for (int i = 0; i < jars.length && entry == null; i++) {
			entry = getJarEntry(jars[i], path);
		}
		return entry;
	}

	protected URL findResource(String name) {
		JarFileEntry entry = findPath(name);
		return entry == null ? null : urlStreamHandler.createURL(entry);
	}

	protected Enumeration findResources(String name) throws IOException {
		Vector urls = new Vector();
		for (int i = 0; i < jars.length; i++) {
			JarFileEntry entry = getJarEntry(jars[i], name);
			if (entry != null) {
				urls.add(urlStreamHandler.createURL(entry));
			}
		}
		return urls.elements();
	}

	protected static JarFileEntry getJarEntry(JarFile jar, String name) {
		if (name.startsWith("/")) {
			name = name.substring(1);
		}

		JarFileEntry jarEntry = null;
		JarEntry entry = jar.getJarEntry(name);
		if (entry != null) {
			jarEntry = new JarFileEntry(jar, entry);
		}

		return jarEntry;
	}
}
