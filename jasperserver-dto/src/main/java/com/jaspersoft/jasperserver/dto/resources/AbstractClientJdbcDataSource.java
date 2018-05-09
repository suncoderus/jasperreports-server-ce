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
package com.jaspersoft.jasperserver.dto.resources;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class AbstractClientJdbcDataSource<JdbcDataSourceType extends  AbstractClientJdbcDataSource<JdbcDataSourceType>>
        extends ClientResource<JdbcDataSourceType> implements ClientReferenceableDataSource{
    private String driverClass;
    private String password;
    private String username;
    private String connectionUrl;
    private String timezone;

    public AbstractClientJdbcDataSource(){
    }

    public AbstractClientJdbcDataSource(AbstractClientJdbcDataSource<JdbcDataSourceType> source){
        super(source);
        driverClass = source.getDriverClass();
        password = source.getPassword();
        username = source.getUsername();
        connectionUrl = source.getConnectionUrl();
        timezone = source.getTimezone();
    }

    public String getDriverClass() {
        return driverClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbstractClientJdbcDataSource that = (AbstractClientJdbcDataSource) o;

        if (connectionUrl != null ? !connectionUrl.equals(that.connectionUrl) : that.connectionUrl != null)
            return false;
        if (driverClass != null ? !driverClass.equals(that.driverClass) : that.driverClass != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (driverClass != null ? driverClass.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (connectionUrl != null ? connectionUrl.hashCode() : 0);
        return result;
    }

    // unchecked cast to BuilderType safety assured by the rule of usage BuilderType generic parameter.
    @SuppressWarnings("unchecked")
    public JdbcDataSourceType setDriverClass(String driverClass) {
        this.driverClass = driverClass;
        return(JdbcDataSourceType) this;
    }

    public String getPassword() {
        return password;
    }

    // unchecked cast to BuilderType safety assured by the rule of usage BuilderType generic parameter.
    @SuppressWarnings("unchecked")
    public JdbcDataSourceType setPassword(String password) {
        this.password = password;
        return (JdbcDataSourceType) this;
    }

    public String getUsername() {
        return username;
    }

    // unchecked cast to BuilderType safety assured by the rule of usage BuilderType generic parameter.
    @SuppressWarnings("unchecked")
    public JdbcDataSourceType setUsername(String username) {
        this.username = username;
        return (JdbcDataSourceType) this;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    // unchecked cast to BuilderType safety assured by the rule of usage BuilderType generic parameter.
    @SuppressWarnings("unchecked")
    public JdbcDataSourceType setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
        return (JdbcDataSourceType) this;
    }

    public String getTimezone() {
        return timezone;
    }

    // unchecked cast to BuilderType safety assured by the rule of usage BuilderType generic parameter.
    @SuppressWarnings("unchecked")
    public JdbcDataSourceType setTimezone(String timezone) {
        this.timezone = timezone;
        return (JdbcDataSourceType) this;
    }

    @Override
    public String toString() {
        return "AbstractClientJdbcDataSource{" +
                "driverClass='" + driverClass + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", connectionUrl='" + connectionUrl + '\'' +
                ", timezone='" + timezone + '\'' +
                "} " + super.toString();
    }
}
