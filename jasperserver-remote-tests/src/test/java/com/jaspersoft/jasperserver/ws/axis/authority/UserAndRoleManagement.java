/**
 * UserAndRoleManagement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.axis.authority;

public interface UserAndRoleManagement extends java.rmi.Remote {
    public com.jaspersoft.jasperserver.ws.authority.WSUser[] findUsers(com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria criteria) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSUser putUser(com.jaspersoft.jasperserver.ws.authority.WSUser user) throws java.rmi.RemoteException;
    public void deleteUser(com.jaspersoft.jasperserver.ws.authority.WSUser user) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSRole[] findRoles(com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria criteria) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSRole putRole(com.jaspersoft.jasperserver.ws.authority.WSRole role) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSRole updateRoleName(com.jaspersoft.jasperserver.ws.authority.WSRole oldRole, java.lang.String newName) throws java.rmi.RemoteException;
    public void deleteRole(com.jaspersoft.jasperserver.ws.authority.WSRole role) throws java.rmi.RemoteException;
}
