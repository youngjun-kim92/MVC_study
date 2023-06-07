package MVCproject.board;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;




@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	BoardService boardService;
	ArticleVO articleVO;
	//글에 첨부한 이미지 저장위치를 상수(?..final은 없지만 ㅎ)로 선언
	private static String ARTICLE_IMG_REPO = "D:\\mornstarY\\board\\image_upload";
	
	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage="";
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		//새 글이 추가되었습니다 스크립트를 남기려고 print 찍음
		PrintWriter out;
		//세선 변수 선언
		HttpSession session;
		
		String action = request.getPathInfo(); //요청명을 가져옴
		System.out.println("요청이름 : " + action);
		
		try {
			//글 목록을 담을 변수 articlesList
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			//getPathInfo()는 추가 경로 정보가 없으면 null을 반환 (그래서 조회페이지)
			if (action == null || action.equals("/listArticles.do")) {
				
				// 페이징 시작 !
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
					/* 최초 요청시에는 section이나 page에 null값이 들어있으므로 1을 반환할 것(세팅) */
				int section = Integer.parseInt((_section == null)?"1":_section);
				int pageNum = Integer.parseInt((_pageNum == null)?"1":_pageNum);
				
				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);				
				
				//listArticles()라는 메서드 호출 할 예정
				//articlesList = boardService.listArticles();				
				Map articleMap = boardService.listArticles(pagingMap);
				//listArticles()다녀와서
				articleMap.put("section", section);
				articleMap.put("pageNum", pageNum);
				request.setAttribute("articleMap", articleMap);
				//request.setAttribute("_articlesList", articlesList);
				nextPage = "/viewBoard/listArticles.jsp";
			} else if (action.equals("/articleForm.do")) {
				//글쓰기 페이지로 이동
				nextPage = "/viewBoard/articleForm.jsp";				
			} else if (action.equals("/addArticle.do"))	{
				// 이미지 업로드 방식을 위한 작업(반환받은 새 글 번호를 저장하는 경우)
				int articleNo = 0;
				
				//글 등록해서 돌아온 경우
				Map<String, String> articleMap = upload(request, response);				
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				//기본 0으로 세팅했기 때문에 굳이 안적어도 됨
				articleVO.setParentNo(0);
				articleVO.setId("happy12"); //로그인DB연결을 한게 아니라서 임시로 작성자로 등록. 제대로 연결하는건 스프링때 
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				//이미지를 첨부하지 않았으면 에러남 
				//insert문을 위해 boardService다녀옴
				//글번호에 맞는 이미지를 위해 insert수행 후 생성된 글번호 반환 받을 것
				articleNo = boardService.addArticle(articleVO);
				//새 글 추가시 이미지를 첨부할 경우에만 if문 수행
				if(imageFileName != null && imageFileName.length() != 0) {
				//임시폴더에 이미지 저장 - 글번호 파일 생성 - 폴더 생성 - 파일을 생성 폴더로 이동 - 임시폴더에 이미지파일 삭제
				//일단 이미지 저장 소스 (File : 파일 관련한 처리를 담당하는 클래스)
					File srcFile = new File(ARTICLE_IMG_REPO + "\\temp\\" + imageFileName);
				//글번호로 File객체 생성
					File destDir = new File(ARTICLE_IMG_REPO + "\\" + articleNo);
				//mkdirs() 폴더 생성 메서드
					destDir.mkdirs();
				//moveFileToDirectory(scrFile, destDir, false/true) : 파일을 디렉터리로 이동
				//srcFile를 destDir로, true일 때 옮김/ false는 안 옮기는 것
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				//임시폴더에 있는 파일 삭제
					srcFile.delete();				
				}
				//새 글이 등록되었습니다 프린트 - 다른 포워딩 방법 쓸 것
				out = response.getWriter();
				out.print("<script>");
				out.print("alert('새 글을 추가했습니다');");
					//location 포워딩 
				out.print("location.href='" + request.getContextPath() + 
						"/board/listArticles.do';");
				out.print("</script>");
				return;  //아래에 있는 포워딩 없이 돌아감
				//다 다녀오면 추가한 사람까지 목록 보기
				//nextPage = "/board/listArticles.do";	
				
			} else if(action.equals("/viewArticle.do")) {
				//listArticles.jsp에서 넘어옴
				//선택한 글 내용 상세보기
				String articleNo = request.getParameter("articleNo");
				//viewArticle메서드를 다녀옴
				//getParameter로 받은 "articleNo"는 문자라서 String으로 받았지만 DB는 number이므로 숫자로 바꿈
				articleVO = boardService.viewArticle(Integer.parseInt(articleNo));
				request.setAttribute("article", articleVO);
				//상세 글 보는 viewArticle.jsp 화면으로 포워딩
				nextPage = "/viewBoard/viewArticle.jsp";
				
			} else if(action.equals("/modArticle.do")) {
				//수정작업 하러 옴
				//Map의 역할이 뭐였지?
				Map<String, String> articleMap = upload(request, response);
				int articleNo = Integer.parseInt(articleMap.get("articleNo"));
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setArticleNo(articleNo);
				articleVO.setParentNo(0);
				articleVO.setId("happy12");
				//실제로는 id도 작성자 id부분을 input=hidden으로 같이 가져와야 함
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.modArticle(articleVO);
				
				//이미지를 새로 첨부한 경우에만 수행
				if(imageFileName != null && imageFileName.length() != 0) {
					//새 이미지를 글 쓴것처럼 경로에 삽입하고, 기존이미지(oldFile)는 그 경로 위치에서 삭제  
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMG_REPO + "\\temp\\" + imageFileName);
					File destDir = new File(ARTICLE_IMG_REPO + "\\" + articleNo);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					File oldFile = new File(ARTICLE_IMG_REPO + "\\" + articleNo + "\\" + originalFileName);
					oldFile.delete();					
				}				
				// 알림창과 수정된 글 보여주기
				out = response.getWriter();
				out.print("<script>");
				out.print("alert('글을 수정했습니다');");
					//location 포워딩 - 수정한 글 정보를 다시 보여주는 화면으로 돌아가게 함 
				out.print("location.href='" + request.getContextPath() + 
						"/board/viewArticle.do?articleNo=" + articleNo + "';");
				out.print("</script>");
				return; 			
				
			} else if(action.equals("/removeArticle.do")) {
				int articleNo = Integer.parseInt(request.getParameter("articleNo"));
				//이미지도 삭제하려고 삭제된 리스트 기억해서 돌아옴
				List<Integer> articleNoList = boardService.removeArticle(articleNo);
				for(int _articleNo:articleNoList) {
					//이미지 저장 폴더
					File imgDir = new File(ARTICLE_IMG_REPO + "\\" + _articleNo);
					//이미지 폴더가 존재하냐고 물음
					if(imgDir.exists()) {
						//존재하면 폴더(directory) 삭제
						FileUtils.deleteDirectory(imgDir);
					}
				}// for End
				// 알림창과 나머지 목록 보여주기
				out = response.getWriter();
				out.print("<script>");
				out.print("alert('글을 삭제했습니다');");
					//location 포워딩 - 수정한 글 정보를 다시 보여주는 화면으로 돌아가게 함 
				out.print("location.href='" + request.getContextPath() + 
						"/board/listArticles.do?articleNo=" + articleNo + "';");
				out.print("</script>");
				return; 							
			}///removeArticle.do End
			else if(action.equals("/replyForm.do")) {
				//부모글번호를 저장한채로 답글작성화면으로 이동
				String req = request.getParameter("parentNo");
				if(req == null) return;
				int parentNo = Integer.parseInt(req);
				session = request.getSession();
				session.setAttribute("parentNo", parentNo);
				nextPage = "/viewBoard/replyForm.jsp";
			} else if(action.equals("/addReply.do")) {
				//답변글 작성한 정보 가져옴
				session = request.getSession();
				//세션에 보관되어 있던 parentNo를 가져옴 (= 원글(부모)의 articleNo)
				int parentNo = (Integer)session.getAttribute("parentNo");
				//반드시 지워주어야 함. 기존의 부모글 정보를 남겨놓지 않기 위해
				session.removeAttribute("parentNo");
				//articleMap : article에 대한 정보 담아오기
				Map<String, String> articleMap = upload(request, response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNo(parentNo);
				articleVO.setId("son");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				//이미지가 있다면 articleNo를 이용해서 폴더를 만들어야 하기때문에 addReply() 갔다가 번호 받아옴
				int articleNo = boardService.addReply(articleVO);	
				
				if(imageFileName != null && imageFileName.length() != 0 ) {
					//이미지 넣었을 때 처리
					File srcFile = new File(ARTICLE_IMG_REPO + "\\temp\\" + imageFileName);//첨부된이미지첨부경로
					File destDir = new File(ARTICLE_IMG_REPO + "\\" + articleNo); //저장경로
					destDir.mkdirs(); //디렉터리생성
					FileUtils.moveFileToDirectory(srcFile, destDir, true); //파일옮기기					
				}
				out = response.getWriter();
				out.print("<script>");
				out.print("alert('답글을 추가했습니다.');");
					//location 포워딩 - 수정한 글 정보를 다시 보여주는 화면으로 돌아가게 함 
				out.print("location.href='" + request.getContextPath() + 
						"/board/viewArticle.do?articleNo=" + articleNo + "';");
				out.print("</script>");
				return; 						
			}//답글추가 addReply.do End
			
			//포워딩
			RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
			dispatcher.forward(request, response);
			
		} catch (Exception e) {
			System.out.println("요청 처리 중 에러!!");
			e.printStackTrace();
		}		
	}
	
	//이미지 파일 업로드 + 새 글 관련 정보 저장
	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//return값 없으면 에러
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding="utf-8";
		//글 이미지 저장 폴더에 대한 파일 객체를 생성
		File currentDirPath = new File(ARTICLE_IMG_REPO);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//저장소 세팅
		factory.setRepository(currentDirPath);
		//최대 크기. (더 큰 파일이 들어오면, 버퍼를 이용해서 처리하게 됨)
		factory.setSizeThreshold(1024*1024);		
		ServletFileUpload upload = new ServletFileUpload(factory);
				
		//파일 관련은 항상 try-catch
		try {
			//글제목, 글내용, 이미지 첨부가 객체 파일로 날아옴
			List items = upload.parseRequest(request);
			for(int i=0; i<items.size(); i++) {
				FileItem fileItem = (FileItem)items.get(i);
				if(fileItem.isFormField()) {
				//form에 관련된 것인지 => (제목이나 내용)
					System.out.println(fileItem.getFieldName() + "=" + 
										fileItem.getString(encoding));					
					//파일 업로드할 이미지와 같이 전송된 새글 관련(제목, 내용) 매개변수를 Map(키, 값)으로 저장
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
				} else {
					//form 관련이 아닌지 => (이미지)
					System.out.println("매개변수이름 : " + fileItem.getFieldName());
					System.out.println("파일(이미지)이름 : " + fileItem.getName());
					System.out.println("파일(이미지)크기 : " + fileItem.getSize() + "bytes");
					if(fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						if(idx == -1) {
							//idx == -1 : 경로가 c:/ 로 / 처럼 표시될 때
							idx = fileItem.getName().lastIndexOf("/");
						}
						//fileName : 실제 첨부한 이미지 이름
						String fileName = fileItem.getName().substring(idx + 1);
						// DB에 이미지 fileName담기
						articleMap.put(fileItem.getFieldName(), fileName);
						
						//파일 처리(파일 경로 처리)
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						// 실제 저장
						fileItem.write(uploadFile);								
					}
				}
			}			
		} catch (Exception e) {
			System.out.println("이미지 파일 업로드 중 에러!!");
			e.printStackTrace();
		}
		
		
		return articleMap;
	}
} //BoardController End