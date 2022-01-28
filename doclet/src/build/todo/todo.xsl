<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="text" />
	<xsl:preserve-space elements="text"/>
	
	<xsl:template match="document" >
		<xsl:apply-templates select="body" />
	</xsl:template>
	
	<xsl:template match="body" >
		<xsl:apply-templates select="section" />
	</xsl:template>
	
	<xsl:template match="section">
		<text>TODO</text>
		<text>&#10;====&#10;&#10;</text>
		<xsl:apply-templates select="subsection" />
    </xsl:template>
	
	<xsl:template match="subsection">
        <text>&#10;&#10;<xsl:value-of select="@name"/>
		</text>
		<text>&#10;--------------------------------------------------------------------------------
		</text>
		<xsl:apply-templates select="ul" />
		<text>&#10;
		</text>
    </xsl:template>

	
	<xsl:template match="ul">
		<xsl:apply-templates select="li" />
	</xsl:template>
	
	<xsl:template match="li">
		<xsl:value-of select="."/>
	</xsl:template>


	<xsl:template match="author" />
	<xsl:template match="title" />
	<xsl:template match="meta" />
	<xsl:template match="properties" />
	
</xsl:stylesheet>