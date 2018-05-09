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
package com.jaspersoft.jasperserver.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MyDumbBean class
 *
 * @author Steve Rosen
 */
public class MyDumbBean {
    protected final static Log m_logger = LogFactory.getLog(MyDumbBean.class);
    private String m_name;

    /**
     * MyDumbBean
     */
    public MyDumbBean() {
        m_logger.info("MyDumbBean() is being created...");
    }

    /**
     * setBeanName
     *
     * sets the name of this MyDumbBean object
     */
    public void setBeanName(String name) {
        m_logger.info("setBeanName() name = " + name);
        m_name = name;
        return;
    }

    /**
     * getBeanName
     *
     * gets the name of this MyDumbBean object
     *
     * @return  the name
     */
    public String getBeanName() {
        return m_name;
    }

    /**
     * toString
     *
     * Used to print out the name of this MyDumbBean for debugging purposes
     *
     * @return  the String
     */
    public String toString() {
        return m_name;
    }
}
