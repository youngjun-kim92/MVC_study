package MVCproject.member;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class MemberDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private DataSource dataFactory;
	
	public MemberDAO() {
		try {
			//JNDI(Java Naming and Directory Interface)를 이용
			//필요한 자원을 키/값의 쌍으로 저장한 후 필요한 키를 이용해 값을 얻는 방법
			Context ctx=new InitialContext();
			Context envContext=(Context)ctx.lookup("java:/comp/env");
			dataFactory=(DataSource)envContext.lookup("jdbc/oracle");
		} 
		catch (Exception e) {
			System.out.println("DB연결 오류");
		}
	}
	
	//회원 목록 메소드
	public List<MemberVO> listMembers() {
		List<MemberVO> membersList=new ArrayList();
		try {
			conn=dataFactory.getConnection();
			String query="select * from membertbl order by joinDate desc";
			System.out.println(query);
			pstmt=conn.prepareStatement(query);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()) {
				String id=rs.getString("id");
				String pwd=rs.getString("pwd");
				String name=rs.getString("name");
				String email=rs.getString("email");
				Date joinDate=rs.getDate("joinDate");
				MemberVO memVo=new MemberVO(id, pwd, name, email, joinDate);
				membersList.add(memVo);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("DB 조회 중 에러!!");
			e.printStackTrace();
		}
		return membersList;
	}
	
	//회원 등록 메소드
	public void addMember(MemberVO memVo) {
		try {
			conn=dataFactory.getConnection();
			String id=memVo.getId();
			String pwd=memVo.getPwd();
			String name=memVo.getName();
			String email=memVo.getEmail();
			String query="insert into membertbl (id,pwd,name,email) values(?,?,?,?)";
			System.out.println(query);
			pstmt=conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			pstmt.setString(3, name);
			pstmt.setString(4, email);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("DB 등록 중 에러!!");
			e.printStackTrace();
		}
	}
	
	//수정할 회원정보 찾는 메소드
	public MemberVO findMember(String _id) {
		MemberVO memFind=null;
		try {
			conn=dataFactory.getConnection();
			String query="select * from membertbl where id=?";
			pstmt=conn.prepareStatement(query);
			pstmt.setString(1, _id);
			System.out.println(query);
			ResultSet rs=pstmt.executeQuery();
			rs.next();
			String id=rs.getString("id");
			String pwd=rs.getString("pwd");
			String name=rs.getString("name");
			String email=rs.getString("email");
			Date joinDate=rs.getDate("joinDate");
			memFind=new MemberVO(id, pwd, name, email, joinDate);
			pstmt.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("수정할 자료 찾는 중 에러!!");
			e.printStackTrace();
		}
		return memFind;
	}
	
	//회원 정보 수정 메소드
	public void modMember(MemberVO memVo) {
		String id=memVo.getId();
		String pwd=memVo.getPwd();
		String name=memVo.getName();
		String email=memVo.getEmail();
		try {
			conn=dataFactory.getConnection();
			String query="update membertbl set pwd=?, name=?, email=? where id=?";
			System.out.println(query);
			pstmt=conn.prepareStatement(query);
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
	}
	//회원 정보 삭제 메소드
	public void delMember(String id) {
		try {
			conn=dataFactory.getConnection();
			String query="delete * from membertbl where id=?";
			System.out.println(query);
			pstmt=conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("회원정보 삭제 중 에러!!");
			e.printStackTrace();
		}
	}
	
	
}
