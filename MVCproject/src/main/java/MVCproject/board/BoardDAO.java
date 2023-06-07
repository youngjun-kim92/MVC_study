package MVCproject.board;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private DataSource dataFactory;
	
	public BoardDAO() {
		try {
			//JNDI(Java Naming and Directory Interface)를 이용
			//필요한 자원을 키/값의 쌍으로 저장한 후 필요한 키를 이용해 값을 얻는 방법
			Context ctx=new InitialContext();
			Context envContext=(Context)ctx.lookup("java:/comp/env");
			dataFactory=(DataSource)envContext.lookup("jdbc/oracle");
		} 
		catch (Exception e) {
			System.out.println("DB연결 오류");
			e.printStackTrace();
		}
	}
	//글 목록 페이징 메소드
	public List<ArticleVO> selectAllArticles(Map<String, Integer> pagingMap) {
		List<ArticleVO> articlesList=new ArrayList<ArticleVO>();
		int section=pagingMap.get("section");
		int pageNum=pagingMap.get("pageNum");
		try {
			conn=dataFactory.getConnection();
			String query="SELECT * FROM (SELECT ROWNUM AS recNum, LVL, articleNo, parentNo, title, id, writeDate FROM (SELECT LEVEL AS LVL, articleNo, parentNo, title, id, writeDate FROM boardtbl START WITH parentNo=0 CONNECT BY PRIOR articleNo=parentNo ORDER SIBLINGS BY articleNo DESC)) WHERE recNum BETWEEN (?-1)*100+(?-1)*10+1 AND (?-1)*100+?*10";
			System.out.println(query);
			pstmt=conn.prepareStatement(query);
			pstmt.setInt(1, section);
			pstmt.setInt(2, pageNum);
			pstmt.setInt(3, section);
			pstmt.setInt(4, pageNum);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()) {
				int level=rs.getInt("level");
				int articleNo=rs.getInt("articleNo");
				int parentNo=rs.getInt("parentNo");
				String title=rs.getString("title");
				String content=rs.getString("content");
				String id=rs.getString("id");
				Date writeDate=rs.getDate("writeDate");
				ArticleVO articleVO=new ArticleVO();
				articleVO.setLevel(level);
				articleVO.setArticleNo(articleNo);
				articleVO.setParentNo(parentNo);
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setId(id);
				articleVO.setWriteDate(writeDate);
				articlesList.add(articleVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("글 목록 조회 중 에러!!");
			e.printStackTrace();
		}
		return articlesList;
	
//	//글 목록 조회 메소드
//	public List<ArticleVO> selectAllArticles() {
//		List<ArticleVO> articlesList=new ArrayList<ArticleVO>();
//		try {
//			conn=dataFactory.getConnection();
//			String query="SELECT LEVEL, articleNo, parentNo, title, content, id, writeDate FROM boardtbl START WITH parentNo=0 CONNECT BY PRIOR articleNo=parentNo ORDER SIBLINGS BY articleNo DESC";
//			System.out.println(query);
//			pstmt=conn.prepareStatement(query);
//			ResultSet rs=pstmt.executeQuery();
//			while(rs.next()) {
//				int level=rs.getInt("level");
//				int articleNo=rs.getInt("articleNo");
//				int parentNo=rs.getInt("parentNo");
//				String title=rs.getString("title");
//				String content=rs.getString("content");
//				String id=rs.getString("id");
//				Date writeDate=rs.getDate("writeDate");
//				ArticleVO articleVO=new ArticleVO();
//				articleVO.setLevel(level);
//				articleVO.setArticleNo(articleNo);
//				articleVO.setParentNo(parentNo);
//				articleVO.setTitle(title);
//				articleVO.setContent(content);
//				articleVO.setId(id);
//				articleVO.setWriteDate(writeDate);
//				articlesList.add(articleVO);
//			}
//			rs.close();
//			pstmt.close();
//			conn.close();
//		} catch (Exception e) {
//			System.out.println("글 목록 조회 중 에러!!");
//			e.printStackTrace();
//		}
//		return articlesList;
//	}
	
	//글 번호 생성 메소드
	private int getNewArticleNo() {
		int _articleNo=1;
		try {
			conn=dataFactory.getConnection();
			//max함수를 이용해서 가장 큰 번호를 조회
			String query="select max(articleNo) from boardtbl";
			pstmt=conn.prepareStatement(query);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()) {
				_articleNo=rs.getInt(1)+1;
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("글 번호 생성 중 에러!!");
			e.printStackTrace();
		}
		return _articleNo;
	}
	
	//새 글 추가 메소드
	public int insertNewArticle(ArticleVO articleVO) {
		int articleNo=getNewArticleNo();
		try {
			conn=dataFactory.getConnection();
			int parentNo=articleVO.getParentNo();
			String title=articleVO.getTitle();
			String content=articleVO.getContent();
			String imageFileName=articleVO.getImageFileName();
			String id=articleVO.getId();
			String query="insert into boardtbl (articleNo, parentNo, title, content, imageFileName, id) values(?,?,?,?,?,?)";
			pstmt=conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			pstmt.setInt(2, parentNo);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, imageFileName);
			pstmt.setString(6, id);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("새 글 추가 중 에러!!");
			e.printStackTrace();
		}
		return articleNo;
	}
	
	//선택한 글 상세 내용 메소드
	public ArticleVO selectArticle(int articleNo) {
		ArticleVO articleVO=new ArticleVO();
		try {
			conn=dataFactory.getConnection();
			String query="select articleNo, parentNo, title, content, NVL(imageFileName, 'null') as imageFileName, id, writeDate from boardtbl where articleNo=?";
			System.out.println(query);
			pstmt=conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			ResultSet rs=pstmt.executeQuery();
			rs.next();
			int _articleNo=rs.getInt("articleNo");
			int parentNo=rs.getInt("parentNo");
			String title=rs.getString("title");
			String content=rs.getString("content");
			String imageFileName=URLEncoder.encode(rs.getString("imageFileName"),"utf-8");
			if(imageFileName.equals("null")) {
				imageFileName=null;
			}
			String id=rs.getString("id");
			Date writeDate=rs.getDate("writeDate");
			articleVO.setArticleNo(_articleNo);
			articleVO.setParentNo(parentNo);
			articleVO.setTitle(title);
			articleVO.setContent(content);
			articleVO.setImageFileName(imageFileName);
			articleVO.setId(id);
			articleVO.setWriteDate(writeDate);
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("글 상세 구현 중 에러!!");
			e.printStackTrace();
		}
		return articleVO;
	}
	
	//글 수정하기 메소드
	public void updateArticle(ArticleVO articleVO) {
		int articleNo=articleVO.getArticleNo();
		String title=articleVO.getTitle();
		String content=articleVO.getContent();
		String imageFileName=articleVO.getImageFileName();
		try {
			conn=dataFactory.getConnection();
			String query="update boardtbl set title=? content=?";
			if(imageFileName!=null&&imageFileName.length()!=0) {
				query+=", imageFileName=?";
			}
			query+=" where articleNo=?";
			System.out.println(query);
			pstmt=conn.prepareStatement(query);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			if(imageFileName!=null&&imageFileName.length()!=0) {
				pstmt.setString(3, imageFileName);
				pstmt.setInt(4, articleNo);
			}
			else {
				pstmt.setInt(3, articleNo);
			}
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("글 수정 중 에러!!");
			e.printStackTrace();
		}
	}
	
	//삭제할 글 번호 목록 가져오기
	public List<Integer> selectRemovedArticles(int articleNo) {
		List<Integer> articleNoList=new ArrayList<Integer>();
		try {
			conn=dataFactory.getConnection();
			String query="SELECT articleNo FROM boardtbl START WITH articleNo=? CONNECT BY PRIOR articleNo=parentNo";
			pstmt=conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()) {
				articleNo=rs.getInt("articleNo");
				articleNoList.add(articleNo);
			}
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("삭제할 글 번호 목록 가져오기 중 에러!!");
			e.printStackTrace();
		}
		return articleNoList;
	}
	
	//글 삭제 메소드
	public void deleteArticle(int articleNo) {
		try {
			conn=dataFactory.getConnection();
			String query="DELETE FROM boardtbl WHERE articleNo in SELECT articleNo FROM boardtbl START WITH articleNo=? CONNECT BY PRIOR articleNo=parentNo";
			pstmt=conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("글 삭제 중 에러!!");
			e.printStackTrace();
		}
	}
}
