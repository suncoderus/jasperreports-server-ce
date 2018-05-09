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
package com.jaspersoft.jasperserver.war.amazon.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.Endpoint;
import com.amazonaws.services.redshift.AmazonRedshiftClient;
import com.amazonaws.services.redshift.model.Cluster;
import com.jaspersoft.jasperserver.api.JSShowOnlyErrorMessage;
import com.jaspersoft.jasperserver.war.dto.AwsDBInstanceDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.mockito.Matchers;

import java.net.UnknownHostException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * @author vsabadosh
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class AwsDataSourceServiceImplTest {
    private AWSCredentials awsCredentials = null;

    public static final String AWS_REGION = "us-east-1";
    public static final String AWS_DB_ACTIVE_STATUS = "available";
    public static final String AWS_DB_INACTIVE_STATUS = "unavailable";
    public static final String DATA_SOURCE_AWS_EMPTY = "resource.dataSource.aws.empty";

    private List<AwsDBInstanceDTO> expectedAwsDBInstances;

    @Spy
    private AwsDataSourceServiceImpl awsDataSourceService;

    @Mock
    private MessageSource  messageSource;

    @Before
    public void initialize() {
        Map<String, Map<String, Object>> jdbcConnectionMap = new LinkedHashMap<String, Map<String, Object>>();

        jdbcConnectionMap.put("mysql", generateConnectionProperties("MySQL", "jdbc:mysql://$[dbHost]:$[dbPort]/$[dbName]", "com.mysql.jdbc.Driver"));
        jdbcConnectionMap.put("postgresql",  generateConnectionProperties("PostgreSQL", "jdbc:postgresql://$[dbHost]:$[dbPort]/$[dbName]", "org.postgresql.Driver"));
        jdbcConnectionMap.put("oracle",  generateConnectionProperties("Oracle", "jdbc:oracle:thin:@$[dbHost]:$[dbPort]:$[sName]", "oracle.jdbc.OracleDriver"));

        awsDataSourceService.setJdbcConnectionMap(jdbcConnectionMap);
        awsDataSourceService.setAwsDataSourceActiveStatus("available");
        awsDataSourceService.setMessageSource(messageSource);

        awsCredentials = new BasicAWSCredentials("accessKey", "secretKey");

        expectedAwsDBInstances = new ArrayList<AwsDBInstanceDTO>();

        expectedAwsDBInstances.add(generateAwsInstance("instance1", "db1", "mysql", "host1.us-east-1.amazonaws.com", 3306, "version1"));
        expectedAwsDBInstances.add(generateAwsInstance("instance2", "db2", "postgresql", "host2.us-east-1.amazonaws.com", 5432, "version2"));
        expectedAwsDBInstances.add(generateAwsInstance("instance3", "db3", "oracle", "host3.us-east-1.amazonaws.com", 1521, "version3"));
    }

    @Test
    public void getAwsDBInstances_noRdsInstances_returnEmptyStatus() {
        doReturn(new ArrayList<DBInstance>()).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));
        doReturn(DATA_SOURCE_AWS_EMPTY).when(messageSource).getMessage(Matchers.eq(DATA_SOURCE_AWS_EMPTY),
                Matchers.any(String[].class), Matchers.any(Locale.class));
        List<AwsDBInstanceDTO> rdsInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);

        assertEquals(1, rdsInstances.size());
        assertEquals(DATA_SOURCE_AWS_EMPTY, rdsInstances.get(0).getdBInstanceIdentifier());
        assertEquals(AwsDataSourceServiceImpl.RDS, rdsInstances.get(0).getAmazonDbService());
    }

    @Test
    public void getAwsDBInstances_noRedshiftInstances_returnEmptyStatus() {
        doReturn(new ArrayList<Cluster>()).when(awsDataSourceService).getRedshiftInstances(any(AmazonRedshiftClient.class));
        doReturn(DATA_SOURCE_AWS_EMPTY).when(messageSource).getMessage(Matchers.eq(DATA_SOURCE_AWS_EMPTY),
                Matchers.any(String[].class), Matchers.any(Locale.class));
        List<AwsDBInstanceDTO> redshiftInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.Redshift, AWS_REGION);

        assertEquals(1, redshiftInstances.size());
        assertEquals(DATA_SOURCE_AWS_EMPTY, redshiftInstances.get(0).getdBInstanceIdentifier());
        assertEquals(AwsDataSourceServiceImpl.Redshift, redshiftInstances.get(0).getAmazonDbService());
    }

    @Test
    public void getAwsDBInstances_3ActiveRds_return3RdsInstances() {
        doReturn(generateRdsInstances(AWS_DB_ACTIVE_STATUS)).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));
        List<AwsDBInstanceDTO> rdsInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);
        Assert.assertEquals(3, rdsInstances.size());

        int index = 0;
        for(AwsDBInstanceDTO awsDBInstanceDTO : rdsInstances) {
            verifyAssertion(awsDBInstanceDTO, index++, AwsDataSourceServiceImpl.RDS);
        }
    }

    @Test
    public void getAwsDBInstances_3ActiveRedshift_return3RedshiftInstances() {
        doReturn(generateRedshiftInstances(AWS_DB_ACTIVE_STATUS)).when(awsDataSourceService).getRedshiftInstances(any(AmazonRedshiftClient.class));
        List<AwsDBInstanceDTO> redshiftList = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.Redshift, AWS_REGION);
        assertEquals(3, redshiftList.size());
        int index = 0;

        for(AwsDBInstanceDTO awsDBInstanceDTO : redshiftList) {
            verifyAssertion(awsDBInstanceDTO, index++, AwsDataSourceServiceImpl.Redshift);
        }
    }

    @Test
    public void getAwsDBInstances_3InactiveRds_returnEmptyList() {
        doReturn(generateRdsInstances(AWS_DB_INACTIVE_STATUS)).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));
        List<AwsDBInstanceDTO> rdsInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);
        Assert.assertEquals(0, rdsInstances.size());
    }

    @Test
    public void getAwsDBInstances_3InactiveRedshift_returnEmptyList() {
        doReturn(generateRedshiftInstances(AWS_DB_INACTIVE_STATUS)).when(awsDataSourceService).getRedshiftInstances(any(AmazonRedshiftClient.class));
        List<AwsDBInstanceDTO> redshiftInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.Redshift, AWS_REGION);
        assertEquals(0, redshiftInstances.size());
    }

    @Test
    public void getAwsDBInstances_accessDenied_returnAccessDeniedStatus() {
        AmazonServiceException accessDeniedException = new AmazonServiceException("Access denied");
        accessDeniedException.setStatusCode(403);
        accessDeniedException.setErrorCode(AwsDataSourceServiceImpl.ACCESS_DENIED);

        doThrow(accessDeniedException).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));
        doReturn("resource.dataSource.aws.access.denied").when(messageSource).getMessage(Matchers.eq("resource.dataSource.aws.access.denied"),
                Matchers.any(String[].class), Matchers.any(Locale.class));

        List<AwsDBInstanceDTO> rdsInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);

        assertEquals(1, rdsInstances.size());
        assertEquals("resource.dataSource.aws.access.denied", rdsInstances.get(0).getdBInstanceIdentifier());
        assertEquals(AwsDataSourceServiceImpl.RDS, rdsInstances.get(0).getAmazonDbService());
    }

    @Test(expected = JSShowOnlyErrorMessage.class)
    public void getAwsDBInstances_invalidClientTokenId_throwJSShowOnlyErrorMessage() {
        AmazonServiceException invalidClientTokenIdException = new AmazonServiceException("Invalid client token id");
        invalidClientTokenIdException.setStatusCode(403);
        invalidClientTokenIdException.setErrorCode(AwsDataSourceServiceImpl.INVALID_CLIENT_TOKEN_ID);

        doThrow(invalidClientTokenIdException).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));

        awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);
    }

    @Test(expected = JSShowOnlyErrorMessage.class)
    public void getAwsDBInstances_signatureDoesNotMatch_throwJSShowOnlyErrorMessage() {
        AmazonServiceException signatureDoesNotMatchException = new AmazonServiceException("Signature doesn't match");
        signatureDoesNotMatchException.setStatusCode(403);
        signatureDoesNotMatchException.setErrorCode(AwsDataSourceServiceImpl.SIGNATURE_DOES_NOT_MATCH);

        doThrow(signatureDoesNotMatchException).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));

        awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);
    }

    @Test
    public void getAwsDBInstances_unknownHost_returnUnknownHostStatus() {
        AmazonClientException unknownHostException = new AmazonClientException("Amazon client exception");
        unknownHostException.initCause(new UnknownHostException());

        doThrow(unknownHostException).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));
        doReturn("resource.dataSource.aws.unknown.host").when(messageSource).getMessage(Matchers.eq("resource.dataSource.aws.unknown.host"),
                Matchers.any(String[].class), Matchers.any(Locale.class));

        List<AwsDBInstanceDTO> rdsInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);

        assertEquals(1, rdsInstances.size());
        assertEquals("resource.dataSource.aws.unknown.host", rdsInstances.get(0).getdBInstanceIdentifier());
        assertEquals(AWS_REGION, rdsInstances.get(0).getAmazonDbService());
    }

    @Test
    public void getAwsDBInstances_unknownAwsService_returnEmptyList() {
        List<AwsDBInstanceDTO> unknownInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, "UnknownAWSService", AWS_REGION);

        assertEquals(0, unknownInstances.size());
    }

    @Test
    public void getAwsDBInstances_anyAmazonServiceException_returnErrorStatus() {
        String exceptionMessage = "AmazonServiceException";

        AmazonServiceException amazonServiceException = new AmazonServiceException(exceptionMessage);
        doThrow(amazonServiceException).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));
        doReturn("[" + amazonServiceException.getMessage() + "]").when(messageSource).getMessage(Matchers.eq("[" + amazonServiceException.getMessage() + "]"),
                Matchers.any(String[].class), Matchers.any(Locale.class));

        List<AwsDBInstanceDTO> rdsInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);

        assertEquals(1, rdsInstances.size());
        assertEquals("[" + amazonServiceException.getMessage() + "]", rdsInstances.get(0).getdBInstanceIdentifier());
        assertEquals(AwsDataSourceServiceImpl.RDS, rdsInstances.get(0).getAmazonDbService());
    }

    @Test
    public void getAwsDBInstances_anyAmazonClientException_returnErrorStatus() {
        String exceptionMessage = "AmazonClientException";

        AmazonClientException amazonServiceException = new AmazonClientException(exceptionMessage);
        doThrow(amazonServiceException).when(awsDataSourceService).getRdsInstances(any(AmazonRDSClient.class));
        doReturn("[" + exceptionMessage + "]").when(messageSource).getMessage(Matchers.eq("[" + exceptionMessage + "]"),
                Matchers.any(String[].class), Matchers.any(Locale.class));

        List<AwsDBInstanceDTO> rdsInstances = awsDataSourceService.getAwsDBInstances(awsCredentials, AwsDataSourceServiceImpl.RDS, AWS_REGION);

        assertEquals(1, rdsInstances.size());
        assertEquals("[" + exceptionMessage + "]", rdsInstances.get(0).getdBInstanceIdentifier());
        assertEquals(AwsDataSourceServiceImpl.RDS, rdsInstances.get(0).getAmazonDbService());
    }

    private Map<String, Object> generateConnectionProperties(String label, String jdbcUrl, String jdbcDriverClass) {
        Map<String, Object> connectionProperties = new HashMap<String, Object>();
        connectionProperties.put("label", label);
        connectionProperties.put("jdbcUrl", jdbcUrl);
        connectionProperties.put("jdbcDriverClass", jdbcDriverClass);

        return connectionProperties;
    }

    private AwsDBInstanceDTO generateAwsInstance(String identifier, String dbName, String engine, String address, int port, String engineVersion) {
        AwsDBInstanceDTO awsDBInstanceDto = new AwsDBInstanceDTO();
        awsDBInstanceDto.setdBInstanceIdentifier(identifier);
        awsDBInstanceDto.setdBName(dbName);
        awsDBInstanceDto.setEngine(engine);
        awsDBInstanceDto.setAddress(address);
        awsDBInstanceDto.setPort(port);
        awsDBInstanceDto.setEngineVersion(engineVersion);

        return awsDBInstanceDto;
    }

    private void verifyAssertion(AwsDBInstanceDTO awsDBInstanceDTO, int index, String amazonDbService) {
        assertEquals(expectedAwsDBInstances.get(index).getdBInstanceIdentifier(), awsDBInstanceDTO.getdBInstanceIdentifier());
        assertEquals(expectedAwsDBInstances.get(index).getdBName(), awsDBInstanceDTO.getdBName());
        assertEquals(expectedAwsDBInstances.get(index).getAddress(), awsDBInstanceDTO.getAddress());
        assertEquals(expectedAwsDBInstances.get(index).getPort(), awsDBInstanceDTO.getPort());
        if (amazonDbService.equals(AwsDataSourceServiceImpl.RDS)) {
            assertEquals(expectedAwsDBInstances.get(index).getEngine(), awsDBInstanceDTO.getEngine());
            assertEquals(expectedAwsDBInstances.get(index).getEngineVersion(), awsDBInstanceDTO.getEngineVersion());
            assertEquals(AwsDataSourceServiceImpl.RDS, awsDBInstanceDTO.getAmazonDbService());
        } else {
            assertEquals("postgresql", awsDBInstanceDTO.getEngine());
            assertEquals(null, awsDBInstanceDTO.getEngineVersion());
            assertEquals(AwsDataSourceServiceImpl.Redshift, awsDBInstanceDTO.getAmazonDbService());
        }
    }

    private List<DBInstance> generateRdsInstances(String awsDbStatus) {
        List<DBInstance> rdsInstances = new ArrayList<DBInstance>();
        for (AwsDBInstanceDTO awsDBInstanceDto : expectedAwsDBInstances) {
            DBInstance rdsInstance = new DBInstance().withDBInstanceIdentifier(awsDBInstanceDto.getdBInstanceIdentifier())
                    .withDBName(awsDBInstanceDto.getdBName()).withEngine(awsDBInstanceDto.getEngine())
                    .withEngineVersion(awsDBInstanceDto.getEngineVersion()).withEndpoint(new Endpoint()
                    .withAddress(awsDBInstanceDto.getAddress()).withPort(awsDBInstanceDto.getPort()))
                    .withDBInstanceStatus(awsDbStatus);

            rdsInstances.add(rdsInstance);
        }

        return rdsInstances;
    }

    private List<Cluster> generateRedshiftInstances(String awsDbStatus) {
        List<Cluster> redshiftInstances = new ArrayList<Cluster>();
        for (AwsDBInstanceDTO awsDBInstanceDto : expectedAwsDBInstances) {
            Cluster redshiftInstance = new Cluster().withClusterIdentifier(awsDBInstanceDto.getdBInstanceIdentifier())
                    .withDBName(awsDBInstanceDto.getdBName())
                    .withEndpoint(new com.amazonaws.services.redshift.model.Endpoint()
                    .withAddress(awsDBInstanceDto.getAddress()).withPort(awsDBInstanceDto.getPort()))
                    .withClusterStatus(awsDbStatus);

            redshiftInstances.add(redshiftInstance);
        }

        return redshiftInstances;
    }

}
