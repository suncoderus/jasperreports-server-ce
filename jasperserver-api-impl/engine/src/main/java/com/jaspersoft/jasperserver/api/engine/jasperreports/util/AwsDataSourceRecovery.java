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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.AuthorizeDBSecurityGroupIngressRequest;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBSecurityGroup;
import com.amazonaws.services.rds.model.DBSecurityGroupMembership;
import com.amazonaws.services.rds.model.DBSecurityGroupNotFoundException;
import com.amazonaws.services.rds.model.DBSubnetGroup;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.DescribeDBSecurityGroupsRequest;
import com.amazonaws.services.rds.model.DescribeDBSecurityGroupsResult;
import com.amazonaws.services.rds.model.IPRange;
import com.amazonaws.services.rds.model.ModifyDBInstanceRequest;
import com.amazonaws.services.rds.model.RevokeDBSecurityGroupIngressRequest;
import com.amazonaws.services.rds.model.VpcSecurityGroupMembership;
import com.amazonaws.services.redshift.AmazonRedshiftClient;
import com.amazonaws.services.redshift.model.AuthorizeClusterSecurityGroupIngressRequest;
import com.amazonaws.services.redshift.model.Cluster;
import com.amazonaws.services.redshift.model.ClusterSecurityGroup;
import com.amazonaws.services.redshift.model.ClusterSecurityGroupMembership;
import com.amazonaws.services.redshift.model.ClusterSecurityGroupNotFoundException;
import com.amazonaws.services.redshift.model.CreateClusterSecurityGroupRequest;
import com.amazonaws.services.redshift.model.DescribeClusterSecurityGroupsRequest;
import com.amazonaws.services.redshift.model.DescribeClusterSecurityGroupsResult;
import com.amazonaws.services.redshift.model.DescribeClustersRequest;
import com.amazonaws.services.redshift.model.DescribeClustersResult;
import com.amazonaws.services.redshift.model.ModifyClusterRequest;
import com.amazonaws.services.redshift.model.RevokeClusterSecurityGroupIngressRequest;
import com.jaspersoft.jasperserver.api.JSAwsDataSourceRecoveryException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

//import com.amazonaws.services.redshift.AmazonRedshiftClient;
//import com.amazonaws.services.redshift.model.*;

/**
 * @author vsabadosh
 */
public class AwsDataSourceRecovery {

    public static final String RDS = "rds";
    public static final String EC2 = "ec2";
    public static final String Redshift = "redshift";

    private List<String> awsRegions;
    private String awsDataSourceActiveStatus;
    private AwsProperties awsProperties;

    private AwsEc2MetadataClient awsEc2MetadataClient;
    private AwsCredentialUtil awsCredentialUtil;

    public static String DB_VPC_ID = "DB_VPC_ID";
    public static String DB_REGION = "DB_REGION";
    
    //"/32"(e.g. 192.168.1.92/32) at the end of public ip indicate that only this single ip have access.
    public static String ingressIpPermission = "/32";

    private  final Log logger = LogFactory.getLog(AwsDataSourceRecovery.class);

    MessageSource messageSource;

    /**
     * Default Constructor
     */
    public AwsDataSourceRecovery() {
    }

    public void createAwsDSSecurityGroup(AwsReportDataSource awsReportDataSource) {
        // If securityGroupChangesEnabled is true and dbInstanceIdentifier is not included in recovery cache than
        // creates db security group for target RDS/Redshift instance.
        if (awsProperties.isSecurityGroupChangesEnabled()) {
            logger.debug("Starting Recovery "+ awsReportDataSource.getDbService() +" " +
                    awsReportDataSource.getDbInstanceIdentifier() + " instance");
            try {
                if (awsReportDataSource.getDbService().equals(RDS)) {
                    createRDSSecurityGroup(awsReportDataSource);
                } else if (awsReportDataSource.getDbService().equals(Redshift)) {
                    createRedshiftSecurityGroup(awsReportDataSource);
                }
            } catch (Exception ex) {
                String errorMessage = messageSource.getMessage("aws.exception.datasource.recovery.base",
                        new String[]{awsReportDataSource.getName() != null ? awsReportDataSource.getURIString() : ""},
                        LocaleContextHolder.getLocale()) + "\n" + ex.getMessage();
                logger.error(errorMessage);
                throw new JSAwsDataSourceRecoveryException(errorMessage);
            }
        }
    }

