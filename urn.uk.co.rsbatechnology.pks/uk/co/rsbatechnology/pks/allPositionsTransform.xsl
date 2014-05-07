<!--
  XSLT2 transform for positions active:allPositions endpoint 

  Expects an operand of the form:
  
  <set>
  	<identifier>pds:/pks/pos/...</identifier>
  	<identifier>pds:/pks/pos/...</identifier>
  </set>
  
  Transforms into a XRL document of form:
  
  <positions>
	<xrl:include>
		<xrl:identifier>pds:/pks/pos/...</xrl:identifier>
		<xrl:async>true</xrl:async>
	</xrl:include>
	<xrl:include>
		<xrl:identifier>pds:/pks/pos/...</xrl:identifier>
		<xrl:async>true</xrl:async>
	</xrl:include>
  </positions>
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xrl="http://netkernel.org/xrl" version="2.0">
  <xsl:output method="xml" />
  <xsl:template match="/set">
  <positions>
  <xsl:for-each select="identifier">
  	<xrl:include>
  		<xrl:identifier><xsl:value-of select="." /></xrl:identifier>
  		<xrl:async>true</xrl:async>
  	</xrl:include>
  </xsl:for-each>
  </positions>
  </xsl:template>
</xsl:stylesheet>

