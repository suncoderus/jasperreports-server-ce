/**
 * UserAndRoleManagementServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.axis.authority;

import com.jaspersoft.jasperserver.war.JasperServerConstants;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria;
import com.jaspersoft.jasperserver.ws.authority.WSUser;
import com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

public class UserAndRoleManagementServiceTestCase extends junit.framework.TestCase {

    private static String USER = "SERVICE_USER";
    private static String USER_PASSWORD = "password";
    private static String USER_FULL_NAME = "Service User";
    private static String USER_FULL_NAME_UPDATED = "Service User Updated";
    private static String SERVICE_ROLE = "SERVICE_ROLE";
    private static String NEW_ROLE_NAME = "SERVICE_ROLE_UPDATED";
    private static String ORGANIZATION = "organization_1";
    private static String ROLE_USER = "ROLE_USER";
    private static String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    private static String EMAIL = "user@gmail.com";


    public UserAndRoleManagementServiceTestCase(java.lang.String name) {
        super(name);
    }

    protected WSUserSearchCriteria getTestUserSearchCriteria(String name, String tenantId,
                                                             int maxRecords, Boolean includeSubOrgs,
                                                             WSRole[] requiredRoles) {
        WSUserSearchCriteria searchCriteria = new WSUserSearchCriteria();
        searchCriteria.setName(name);
        searchCriteria.setTenantId(tenantId);
        searchCriteria.setMaxRecords(maxRecords);
        searchCriteria.setIncludeSubOrgs(includeSubOrgs);
        searchCriteria.setRequiredRoles(requiredRoles);

        return searchCriteria;
    }

    protected WSRoleSearchCriteria getTestRoleSearchCriteria(String name, String tenantId,
                                                             int maxRecords, Boolean includeSubOrgs) {
        WSRoleSearchCriteria searchCriteria = new WSRoleSearchCriteria();

        searchCriteria.setRoleName(name);
        searchCriteria.setTenantId(tenantId);
        searchCriteria.setMaxRecords(maxRecords);
        searchCriteria.setIncludeSubOrgs(includeSubOrgs);

        return searchCriteria;
    }

    protected WSUser getTestUser(String name, String tenantId, Boolean enabled) {
        WSUser user = new com.jaspersoft.jasperserver.ws.authority.WSUser();
        user.setUsername(name);
        user.setPassword(USER_PASSWORD);
        user.setTenantId(tenantId);
        user.setEnabled(enabled);
        user.setFullName(USER_FULL_NAME);
        user.setEmailAddress(EMAIL);

        user.setRoles(new WSRole[] {
                getTestRole(ROLE_USER, null),
                getTestRole(ROLE_ANONYMOUS, null)
        });

        return user;
    }

    protected WSRole getTestRole(String name, String tenantId) {
        WSRole role = new com.jaspersoft.jasperserver.ws.authority.WSRole();
        role.setRoleName(name);
        role.setTenantId(tenantId);

        return role;
    }

    public void testUserAndRoleManagementServicePortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1UserAndRoleManagementServicePortFindUsers() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.jaspersoft.jasperserver.ws.authority.WSUser[] value = null;

        value = binding.findUsers(getTestUserSearchCriteria("user", null, 0, null, null));

        assertNotNull(value);
        assertEquals(4, value.length);
        assertEquals(1, value[0].getRoles().length);
        assertEquals(2, value[1].getRoles().length);
        assertEquals(2, value[2].getRoles().length);
        assertEquals(1, value[3].getRoles().length);

        value = null;
        value = binding.findUsers(getTestUserSearchCriteria("user", null, 2, null, null));

        assertNotNull(value);
        assertEquals(2, value.length);

        value = null;
        value = binding.findUsers(getTestUserSearchCriteria("user",null, 0, false, null));

        assertNotNull(value);
        assertEquals(4, value.length);

        value = null;
        value = binding.findUsers(getTestUserSearchCriteria("user",null, 0, true, null));

        assertNotNull(value);
        assertEquals(4, value.length);

        value = null;
        try {
            value = binding.findUsers(getTestUserSearchCriteria("user", ORGANIZATION, 0, true, null));
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }

        value = null;
        value = binding.findUsers(getTestUserSearchCriteria("user", null, 0, null,
                new WSRole[] {}));

        assertNotNull(value);
        assertEquals(4, value.length);

        value = null;
        value = binding.findUsers(getTestUserSearchCriteria("user", null, 0, null,
                new WSRole[] {getTestRole("ROLE_USER", null), getTestRole("ROLE_ETL_ADMIN", null)}));

        assertNotNull(value);
        assertEquals(1, value.length);

        value = null;
        try {
            value = binding.findUsers(null);
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
        assertNull(value);

        try {
            binding = (UserAndRoleManagementServiceSoapBindingStub)
                      new UserAndRoleManagementServiceLocator(JasperServerConstants.instance().WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER).
                              getUserAndRoleManagementServicePort();
        }
        catch (ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        value = null;
        try {
            value = binding.findUsers(getTestUserSearchCriteria("user", null, 0, null, null));
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
    }

    public void test2UserAndRoleManagementServicePortPutUser() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.jaspersoft.jasperserver.ws.authority.WSUser value = null;

        value = binding.putUser(getTestUser(USER, null, true));

        assertNotNull(value);
        assertEquals(USER, value.getUsername());
        assertEquals(USER_FULL_NAME, value.getFullName());
        assertEquals(2, value.getRoles().length);
        
        assertTrue(equals(value.getRoles()[0].getRoleName(), new String[]{ROLE_USER, ROLE_ANONYMOUS}));
        assertTrue(equals(value.getRoles()[1].getRoleName(), new String[]{ROLE_USER, ROLE_ANONYMOUS}));

        WSUser user = getTestUser(USER, null, true);
        user.setFullName(USER_FULL_NAME_UPDATED);
        user.setRoles(new WSRole[] {
                getTestRole(ROLE_ANONYMOUS, null)
        });
        value = binding.putUser(user);

        assertNotNull(value);
        assertEquals(USER, value.getUsername());
        assertEquals(USER_FULL_NAME_UPDATED, value.getFullName());
        assertEquals(1, value.getRoles().length);
        assertEquals(ROLE_ANONYMOUS, value.getRoles()[0].getRoleName());

        WSUser[] serviceUsers = binding.findUsers(getTestUserSearchCriteria(USER,null, 0, true, null));

        assertNotNull(serviceUsers);
        assertEquals(1, serviceUsers.length);

        binding.deleteUser(getTestUser(USER, null, true));

        user = getTestUser(USER, null, true);
        user.setRoles(null);

        value = binding.putUser(user);

        assertNotNull(value);
        assertEquals(USER, value.getUsername());
        assertNull(value.getTenantId());
        assertEquals(1, value.getRoles().length);
        assertEquals(ROLE_USER, value.getRoles()[0].getRoleName());

        binding.deleteUser(getTestUser(USER, null, true));

        user = getTestUser(USER, null, true);
        user.setRoles(new WSRole[0]);

        value = binding.putUser(user);

        assertNotNull(value);
        assertEquals(1, value.getRoles().length);
        assertEquals(ROLE_USER, value.getRoles()[0].getRoleName());

        value = null;
        user = getTestUser(USER + "| ", null, true);
        try {
            value = binding.putUser(user);
            assertNull(value);
        } catch (Exception e) {
            assertNotNull(e);
        }

        value = null;
        try {
            value = binding.putUser(null);
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
        assertNull(value);

        value = null;
        user = getTestUser(USER, null, true);
        user.setEmailAddress(" ");
        try {
            value = binding.putUser(user);
            assertNull(value);
        } catch (Exception e) {
            assertNotNull(e);
        }

        value = null;
        user = getTestUser(USER, null, true);
        user.setEmailAddress("dsfsfsdfsdf");
        try {
            value = binding.putUser(user);
            assertNull(value);
        } catch (Exception e) {
            assertNotNull(e);
        }

        try {
            binding = (UserAndRoleManagementServiceSoapBindingStub)
                      new UserAndRoleManagementServiceLocator(JasperServerConstants.instance().WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER).
                              getUserAndRoleManagementServicePort();
        }
        catch (ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        value = null;
        try {
            value = binding.putUser(getTestUser(USER, null, true));
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
    }

    private boolean equals(String value, String[] values) {
        for(String s : values) {
            if (value.equals(s)) {
                return true;
            }
        }

        return false;
    }

    public void test3UserAndRoleManagementServicePortDeleteUser() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        WSUser[] value = null;
        value = binding.findUsers(getTestUserSearchCriteria(USER, null, 0, null, null));
        assertEquals(1, value.length);

        // Test operation
        binding.deleteUser(getTestUser(USER, null, true));

        value = binding.findUsers(getTestUserSearchCriteria(USER, null, 0, null, null));

        assertNotNull(value);
        assertEquals(0, value.length);

        try {
            binding.deleteUser(null);
            assertNotNull(null);
        } catch (RemoteException e) {
            assertNotNull(e);
        }

        try {
            binding = (UserAndRoleManagementServiceSoapBindingStub)
                      new UserAndRoleManagementServiceLocator(JasperServerConstants.instance().WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER).
                              getUserAndRoleManagementServicePort();
        }
        catch (ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        try {
            binding.deleteUser(getTestUser(USER, null, true));
            assertNotNull(null);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
    }

    public void test4UserAndRoleManagementServicePortFindRoles() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.jaspersoft.jasperserver.ws.authority.WSRole[] value = null;

        value = binding.findRoles(getTestRoleSearchCriteria("role", null, 0, null));

        assertNotNull(value);
        assertEquals(5, value.length);
        assertEquals(0, value[0].getUsers().length);

        value = null;
        value = binding.findRoles(getTestRoleSearchCriteria("ROLE_ADMINISTRATOR", null, 0, null));

        assertNotNull(value);
        assertEquals(1, value.length);

        value = null;
        value = binding.findRoles(getTestRoleSearchCriteria("role", null, 3, null));

        assertNotNull(value);
        assertEquals(3, value.length);

        value = null;
        value = binding.findRoles(getTestRoleSearchCriteria("role", null, 0, true));

        assertNotNull(value);
        assertEquals(5, value.length);

        value = null;
        value = binding.findRoles(getTestRoleSearchCriteria("role", null, 0, false));

        assertNotNull(value);
        assertEquals(5, value.length);

        value = null;

        try {
            value = binding.findRoles(getTestRoleSearchCriteria("role", ORGANIZATION, 0, null));
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
 
        value = null;

        value = binding.findRoles(getTestRoleSearchCriteria(null, null, 0, null));
        assertEquals(5, value.length);

        value = null;
        try {
            value = binding.findRoles(null);
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
        assertNull(value);

        try {
            binding = (UserAndRoleManagementServiceSoapBindingStub)
                      new UserAndRoleManagementServiceLocator(JasperServerConstants.instance().WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER).
                              getUserAndRoleManagementServicePort();
        }
        catch (ServiceException jre) {
            if(jre.getLinkedCause()!=null) {
                jre.getLinkedCause().printStackTrace();
            }
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        value = null;
        try {
            value = binding.findRoles(getTestRoleSearchCriteria("role", null, 0, true));
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
    }

    public void test5UserAndRoleManagementServicePortPutRole() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.jaspersoft.jasperserver.ws.authority.WSRole value = null;
        try {
            value = binding.putRole(getTestRole(SERVICE_ROLE + "| ", null));
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
        
        value = binding.putRole(getTestRole(SERVICE_ROLE, null));

        // TBD - validate results
        assertNotNull(value);
        assertEquals(SERVICE_ROLE, value.getRoleName());
        assertFalse(value.getExternallyDefined());

        value = null;

        WSRole role = getTestRole(SERVICE_ROLE, null);
        role.setExternallyDefined(true);

        value = binding.putRole(role);

        assertNotNull(value);
        assertEquals(SERVICE_ROLE, value.getRoleName());
        assertTrue(value.getExternallyDefined());

        value = null;
        try {
            value = binding.putRole(null);
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
        assertNull(value);

        try {
            binding = (UserAndRoleManagementServiceSoapBindingStub)
                      new UserAndRoleManagementServiceLocator(JasperServerConstants.instance().WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER).
                              getUserAndRoleManagementServicePort();
        }
        catch (ServiceException jre) {
            if(jre.getLinkedCause()!=null) {
                jre.getLinkedCause().printStackTrace();
            }
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        value = null;
        try {
            value = binding.putRole(getTestRole(SERVICE_ROLE, null));
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
    }

    public void test6UserAndRoleManagementServicePortUpdateRoleName() throws Exception {
        UserAndRoleManagementServiceSoapBindingStub binding;
        try {
            binding = (UserAndRoleManagementServiceSoapBindingStub)
                          new UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        WSRole role = getTestRole(SERVICE_ROLE, null);
        com.jaspersoft.jasperserver.ws.authority.WSRole value = null;
        value = binding.updateRoleName(role, NEW_ROLE_NAME);
        // TBD - validate results

        assertNotNull(value);
        assertEquals(NEW_ROLE_NAME, value.getRoleName());

        value = null;
        value = binding.updateRoleName(getTestRole(NEW_ROLE_NAME, null), SERVICE_ROLE);

        value = null;
        try {
            value = binding.updateRoleName(null, "w w w");
            assertNull(value);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
        assertNull(value);
    }

    public void test7UserAndRoleManagementServicePortDeleteRole() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.UserAndRoleManagementServiceLocator().getUserAndRoleManagementServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        binding.deleteRole(getTestRole(SERVICE_ROLE, null));

        WSRole[] value = null;
        value = binding.findRoles(getTestRoleSearchCriteria(SERVICE_ROLE, null, 0, null));

        assertNotNull(value);
        assertEquals(0, value.length);

        try {
             binding.deleteRole(null);
            assertNotNull(null);
        } catch (RemoteException e) {
            assertNotNull(e);
        }

        try {
            binding = (UserAndRoleManagementServiceSoapBindingStub)
                      new UserAndRoleManagementServiceLocator(JasperServerConstants.instance().WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER).
                              getUserAndRoleManagementServicePort();
        }
        catch (ServiceException jre) {
            if(jre.getLinkedCause()!=null) {
                jre.getLinkedCause().printStackTrace();
            }
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        value = null;
        try {
            binding.deleteRole(getTestRole(SERVICE_ROLE, null));
            assertNotNull(null);
        } catch (RemoteException e) {
            assertNotNull(e);
        }
    }

}