    private void createRDSSecurityGroup(AwsReportDataSource awsReportDataSource) throws Exception {
        AWSCredentials awsCredentials = awsCredentialUtil.getAWSCredentials(awsReportDataSource.getAWSAccessKey(),
                awsReportDataSource.getAWSSecretKey(), awsReportDataSource.getRoleARN());

        AmazonRDSClient rdsClient = new AmazonRDSClient(awsCredentials);
        DescribeDBInstancesRequest describeDBInstancesRequest = new DescribeDBInstancesRequest().
                withDBInstanceIdentifier(awsReportDataSource.getDbInstanceIdentifier());
        String endpoint = awsReportDataSource.getAWSRegion();
        if (endpoint != null) {
            rdsClient.setEndpoint(RDS + "." + endpoint);
        }
        DBInstance dbInstance;
        DescribeDBInstancesResult describeInstancesResult = rdsClient.describeDBInstances(describeDBInstancesRequest);
        if (describeInstancesResult != null && describeInstancesResult.getDBInstances() != null &&
                describeInstancesResult.getDBInstances().size() > 0) {
            dbInstance = describeInstancesResult.getDBInstances().get(0);
            if (!dbInstance.getDBInstanceStatus().equals(awsDataSourceActiveStatus)) {
                throw new JSException(messageSource.getMessage("aws.exception.datasource.recovery.instance.not.active"
                        , null, LocaleContextHolder.getLocale()));
            }
            Map<String, String> awsDSInstanceDetails = new HashMap<String, String>();
            awsDSInstanceDetails.put(DB_REGION, parseRegionFromSubRegion(dbInstance.getAvailabilityZone()));
            DBSubnetGroup dbSubnetGroup = dbInstance.getDBSubnetGroup();
            if (dbSubnetGroup != null) {
                awsDSInstanceDetails.put(DB_VPC_ID, dbSubnetGroup.getVpcId());
            } else {
                awsDSInstanceDetails.put(DB_VPC_ID, null);
            }

            String instanceSourceIp = determineSourceIpAddress(awsDSInstanceDetails);

            if (!isNotEmpty(instanceSourceIp)) {
                throw new JSException(getErrorMessage("aws.exception.datasource.recovery.public.ip.not.determined"));
            }

            //IP that should be added in CIDRIP of JS DB Security Group
            String ingressIpMask = instanceSourceIp + ingressIpPermission;

            String vpcSecurityGroupId = null;
            if (awsDSInstanceDetails.get(DB_VPC_ID) != null) {
                //Recover VPC Security Group.
                vpcSecurityGroupId = recoverVpcSecurityGroup(awsReportDataSource, awsDSInstanceDetails.get(DB_VPC_ID), ingressIpMask);
            } else {
                //Recover Db Security Group
                //Fount existing JS DB Security Group
                Boolean jsSecurityGroupMembershipFount = true;

                DBSecurityGroup dbSecurityGroup = null;
                try {
                    DescribeDBSecurityGroupsRequest describeDBSecurityGroupsRequest =
                            new DescribeDBSecurityGroupsRequest().withDBSecurityGroupName(awsProperties.getSecurityGroupName());
                    DescribeDBSecurityGroupsResult describeDBSecurityGroupsResult =
                            rdsClient.describeDBSecurityGroups(describeDBSecurityGroupsRequest);
                    dbSecurityGroup = describeDBSecurityGroupsResult.getDBSecurityGroups().get(0);
                }  catch (DBSecurityGroupNotFoundException ex) {
                    jsSecurityGroupMembershipFount = false;
                }

                boolean ingressIpMaskExist = false;
                if (jsSecurityGroupMembershipFount) {
                    List<IPRange> ipRanges = dbSecurityGroup.getIPRanges();
                    for (IPRange ipRange : ipRanges) {
                        if (ipRange.getCIDRIP().contains(ingressIpMask)) {
                            ingressIpMaskExist = true;
                            break;
                        }
                    }
                    if (!ingressIpMaskExist) {
                        //Remove old ingress Ips
                        for (IPRange ipRange : ipRanges) {
                            RevokeDBSecurityGroupIngressRequest revokeDBSecurityGroupIngressRequest =
                                    new RevokeDBSecurityGroupIngressRequest().withDBSecurityGroupName(awsProperties.getSecurityGroupName())
                                            .withCIDRIP(ipRange.getCIDRIP());
                            rdsClient.revokeDBSecurityGroupIngress(revokeDBSecurityGroupIngressRequest);
                        }
                    }
                } else {
                    dbSecurityGroup = rdsClient.createDBSecurityGroup(new CreateDBSecurityGroupRequest()
                            .withDBSecurityGroupName(awsProperties.getSecurityGroupName())
                            .withDBSecurityGroupDescription(awsProperties.getSecurityGroupDescription()));
                }
                //Authorize new ingress Ip
                if (!ingressIpMaskExist) {
                    rdsClient.authorizeDBSecurityGroupIngress(new AuthorizeDBSecurityGroupIngressRequest()
                            .withDBSecurityGroupName(dbSecurityGroup.getDBSecurityGroupName())
                            .withCIDRIP(ingressIpMask));
                }
            }

            if (vpcSecurityGroupId == null) {
                List<DBSecurityGroupMembership> dbSecurityGroupMemberships = dbInstance.getDBSecurityGroups();
                List<String> dbSecurityGroupNames = new ArrayList<String>();
                for (DBSecurityGroupMembership dbSecurityGroupMembership : dbSecurityGroupMemberships) {
                    dbSecurityGroupNames.add(dbSecurityGroupMembership.getDBSecurityGroupName());
                }
                //If RDS Instance does not contain JSSecurityGroup that we should assign it to.
                if (!dbSecurityGroupNames.contains(awsProperties.getSecurityGroupName())) {
                    dbSecurityGroupNames.add(awsProperties.getSecurityGroupName());
                    ModifyDBInstanceRequest modifyDBInstanceRequest = new ModifyDBInstanceRequest().
                            withDBSecurityGroups(dbSecurityGroupNames).
                            withDBInstanceIdentifier(dbInstance.getDBInstanceIdentifier());
                    modifyDBInstanceRequest.setApplyImmediately(true);
                    rdsClient.modifyDBInstance(modifyDBInstanceRequest);
                }
            } else {
                List<VpcSecurityGroupMembership> vpcSecurityGroupMemberships = dbInstance.getVpcSecurityGroups();
                List<String> vpcSecurityGroupIds = new ArrayList<String>();
                for (VpcSecurityGroupMembership vpcSecurityGroupMembership : vpcSecurityGroupMemberships) {
                    vpcSecurityGroupIds.add(vpcSecurityGroupMembership.getVpcSecurityGroupId());
                }
                //If RDS Instance does not contain VPC Security Group that we should assign it to.
                if (!vpcSecurityGroupIds.contains(vpcSecurityGroupId)) {
                    vpcSecurityGroupIds.add(vpcSecurityGroupId);
                    ModifyDBInstanceRequest modifyDBInstanceRequest = new ModifyDBInstanceRequest().
                            withVpcSecurityGroupIds(vpcSecurityGroupIds).
                            withDBInstanceIdentifier(dbInstance.getDBInstanceIdentifier());
                    modifyDBInstanceRequest.setApplyImmediately(true);
                    rdsClient.modifyDBInstance(modifyDBInstanceRequest);
                }
            }
        }
    }

