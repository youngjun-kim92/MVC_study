package MVCproject.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {
	BoardDAO boardDAO;
	
	public BoardService() {
		boardDAO=new BoardDAO();
	}
	
	public Map listArticles(Map<String, Integer> pagingMap) {
		Map articleMap=new HashMap();
		List<ArticleVO> articlesList=boardDAO.selectAllArticles(pagingMap);
		int totAreticles=boardDAO.selectToArticles();
	}
	
//	//글 목록 보기
//	public List<ArticleVO> listArticles() {
//		List<ArticleVO> articlesList=boardDAO.selectAllArticles();
//		return articlesList;
//	}
	
	//글 추가
	public int addArticle(ArticleVO articleVO) {
		return boardDAO.insertNewArticle(articleVO);
	}
	
	//상세 글 보기
	public ArticleVO viewArticle(int articleNo) {
		ArticleVO articleVO=null;
		articleVO=boardDAO.selectArticle(articleNo);
		return articleVO;
	}
	
	//수정
	public void modArticle(ArticleVO articleVO) {
		boardDAO.updateArticle(articleVO);
	}
	
	//삭제
	public List<Integer> removeArticle(int articleNo) {
		List<Integer> articleNoList=boardDAO.selectRemovedArticles(articleNo);
		boardDAO.deleteArticle(articleNo);
		return articleNoList;
	}
	
	//답글 추가
	public int addReply(ArticleVO articleVO) {
		return boardDAO.insertNewArticle(articleVO);
	}
	
}
