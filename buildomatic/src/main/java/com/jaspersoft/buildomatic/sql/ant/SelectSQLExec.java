/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.buildomatic.sql.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Vladimir Tsukur
 */
public class SelectSQLExec extends AdvancedSQLExec {

    /** The name of the property that receives number of rows in result set. */
    private String selectRowCountProperty;

    /** Holds <code>true</code> if selection was made already. */
    private boolean selected = false;

    /**
     * <p>Copy of <code>onError</code> field.</p>
     *
     * <p>This field is required because
     * code doesn't have access to original <code>onError</code> field declared private.
     * Field is initialized when overridden {@link #setSelectRowCountProperty} setter is called.</p>
     */
    private String onErrorCopy = "abort";

    /**
     * Default constructor.
     */
    public SelectSQLExec() {
        // No operations.
    }

    /**
     * Overrides the name of the property that receives number of rows in result set.
     *
     * @param selectRowCountProperty the name of the property that receives number of rows in result set.
     */
    public void setSelectRowCountProperty(String selectRowCountProperty) {
        this.selectRowCountProperty = selectRowCountProperty;
    }

    @Override
    public void setOnerror(OnError action) {
        super.setOnerror(action);
        this.onErrorCopy = action.getValue();
    }

    /**
     * Execute the SELECT SQL statement.
     *
     * @param sql the SQL statement to execute.
     * @param out the place to put output.
     *
     * @throws java.sql.SQLException on SQL problems.
     */
    protected void execSQL(String sql, PrintStream out) throws SQLException {
        // Check and ignore empty statements
        if ("".equals(sql.trim())) {
            return;
        }

        // Skip all statements after first selection.
        if (selected) {
            return;
        }

        ResultSet resultSet = null;
        try {
            setTotalSql(getTotalSql() + 1);

            log("SQL: " + sql, Project.MSG_VERBOSE);

            resultSet = getStatement().executeQuery(sql);
            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            log(rowCount + " rows returned", Project.MSG_VERBOSE);
            setProperty(selectRowCountProperty, String.valueOf(rowCount));

            setGoodSql(getGoodSql() + 1);

            selected = true;
        }
        catch (SQLException e) {
            log("Failed to execute: " + sql, Project.MSG_ERR);
            setErrorProperty();
            if (!onErrorCopy.equals("abort")) {
                log(e.toString(), Project.MSG_ERR);
            }
            if (!onErrorCopy.equals("continue")) {
                throw e;
            }
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    //ignore
                }
            }
        }
    }

    protected int getTotalSql() {
        return ((Integer) ReflectionUtils.getFieldValue(ReflectionUtils.findField(SQLExec.class, "totalSql"), this));
    }

    protected void setTotalSql(int totalSql) {
        ReflectionUtils.setFieldValue(ReflectionUtils.findField(SQLExec.class, "totalSql"), this, totalSql);
    }

    protected int getGoodSql() {
        return ((Integer) ReflectionUtils.getFieldValue(ReflectionUtils.findField(SQLExec.class, "goodSql"), this));
    }

    protected void setGoodSql(int goodSql) {
        ReflectionUtils.setFieldValue(ReflectionUtils.findField(SQLExec.class, "goodSql"), this, goodSql);
    }

}
