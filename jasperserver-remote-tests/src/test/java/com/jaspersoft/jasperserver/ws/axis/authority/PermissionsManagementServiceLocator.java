/**
 * PermissionsManagementServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.axis.authority;

import com.jaspersoft.jasperserver.war.JasperServerConstants;

public class PermissionsManagementServiceLocator extends org.apache.axis.client.Service implements com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementService {

    public PermissionsManagementServiceLocator() {
    }


    public PermissionsManagementServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PermissionsManagementServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PermissionsManagementServicePort
    private java.lang.String PermissionsManagementServicePort_address = JasperServerConstants.instance().WS_PERMISSIONS_MANAGEMENT_END_POINT_URL;

    public java.lang.String getPermissionsManagementServicePortAddress() {
        return PermissionsManagementServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PermissionsManagementServicePortWSDDServiceName = "PermissionsManagementServicePort";

    public java.lang.String getPermissionsManagementServicePortWSDDServiceName() {
        return PermissionsManagementServicePortWSDDServiceName;
    }

    public void setPermissionsManagementServicePortWSDDServiceName(java.lang.String name) {
        PermissionsManagementServicePortWSDDServiceName = name;
    }

    public com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagement getPermissionsManagementServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PermissionsManagementServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPermissionsManagementServicePort(endpoint);
    }

    public com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagement getPermissionsManagementServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub _stub = new com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getPermissionsManagementServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPermissionsManagementServicePortEndpointAddress(java.lang.String address) {
        PermissionsManagementServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagement.class.isAssignableFrom(serviceEndpointInterface)) {
                com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub _stub = new com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub(new java.net.URL(PermissionsManagementServicePort_address), this);
                _stub.setPortName(getPermissionsManagementServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PermissionsManagementServicePort".equals(inputPortName)) {
            return getPermissionsManagementServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.jasperforge.org/jasperserver/ws", "PermissionsManagementService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.jasperforge.org/jasperserver/ws", "PermissionsManagementServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PermissionsManagementServicePort".equals(portName)) {
            setPermissionsManagementServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
