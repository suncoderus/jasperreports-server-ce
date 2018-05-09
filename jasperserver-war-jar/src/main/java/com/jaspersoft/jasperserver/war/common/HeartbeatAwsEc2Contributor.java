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

package com.jaspersoft.jasperserver.war.common;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsEc2MetadataClient;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.InstanceProductTypeResolver;


/**
 *
 * @author vsabadosh
 */
public class HeartbeatAwsEc2Contributor implements HeartbeatContributor {
    private AwsEc2MetadataClient awsEc2MetadataClient;
    private InstanceProductTypeResolver instanceProductTypeResolver;

    @Override
    public void contributeToHttpCall(HeartbeatCall call) {
        String awsEc2InstanceType;
        if (awsEc2MetadataClient.isEc2Instance()) {
            awsEc2InstanceType = awsEc2MetadataClient.getEc2InstanceMetadataItem(AwsEc2MetadataClient.INSTANCE_TYPE_RESOURCE);
            call.addParameter("ec2InstanceType", awsEc2InstanceType);
        }
        call.addParameter("ec2Instance", instanceProductTypeResolver.getAwsProductNameForHeartBeat());
    }
    


    public void setAwsEc2MetadataClient(AwsEc2MetadataClient awsEc2MetadataClient) {
        this.awsEc2MetadataClient = awsEc2MetadataClient;
    }

    public void setInstanceProductTypeResolver(InstanceProductTypeResolver instanceProductTypeResolver) {
        this.instanceProductTypeResolver = instanceProductTypeResolver;
    }
}
