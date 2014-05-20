<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  	<xsl:output method="html" />
	<xsl:template match ="positions">
	    <table>
	    <tr>
	      <th align ="left">No.</th>
	      <th align ="left">ID</th>
	      <th align ="left">Amount1 Type</th>
	      <th align ="left">Amount1 Symbol</th>
	      <th align ="left">Amount1</th>
	      <th align ="left">Amount2 Type</th>
	      <th align ="left">Amount2 Symbol</th>
	      <th align ="left">Amount2</th>
	    </tr>
	    <xsl:for-each select ="position">
	    	<xsl:sort select="@id" />    
		      <tr>
		        <td>
		          <xsl:number/>
		        </td>
		        <td>
		          <xsl:value-of select ="@id"/>
		        </td>
		        <xsl:for-each select="amounts/amount">
		        	<td>
		        		<xsl:value-of select="type"/>
		        	</td>
		        	<td>
		        		<xsl:value-of select="symbol"/>
		        	</td>
		        	<td>
		        		<xsl:value-of select="value"/>
		        	</td>
		        </xsl:for-each>
		      </tr>    
	    </xsl:for-each>
	    </table>
	</xsl:template>
</xsl:stylesheet>