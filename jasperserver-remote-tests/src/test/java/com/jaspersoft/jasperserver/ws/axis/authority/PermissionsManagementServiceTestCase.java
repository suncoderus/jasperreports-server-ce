/**
 * PermissionsManagementServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.axis.authority;

import com.jaspersoft.jasperserver.ws.authority.WSObjectPermission;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSUser;
import org.apache.axis.AxisFault;

public class PermissionsManagementServiceTestCase extends junit.framework.TestCase {

    private static String ORGANIZATIONS = "organizations";

    public PermissionsManagementServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testPermissionsManagementServicePortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceLocator().getPermissionsManagementServicePortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1PermissionsManagementServicePortGetPermissionsForObject() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceLocator().getPermissionsManagementServicePort();
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
        com.jaspersoft.jasperserver.ws.authority.WSObjectPermission[] objectPermissions =
                binding.getPermissionsForObject("repo:/");

        assertNotNull(objectPermissions);
        assertTrue(objectPermissions.length > 0);
        assertEquals("repo:/", objectPermissions[0].getUri());

        try {
            binding.getPermissionsForObject("repo:/someWrongURI");
            fail("No exception for wrong URI");
        } catch (AxisFault e) {
            assertEquals("There is no resource or folder for target URI \"repo:/someWrongURI\"", e.getMessage());
        }
    }

    public void test2PermissionsManagementServicePortPutPermission() throws Exception {
        com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub binding;
        try {
            binding = (com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceSoapBindingStub)
                          new com.jaspersoft.jasperserver.ws.axis.authority.PermissionsManagementServiceLocator().getPermissionsManagementServicePort();
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
        String resourceUri = "repo:/reports";
        com.jaspersoft.jasperserver.ws.authority.WSObjectPermission[] objectPermissions =
                binding.getPermissionsForObject(resourceUri);

        int initialPermissionsCount = objectPermissions.length;

        com.jaspersoft.jasperserver.ws.authority.WSObjectPermission value = null;

        WSObjectPermission o = new WSObjectPermission();
        o.setUri(resourceUri);
        o.setPermissionMask(2);

        WSUser wsUser = new WSUser();
        wsUser.setUsername("joeuser");
        wsUser.setTenantId(ORGANIZATIONS);

        o.setPermissionRecipient(wsUser);

        value = binding.putPermission(o);

        assertNotNull(value);
        assertEquals(resourceUri, value.getUri());

        objectPermissions = binding.getPermissionsForObject(resourceUri);

        assertNotNull(objectPermissions);
        assertEquals(initialPermissionsCount + 1, objectPermissions.length);

        binding.deletePermission(value);

        objectPermissions = binding.getPermissionsForObject(resourceUri);

        assertNotNull(objectPermissions);
        assertEquals(initialPermissionsCount, objectPermissions.length);
    }

    public void test3PermissionsManagementServicePortDeletePermission() throws Exception {
        // deletePermission method tested in the test2PermissionsManagementServicePortPutPermission
    }

}
