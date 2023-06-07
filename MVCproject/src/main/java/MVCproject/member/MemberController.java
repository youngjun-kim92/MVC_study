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
	
	MemberDAO dao;		//객체 선언
	
	
	@Override
	public void init() throws ServletException {
		dao=new MemberDAO(); 		//MemberDAO 객체를 생성
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage=null;
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String action=request.getPathInfo();					//요청명을 가져오는 메소드
		System.out.println("요청 매핑이름 : "+action);
		if(action==null||action.equals("/listMembers.do")) {
			List<MemberVO> membersList=dao.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage="/viewMember/listMembers.jsp";
		}
		else if(action.equals("/addMember.do")) {
			String id=request.getParameter("id");
			String pwd=request.getParameter("pwd");
			String name=request.getParameter("name");
			String email=request.getParameter("email");
			MemberVO memVo=new MemberVO(id, pwd, name, email);
			dao.addMember(memVo);
			request.setAttribute("msg", "addMember");
			nextPage="/member/listMembers.do";
		}
		else if(action.equals("/memberForm.do")) {
			nextPage="/viewMember/memberForm.jsp";
		}
		else if(action.equals("/modMemberForm.do")) {
			String id=request.getParameter("id");
			MemberVO memFindInfo=dao.findMember(id);
			request.setAttribute("memFindInfo", memFindInfo);
			nextPage="/viewMember/modMemberForm.jsp";
		}
		else if(action.equals("/modMember.do")) {
			String id=request.getParameter("id");
			String pwd=request.getParameter("pwd");
			String name=request.getParameter("name");
			String email=request.getParameter("email");
			MemberVO memVo=new MemberVO(id, pwd, name, email);
			dao.modMember(memVo);
			request.setAttribute("msg", "modified");
			nextPage="/member/listMembers.do";
		}
		else if(action.equals("/delMember.do")) {
			String id=request.getParameter("id");
			dao.delMember(id);
			request.setAttribute("msg", "deleted");
			nextPage="/member/listMembers.do";
		}
		else {
			List<MemberVO> membersList=dao.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage="/viewMember/listMembers.jsp";
		}
		RequestDispatcher dispatcher=request.getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);
	}

}
