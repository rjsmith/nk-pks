<!--
  XSLT2 transform for positions active:allPositionDeltas endpoint 

  Expects an operand of the form:
  
  <set>
  	<identifier>pds:/pks/delta/...</identifier>
  	<identifier>pds:/pks/delta/...</identifier>
  </set>
  
  Transforms into a XRL document of form:
  
  <positionDeltas>
	<xrl:include>
		<xrl:identifier>pds:/pks/delta/...</xrl:identifier>
		<xrl:async>true</xrl:async>
	</xrl:include>
	<xrl:include>
		<xrl:identifier>pds:/pks/delta/...</xrl:identifier>
		<xrl:async>true</xrl:async>
	</xrl:include>
  </positionDeltas>
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xrl="http://netkernel.org/xrl" version="2.0">
  <xsl:output method="xml" />
  <xsl:template match="/set">
  <positionDeltas>
  <xsl:for-each select="identifier">
  	<xrl:include>
  		<xrl:identifier><xsl:value-of select="." /></xrl:identifier>
  		<xrl:async>true</xrl:async>
  	</xrl:include>
  </xsl:for-each>
  </positionDeltas>
  </xsl:template>
</xsl:stylesheet>

