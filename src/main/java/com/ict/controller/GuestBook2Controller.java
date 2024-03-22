package com.ict.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ict.dao.VO;
import com.ict.service.GuestBook2Service;

@Controller
public class GuestBook2Controller {
	@Autowired // 다른 클래스를 사용하기 위해서 Autowired 했다.
	private GuestBook2Service guestBook2Service;
	
	// 암호화는 스프링 security 에서 지원하므로 pom.xml에서 추가해야 된다.
	// spring_security.xml을 만들어서 bean을 생성.
	// web.xml 에서 지정해줘야 spring_security.xml 읽을 수 있도록 지정
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
//	@GetMapping("/")
//	public ModelAndView getFirst() {
//		return new ModelAndView("redirect:gb2_list.do");
//	}
	// 리스트
	@GetMapping("gb2_list.do")
	public ModelAndView getGuestbook2List() {
		ModelAndView mv = new ModelAndView("list");
		List<VO> list = guestBook2Service.getGuestBook2List();
		if(list != null) {
			mv.addObject("list", list);
			return mv;
		}
		return new ModelAndView("error");
		
	}
	// 방명록 쓰기
	@GetMapping("gb2_write.do")
	public ModelAndView getGuestbook2Write() {
		return new ModelAndView("write");
	}
	
	@PostMapping("gb2_write_ok.do")
	public ModelAndView getGuestbook2WriteOK(VO vo, HttpServletRequest request) {
		try {
			ModelAndView mv = new ModelAndView("redirect:gb2_list.do");
			String path = request.getSession().getServletContext().getRealPath("/resources/upload");

			// 넘어온 파일의 정보 중 파일의 이름은 f_name에 넣어줘야 DB에 저장 할 수있다.
			MultipartFile file = vo.getFile();
			if(file.isEmpty()) { // file을 올릴 수 있고 안올릴 수 있어서
				vo.setF_name(""); // file 이 아무것도 없습니다.
			}else { // 파일 이름을 변경해야 한다.(중복되면 안되기 때문에)
				// 파라미터로 받은 file을 이용해서 DB에 저장할 f_name을 채워주자.
				// 그러나 같은 이름의 파일이 있으면 업로드가 안되므로 
				// 파일이름을 UUID를 이용해서 변경 후 DB에 저장하자.
				UUID uuid = UUID.randomUUID();
				String f_name = uuid.toString()+"_"+file.getOriginalFilename(); 
				vo.setF_name(f_name);
				//////////////////////// DB 저장 끝 ///////////////////////////
				
				/////////////////////// 이미지 저장 ////////////////////////////
				byte[] in = vo.getFile().getBytes();
				File out = new File(path, f_name);
				FileCopyUtils.copy(in, out);
			}
			////////////////////////// 패스워드 암호화 //////////////////////////
			String pwd = passwordEncoder.encode(vo.getPwd());
			vo.setPwd(pwd);
			
			// DB 저장
			int result = guestBook2Service.getGuestBook2Insert(vo);
			if(result > 0) {
				return mv;
			}
			return new ModelAndView("error");
		} catch (Exception e) {
			System.out.println(e);
		}
		return new ModelAndView("error");

	}
	
	// 상세보기
	@GetMapping("gb2_detail.do")
	public ModelAndView getGuestBook2Detail(String idx) {
		ModelAndView mv = new ModelAndView("onelist");
		VO vo = guestBook2Service.getGuestBook2Detail(idx);
		if(vo != null) {
			mv.addObject("vo", vo);
			return mv;
			
		}
		return new ModelAndView("error");
	}
	// 파일 업로드
	@GetMapping("guestbook2_down.do")
	public void getGuestBook2Down(HttpServletRequest request, HttpServletResponse response) {
		try {
			String f_name = request.getParameter("f_name");
			String path = request.getSession().getServletContext().getRealPath("/resources/upload/"+f_name);
			String r_path = URLEncoder.encode(path, "UTF-8");
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachement; filename="+r_path);
			
			File file = new File(new String(path.getBytes(), "UTF-8"));
			FileInputStream in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();
			FileCopyUtils.copy(in, out);
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	@PostMapping("gb2_delete.do")
	public ModelAndView getGuestBook2Delete(@ModelAttribute("vo2")VO vo) { // 파라미터값 idx 받음
		return new ModelAndView("delete");
	}
	
	@PostMapping("gb2_delete_ok.do")
	public ModelAndView getGuestBook2DeleteOK(VO vo) {
		ModelAndView mv = new ModelAndView();
		
		// 비밀번호가 맞는지/틀린지 검사하자.(DB에 있는 비밀번호가 암호화 되어있다.)
		// jsp에서 입력한 pwd
		String cpwd = vo.getPwd();
		
		VO vo2 = guestBook2Service.getGuestBook2Detail(vo.getIdx());
		
		// DB에서 가지고 온 암호화된 pwd
		String dpwd = vo2.getPwd();
		
		// passwordEncoder.matches(암호화X, 암호화O)
		// 일치하면 true 일치하지 않으면 false
		if(! passwordEncoder.matches(cpwd, dpwd)) {
			mv.setViewName("delete");
			mv.addObject("pwdchk", "fail");
			mv.addObject("vo2", vo);
			return mv;
		}else {
			int result = guestBook2Service.getGuestBook2Delete(vo.getIdx());
			if(result > 0) {
				mv.setViewName("redirect:gb2_list.do");
				return mv;
			}
		}
		return new ModelAndView("error");
	}
	
	@PostMapping("gb2_update.do")
	public ModelAndView getGuestBook2Update(String idx) {
		ModelAndView mv = new ModelAndView("update");
		VO vo = guestBook2Service.getGuestBook2Detail(idx);
		if(vo != null) {
			mv.addObject("vo", vo);
			return mv;
		}
		return new ModelAndView("error");
	}
	
	@PostMapping("gb2_update_ok.do")
	public ModelAndView getGuestBook2UpdateOK(VO vo, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		
		String cpwd = vo.getPwd();
		
		VO vo2 = guestBook2Service.getGuestBook2Detail(vo.getIdx());
		String dpwd = vo2.getPwd();
		
		if(! passwordEncoder.matches(cpwd, dpwd)) {
			mv.setViewName("update");
			mv.addObject("pwdchk", "fail");
			mv.addObject("vo", vo2);
			return mv;
		} else {
			try {
				String path = request.getSession().getServletContext().getRealPath("/resources/upload");
				MultipartFile file = vo.getFile();
				String old_f_name = vo.getOld_f_name();
				if(file.isEmpty()) {
					vo.setF_name(old_f_name);
				}else {
					UUID uuid = UUID.randomUUID();
					String f_name = uuid.toString()+"_"+file.getOriginalFilename();
					vo.setF_name(f_name);
					// 이미지 복사 붙이기
					byte[] in = file.getBytes();
					File out = new File(path, f_name);
					FileCopyUtils.copy(in, out);
					
				}
				int result = guestBook2Service.getGuestBook2Update(vo);
				if(result>0) {
					mv.setViewName("redirect:gb2_detail.do?idx="+vo.getIdx());
					return mv;
				}
				
			} catch (Exception e) {
				System.out.println(e);
			}
			
		}
		return new ModelAndView("error");
	}
	
}





























