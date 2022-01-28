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
	
	<xsl:template match="ul">
		<xsl:apply-templates select="li" />
	</xsl:template>
	
	<xsl:template match="li">
		<xsl:apply-templates select="a" />
	</xsl:template>
	
	<xsl:template match="a">
		<xsl:apply-templates select="problem" />
	</xsl:template>

	
	<xsl:template match="section">
		<xsl:value-of select="@name"/>
		<text>&#10;==========================
		</text>
		<text>&#10;Index</text>
		<text>&#10;-----&#10;</text>
		<xsl:apply-templates select="ul" />
		<text>&#10;&#10;&#10;Details</text>
		<text>&#10;-------&#10;</text>
		<xsl:apply-templates select="subsection" />
    </xsl:template>

	<xsl:template match="problem">
		<text>&#10;Question <xsl:number count="li" format="1"/>: <xsl:value-of select="."/></text>
	</xsl:template>

	
	<xsl:template match="subsection">
		<xsl:apply-templates select="solution" />
	</xsl:template>
	
	<xsl:template match="solution">
		<text>&#10;-----------------------------------------------------------------------------</text>
		<text>&#10;&#10;Question <xsl:number count="subsection" format="1"/>: <xsl:value-of select="@text"/></text>
		<xsl:apply-templates select="p" />
		<text>&#10;&#10;</text>
	</xsl:template>
	
	<xsl:template match="p">
		<text>&#10;&#10;Answer:&#10; <xsl:value-of select="."/></text>
	</xsl:template>

	<xsl:template match="author" />
	<xsl:template match="title" />
	<xsl:template match="meta" />
	<xsl:template match="properties" />
	
</xsl:stylesheet>