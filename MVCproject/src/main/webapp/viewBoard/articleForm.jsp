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
<title>글쓰기</title>
<%-- jQuery 갖고오기(CDN방식) --%>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
	//이미지 미리보기 구현
	function readImage(input) {
		//선택한 파일에 뭔가 값이 들어있으면 수행
		if(input.files && input.files[0]) {
			//FileReader() : 파읽을 읽어주는 jQuery에서 제공해주는 객체
			let reader = new FileReader(); 
			reader.onload = function (event){
				//event.target.result : 이미지 경로?
				$("#preview").attr('src', event.target.result);				
			}
			reader.readAsDataURL(input.files[0]);					
		} else {
			//취소 눌러서 선택한 파일에 값이 없을 때, 미리보기 없애기
			$("#preview").attr('src', '#');
		}
 	}
	//다른 액션으로 submit(글을 등록하지 않고 목록으로 가기)
	function backToList(obj) {
		//obj는 현재 저(아래) form을 매개변수로 받음
		obj.action = "${contextPath}/board/listArticles.do";
		obj.submit();
	}
</script>
</head>
<body>
	<h2 align="center">새 글쓰기</h2>
	<!-- enctype="multipart : 글 + 파일  -->
	<form action="${contextPath}/board/addArticle.do" method="post" enctype="multipart/form-data">
		<table align="center">
			<tr>
				<td align="right">글 제목 :</td>
				<td colspan="2"><input type="text" size="50" name="title"></td>
			</tr>
			<tr>
				<td align="right">글 내용: </td>
				<td colspan="2"><textarea rows="10" cols="52" name="content" maxlength="4000"></textarea></td>
			</tr>
			<tr>
				<td align="right">이미지파일첨부 : </td>				
				<!-- 이미지 미리보기 구현, onchange : 이미지가 바뀌면 수행됨. this: 지금 이 input form을 말하는 것 -->
				<td><input type="file" name="imageFileName" onchange="readImage(this)"></td>				
				<td><img id="preview" src="#" width=200 alt=""></td>
			</tr>
			<tr>
				<td align="right">&nbsp;</td>
				<td colspan="2">
					<input type="submit" value="글쓰기">
					<input type="button" value="목록보기" onclick="backToList(this.form)">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>