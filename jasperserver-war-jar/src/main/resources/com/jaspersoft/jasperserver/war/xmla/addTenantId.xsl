<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright © 2005 - 2018 TIBCO Software Inc.
  ~ http://www.jaspersoft.com.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <https://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
 xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
   xmlns:uu="urn:schemas-microsoft-com:xml-analysis"
>

		<!-- This transformation is used on XML/A SOAP requests,
		it will find the <DataSourceInfo/> element 
		and will append the TenentID=...; postfix to it. -->
		
        <xsl:output method="xml" indent="yes" />

		<!--  the 'organization_unknown_placeholder' is replaced to the real
		organization name before the transformation.
		the value variable holds the postfix we want to add to the datasource info-->
		
        <xsl:variable name="organization" select="'organization_unknown_placeholder'" />
        <xsl:variable name="key" select="'TenantID='" />
        <xsl:variable name="value" select="concat($key,$organization,';')" />

		<!-- This template applies to the text within the <DataSourceInfo/> element.
		It will first apply the default template which is to copy the content
		and then it adds the value of the value variable -->
        
        <xsl:template match="uu:DataSourceInfo/text()">
                <xsl:copy>
                        <xsl:apply-templates />
                </xsl:copy>
                <xsl:value-of select="$value" />
        </xsl:template>

		<!-- by default all nodes are just copied from input to output -->
        
        <xsl:template match="@* | node()">
                <xsl:copy>
                        <xsl:apply-templates />
                </xsl:copy>
        </xsl:template>

</xsl:stylesheet>
