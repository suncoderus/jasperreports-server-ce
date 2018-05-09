/**
 * PermissionsManagement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.axis.authority;

public interface PermissionsManagement extends java.rmi.Remote {
    public com.jaspersoft.jasperserver.ws.authority.WSObjectPermission[] getPermissionsForObject(java.lang.String targetURI) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSObjectPermission putPermission(com.jaspersoft.jasperserver.ws.authority.WSObjectPermission objectPermission) throws java.rmi.RemoteException;
    public void deletePermission(com.jaspersoft.jasperserver.ws.authority.WSObjectPermission objectPermission) throws java.rmi.RemoteException;
}
