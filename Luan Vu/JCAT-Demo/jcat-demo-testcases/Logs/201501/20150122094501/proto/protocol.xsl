<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:java="java"
				xmlns:log4j="http://jakarta.apache.org/log4j/">

	<xsl:output method="html"
    cdata-section-elements="script style"
    indent="yes"/>
	


	<!--<xsl:template match="mydocument">
	  <html>
	    <head>
	      <title>Log</title>
	    </head>
	    <body>
	      <table>
	        <tbody>
	          <xsl:for-each select="log4j:event">
	            <tr>
	              <th>
	                <xsl:apply-templates/>
	              </th>
				  <xsl:if test="position() mod 2 = 0">
					<xsl:attribute name="bgcolor">yellow</xsl:attribute>
				  </xsl:if>
	              <xsl:for-each select="order">
	                <td>
						ghtt
	                </td>
	              </xsl:for-each>
	            </tr>
	          </xsl:for-each>
	        </tbody>
	      </table>
	    </body>
	  </html>
	</xsl:template> -->
	
	<xsl:template match="logdocument">
	<html>
		<head>
			<!--<meta content="text/html; charset=ISO-8859-1" http-equiv="Content-Type"/>-->
			<link type="text/css" href="./css/screen.css" rel="stylesheet"/>
			<title>Protocol Log</title>
		</head>
		<body>
			<script language="JavaScript" type="text/javascript">
				function sw(itemid) {
					var tbody = document.getElementById('tbody_' + itemid);
					if (tbody.style.display == "none") {
						tbody.style.display = "";
						var expandimage = document.getElementById('expand_' + itemid);
						expandimage.src = "img/nolines_minus.gif";
					} else {
						tbody.style.display = "none";
						var expandimage = document.getElementById('expand_' + itemid);
						expandimage.src = "img/nolines_plus.gif";
					}
				}
				
				function closeall() {
					var tbodies = document.getElementsByTagName("tbody");
					for (var j = 0; j &lt; tbodies.length; j++) {
						var tbody = document.getElementById('tbody_' + j);
						if (tbody) {
							if (tbody.style.display == ""){
								tbody.style.display = "none";
								var expandimage = document.getElementById('expand_' + j);
								expandimage.src = "img/nolines_plus.gif";
							}
						}
					}
				}
				
				function openall() {
					var tbodies = document.getElementsByTagName("tbody");
					for (var j = 0; j &lt; tbodies.length; j++) {
						var tbody = document.getElementById('tbody_' + j);
						if (tbody) {
							if (tbody.style.display == "none") {
								tbody.style.display = "";
								var expandimage = document.getElementById('expand_' + j);
								expandimage.src = "img/nolines_minus.gif";
							}
						}
					}
				}
			</script>
			<!-- <xsl:call-template name="header"/>
			<h1><xsl:apply-templates select="title" mode="raw"/></h1>
			<xsl:apply-templates select="log4j:event"/> -->
			
			<div id="header">
				<h1>Protocol Log for testcase <xsl:value-of select="testcase"/></h1>
			</div>
		
			<div id="body">
				<h2>Test case description</h2>
				
				<br/>
				<a href="javascript:closeall();">close all</a> | <a href="javascript:openall();">open all</a>
			
				<table cellspacing="0">
					<tr bgcolor="#9acd32">
						<th>JCAT</th>
						<th class="protocol">Protocol</th>
						<th>SUT</th>
					</tr>
			
					<xsl:for-each select="log4j:event">
				
						<xsl:choose>
							<!--<xsl:when test="logger &gt; 10">-->
							<xsl:when test="@logger='netconf.send'">
								<tr>
									<td class="script">
										<a href="javascript:sw({@mNr});"><img src="img/nolines_minus.gif" id="expand_{@mNr}"></img></a>&#x00A0;
										<xsl:value-of select="@timestamp"/>
									</td>
									<td class="script"><xsl:value-of select="@logger"/></td>
									<td class="script">&#x00A0;</td>
								</tr>
								<tbody id="tbody_{@mNr}">
								<!--<xsl:attribute name="id">
								<xsl:value-of select="@mNr" />
								</xsl:attribute>--> 
									<tr>
										<td class="message">
											<pre class="formated">
												<xsl:value-of select="log4j:message"/>
											</pre>
										</td>
										<td class= "image">
											<img class="netconf" alt="Netconf_left_to_right" src="img/arrow_lr.gif">
												<!--<xsl:attribute name="src">
													arrow_lr.gif
												</xsl:attribute> -->  
											</img>
										</td>
										<td class="empty">&#x00A0;</td>
									</tr>
								</tbody>
							</xsl:when>
							<xsl:when test="@logger='netconf.receive'">
								<tr>
									<td class="script">
										<a href="javascript:sw({@mNr});"><img src="img/nolines_minus.gif" id="expand_{@mNr}"></img></a>&#x00A0;
										<xsl:value-of select="@timestamp"/>
									</td>
									<td class="script"><xsl:value-of select="@logger"/></td>
									<td class="script">&#x00A0;</td>
								</tr>
								<tbody id="tbody_{@mNr}">
									<tr>
										<td class="empty">&#x00A0;</td>
										<td class="image">
											<img class="netconf" alt="Netconf_right_to_left" src="img/arrow_rl.gif">
											</img>
										</td>
										<td class="message"> 
											<pre class="formated">
												<xsl:value-of select="log4j:message"/>
											</pre>
										</td>
									</tr>
								</tbody>
							</xsl:when>
							<xsl:when test="@logger='snmp.send'">
								<tr>
									<td class="script">
										<a href="javascript:sw({@mNr});"><img src="img/nolines_minus.gif" id="expand_{@mNr}"></img></a>&#x00A0;
										<xsl:value-of select="@timestamp"/>
									</td>
									<td class="script"><xsl:value-of select="@logger"/></td>
									<td class="script">&#x00A0;</td>
								</tr>
								<tbody id="tbody_{@mNr}">
									<tr>
										<td class="message">
											<pre class="formated">
												<xsl:value-of select="log4j:message"/>
											</pre>
										</td>
										<td class= "image">
											<img class="snmp" alt="Snmp_left_to_right" src="img/snmp_lr.gif">
												<!--<xsl:attribute name="src">
													arrow_lr.gif
												</xsl:attribute> -->  
											</img>
										</td>
										<td class="empty">&#x00A0;</td>
									</tr>
								</tbody>
							</xsl:when>
							<xsl:when test="@logger='snmp.receive'">
								<tr>
									<td class="script">
										<a href="javascript:sw({@mNr});"><img src="img/nolines_minus.gif" id="expand_{@mNr}"></img></a>&#x00A0;
										<xsl:value-of select="@timestamp"/>
									</td>
									<td class="script"><xsl:value-of select="@logger"/></td>
									<td class="script">&#x00A0;</td>
								</tr>
								<tbody id="tbody_{@mNr}">
									<tr>
										<td class="empty">&#x00A0;</td>
										<td class="image">
											<img class="snmp" alt="Snmp_right_to_left" src="img/snmp_rl.gif">
											</img>
										</td>
										<td class="message">
											<pre class="formated">
												<xsl:value-of select="log4j:message"/>
											</pre>
										</td>
									</tr>
								</tbody>
							</xsl:when>
							<!-- Case the message is from testcase.info-->
							<xsl:when test="@logger='testcase.info'">
								<tr>
									<td class="teststep">
										<h3 class="teststep">
											<xsl:value-of select="log4j:message"/>
										</h3>
									</td>
									<td class="teststep">&#x00A0;</td>
									<td class="teststep">&#x00A0;</td>
								</tr>
							</xsl:when>

							<xsl:when test="@logger='testcase.error'">
								<tr>
									<td class="teststepError">
										<h3 class="teststepError">
											<xsl:value-of select="log4j:message"/>
										</h3>
									</td>
									<td class="teststep">&#x00A0;</td>
									<td class="teststep">&#x00A0;</td>
								</tr>
							</xsl:when>
							
							<xsl:when test="@logger='testcase.info'">
								<tr>
									<td class="teststep">
										<h3 class="teststep">
											<xsl:value-of select="log4j:message"/>
										</h3>
									</td>
									<td class="teststep">&#x00A0;</td>
									<td class="teststep">&#x00A0;</td>
								</tr>
							</xsl:when>
							
							
							<xsl:otherwise>
								<tr>
									<td class="empty">&#x00A0;</td>
									<td class="image">
									&#x00A0;
									</td>
									<td class="message">
										<pre class="formated">
											<xsl:value-of select="log4j:message"/>
										</pre>
									</td>
								</tr>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</table>
			</div>
		</body>
	</html> 
	<xsl:text>
	</xsl:text>
	<xsl:comment>this HTML page was generated by an xslt transformation</xsl:comment>
