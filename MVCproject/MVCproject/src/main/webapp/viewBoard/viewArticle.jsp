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
<title>글 상세보기 창</title>
<style type="text/css">
	#tr_button_modify {
		display: none;
	}
	td {
		text-align: center;	
	}
</style>
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
		}/* else {
			//취소 눌러서 선택한 파일에 값이 없을 때, 미리보기 없애기
			//한번 이미지 올리고 글을 등록한 이후에 상세보기에서 수정할 수 없도록 else부분 삭제
			$("#preview").attr('src', '#');
		}*/
 	}//readImage End
 	
 	// 수정하기 함수
	function fn_enable(obj) {
 		// id_title : 제목 input태그의 id
 		document.getElementById("id_title").disabled=false;
 		document.getElementById("id_content").disabled=false;
 		//document.getElementById("id_imgFile").disabled=false;
 		// - 이렇게 하면 이미지 안넣은 사람이 있어서 에러남
 		//그래서 변수에 넣어서 처리
 		let imgName = document.getElementById("id_imgFile"); 
 		if(imgName != null) {
 			imgName.disabled=false;
 		}
 		document.getElementById("tr_button_modify").style.display="block";
 		document.getElementById("tr_button").style.display="none";
 	}
 	
 	// 취소하기 함수
 	function toList(obj) {
 		obj.action = "${contextPath}/board/viewArticle.do?articleNo=${article.articleNo}";
 		obj.submit();
 	}
 	
 	// 수정하기 함수
 	function fn_modify_article(obj) {
 		obj.action = "${contextPath}/board/modArticle.do";
 		obj.submit();
 	}
 	
 	// 삭제하기 함수 (url : 매핑정보를 받을 매개변수 이름, articleNo : 글번호를 받을 매개변수)
 	function fn_remove_article(url, articleNo) {
 		let d_form = document.createElement("form"); //동적으로 form태그를 생성하여 d_form에 넣음
 		d_form.setAttribute("action", url); 
 		d_form.setAttribute("method", "post");
 		//hidden으로 아이디 가져가기
 		let articleNoInput = document.createElement("input");
 		articleNoInput.setAttribute("type", "hidden");
 		articleNoInput.setAttribute("name", "articleNo");
 		articleNoInput.setAttribute("value", articleNo);
 		d_form.appendChild(articleNoInput);
 		document.body.appendChild(d_form);
 		d_form.submit();
 	}
 	
 	// 목록으로 가기
	function backToList(obj) {
		//obj는 현재 저(아래) form을 매개변수로 받음
		obj.action = "${contextPath}/board/listArticles.do";
		obj.submit();
	}
 	
 	// 답글쓰기
 	function fn_reply_form(url, parentNo) {
 		let r_form = document.createElement("form");
 		r_form.setAttribute("action", url);
 		r_form.setAttribute("method", "post");
 		let parentNoInput = document.createElement("input");
 		parentNoInput.setAttribute("type", "hidden");
 		parentNoInput.setAttribute("name", "parentNo");
 		parentNoInput.setAttribute("value", parentNo);
 		r_form.appendChild(parentNoInput);
 		document.body.appendChild(r_form);
 		r_form.submit();
 	}
</script>
</head>
<body>
	<form name="frmArticle" action="${contextPath}" method="post" enctype="multipart/form-data">
		<table align="center">
			<tr>
				<td width="150" align="center" bgcolor="ff9933">글번호</td>
				<td><input type="text" value="${article.articleNo}" disabled></td>
				<input type="hidden" name="articleNo" value="${article.articleNo}">
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">작성자아이디</td>
				<td><input type="text" value="${article.id}" disabled></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">제목</td>
				<td><input type="text" id="id_title" name="title" value="${article.title}" disabled></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">내용</td>				
				<td><textarea row="10" cols="50" id="id_content" name="content" disabled>${article.content}</textarea></td>
			</tr>
			<!-- 이미지가 있을 때만 이 부분이 보이도록 하기(선택사항) -->
			<c:if test="${not empty article.imageFileName}">
				<tr>
					<td width="150" align="center" bgcolor="#ff9933" rowspan="2">이미지</td>
					<td><input type="hidden" name="originalFileName" value="${article.imageFileName}">
					<!-- 경로(src) : FileDownloadController 매핑인 /lownload.do에서 매개변수를 가지고 감 -->
						<img id="preview" src="${contextPath}/download.do?imageFileName=${article.imageFileName}&articleNo=${article.articleNo}" width="200">
					</td>					
				</tr>
				<tr>
					<!-- 이미지가 바뀌면 readImage()메서드 호출, this : 지금 이 input 태그 -->
					<td><input type="file" id="id_imgFile" name="imageFileName" onchange="readImage(this)" disabled></td>
				</tr>
			</c:if>	
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">등록일자</td>
				<td><input type="text" value="${article.writeDate}" disabled></td>
			</tr>
			<!-- 수정하기 누른 상태의 버튼 영역 -->
			<tr id="tr_button_modify">
				<td align="center" colspan="2">
					<input type="button" value="수정반영하기" onclick="fn_modify_article(frmArticle)">
					<input type="button" value="취소" onclick="toList(frmArticle)">
				</td>
			</tr>
			
			<!-- 버튼 영역 -->
			<tr id="tr_button">
				<td align="center" colspan="2">
					<!-- this.form : 현재 form을 가리키는 객체 매개변수 -->
					<input type="button" value="수정하기" onclick="fn_enable(this.form)">
			   <!-- <input type="button" value="삭제하기" onclick="fn_remove_article(매핑정보, 글번호)">  -->
					<input type="button" value="삭제하기" onclick="fn_remove_article('${contextPath}/board/removeArticle.do', ${article.articleNo})">
					<input type="button" value="목록으로" onclick="backToList(this.form)">
			   <!-- <input type="button" value="답글쓰기" onclick="fn_reply_form(매핑이름, 글번호)"> -->	
					<input type="button" value="답글쓰기" onclick="fn_reply_form('${contextPath}/board/replyForm.do', ${article.articleNo})">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>