    private void createRedshiftSecurityGroup(AwsReportDataSource awsReportDataSource) throws Exception {

        AWSCredentials awsCredentials = awsCredentialUtil.getAWSCredentials(awsReportDataSource.getAWSAccessKey(),
                awsReportDataSource.getAWSSecretKey(), awsReportDataSource.getRoleARN());

        AmazonRedshiftClient redshiftClient = new AmazonRedshiftClient(awsCredentials);
        DescribeClustersRequest describeClustersRequest = new DescribeClustersRequest().
                withClusterIdentifier(awsReportDataSource.getDbInstanceIdentifier());
        String endpoint = awsReportDataSource.getAWSRegion();
        if (endpoint != null) {
            redshiftClient.setEndpoint(Redshift + "." + endpoint);
        }
        Cluster cluster;
        DescribeClustersResult describeClustersResult = redshiftClient.describeClusters(describeClustersRequest);
        if (describeClustersResult != null && describeClustersResult.getClusters() != null &&
                describeClustersResult.getClusters().size() > 0) {
            cluster = describeClustersResult.getClusters().get(0);
            if (!cluster.getClusterStatus().equals(awsDataSourceActiveStatus)) {
                throw new JSException(getErrorMessage("aws.exception.datasource.recovery.instance.not.active"));
            }
            Map<String, String> awsDSInstanceDetails = new HashMap<String, String>();
            awsDSInstanceDetails.put(DB_REGION, parseRegionFromSubRegion(cluster.getAvailabilityZone()));
            String vpcId = cluster.getVpcId();
            if (isNotEmpty(vpcId)) {
                awsDSInstanceDetails.put(DB_VPC_ID, vpcId);
            } else {
                awsDSInstanceDetails.put(DB_VPC_ID, null);
            }

            String instanceSourceIp = determineSourceIpAddress(awsDSInstanceDetails);

            if (!isNotEmpty(instanceSourceIp)) {
                throw new JSException(getErrorMessage("aws.exception.datasource.recovery.public.ip.not.determined"));
            }
            //IP that should be added in CIDRIP of JS DB Security Group
            String ingressIpMask = instanceSourceIp + ingressIpPermission;

            String vpcSecurityGroupId = null;
            if (awsDSInstanceDetails.get(DB_VPC_ID) != null) {
                //Recover VPC Security Group.
                vpcSecurityGroupId = recoverVpcSecurityGroup(awsReportDataSource, awsDSInstanceDetails.get(DB_VPC_ID), ingressIpMask);
            } else {
                //Recover Cluster Security Group.

                //Fount existing JS DB Security Group
                Boolean jsSecurityGroupMembershipFount = true;

                ClusterSecurityGroup clusterSecurityGroup = null;
                try {
                    DescribeClusterSecurityGroupsRequest describeClusterSecurityGroupsRequest =
                            new DescribeClusterSecurityGroupsRequest().withClusterSecurityGroupName(awsProperties.getSecurityGroupName());
                    DescribeClusterSecurityGroupsResult describeClusterSecurityGroupsResult =
                            redshiftClient.describeClusterSecurityGroups(describeClusterSecurityGroupsRequest);
                    clusterSecurityGroup = describeClusterSecurityGroupsResult.getClusterSecurityGroups().get(0);
                }  catch (ClusterSecurityGroupNotFoundException ex) {
                    jsSecurityGroupMembershipFount = false;
                }

                boolean ingressIpMaskExist = false;
                if (jsSecurityGroupMembershipFount) {
                    List<com.amazonaws.services.redshift.model.IPRange> ipRanges = clusterSecurityGroup.getIPRanges();
                    for (com.amazonaws.services.redshift.model.IPRange ipRange : ipRanges) {
                        if (ipRange.getCIDRIP().contains(ingressIpMask)) {
                            ingressIpMaskExist = true;
                            break;
                        }
                    }
                    if (!ingressIpMaskExist) {
                        //Remove old ingress Ips
                        for (com.amazonaws.services.redshift.model.IPRange ipRange : ipRanges) {
                            RevokeClusterSecurityGroupIngressRequest revokeClusterSecurityGroupIngressRequest =
                                    new RevokeClusterSecurityGroupIngressRequest().withClusterSecurityGroupName(awsProperties.getSecurityGroupName())
                                            .withCIDRIP(ipRange.getCIDRIP());
                            redshiftClient.revokeClusterSecurityGroupIngress(revokeClusterSecurityGroupIngressRequest);
                        }
                    }
                } else {
                    clusterSecurityGroup = redshiftClient.createClusterSecurityGroup(new CreateClusterSecurityGroupRequest()
                            .withClusterSecurityGroupName(awsProperties.getSecurityGroupName())
                            .withDescription(awsProperties.getSecurityGroupDescription()));
                }
                if (!ingressIpMaskExist) {
                    redshiftClient.authorizeClusterSecurityGroupIngress(new AuthorizeClusterSecurityGroupIngressRequest()
                            .withClusterSecurityGroupName(clusterSecurityGroup.getClusterSecurityGroupName())
                            .withCIDRIP(ingressIpMask));
                }
            }
            if (vpcSecurityGroupId == null) {
                List<ClusterSecurityGroupMembership> clusterSecurityGroupMemberships = cluster.getClusterSecurityGroups();
                List<String> clusterSecurityGroupNames = new ArrayList<String>();
                for (ClusterSecurityGroupMembership clusterSecurityGroupMembership : clusterSecurityGroupMemberships) {
                    clusterSecurityGroupNames.add(clusterSecurityGroupMembership.getClusterSecurityGroupName());
                }
                //If Redshift Instance does not contain JSSecurityGroup that we should assign it to.
                if (!clusterSecurityGroupNames.contains(awsProperties.getSecurityGroupName())) {
                    clusterSecurityGroupNames.add(awsProperties.getSecurityGroupName());
                    ModifyClusterRequest modifyClusterRequest = new ModifyClusterRequest().
                            withClusterSecurityGroups(clusterSecurityGroupNames).
                            withClusterIdentifier(cluster.getClusterIdentifier());
                    redshiftClient.modifyCluster(modifyClusterRequest);
                }
            } else {
                List<com.amazonaws.services.redshift.model.VpcSecurityGroupMembership> vpcSecurityGroupMemberships = cluster.getVpcSecurityGroups();
                List<String> vpcSecurityGroupIds = new ArrayList<String>();
                for (com.amazonaws.services.redshift.model.VpcSecurityGroupMembership vpcSecurityGroupMembership : vpcSecurityGroupMemberships) {
                    vpcSecurityGroupIds.add(vpcSecurityGroupMembership.getVpcSecurityGroupId());
                }
                //If Redshift Instance does not contain VPC Security Group that we should assign it to.
                if (!vpcSecurityGroupIds.contains(vpcSecurityGroupId)) {
                    vpcSecurityGroupIds.add(vpcSecurityGroupId);
                    ModifyClusterRequest modifyClusterRequest = new ModifyClusterRequest().
                            withVpcSecurityGroupIds(vpcSecurityGroupIds).
                            withClusterIdentifier(cluster.getClusterIdentifier());
                    redshiftClient.modifyCluster(modifyClusterRequest);
                }
            }
        }
    }