</xsl:template>

			<xsl:template name="header">
			<xsl:if test="@logger">
			    <xsl:apply-templates select="@logger" />Logger<br/><br/>
			</xsl:if>
			</xsl:template>
			
			<xsl:template match="log4j:message">
				<xsl:text>author: </xsl:text>
				<xsl:apply-templates/>
			</xsl:template>
			
			
			<xsl:template match="example">
			<p/><br/>
			<table border="1" cellpadding="5" width="100%" bgcolor="#f5dcb3"><tr><td><pre>
			<xsl:apply-templates/>
			</pre></td></tr></table>
			<br/>
			</xsl:template>
			
			
			<xsl:template match="log4j:event">
			<h1 class="title">
			<xsl:value-of select="log4j:event"/> Test<br/>
			</h1>
			
			</xsl:template>
			
			<xsl:template name="increase">
				<xsl:param name="i"/>
				<xsl:value-of select="$i + 1"/>
			</xsl:template>
			
	<xsl:template match="link">
		<xsl:choose>
			<xsl:when test="@href">
				<tt><a href="{@href}"><xsl:apply-templates/></a></tt>
			</xsl:when>
			<xsl:when test="not(@href)">
				<tt><a href="{text()[1]}"><xsl:apply-templates/></a></tt>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	
</xsl:stylesheet>