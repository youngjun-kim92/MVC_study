package MVCproject.member;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.spi.DirStateFactory.Result;
import javax.sql.DataSource;

public class MemberDAO {
	
	private Connection conn;
	private PreparedStatement pstmt;
	private DataSource dataFactory;
	
	public MemberDAO() {
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
	
	//회원 목록 메서드
	public List<MemberVO> listMembers() {
		List<MemberVO> membersList = new ArrayList();
		try {
			conn = dataFactory.getConnection();
			
			String query = "select * from membertbl order by joinDate desc";
			//공부하는 단계에서 이거 확인해주면 좋음
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				//("")안의 값은 컬럼명과 같은지 꼭 확인
				String id = rs.getString("id");
				String pwd = rs.getString("pwd");
				String name = rs.getString("name");
				String email = rs.getString("email");
				Date joinDate = rs.getDate("joinDate") ;
				MemberVO memVo = new MemberVO(id, pwd, name, email, joinDate);				
				membersList.add(memVo);
			}
			rs.close();
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			System.out.println("DB 조회 중 에러!!");
			e.printStackTrace();
		}
		return membersList; //MemberController로 넘길것
	}
	
	//회원 등록 메서드
	public void addMember(MemberVO memVo) {
		try {
			conn = dataFactory.getConnection();
			String id = memVo.getId();
			String pwd = memVo.getPwd();
			String name = memVo.getName();
			String email = memVo.getEmail();
			
			String query = "insert into membertbl (id, pwd, name, email) values(?, ?, ?, ?)";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			pstmt.setString(3, name);
			pstmt.setString(4, email);
			pstmt.executeUpdate(); //자료 등록 실행(이걸 빼먹으면 insert가 수행X)
			pstmt.close();
			conn.close();		
					
		} catch (Exception e) {
			System.out.println("DB 등록 중 에러!!");
			e.printStackTrace();
		}
	}//addMember() End
	
	//수정할 회원정보 찾는 메서드
	public MemberVO findMember(String _id) {
		MemberVO memFind = null;
		try {
			//DB 연결 작업
			conn = dataFactory.getConnection();
			//찾기
			String query = "select * from membertbl where id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, _id);
			System.out.println(query);
			
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			//수정할 변수 말고 다른 정보도 나오도록 다 넣어줌
			String id = rs.getString("id");
			String pwd = rs.getString("pwd");
			String name = rs.getString("name");
			String email = rs.getString("email");
			Date joinDate = rs.getDate("joinDate");
			
			memFind = new MemberVO(id, pwd, name, email, joinDate);
						
			pstmt.close();
			rs.close();
			conn.close();
			
		} catch (Exception e) {
			System.out.println("수정할 DB자료 찾는 중 에러!!");
			e.printStackTrace();
		}		
		return memFind;
	} //findMember() End
	
	//회원 정보 수정 메서드
 	public void modMember(MemberVO memVo) {
 		String id = memVo.getId();
 		String pwd = memVo.getPwd();
 		String name = memVo.getName();
 		String email = memVo.getEmail();
 		//DB는 오류가 날 수 있으므로 항상 try-catch 사용
 		try {
			conn = dataFactory.getConnection();
			String query = "update membertbl set pwd=?, name=?, email=? where id=?";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);		
			pstmt.setString(1, pwd);
			pstmt.setString(2, name);
			pstmt.setString(3, email);
			pstmt.setString(4, id);
			pstmt.executeUpdate();
						
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			System.out.println("회원정보 수정 중 에러!!");
			e.printStackTrace();
		}
 	} // modMember End
 	
 	//회원 탈퇴(삭제) 메서드
 	public void delMember(MemberVO memVo) {
 		String id = memVo.getId();
 		
 		//DB는 오류가 날 수 있으므로 항상 try-catch 사용
 		try {
 			conn = dataFactory.getConnection();
			String query = "delete membertbl where id=?";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			
			pstmt.setString(1, id);		
			pstmt.executeUpdate();
						
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			System.out.println("회원 삭제 중 에러!!");
			e.printStackTrace();
		}
 	}

	
} // MemberDAO End