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
	
	//커넥션 풀 연결
	public BoardDAO() {
		try {
			//커넥션 풀은 JNDI(Java Naming Directory Interface)를 이용
			// JNDI : 필요한 자원을 키와 값의 쌍으로 저장 한 후, 키를 이용하여 값을 얻는 방식
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			//DataSource : Servers - context.xml에 있는 resource 부분을 읽어옴
			dataFactory = (DataSource)envContext.lookup("jdbc/oracle");			
		} catch (Exception e) {
			//연결오류 메시지가 난다면 이 메시지가 뜨고 Servers프로젝트 - context.xml확인
			System.out.println("DB연결오류");
		}
	}
	//글 목록 + 페이징 메서드
	public List<ArticleVO> selectAllArticles(Map<String, Integer> pagingMap) {		
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			int section = pagingMap.get("section");
			int pageNum = pagingMap.get("pageNum");
			
			try {
				conn = dataFactory.getConnection();
				//10개는 하나의 페이지, 100개는 하나의 섹션. next누르면 section이 2로 바뀜 
				//계층형 글을 먼저 세어서 합쳐서 10개씩 끊을 것
				//ROWNUM : 행 갯수 
				//recNum : 레코드 번호 (별칭)
				//LVL : Level 번호
				//WHERE 페이징 기법의 주 공식
				String query = "SELECT * FROM (SELECT ROWNUM AS recNum, LVL, articleNo, parentNo, title, id, writeDate " +							
							"FROM (SELECT LEVEL as LVL, articleNo, parentNo, title, id, writeDate FROM boardtbl " + 
							"START WITH parentNo=0 CONNECT BY PRIOR articleNo = parentNo ORDER SIBLINGS BY articleNo DESC)) " + 
							"WHERE recNUM BETWEEN (?-1)*100+(?-1)*10+1 AND (?-1)*100+?*10";
						
				System.out.println(query);
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, section);
				pstmt.setInt(2, pageNum);
				pstmt.setInt(3, section);
				pstmt.setInt(4, pageNum);
				
				
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()) {
					//LEVEL 저장
					int level = rs.getInt("LVL");
					int articleNo = rs.getInt("articleNo");
					int parentNo = rs.getInt("parentNo");
					String title = rs.getString("title");
					String id = rs.getString("id");
					Date writeDate = rs.getDate("writeDate");
					
					//글정보를 articleVO에 담음
					ArticleVO articleVO = new ArticleVO();
					articleVO.setLevel(level);
					articleVO.setArticleNo(articleNo);
					articleVO.setParentNo(parentNo);
					articleVO.setTitle(title);					
					articleVO.setId(id);
					articleVO.setWriteDate(writeDate);
					
					//모든 글목록을 articleList에 담음
					articlesList.add(articleVO);
				}
				
			} catch (Exception e) {
				System.out.println("글 페이징 목록 조회 중 에러!!");
				e.printStackTrace();
			}
			return articlesList;
	}
	
	//전체 글 목록 수 처리
	 public int selectToArticles() {
		 int totCount = 0;
		 try {
			conn = dataFactory.getConnection();
			String query = "SELECT count(*) FROM boardtbl";
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			
			//if 쓰는 이유?... 자료가 있으면 next 할 수 있고 , 숫자 한개만 넘어올 거니까 getInt(1)로 컬럼 1이라고 지칭
			if(rs.next()) {
				totCount = rs.getInt(1);				
			}			
			rs.close();
			pstmt.close();
			conn.close();			
		} catch (Exception e) {
			System.out.println("전체 글 목록 수 처리 중 에러!!");
			e.printStackTrace();
		}
		 return totCount;
	 }
	
	
	//글 목록 메서드
	public List<ArticleVO> selectAllArticles() {
		List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
		try {
			conn = dataFactory.getConnection();
			// 계층형 쿼리문
			// LEVEL : 계층형 쿼리문을 만들 때 
			// 일반 게시판 : String query = "SELECT articleNo, title, content, ID, writeDate FROM boardtbl order by articleNo order by parentNo Desc";
			//imageFileName은 목록에서 보이지 않으므로 작성 x
			// 대문자 = 명령어(예약어, reserved), 소문자 = 개발자가 준 이름
			// START WITH : 뒤에 있는 조건을 참조하여 parentNo=0 이 최상위 계층임을 알려주는 것			
			String query = "SELECT LEVEL, articleNo, parentNo, title, content, ID, writeDate " + 
			"FROM boardtbl "
			+ "START WITH parentNo=0 "
			+ "CONNECT BY PRIOR articleNo=parentNo ORDER SIBLINGS BY articleNo DESC";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				//LEVEL 저장
				int level = rs.getInt("level");
				int articleNo = rs.getInt("articleNo");
				int parentNo = rs.getInt("parentNo");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");
				
				//글정보를 articleVO에 담음
				ArticleVO articleVO = new ArticleVO();
				articleVO.setLevel(level);
				articleVO.setArticleNo(articleNo);
				articleVO.setParentNo(parentNo);
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setId(id);
				articleVO.setWriteDate(writeDate);
				
				//모든 글목록을 articleList에 담음
				articlesList.add(articleVO);
			}
			
		} catch (Exception e) {
			System.out.println("글 목록 조회 중 에러!!");
			e.printStackTrace();
		}
		return articlesList;
	}// selectAllArticles() End
	
	//글 번호 생성 메서드
	private int getNewArticleNo() {
		int _articleNo = 1;
		try {
			conn = dataFactory.getConnection();
			//max함수를 이용하여 가장 큰 번호 조회
			String query = "select max(articleNo) from boardtbl";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery(); 
			if(rs.next()) {
				//getInt(1) : rs에 담긴 첫번째 칼럼
				//getInt(1)+1 : 글번호를 7로 만들어 줌
				_articleNo = rs.getInt(1)+1;			
			}
			rs.close();
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			System.out.println("새 글번호 가져오는 중 에러!!");
			e.printStackTrace();
		}
		return _articleNo;
	}//getNewArticleNo() End
	
	//새 글 추가 메서드
	//public void insertNewArticle(ArticleVO articleVO) {
	public int insertNewArticle(ArticleVO articleVO) {
		int articleNo = getNewArticleNo();
		try {
			conn = dataFactory.getConnection();
			//getParentNo() : 지금은 0 이지만 일단 그대로 받을 것
			int parentNo = articleVO.getParentNo();
			String title = articleVO.getTitle();
			String content = articleVO.getContent();
			String imageFileName = articleVO.getImageFileName();
			String id = articleVO.getId();
			
			//쿼리문 수행
			String query = "insert into boardtbl (articleNo, parentNo, title, content, imageFileName, id) values(?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(query);
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
		//return 추가
		return articleNo;
	}// 새 글 추가 메서드 End
	
	//선택한 글 내용 상세보기 메서드
	public ArticleVO selectArticles(int articleNo) {
		ArticleVO articleVO = new ArticleVO();
		//DB처리는 오류 커버 try-catch
		try {
			conn = dataFactory.getConnection();
			String query = "select articleNo, parentNo, title, content, " 
			//한글 이름 이미지 처리를 위해 이 부분 처리 해줘야 함. 영어로만 받는다면 안해도 됨
				//imageFileName에 아무것도 없으면 null을 넣어주고 아니면 파일 이름 그대로 가져온다는 뜻
			+ "NVL(imageFileName, 'null') as imageFileName, id, writeDate "
			+ "from boardtbl where articleNo=?";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			//글번호 가져오기 get("")
			int _articleNo = rs.getInt("articleNo");
			int parentNo = rs.getInt("parentNo");
			String title = rs.getString("title");
			String content = rs.getString("content");
			String imageFileName = URLEncoder.encode(rs.getString("imageFileName"),"utf-8");
			if(imageFileName.equals("null")) {
				//imageFileName이 null이면 null을 가져갈 것 - 왜? 굳이? , 무슨 변화가 있는건가?
				imageFileName = null;
			}
			String id = rs.getString("id");
			Date writeDate = rs.getDate("writeDate");
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
			System.out.println("글 내용 상세보기 구현중 에러!!");
			e.printStackTrace();
		}
		return articleVO;
	}// 글 내용 상세보기 메서드 End
	
	//글 수정하기 메서드
	public void updateArticle(ArticleVO articleVO) {
		int articleNo = articleVO.getArticleNo();
		String title = articleVO.getTitle();
		String content = articleVO.getContent();
		String imageFileName = articleVO.getImageFileName();
		try {
			conn=dataFactory.getConnection();
			String query = "UPDATE boardtbl SET title=?, content=?";
			//이미지를 바꿨을수도, 아닐 수도 있어서 if 추가
			if(imageFileName != null && imageFileName.length() != 0) {
				query += ", imagefileName=?";
			}
			query += " where articleNo=?";
			//
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			if(imageFileName != null && imageFileName.length() != 0) {
				pstmt.setString(3, imageFileName);
				pstmt.setInt(4, articleNo);
			} else {
				pstmt.setInt(3, articleNo);
			}
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
					
		} catch (Exception e) {
			System.out.println("글 수정 중 에러!!");
			e.printStackTrace();
		}
	}//수정 메서드updateArticle() End
	
	//삭제할 글 번호 목록 가져오기 메서드
	public List<Integer> selectRemovedArticles(int articleNo){
		List<Integer> articleNoList = new ArrayList<Integer>();
		try {
			conn = dataFactory.getConnection();
			//일반 게시판
			//String query = "select articleNo from boardtbl where articleNo=?"
			//답변형 게시판
			//start with : 최상위 찾기 = 내가 받은 글번호를 최상위 글로 하겠다.
			String query = "SELECT articleNo FROM boardtbl START WITH articleNO=?"
					+ " CONNECT BY PRIOR articleNo=parentNo";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				// getInt로 "컬럼명" 받기
				articleNo = rs.getInt("articleNo");
				articleNoList.add(articleNo);				
			}
			pstmt.close();
			conn.close();
					
		} catch (Exception e) {
			System.out.println("삭제할 글번호 목록 가져오기 중 에러!!");
			e.printStackTrace();
		}
		return articleNoList;
	}//삭제할 글번호 목록 가져오기 메서드 selectRemovedArticles() End
	
	//글 삭제 메서드
	public void deleteArticle(int articleNo) {
		try {
			conn=dataFactory.getConnection();
			String query = "DELETE FROM boardtbl WHERE articleNo in "
							+ "(SELECT articleNo FROM boardtbl START WITH articleNo=? "
							+ "CONNECT BY PRIOR articleNo=parentNo)";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("글 삭제 중 에러!!");
			e.printStackTrace();
		}
	}//글 삭제 메서드 deleteArticle() End
	
}