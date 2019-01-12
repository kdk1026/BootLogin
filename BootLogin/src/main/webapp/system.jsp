<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>System / Request</title>
</head>
<body>

<%--
	로드밸런싱 환경 시, 점검  
--%>

<pre>
[System Info]
<%
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	out.println("\t" + " (Date) " + formatter.format(new Date()));
	out.println();
	
	InetAddress inet = Inet4Address.getLocalHost();
	out.println("\t" + " (Host Name) " + inet.getHostName());
	out.println();
	
	InetAddress[] local = InetAddress.getAllByName(inet.getHostName());
	for (int i=0; i < local.length; i++) {
		out.println("\t" + " (Host Addr" + (i+1) + ") " + local[i].getHostAddress());
	}
	
	out.println();
	out.println("\t" + " (Public Addr) " + this.excuteGet("http://ipv4.icanhazip.com"));
%>

[System Properties]
<%
	Properties props = System.getProperties();
	Enumeration en = props.propertyNames();
	while ( en.hasMoreElements() ) {
		String sKey = String.valueOf(en.nextElement());
		String sVal = props.getProperty(sKey);
		out.println("\t" + sKey +" = "+ sVal);
	}
%>

[Request Info]
<%
	out.println("\t" + " (Server Name) " + request.getServerName());
	out.println("\t" + " (Character Encoding) " + request.getCharacterEncoding());
	out.println("\t" + " (Locale) " + request.getLocale().toString());

	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
		for (Cookie c : cookies) {
			out.println("\t" + " (Cookie) " + c.getName() +" = "+ c.getValue());
		}
	}
	
	out.println();
	out.println("\t" + " (Http or Https) " + request.getScheme());
	
	out.println();
	out.println("\t" + " (Local Addr) " + request.getLocalAddr());
	out.println("\t" + " (Remote Addr) " + this.getIpAddr(request));
%>
</pre>

</body>
</html>
<%!
public static String getIpAddr(HttpServletRequest request) {
	
	String sIpAddr = request.getHeader("X-Forwarded-For");
	
	if (sIpAddr == null) {
		sIpAddr = request.getHeader("Proxy-Client-IP");
	}
	
	if (sIpAddr == null) {
		sIpAddr = request.getHeader("WL-Proxy-Client-IP");
	}
	
	if (sIpAddr == null) {
		sIpAddr = request.getHeader("HTTP_CLIENT_IP");
	}
	
	if (sIpAddr == null) {
		sIpAddr = request.getHeader("HTTP_X_FORWARDED_FOR");
	}
	
	if (sIpAddr == null) {
		sIpAddr = request.getRemoteAddr();
	}
	
	return sIpAddr;
}

public static String excuteGet(String url) {
	String sRet = "";
	
	HttpURLConnection conn = null;
	
	try {
		conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		
		conn.setUseCaches(false);
		conn.setDoInput(true);
		
		InputStream is = new BufferedInputStream(conn.getInputStream());
		int nRead = 0;
		byte[] buffer = new byte[8192];
		
		while ( (nRead = is.read(buffer)) != -1) {
			sRet = new String(buffer, 0, nRead);
		}
		
		is.close();
		
	} catch (Exception e) {
		sRet = e.getMessage();
		
	} finally {
		if (conn != null) {
			conn.disconnect();
		}
	}
	
	return sRet;
}
%>