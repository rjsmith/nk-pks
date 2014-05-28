<!-- 
	XSLT2 transform from list of builder endpoints to a DynamicImport config xml document
 -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" />
  <xsl:template match="/builderEndpoints">
  <config>
  <xsl:for-each select="builderEndpoint">
  	<space>
  		<uri><xsl:value-of select="space" /></uri>
  	</space>
  </xsl:for-each>
  </config>
  </xsl:template>
</xsl:stylesheet>
