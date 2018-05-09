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
import com.amazonaws.auth.*;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.redshift.AmazonRedshiftClient;
import com.amazonaws.services.redshift.model.Cluster;
import com.jaspersoft.jasperserver.api.JSShowOnlyErrorMessage;
import com.jaspersoft.jasperserver.war.dto.AwsDBInstanceDTO;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.net.UnknownHostException;
import java.util.*;

/**
 * @author vsabadosh
 * @version $Id$
 */
public class AwsDataSourceServiceImpl implements AwsDataSourceService {
    
    public static final String RDS = "rds";
    public static final String Redshift = "redshift";

    public static final String jdbcUrl = "jdbcUrl";
    public static final String jdbcDriverClass = "jdbcDriverClass";

    public static final String ACCESS_DENIED = "accessdenied";
    public static final String INVALID_CLIENT_TOKEN_ID = "invalidclienttokenid";
    public static final String SIGNATURE_DOES_NOT_MATCH = "signaturedoesnotmatch";

    private String awsDataSourceActiveStatus;

    private MessageSource messageSource;

    private Map<String, Map<String, Object>> jdbcConnectionMap;

    private static final Log log = LogFactory.getLog(AwsDataSourceServiceImpl.class);

    public List<AwsDBInstanceDTO> getAwsDBInstances(AWSCredentials awsCredentials, String amazonDBService,
            String endpoint) {
        try {
            if (amazonDBService.toLowerCase().equals(RDS)) {
                //Make RDS service calls to read all available RDS instances
                AmazonRDSClient rdsClient = new AmazonRDSClient(awsCredentials);
                if (endpoint != null) {
                    rdsClient.setEndpoint(RDS + "." + endpoint);
                }
                return toRDSInstancesDTOs(getRdsInstances(rdsClient), amazonDBService);
            } else if (amazonDBService.toLowerCase().equals(Redshift)) {
                //Make RedShift service calls to read all available RedShift instances
                AmazonRedshiftClient redshiftClient = new AmazonRedshiftClient(awsCredentials);
                if (endpoint != null) {
                    redshiftClient.setEndpoint(Redshift + "." + endpoint);
                }
                return toRedshiftInstancesDTOs(getRedshiftInstances(redshiftClient), amazonDBService);
            } else {
                return new ArrayList<AwsDBInstanceDTO>();
            }
        } catch (AmazonServiceException ex) {
            log.warn("Loading AWS data source metadata for " + amazonDBService +": " + ex.getMessage());

            String errorCode = ex.getErrorCode();
            if (ex.getStatusCode() == 403 && errorCode != null) {
                errorCode = errorCode.toLowerCase();
                if (errorCode.equals(ACCESS_DENIED)) {
                    return generateDBServiceInfoStatus(amazonDBService, "resource.dataSource.aws.access.denied");
                } else if (errorCode.equals(INVALID_CLIENT_TOKEN_ID)) {
                    throw new JSShowOnlyErrorMessage(messageSource.getMessage("" +
                            "aws.exception.datasource.accessKey.invalid", null, LocaleContextHolder.getLocale()));
                } else if (errorCode.equals(SIGNATURE_DOES_NOT_MATCH)) {
                    throw new JSShowOnlyErrorMessage(messageSource.getMessage("" +
                            "aws.exception.datasource.secretKey.invalid", null, LocaleContextHolder.getLocale()));
                }
            }

            return generateDBServiceInfoStatus(amazonDBService, "[" + ex.getMessage() + "]");
        } catch (AmazonClientException ex) {
            if (ex.getCause() instanceof UnknownHostException) {
                return generateDBServiceInfoStatus(endpoint, "resource.dataSource.aws.unknown.host");
            }

            return generateDBServiceInfoStatus(amazonDBService, "[" + ex.getMessage() + "]");
        }
    }

    protected List<DBInstance> getRdsInstances(AmazonRDSClient rdsClient) {
        return rdsClient.describeDBInstances().getDBInstances();
    }

    protected List<Cluster> getRedshiftInstances(AmazonRedshiftClient redshiftClient) {
        return redshiftClient.describeClusters().getClusters();
    }

