<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 

<xsl:template match="/">
  <html>
  <body>
  <h2>Fragekatalog: <xsl:value-of select="/Fragenkatalog/@name"/></h2>  
<table border="1">
    <tr bgcolor="#9acd32">
      <th>Frage</th>
      <th>Antwort 1 (richtig)</th>
	  <th>Antwort 2</th>
	  <th>Antwort 3</th>
	  <th>Antwort 4</th>
    </tr>
    <xsl:for-each select="/Fragenkatalog/Frage">
    <tr>
      <td><xsl:value-of select="Fragetext"/></td>
      <td><xsl:value-of select="Antwort[1]"/> </td>
	  <td><xsl:value-of select="Antwort[2]"/> </td>
      <td><xsl:value-of select="Antwort[3]"/> </td>
      <td><xsl:value-of select="Antwort[4]"/> </td>
    </tr>
    </xsl:for-each>
  </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>