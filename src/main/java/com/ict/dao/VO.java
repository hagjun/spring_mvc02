package com.ict.dao;

import org.springframework.web.multipart.MultipartFile;

public class VO {
	// f_name => DB컬럼과 같으므로 DB에 저장할 때 사용 
	private String idx, name, subject, content, email, pwd, f_name, regdate, old_f_name;
	// 클라이언트가 보낸 파일에 대한 파라미터에 저장할 때 사용할 변수 : file
	// Multipart 에 VO 담아서 저장
	private MultipartFile file ; // 파일 업로드 할때 필요함
	
	public String getIdx() {
		return idx;
	}
	public void setIdx(String idx) {
		this.idx = idx;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getF_name() {
		return f_name;
	}
	public void setF_name(String f_name) {
		this.f_name = f_name;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public String getOld_f_name() {
		return old_f_name;
	}
	public void setOld_f_name(String old_f_name) {
		this.old_f_name = old_f_name;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	
	
	
}
