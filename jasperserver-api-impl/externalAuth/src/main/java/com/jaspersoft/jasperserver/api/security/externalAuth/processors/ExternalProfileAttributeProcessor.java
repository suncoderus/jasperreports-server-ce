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
package com.jaspersoft.jasperserver.api.security.externalAuth.processors;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetails;
import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_AUTH_DETAILS;

/**
 * User: dlitvak
 * Date: 12/23/13
 */
public class ExternalProfileAttributeProcessor extends AbstractExternalUserProcessor  {
	private final static Logger logger = LogManager.getLogger(ExternalProfileAttributeProcessor.class);
	public static final String ENC_PROFILE_ATTRIB_SUFFIX = "_ENC";

	/**
	 * Main processor method.
	 */
	@Override
	public void process() {
		logger.debug("Process profile attributes");
		ProcessorData processorData = ProcessorData.getInstance();
		UserDetails externalUserDetails = (UserDetails) processorData.getData(EXTERNAL_AUTH_DETAILS);
		if (externalUserDetails instanceof ExternalUserDetails) {
			Map<String, Object> addlDetailsMap = ((ExternalUserDetails)externalUserDetails).getAdditionalDetailsMap();
		Map<String, String> paMap = (Map<String, String>)addlDetailsMap.get(ExternalUserDetails.PROFILE_ATTRIBUTES_ADDITIONAL_MAP_KEY);

		final ProfileAttributeService profileAttributeService = getProfileAttributeService();
		List<ProfileAttribute> currentProfileAttribs = (List<ProfileAttribute>) profileAttributeService.getProfileAttributesForPrincipal();
		Map<String, ProfileAttribute> currPaMap = new HashMap<String, ProfileAttribute>();
		for (ProfileAttribute pa : currentProfileAttribs)
			currPaMap.put(pa.getAttrName(), pa);

		Key secret = null;
		for (Map.Entry<String, String> pair : paMap.entrySet()) {
			try {
				String attrName = pair.getKey();
				String attrValue = pair.getValue();
				if (attrName.endsWith(ENC_PROFILE_ATTRIB_SUFFIX)) {
					if (secret == null)
						secret = KeystoreManager.getInstance().getBuildKey();
					attrValue = EncryptionEngine.encrypt(secret, attrValue);
				}

				profileAttributeService.setCurrentUserPreferenceValue(attrName, attrValue);
				currPaMap.remove(attrName);
			} catch (Exception e) {
				logger.warn("Profile attribute " + pair.getValue() + " failed to be saved to the repository with error: " + e);
			}
		}

		//remove the keys not in the latest profile attribute update
		profileAttributeService.deleteProfileAttributes(currPaMap);
		}
	}
}
