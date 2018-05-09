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

package com.jaspersoft.jasperserver.dto.job;


import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.connection.FtpConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
@XmlRootElement(name = "outputFTPInfo")
public class ClientJobFtpInfo implements DeepCloneable<ClientJobFtpInfo>{

    private String userName;
    private String password;
    private String folderPath;
    private String serverName;
    private FtpConnection.FtpType type;
    private String protocol;
    private Integer port;
    private Boolean implicit;
    private Long pbsz;
    private String prot;
    private Map<String, String> propertiesMap;
    private String sshKey;
    private String sshPassphrase;

    public ClientJobFtpInfo() {
    }

    public ClientJobFtpInfo(ClientJobFtpInfo other) {
        this.folderPath = other.folderPath;
        this.implicit = other.implicit;
        this.password = other.password;
        this.pbsz = other.pbsz;
        this.port = other.port;
        this.propertiesMap = (other.propertiesMap != null) ? new LinkedHashMap<String, String>(other.propertiesMap) : null;
        this.prot = other.prot;
        this.protocol = other.protocol;
        this.serverName = other.serverName;
        this.type = other.type;
        this.userName = other.userName;
        this.sshKey = other.sshKey;
        this.sshPassphrase = other.sshPassphrase;
    }

    public String getUserName() {
        return userName;
    }

    public ClientJobFtpInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Boolean getImplicit() {
        return implicit;
    }

    public String getPassword() {
        return password;
    }

    public ClientJobFtpInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public ClientJobFtpInfo setFolderPath(String folderPath) {
        this.folderPath = folderPath;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public ClientJobFtpInfo setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public FtpConnection.FtpType getType() {
        return type;
    }

    public ClientJobFtpInfo setType(FtpConnection.FtpType type) {
        this.type = type;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public ClientJobFtpInfo setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ClientJobFtpInfo setPort(Integer port) {
        this.port = port;
        return this;
    }

    public ClientJobFtpInfo setImplicit(Boolean implicit) {
        this.implicit = implicit;
        return this;
    }

    public Long getPbsz() {
        return pbsz;
    }

    public ClientJobFtpInfo setPbsz(Long pbsz) {
        this.pbsz = pbsz;
        return this;
    }

    public String getProt() {
        return prot;
    }

    public ClientJobFtpInfo setProt(String prot) {
        this.prot = prot;
        return this;
    }

    public Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public ClientJobFtpInfo setPropertiesMap(Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
        return this;
    }


    public String getSshKey() {
        return sshKey;
    }

    public ClientJobFtpInfo setSshKey(String sshKey) {
        this.sshKey = sshKey;
        return this;
    }

    public String getSshPassphrase() {
        return sshPassphrase;
    }

    public ClientJobFtpInfo setSshPassphrase(String sshPassphrase) {
        this.sshPassphrase = sshPassphrase;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobFtpInfo)) return false;

        ClientJobFtpInfo that = (ClientJobFtpInfo) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (folderPath != null ? !folderPath.equals(that.folderPath) : that.folderPath != null) return false;
        if (serverName != null ? !serverName.equals(that.serverName) : that.serverName != null) return false;
        if (type != that.type) return false;
        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (implicit != null ? !implicit.equals(that.implicit) : that.implicit != null) return false;
        if (pbsz != null ? !pbsz.equals(that.pbsz) : that.pbsz != null) return false;
        if (prot != null ? !prot.equals(that.prot) : that.prot != null) return false;
        if (propertiesMap != null ? !propertiesMap.equals(that.propertiesMap) : that.propertiesMap != null)
            return false;
        if (sshKey != null ? !sshKey.equals(that.sshKey) : that.sshKey != null) return false;
        return sshPassphrase != null ? sshPassphrase.equals(that.sshPassphrase) : that.sshPassphrase == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (folderPath != null ? folderPath.hashCode() : 0);
        result = 31 * result + (serverName != null ? serverName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (implicit != null ? implicit.hashCode() : 0);
        result = 31 * result + (pbsz != null ? pbsz.hashCode() : 0);
        result = 31 * result + (prot != null ? prot.hashCode() : 0);
        result = 31 * result + (propertiesMap != null ? propertiesMap.hashCode() : 0);
        result = 31 * result + (sshKey != null ? sshKey.hashCode() : 0);
        result = 31 * result + (sshPassphrase != null ? sshPassphrase.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobFtpInfo{" +
                "folderPath='" + folderPath + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", serverName='" + serverName + '\'' +
                ", type=" + type +
                ", protocol='" + protocol + '\'' +
                ", port=" + port +
                ", implicit=" + implicit +
                ", pbsz=" + pbsz +
                ", prot='" + prot + '\'' +
                ", propertiesMap=" + propertiesMap +
                ", sshKey='" + sshKey + '\'' +
                ", sshPassphrase='" + sshPassphrase + '\'' +
                '}';
    }

    @Override
    public ClientJobFtpInfo deepClone() {
        return new ClientJobFtpInfo(this);
    }
}
