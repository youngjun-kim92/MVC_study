<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false"
    %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%
	request.setCharacterEncoding("utf-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입창</title>
</head>
<body>	
	<form action="${contextPath}/member/addMember.do" method="post">
		<h2 align="center">회원 가입창</h2>
		<table align="center">			
			<tr>
				<td width="200"><P align="right">아이디</P></td>
				<td width="400"><input type="text" name="id"></td>
			</tr>
			<tr>
				<td width="200"><P align="right">비밀번호</P></td>
				<td width="400"><input type="password" name="pwd"></td>
			</tr>
			<tr>
				<td width="200"><P align="right">이름</P></td>
				<td width="400"><input type="text" name="name"></td>
			</tr>
			<tr>				
				<td width="200"><P align="right">이메일</P></td>
				<td width="400"><input type="text" name="email"></td>
			</tr>
			<tr>				
				<td width="200">&nbsp;</td>
				<td width="400">
					<input type="submit" value="가입하기">
					<input type="reset" value="다시입력">
				</td>
			</tr>
			
		</table>	
	</form>
</body>
</html>