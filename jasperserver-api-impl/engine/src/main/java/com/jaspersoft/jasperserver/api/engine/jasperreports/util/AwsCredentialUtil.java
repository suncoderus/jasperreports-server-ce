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

import com.amazonaws.auth.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.jaspersoft.jasperserver.api.JSShowOnlyErrorMessage;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * @author vsabadosh
 */
public class AwsCredentialUtil {
    private static final Log logger = LogFactory.getLog(AwsCredentialUtil.class);

    private MessageSource messageSource;

    public AWSCredentials getAWSCredentials(String awsAccessKey, String awsSecretKey, String roleARN) {
        AWSCredentials awsCredentials;
        if (isNotEmpty(awsAccessKey) && isNotEmpty(awsSecretKey)) {
            awsCredentials = new BasicAWSCredentials(awsAccessKey.trim(), awsSecretKey.trim());

            // Use user long-term credentials to call the
            // AWS Security Token Service (STS) AssumeRole API, specifying
            // the ARN for the role -RO-role in amazon account.
            if (isNotEmpty(roleARN)) {
                AWSSecurityTokenServiceClient stsClient = new
                        AWSSecurityTokenServiceClient(awsCredentials);

                AssumeRoleRequest assumeRequest = new AssumeRoleRequest()
                        .withRoleArn(roleARN.trim())
                        .withRoleSessionName("JRSRequest");

                AssumeRoleResult assumeResult = null;
                try {
                    assumeResult = stsClient.assumeRole(assumeRequest);
                } catch (Exception ex) {
                    logger.error(ex);
                    throw new JSShowOnlyErrorMessage(ex.getMessage());
                }

                // AssumeRole returns temporary security credentials for
                // the IAM role.
                awsCredentials = new BasicSessionCredentials(
                        assumeResult.getCredentials().getAccessKeyId(),
                        assumeResult.getCredentials().getSecretAccessKey(),
                        assumeResult.getCredentials().getSessionToken());
            }
        } else {
            //Try getting Ec2 instance credentials.
            AWSCredentialsProvider instanceCredentialsProvider = new DefaultAWSCredentialsProviderChain();
            try {
                awsCredentials = instanceCredentialsProvider.getCredentials();
            } catch (Exception ex) {
                logger.error("Exception loading default JRS instance credentials", ex);
                throw new JSShowOnlyErrorMessage(messageSource.getMessage("aws.exception.datasource.load.default.credentials",
                        null, LocaleContextHolder.getLocale()));
            }
        }

        return awsCredentials;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