    private List<AwsDBInstanceDTO> toRDSInstancesDTOs(List<DBInstance> dbInstances, String amazonDBService) {
        List<AwsDBInstanceDTO> awsDBInstanceDTOs = new ArrayList<AwsDBInstanceDTO>();
        if (dbInstances != null && dbInstances.size() > 0) {
            for (DBInstance dbInstance : dbInstances) {
                if (dbInstance.getDBInstanceStatus().toLowerCase().equals(awsDataSourceActiveStatus)) {
                    AwsDBInstanceDTO awsDBInstanceDTO = new AwsDBInstanceDTO();
                    awsDBInstanceDTO.setdBInstanceIdentifier(dbInstance.getDBInstanceIdentifier());
                    awsDBInstanceDTO.setdBName(dbInstance.getDBName());
                    awsDBInstanceDTO.setEngine(dbInstance.getEngine());
                    awsDBInstanceDTO.setEngineVersion(dbInstance.getEngineVersion());
                    awsDBInstanceDTO.setAddress(dbInstance.getEndpoint().getAddress());
                    awsDBInstanceDTO.setPort(dbInstance.getEndpoint().getPort());
                    awsDBInstanceDTO.setAmazonDbService(amazonDBService.toLowerCase());

                    updateWithConnectionUrl(awsDBInstanceDTO);

                    awsDBInstanceDTOs.add(awsDBInstanceDTO);
                }
            }
        } else  {
            return generateDBServiceInfoStatus(amazonDBService, "resource.dataSource.aws.empty");
        }
        return awsDBInstanceDTOs;
    }

    private List<AwsDBInstanceDTO> toRedshiftInstancesDTOs(List<Cluster> dbClusters, String amazonDBService) {
        List<AwsDBInstanceDTO> awsDBInstanceDTOs = new ArrayList<AwsDBInstanceDTO>();
        if (dbClusters != null && dbClusters.size() > 0) {
            for (Cluster dbCluster : dbClusters) {
                if (dbCluster.getClusterStatus().toLowerCase().equals(awsDataSourceActiveStatus)) {
                    AwsDBInstanceDTO awsDBInstanceDTO = new AwsDBInstanceDTO();
                    awsDBInstanceDTO.setdBInstanceIdentifier(dbCluster.getClusterIdentifier());
                    awsDBInstanceDTO.setdBName(dbCluster.getDBName());
                    awsDBInstanceDTO.setEngine("postgresql");
                    awsDBInstanceDTO.setEngineVersion(null);
                    awsDBInstanceDTO.setAddress(dbCluster.getEndpoint().getAddress());
                    awsDBInstanceDTO.setPort(dbCluster.getEndpoint().getPort());
                    awsDBInstanceDTO.setAmazonDbService(amazonDBService.toLowerCase());

                    updateWithConnectionUrl(awsDBInstanceDTO);

                    awsDBInstanceDTOs.add(awsDBInstanceDTO);
                }
            }
        } else  {
            return generateDBServiceInfoStatus(amazonDBService, "resource.dataSource.aws.empty");
        }
        return awsDBInstanceDTOs;
    }

    private List<AwsDBInstanceDTO> generateDBServiceInfoStatus(String amazonDBService, String statusMessageKey) {
        List<AwsDBInstanceDTO> awsDBInstanceDTOs = new ArrayList<AwsDBInstanceDTO>();
        AwsDBInstanceDTO awsDBInstanceDTO = new AwsDBInstanceDTO();
        String statusMessage = messageSource.getMessage(statusMessageKey, new String[]{amazonDBService},
                LocaleContextHolder.getLocale());
        
        awsDBInstanceDTO.setdBInstanceIdentifier(statusMessage);
        awsDBInstanceDTO.setAmazonDbService(amazonDBService);
        
        awsDBInstanceDTOs.add(awsDBInstanceDTO);
        return awsDBInstanceDTOs;
    }
    
    private void updateWithConnectionUrl(AwsDBInstanceDTO awsDBInstanceDTO) {
        for (String dbType : jdbcConnectionMap.keySet()) {
            if (awsDBInstanceDTO.getEngine().startsWith(dbType)) {
                Map<String, Object> dbProperties = jdbcConnectionMap.get(dbType);

                awsDBInstanceDTO.setJdbcTemplate((String) dbProperties.get(jdbcUrl));

                Map<String, String> values = new HashMap<String, String>();
                values.put("dbHost", awsDBInstanceDTO.getAddress());
                values.put("dbPort", String.valueOf(awsDBInstanceDTO.getPort()));
                values.put("dbName", awsDBInstanceDTO.getdBName());
                StrSubstitutor sub = new StrSubstitutor(values, "$[", "]");
                awsDBInstanceDTO.setJdbcUrl(sub.replace(dbProperties.get(jdbcUrl)));

                awsDBInstanceDTO.setJdbcDriverClass((String) dbProperties.get(jdbcDriverClass));
                break;
            }
        }
    }

    public void setJdbcConnectionMap(Map<String, Map<String, Object>> jdbcConnectionMap) {
        this.jdbcConnectionMap = jdbcConnectionMap;
    }

    public void setAwsDataSourceActiveStatus(String awsDataSourceActiveStatus) {
        this.awsDataSourceActiveStatus = awsDataSourceActiveStatus;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