    private String determineSourceIpAddress(Map<String, String> awsDSInstanceDetails) throws Exception {
        logger.debug("Determine source Ip address for AWS data source recovery");

        String awsDSInstanceVpcId = awsDSInstanceDetails.get(DB_VPC_ID);

        if (awsEc2MetadataClient.isEc2Instance()) {
            //So, JRS application is in EC2 instance.
            String ec2InstanceRegion = parseRegionFromSubRegion(awsEc2MetadataClient.
                    getEc2InstanceMetadataItem(AwsEc2MetadataClient.REGION_RESOURCE));
            String awsDSInstanceRegion = awsDSInstanceDetails.get(DB_REGION);

            if (awsEc2MetadataClient.isEc2InstanceInTargetVpc(awsDSInstanceVpcId)) {
                //We could provide private ip, if we are in the same VPC and outside in the one Region.
                if (ec2InstanceRegion.equals(awsDSInstanceRegion)) {
                    return getIpAddress(AwsEc2MetadataClient.PRIVATE_IP_RESOURCE);
                } else {
                    return getIpAddress(AwsEc2MetadataClient.PUBLIC_IP_RESOURCE);
                }
            } else if (awsEc2MetadataClient.isEc2InstanceInVpc() && awsDSInstanceVpcId == null) {
                return getIpAddress(AwsEc2MetadataClient.PUBLIC_IP_RESOURCE);
            } else {
                throw new JSException(getErrorMessage("aws.exception.datasource.recovery.is.not.possible"));
            }
        } else {
            if (awsDSInstanceVpcId != null) {
                throw new JSException(getErrorMessage("aws.exception.datasource.recovery.is.not.possible"));
            } else {
                return getIpAddress(null);
            }
        }
    }
    
