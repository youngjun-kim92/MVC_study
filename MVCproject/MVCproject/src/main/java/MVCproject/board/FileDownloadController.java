package MVCproject.board;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/download.do")
public class FileDownloadController extends HttpServlet {
	//업로드 경로를 선언
	private static String ARTICLE_IMG_REPO = "D:\\mornstarY\\board\\image_upload";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		doHandle(request, response);
	}
	
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
	//글 번호와 이미지 파일이름을 가져옴(파라메터로 받음)
		String articleNo = request.getParameter("articleNo");
		String imageFileName = request.getParameter("imageFileName");
	//OutputStream : 읽어와서 클라이언트 화면에 뿌려줌 - outs 객체 생성
		OutputStream outs = response.getOutputStream();
		//경로
		String path = ARTICLE_IMG_REPO + "\\" + articleNo + "\\" + imageFileName;
		File imageFile = new File(path);
	//setHeader 캐시를 사용하지 않게 설정
		response.setHeader("Cache-Control", "no-cache");
	//response 헤더 정보 설정 - 이미지 파일을 내려받는데 필요한 정보 설정
		response.addHeader("Content-disposition", "attachment;fileName=" + imageFileName);
		FileInputStream fis = new FileInputStream(imageFile);
		byte[] buffer = new byte[1024*8]; //버퍼를 이용해서 한번에 8kb씩 전송
		while(true) {
			int count = fis.read(buffer);
			//더 이상 읽을 것이 없으면 while문 탈출
			if(count == -1) break;
			outs.write(buffer, 0, count);
		}
		fis.close();
		outs.close();		
	}
}