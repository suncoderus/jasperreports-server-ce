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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import net.sf.jasperreports.engine.JasperPrint;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.PartialMock;

import java.awt.image.BufferedImage;


public class ThumbnailGeneratorTest extends UnitilsJUnit4 {

    @TestedObject
    private PartialMock<ThumbnailGenerationServiceImpl> generationService;

    private JasperPrint jasperPrint = new JasperPrint();
    private BufferedImage fakeImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

    final int longestEdge = 300;
    final int pageHeight = 768;
    final int pageWidth = 1024;
    final float expectedZoomRatio = 299 / 1024f; // (longestEdge - 1) / max(width, height)


    @Before
    public void before() {

        // Setup JasperPrint
        jasperPrint.setPageHeight(pageHeight);
        jasperPrint.setPageWidth(pageWidth);

        // Setup ThumbnailGenerationService
        generationService.getMock().setLongestEdge(longestEdge);
        generationService.getMock().setJpgQuality(0.8f);
        generationService.returns(fakeImage).obtainImage(jasperPrint, null, expectedZoomRatio);
    }

    /**
     * This test ensures that the zoom ratio is properly calculated and used when obtaining a JasperPrint image
     */
    @Test
    public void zoomRatioCalculatedProperlyTest() {
        generationService.getMock().createThumbnail(jasperPrint, null);

        generationService.assertInvoked().createThumbnail(jasperPrint, null);
        generationService.assertInvoked().obtainImage(jasperPrint, null, expectedZoomRatio);
    }
}