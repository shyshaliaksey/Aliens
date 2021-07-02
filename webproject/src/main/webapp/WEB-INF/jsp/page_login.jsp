<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js'></script>
<html>
<head>
<title>Login</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css">
<link href="<c:url value="${CSS_MAIN}"/>" rel="stylesheet">
<script type="text/javascript" src="<c:url value='${JS_LOGIN}'/>"></script>
</head>
<body>
<main role="main" class="container">
	<jsp:include page="${TEMPLATE_NAV_JSP}"/>
	<h3>Login Page</h3>
	<jsp:include page="${FORM_LOGIN_JSP}"/>
</main>
</body>
</html>

