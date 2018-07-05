<%@ page language="java" contentType="text/html; charset=gbk"
	import="com.baidu.ueditor.ActionEnter,com.java1234.upload.BlogActionEnter"
    pageEncoding="gbk"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%

    request.setCharacterEncoding( "gbk" );
	response.setHeader("Content-Type" , "text/html");
	
	String rootPath = application.getRealPath( "/" );
	
	out.write( new BlogActionEnter( request, rootPath ).exec() );
	
%>