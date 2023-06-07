package MVCproject.board;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;

import org.apache.taglibs.standard.tag.common.sql.UpdateTagSupport;

public class ArticleVO {
	//필드
	//글의 깊이(계층, 들여쓰기)를 저장하는 필드. (DB설계에는 없음)	
	private int level;
	private int articleNo;
	private int parentNo;
	private String title;
	private String content;
	private String imageFileName;
	private Date writeDate;
	private String id;
	
	//생성자
	public ArticleVO() {
		System.out.println("ArticleVO 생성");
	}

	public ArticleVO(int level, int articleNo, int parentNo, String title, String content, String imageFileName,
			Date writeDate, String id) {		
		this.level = level;
		this.articleNo = articleNo;
		this.parentNo = parentNo;
		this.title = title;
		this.content = content;
		this.imageFileName = imageFileName;
		this.writeDate = writeDate;
		this.id = id;
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
		//상세보기 구현
		try {
			if(imageFileName != null && imageFileName.length() != 0 ) {
				imageFileName = URLDecoder.decode(imageFileName, "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("이미지 읽는 중 에러!!");
		}
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		try {
			if(imageFileName != null && imageFileName.length() != 0 ) {
				this.imageFileName = URLEncoder.encode(imageFileName, "utf-8");
			} else {
				this.imageFileName = null;				
			}
		} catch (UnsupportedEncodingException e) {
			//인코딩 중 에러날 수 있음
			System.out.println("이미지 저장 중 에러!!");
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
