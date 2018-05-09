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
package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: LocalesListImpl.java 5713 2006-11-21 11:44:34Z lucian $
 */
public class FileVirtualizerFactory extends AbstractIndividualVirtualizerFactory
{
	
	private static final Log log = LogFactory.getLog(FileVirtualizerFactory.class); 
	
	/**
	 * 
	 */
	private int maxSize = 100;
	private String directory = null;
	
	/**
	 * 
	 */
	public JRVirtualizer getVirtualizer()
	{
		if (directory == null)
		{
			return new JRFileVirtualizer(maxSize);
		}
		
		return new JRFileVirtualizer(maxSize, directory);
	}


	/**
	 * 
	 */
	public int getMaxSize() 
	{
		return maxSize;
	}

	
	/**
	 * 
	 */
	public void setMaxSize(int maxSize) 
	{
		this.maxSize = maxSize;
	}


	/**
	 * 
	 */
	public String getDirectory() 
	{
		return directory;
	}

	
	/**
	 * 
	 */
	public void setDirectory(String directory) 
	{
		this.directory = directory;
	}


}
