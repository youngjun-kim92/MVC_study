package MVCproject.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {	
	BoardDAO boardDAO;
	
	//생성자
	public BoardService() {
		boardDAO = new BoardDAO();
	}
	//페이징 했을 때 목록보기(오버라이딩)
	public Map listArticles(Map<String, Integer> pagingMap) {
		Map articleMap = new HashMap<>();
		//페이징 목록 가져오기
		List<ArticleVO> articlesList = boardDAO.selectAllArticles(pagingMap);
		//총 글 갯수를 넘겨받을 것
		int totArticles = boardDAO.selectToArticles();
		articleMap.put("articlesList", articlesList);
		//next prev 테스트
		//articleMap.put("totArticles", totArticles);
		articleMap.put("totArticles", 230);
		//Map에는 글 자료, 총 갯수가 담겨있음
		return articleMap;
	}
	
	//페이징 안했을 때 목록보기
	public List<ArticleVO> listArticles() {
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;		
	}
	
	//새 글 추가 메서드 호출
	//public void addArticle(ArticleVO articleVO) {
		//boardDAO.insertNewArticle(articleVO);
	public int addArticle(ArticleVO articleVO) {
		return boardDAO.insertNewArticle(articleVO);
	}
	
	//선택한 글 내용 상세보기
	public ArticleVO viewArticle(int articleNo) {
		ArticleVO articleVO = null;
		//선택된 글번호를 보여주는 메서드 selectArticles()를 호출
		articleVO = boardDAO.selectArticles(articleNo);
		return articleVO;
	}
	
	//수정된 글 보기
	public void modArticle(ArticleVO articleVO) {
		boardDAO.updateArticle(articleVO);
	}	
	// 글 삭제하기 호출
	public List<Integer> removeArticle(int articleNo) {
		List<Integer> articleNoList = boardDAO.selectRemovedArticles(articleNo);
		boardDAO.deleteArticle(articleNo);
		return articleNoList;
	}
	// 답글 저장하기 호출
	public int addReply(ArticleVO articleVO) {
		return boardDAO.insertNewArticle(articleVO);
	}
	
}//class BoardService End