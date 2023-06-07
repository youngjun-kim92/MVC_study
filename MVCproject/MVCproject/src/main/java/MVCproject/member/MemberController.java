package MVCproject.member;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/member/*")
public class MemberController extends HttpServlet {
	MemberDAO dao;
	
	@Override
	public void init() throws ServletException {
		dao = new MemberDAO(); //MemberDAO객체를 생성
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = null;
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		// getPathInfo() : 요청명을 가져오는 메소드
		String action = request.getPathInfo();
		System.out.println("요청 매핑이름 : " + action);
		if(action == null || action.equals("/listMembers.do")) {
			// 회원 목록 조회
			List<MemberVO> membersList = dao.listMembers(); //DAO에서 받아옴
			request.setAttribute("membersList", membersList);
			nextPage = "/viewMember/listMembers.jsp";
			
		} else if (action.equals("/addMember.do")) {
			// ("/addMember.do") / 꼭 써야 함(경로 표시)
			//회원 등록 (추가)
			
			//1. 일단 회원 가입 버튼 누르면 (2. Form에서 정보입력 후 가입하기 누르면) 3.넘어와서 작업
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			
			//DB에 저장하기 위해 세팅
			MemberVO memVo = new MemberVO(id, pwd, name, email);
			dao.addMember(memVo);
			
			//알림창
			request.setAttribute("msg", "addMember");
			
			//방금 추가한 회원목록도 추가하여 조회하기 위해 다시 select문이 수행하게 함
			//그래서 nextPage = "/member/listMembers.jsp";가 아님
			nextPage = "/member/listMembers.do";			
			
		} else if(action.equals("/memberForm.do")) {
			// 1. 회원가입페이지로 이동
			nextPage = "/viewMember/memberForm.jsp";
			
		} else if(action.equals("/modmemberForm.do")) {
			//회원정보 수정
			String id = request.getParameter("id");
			//아이디를 찾는 메소드 findMember() 만들 것. 찾은 id를 memVo에 넣어옴
			MemberVO memFindInfo = dao.findMember(id);			
			
			//바꾼 정보 담기
			request.setAttribute("memFindInfo", memFindInfo);
			//회원정보 수정 페이지로 이동
			nextPage = "/viewMember/modMemberForm.jsp";
			
		} else if(action.equals("/modMember.do")){
			//회원 정보 수정 페이지 새로고침- 회원 수정 메소드 호출됨
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			MemberVO memVo = new MemberVO(id, pwd, name, email);
			dao.modMember(memVo);
			
			//알림창
			request.setAttribute("msg", "modified");
			
			nextPage = "/member/listMembers.do";
			
		} else if(action.equals("/delMember.do")) {
			//회원 탈퇴 (삭제)
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			MemberVO memVo = new MemberVO(id, pwd, name, email);
			dao.delMember(memVo);
			
			//알림창
			request.setAttribute("msg", "deleted");
			
			nextPage = "/member/listMembers.do";			
		} else {
			// 회원 목록 조회
			List<MemberVO> membersList = dao.listMembers(); //DAO에서 받아옴
			request.setAttribute("membersList", membersList);
			nextPage = "/viewMember/listMembers.jsp";
		}
//		RequestDispatcher dispatcher = request.getRequestDispatcher("/viewMember/listMembers.jsp");
		RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);
	}
}