    private String getIpAddress(String source) {
        if (!isEmpty(awsProperties.getSecurityGroupIngressPublicIp())) {
            return awsProperties.getSecurityGroupIngressPublicIp();
        }

        if (isNotEmpty(source)) {
            return awsEc2MetadataClient.getEc2InstanceMetadataItem(source);
        } else {
            throw new JSException(getErrorMessage("aws.exception.datasource.recovery.public.ip.not.provided"));
        }
    }
    
    private String recoverVpcSecurityGroup(AwsReportDataSource awsReportDataSource, String vpcId, String ingressPublicIp) {
        AWSCredentials awsCredentials = awsCredentialUtil.getAWSCredentials(awsReportDataSource.getAWSAccessKey(),
                awsReportDataSource.getAWSSecretKey(), awsReportDataSource.getRoleARN());
        //Security
        AmazonEC2Client amazonEc2Client = new AmazonEC2Client(awsCredentials);
        String endpoint = awsReportDataSource.getAWSRegion();
        if (endpoint != null) {
            amazonEc2Client.setEndpoint(EC2 + "." + endpoint);
        }

        SecurityGroup vpcSecurityGroup = null;
        try {
            DescribeSecurityGroupsResult describeSecurityGroupsResult =
                    amazonEc2Client.describeSecurityGroups();
            if (describeSecurityGroupsResult != null && describeSecurityGroupsResult.getSecurityGroups() !=null && 
                    describeSecurityGroupsResult.getSecurityGroups().size() > 0) {
                for (SecurityGroup securityGroup : describeSecurityGroupsResult.getSecurityGroups()) {
                    if (securityGroup.getVpcId() != null && securityGroup.getVpcId().equals(vpcId) &&
                            securityGroup.getGroupName().equals(awsProperties.getSecurityGroupName())) {
                        vpcSecurityGroup = securityGroup;
                        break;
                    }
                }
            }
        }  catch (Exception ex) {
            //Have to be empty.
        }

        boolean ingressIpMaskExist = false;
        String vpcSecurityGroupId;
        if (vpcSecurityGroup != null) {
            vpcSecurityGroupId = vpcSecurityGroup.getGroupId();

            List<IpPermission> ipPermissions = vpcSecurityGroup.getIpPermissions();
            if (ipPermissions != null && ipPermissions.size() > 0) {
                for (IpPermission ipPermission : ipPermissions) {
                    if (ipPermission.getIpRanges() != null && ipPermission.getIpRanges().size() > 0 &&
                            ipPermission.getIpRanges().contains(ingressPublicIp)) {
                        ingressIpMaskExist = true;
                    }
                }
            }
            if (!ingressIpMaskExist && ipPermissions != null && ipPermissions.size() > 0) {
                RevokeSecurityGroupIngressRequest revokeSecurityGroupIngressRequest =
                        new RevokeSecurityGroupIngressRequest().withGroupId(vpcSecurityGroup.getGroupId()).withIpPermissions()
                                .withIpPermissions(vpcSecurityGroup.getIpPermissions());
                amazonEc2Client.revokeSecurityGroupIngress(revokeSecurityGroupIngressRequest);
            }
        } else {
            vpcSecurityGroupId = amazonEc2Client.createSecurityGroup(new CreateSecurityGroupRequest()
                    .withGroupName(awsProperties.getSecurityGroupName()).withVpcId(vpcId).withDescription(awsProperties.getSecurityGroupDescription())).getGroupId();
        }

        if (!ingressIpMaskExist) {
            IpPermission ipPermission = new IpPermission().withIpProtocol("tcp").
                    withIpRanges(ingressPublicIp).withFromPort(0).withToPort(65535);
            List<IpPermission> ipPermissions = new ArrayList<IpPermission>();
            ipPermissions.add(ipPermission);
            AuthorizeSecurityGroupIngressRequest authorizeRequest = new AuthorizeSecurityGroupIngressRequest().
                    withIpPermissions(ipPermissions).withGroupId(vpcSecurityGroupId);
            amazonEc2Client.authorizeSecurityGroupIngress(authorizeRequest);
        }
        
        return vpcSecurityGroupId;
    }
    
    private String getErrorMessage(String errorCode) {
        return messageSource.getMessage(errorCode, null, LocaleContextHolder.getLocale());
    }

    private String parseRegionFromSubRegion(String region) {
        String regionPrefix = ".amazonaws.com";
        if (region != null) {
            for (String awsRegion : awsRegions) {
                String[] parse = awsRegion.split(regionPrefix);
                if (region.contains(parse[0])) {
                    return awsRegion;
                }
            }
        }
        return null;
    }

    public void setAwsRegions(List<String> awsRegions) {
        this.awsRegions = awsRegions;
    }

    public void setAwsDataSourceActiveStatus(String awsDataSourceActiveStatus) {
        this.awsDataSourceActiveStatus = awsDataSourceActiveStatus;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setAwsEc2MetadataClient(AwsEc2MetadataClient awsEc2MetadataClient) {
        this.awsEc2MetadataClient = awsEc2MetadataClient;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setAwsProperties(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    public void setAwsCredentialUtil(AwsCredentialUtil awsCredentialUtil) {
        this.awsCredentialUtil = awsCredentialUtil;
    }
}
