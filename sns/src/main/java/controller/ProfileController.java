package controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import service.IF_FollowListService;
import service.IF_ProfileService;
import util.FileDataUtil;
import vo.ProfileVO;

@Controller
public class ProfileController {
	@Inject
	IF_ProfileService pServe;
	@Inject
	IF_FollowListService fServe;
	
	@Inject
	FileDataUtil upload;
	
	//프로필 view 불러오기
	@GetMapping("/profile")
	public String profile(@RequestParam("id") String id,Model model) {
		model.addAttribute("id", id);
		return "profile";
	}
	
	//닉네임 중복 체크
	@PostMapping("/profileChk")
	public void nickChk(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String nickName = request.getParameter("nickName");
		response.setContentType("text/html; charset=UTF-8");
		if(pServe.chk(nickName) == 0) {
			response.getWriter().print(nickName);
		} else {
			response.getWriter().print("null");
		}
	}
	
	//profile table에 insert
	@PostMapping("/profileSave")
	public String insert(@ModelAttribute ProfileVO pVO, MultipartFile[] myPhoto) throws Exception {
		if(myPhoto != null) {
			pVO.setPhoto(upload.fileUpload(myPhoto)[0]);
		}
		pServe.insert(pVO);
		return "redirect:/loginpage";
	}
	
	//각 개인의 profile 보기
	@GetMapping("/profileShow")
	public String profileShow(Model model, HttpSession session) throws Exception {
		ProfileVO p = pServe.select(String.valueOf(session.getAttribute("userid")));
		if(p.getPhoto() == null) {
			p.setPhoto("null");
		}
		model.addAttribute("profile", p);
		model.addAttribute("following", fServe.followingSelect(String.valueOf(session.getAttribute("userid"))));
		model.addAttribute("follower", fServe.followerSelect(String.valueOf(session.getAttribute("userid"))));
		return "profileShow";
	}
	
	//프로필 수정
	@PostMapping("/profileUpdate")
	public String update(@ModelAttribute ProfileVO pVO, MultipartFile[] proPhoto, HttpSession session) throws Exception {
		pVO.setId(String.valueOf(session.getAttribute("userid")));
		pVO.setNickName(String.valueOf(session.getAttribute("nickName")));
		String file = upload.fileUpload(proPhoto)[0];
		if(file != null) {
			pVO.setPhoto(file);
		} else {
			if(pVO.getPhoto().equals("COMPLETE")) {
				pVO.setPhoto(null);
			}
		}
		pServe.update(pVO);
		return "redirect:/profileShow";
	}
	
	//프로필 수정 화면 띄우기
	@GetMapping("/profileEdit")
	public String profileEdit(Model model, HttpSession session) throws Exception {
		ProfileVO p = pServe.select(String.valueOf(session.getAttribute("userid")));
		if(p.getPhoto() == null) {
			p.setPhoto("null");
		}
		model.addAttribute("profile", p);
		return "profileEdit";
	}
}
