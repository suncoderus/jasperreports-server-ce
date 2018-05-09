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

/**
 * @author Jun-Sun Whang
 * @version $Id$
 */
public class StyleInfo {

	private float firstLineLeading = 0;
	private int firstLineMaxFontSize = 0;
	private int fontSizeSum = 0;
	private float textHeight = 0;
	private int lines = 1;
	private int textOffset = 0;
	private boolean isLeftToRight = true;

	public float getFirstLineLeading() {
		return firstLineLeading;
	}

	public void setFirstLineLeading(float firstLineLeading) {
		this.firstLineLeading = firstLineLeading;
	}

	public int getFirstLineMaxFontSize() {
		return firstLineMaxFontSize;
	}

	public void setFirstLineMaxFontSize(int firstLineMaxFontSize) {
		this.firstLineMaxFontSize = firstLineMaxFontSize;
	}

	public int getFontSizeSum() {
		return fontSizeSum;
	}

	public void setFontSizeSum(int fontSizeSum) {
		this.fontSizeSum = fontSizeSum;
	}

	public boolean isLeftToRight() {
		return isLeftToRight;
	}

	public void setLeftToRight(boolean isLeftToRight) {
		this.isLeftToRight = isLeftToRight;
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public float getTextHeight() {
		return textHeight;
	}

	public void setTextHeight(float textHeight) {
		this.textHeight = textHeight;
	}

	public int getTextOffset() {
		return textOffset;
	}

	public void setTextOffset(int textOffset) {
		this.textOffset = textOffset;
	}

}