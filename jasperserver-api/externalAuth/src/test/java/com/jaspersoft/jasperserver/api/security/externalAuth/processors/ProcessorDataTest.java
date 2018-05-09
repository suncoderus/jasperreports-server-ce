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

import org.junit.Before;
import org.junit.Test;

import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_JRS_USER_TENANT_ID;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * User: dlitvak
 * Date: 3/22/13
 */
public class ProcessorDataTest {
	public static final String TEST_TENANT = "testTenant";
	private ProcessorData processorData;

	@Before
	public void setUp() {
		processorData = ProcessorData.getInstance();
		processorData.clearData();

		assertNotNull("processorData is null", processorData);
		assertEquals("processorData is not singleton", processorData, ProcessorData.getInstance());
		assertNotNull("processorData map should not be null", processorData.getDataKeys());
		assertTrue("processorData map should empty", processorData.getDataKeys().isEmpty());
	}

	@Test
	public void testAddRemoveData() throws Exception {
		processorData.addData(EXTERNAL_JRS_USER_TENANT_ID, TEST_TENANT);
		assertEquals("Added processorData is not the same as the one later retrieved", processorData.getData(EXTERNAL_JRS_USER_TENANT_ID), TEST_TENANT);

		assertEquals("processorData map should have size 1", processorData.getDataKeys().size(), 1);
	}

	@Test
	public void testMultiThreadProcessorData() {
		processorData.addData(EXTERNAL_JRS_USER_TENANT_ID, TEST_TENANT);
		assertEquals("Added processorData is not the same as the one later retrieved", processorData.getData(EXTERNAL_JRS_USER_TENANT_ID), TEST_TENANT);

		new Thread(new Runnable() {
			@Override
			public void run() {
				assertNull("Added processorData on one thread should not be accessible from a different thread.", ProcessorData.getInstance().getData(EXTERNAL_JRS_USER_TENANT_ID));
			}
		}).start();
	}
}
