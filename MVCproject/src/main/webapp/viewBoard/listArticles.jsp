<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false"
    %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="articlesList" value="${articleMap.articlesList}"/>
<c:set var="totArticles" value="${articleMap.totArticles}"/>
<c:set var="section" value="${articleMap.section}"/>
<c:set var="pageNum" value="${articleMap.pageNum}"/>
<c:choose>
	<c:when test="${section > totArticles/100}">
		<c:set var="endValue" value="${((totArticles%100)%10)==0 ? (totArticles%100)/10 : (totArticles%100)/10+1}"/>
	</c:when>
	<c:otherwise>
		<c:set var="endValue" value="10" />
	</c:otherwise>
</c:choose>	
<%
	request.setCharacterEncoding("utf-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글 목록창</title>
<style type="text/css">
 	a {
 		text-decoration: none;
 		color:black;
 	}
 	.selPage {
 		color: red;
 		font-weight: bold;
 	}
 	.noLine {
 		color: black;
 		font-weight: normal;
 	}
 	a:hover {
 		text-decoration: underline;
 	} 	
</style>
</head>
<body>
	<table align="center" border="1" width="80%">
		<tr align="center" bgcolor="lightblue">
			<!-- 표항목 제목에 있는 글번호는 articleNo가 아님.
			 DB에 있는 4번 글이 제일 위에 올 것 -->
			<th>No</th><th>articleNo</th><th>작성자</th><th>제목</th><th>작성일</th>
		</tr>
		<c:choose>
			<c:when test="${empty articlesList}">
			<%--글이 없을 때--%>
				<tr>
					<td colspan="5" align="center">등록된 글이 없습니다.</td>
				</tr>
			</c:when>
			<c:when test="${!empty articlesList}">
				<c:forEach var="article" items="${articlesList}" varStatus="articleNum">
					<tr align="center">
						<%-- 기본 글번호 처리 
						<td width="5%">${articleNum.count}</td> --%>
						<td width="5%">${(section-1)*100+(pageNum-1)*10+articleNum.count}</td>
						<td width="5%">${article.articleNo}</td>
						<td width="10%">${article.id}</td>
						<td align="left" width="50%">
							<span style="padding-left:10px"></span>
							
							<%-- 답변형 게시판에서만 나오는 choose 내부의 choose (level활용) --%>							
							<c:choose>
								<%--level 1이면 부모(최상위), 2이면 답글 --%>
								<c:when test="${article.level > 1}">
									<%-- level만큼 패딩이 들어감 --%>
									<c:forEach begin="1" end="${article.level-1}" step="1">
										<span style="padding-left:30px"></span>
									</c:forEach>
									
									<%-- 상세보기를 위한 작업. 글제목에 링크 걸기 --%>
									[답변]<a href="${contextPath}/board/viewArticle.do?articleNo=${article.articleNo}">${article.title}</a>
								</c:when>
								
								<%-- 최상위 글--%>
								<c:otherwise>
									<a href="${contextPath}/board/viewArticle.do?articleNo=${article.articleNo}">${article.title}</a>
								</c:otherwise>
							</c:choose>
						</td>
						<%-- 글 갯수만큼 forEach를 돌면서 작성날짜 뿌려줌 --%>
						<td width="15%">${article.writeDate}</td>
					</tr>
				</c:forEach>
			</c:when>
		</c:choose>
	</table>
	<div align="center">
		<c:if test="${totArticles != 0 }">
			<c:choose>
				<%-- 등록된 글 수가 100개 초과인 경우 --%>
				<c:when test="${totArticles > 100 }">					
					<c:forEach var="page" begin="1" end="${endValue}" step="1">					
						<%-- 이전 섹션으로 가기 --%>
						<c:if test="${section > 1 && page == 1}">
							<%-- 앞으로 페이지 가기 --%>
							<%--<a href = "${contextPath}/board/listArticles.do?
							section=${section-1}&pageNum=10">prev</a> --%>
							<%-- 10페이지씩 움직이기 --%>
							<a href = "${contextPath}/board/listArticles.do?
							section=${section-1}&pageNum=${currentPage}">prev</a>	
							<%-- section=${section-1}&pageNum=${(section-1)*10+1}">prev</a> --%>
						</c:if>
						<c:choose>							
							<c:when test="${page == pageNum}">
								<a class="selPage" href="${contextPath}/board/listArticles.do?
								section=${section}&pageNum=${page}"> ${(section-1)*10+page} </a>
								<%-- 10페이지씩 움직이기 변수 추가 --%>
								<c:set var="currentPage" value="${pageNum}" scope="application"/>
							</c:when>							
							<c:otherwise> 
								<a class="noLine" href="${contextPath}/board/listArticles.do?
								section=${section}&pageNum=${page}"> ${(section-1)*10+page} </a>
							</c:otherwise>
						</c:choose>					 
						<%-- 다음 섹션으로 가기 --%>
						<c:if test="${page == 10 and totArticles/100 > section}">
							<%-- 10페이지씩 움직이기 (기존 :pageNum=1) --%>
							<a href="${contextPath}/board/listArticles.do?
							section=${section+1}&pageNum=${currentPage}">next</a>
						</c:if>					
					</c:forEach>					
				</c:when>
				<%-- 등록된 글 수가 100개 이하인 경우 --%>
				<c:when test="${totArticles <= 100 }">
					<%-- 딱 떨어질 때 --%>
					<c:if test="${(totArticles mod 10) == 0 }">
						<c:set var="totArticles" value="${totArticles-1} }"/>
					</c:if>
					<c:forEach var="page" begin="1" end="${totArticles/10+1}" step="1">
						<c:choose>
							<c:when test="${page==pageNum}">
								<a class="selPage" href="${contextPath}/board/listArticles.do?
								section=${section}&pageNum=${page}">${page}</a>
							</c:when>							
							<c:otherwise><%-- 똑같은데 클래스 이름만 다름(스타일만 다르게) --%>
								<a class="noLine" href="${contextPath}/board/listArticles.do?
								section=${section}&pageNum=${page}">${page}</a>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:when>
			</c:choose>			
		</c:if>		
	</div>
	
	<p align="center"><a href="${contextPath}/board/articleForm.do">글쓰기</a></p>
</body>
</html>