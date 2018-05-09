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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = ResourceMediaType.CUSTOM_DATA_SOURCE_CLIENT_TYPE)
public class ClientCustomDataSource extends ClientResource<ClientCustomDataSource> implements ClientReferenceableDataSource {
    private String serviceClass;
    private String dataSourceName;
    private List<ClientProperty> properties;
    private Map<String, ClientReferenceableFile> resources;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public ClientCustomDataSource(){
    }

    public ClientCustomDataSource(ClientCustomDataSource source){
        super(source);
        serviceClass = source.getServiceClass();
        dataSourceName = source.getDataSourceName();
        final List<ClientProperty> sourceProperties = source.getProperties();
        if(sourceProperties != null){
            properties = new ArrayList<ClientProperty>();
            for(ClientProperty property : sourceProperties){
                properties.add(new ClientProperty(property));
            }
        }
        final Map<String, ClientReferenceableFile> sourceResources = source.getResources();
        if(sourceResources != null){
            resources = new HashMap<String, ClientReferenceableFile>(sourceResources.size());
            for(String key : sourceResources.keySet()){
                final ClientReferenceableFile clientReferenceable = sourceResources.get(key);
                ClientReferenceableFile clone;
                if(clientReferenceable instanceof ClientReference){
                    clone = new ClientReference((ClientReference) clientReferenceable);
                } else {
                    clone = new ClientFile((ClientFile) clientReferenceable);
                }
                resources.put(key, clone);
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientCustomDataSource)) return false;
        if (!super.equals(o)) return false;

        ClientCustomDataSource that = (ClientCustomDataSource) o;

        if (dataSourceName != null ? !dataSourceName.equals(that.dataSourceName) : that.dataSourceName != null)
            return false;
        if (properties != null ? !new HashSet(properties).equals(new HashSet(that.properties)) : that.properties != null) return false;
        if (resources != null ? !resources.equals(that.resources) : that.resources != null) return false;
        if (serviceClass != null ? !serviceClass.equals(that.serviceClass) : that.serviceClass != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (serviceClass != null ? serviceClass.hashCode() : 0);
        result = 31 * result + (dataSourceName != null ? dataSourceName.hashCode() : 0);
        result = 31 * result + (properties != null ?  new HashSet(properties).hashCode() : 0);
        result = 31 * result + (resources != null ? resources.hashCode() : 0);
        return result;
    }

    @XmlJavaTypeAdapter(FilesMapXmlAdapter.class)
    @XmlElement(name = "resources")
    public Map<String, ClientReferenceableFile> getResources() {
        return resources;
    }

    public void setResources(Map<String, ClientReferenceableFile> resources) {
        this.resources = resources;
    }

    public ClientCustomDataSource setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        return this;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public ClientCustomDataSource setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
        return this;
    }

    public List<ClientProperty> getProperties() {
        return properties;
    }

    public ClientCustomDataSource setProperties(List<ClientProperty> properties) {
        String prefix = "repo:";
        this.properties = properties;
        /**
         * for testing purpose:
         * add resource reference of JSON custom data source to client custom data source
         */
        if (properties != null) {
            for (ClientProperty property : properties) {
                if (property.getKey().equals("fileName")) {
                    String value = property.getValue();
                    if ((property.getValue() != null) && property.getValue().startsWith(prefix)) {
                        Map<String, ClientReferenceableFile> sourceResources = new HashMap<String, ClientReferenceableFile>();
                        String uri = value.substring(prefix.length());
                        sourceResources.put("dataFile", new ClientReference(uri));
                        setResources(sourceResources);
                    }
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "ClientCustomDataSource{" +
                "serviceClass='" + serviceClass + '\'' +
                ", dataSourceName='" + dataSourceName + '\'' +
                ", properties=" + properties +
                ", resources=" + resources +
                '}';
    }
}
