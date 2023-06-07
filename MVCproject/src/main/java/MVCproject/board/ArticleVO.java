package MVCproject.board;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;

public class ArticleVO {
	private int level;		//글의 깊이를 저장하는 필드
	private int articleNo;	//글번호
	private int parentNo;	//부모글번호
	private String title;	//글제목
	private String content;	//글내용
	private String imageFileName;	//이미지파일이름
	private String id;
	private Date writeDate;
	
	public ArticleVO() {
		System.out.println("ArticleVO생성");
	}

	public ArticleVO(int level, int articleNo, int parentNo, String title, String content, String imageFileName, String id,
			Date writeDate) {
		this.level = level;
		this.articleNo = articleNo;
		this.parentNo = parentNo;
		this.title = title;
		this.content = content;
		this.imageFileName = imageFileName;
		this.id=id;
		this.writeDate = writeDate;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getArticleNo() {
		return articleNo;
	}

	public void setArticleNo(int articleNo) {
		this.articleNo = articleNo;
	}

	public int getParentNo() {
		return parentNo;
	}

	public void setParentNo(int parentNo) {
		this.parentNo = parentNo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageFileName() {
		try {
			if(imageFileName!=null&&imageFileName.length()!=0) {
				imageFileName=URLDecoder.decode(imageFileName,"utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("이미지 읽는 중 에러!");
		}
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		try {
			if(imageFileName!=null&&imageFileName.length()!=0) {
				this.imageFileName=URLEncoder.encode(imageFileName,"utf-8");
			}
			else {
				this.imageFileName=null;
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("이미지 저장 중 에러!");
		}
		this.imageFileName = imageFileName;
	}

	public Date getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}

