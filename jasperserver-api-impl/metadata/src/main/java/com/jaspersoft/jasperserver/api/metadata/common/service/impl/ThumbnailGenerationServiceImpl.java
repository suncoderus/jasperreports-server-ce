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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.service.ThumbnailGenerationService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReportsContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author Grant Bacon <gbacon@jaspersoft.com>
 * @version
 */

public class ThumbnailGenerationServiceImpl implements ThumbnailGenerationService {

    protected static final Log log = LogFactory.getLog(ThumbnailGenerationService.class);

    public static final String IMAGEWRITE_TYPE_JPG = "jpeg";
    public static final int THUMBNAIL_PAGE_INDEX = 0;

    /**
     * A value in pixels representing the longest edge of the expected thumbnail
     */
    private int longestEdge;
    /**
     * A value between 0 and 1 (inclusive) which defines the quality you wish jpg images to be.
     */
    private float jpgQuality;

    public ByteArrayOutputStream createThumbnail(JasperPrint jasperPrint, JasperReportsContext jasperReportsContext) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage thumbnail;
        float reportWidth = (float) jasperPrint.getPageWidth();
        float reportHeight = (float) jasperPrint.getPageHeight();

        float zoomRatio;

        try {
            zoomRatio = (longestEdge - 1) / Math.max(reportWidth, reportHeight);
            thumbnail = obtainImage(jasperPrint, jasperReportsContext, zoomRatio);
            if ((jpgQuality >= 0 && jpgQuality <= 1)) {
                ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(IMAGEWRITE_TYPE_JPG).next(); // grab the first image writer for jpeg files
                ImageWriteParam writerParam = imageWriter.getDefaultWriteParam();
                writerParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writerParam.setCompressionQuality(jpgQuality);
                imageWriter.setOutput(ImageIO.createImageOutputStream(byteArrayOutputStream));
                imageWriter.write(null, new IIOImage(thumbnail, null, null), writerParam);
                imageWriter.dispose();
            } else {
                // Invalid jpgQuality set, use ImageIO's default quality setting
                ImageIO.write(thumbnail, IMAGEWRITE_TYPE_JPG, byteArrayOutputStream);
            }
        } catch (IOException e) {
            log.error(e, e);
        }
        return byteArrayOutputStream;
    }

    protected BufferedImage obtainImage(JasperPrint jrPrint, JasperReportsContext context, float zoomRatio) {
        try {
            return (BufferedImage) JasperPrintManager.getInstance(context).printToImage(jrPrint, THUMBNAIL_PAGE_INDEX, zoomRatio);
        } catch (JRException e) {
            log.error(e, e);
            throw new JSException("jsexception.error.generating.image.from.report");
        }
    }

    public int getLongestEdge() {
        return longestEdge;
    }

    public void setLongestEdge(int longestEdge) {
        this.longestEdge = longestEdge;
    }

    public float getJpgQuality() {
        return jpgQuality;
    }

    public void setJpgQuality(float jpgQuality) {
        this.jpgQuality = jpgQuality;
    }
}
