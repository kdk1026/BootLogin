package kr.co.test.controller.web.session;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import common.LogDeclare;
import common.ResponseCodeEnum;
import config.mvc.resolver.ParamCollector;
import kr.co.test.model.ResultVo;
import kr.co.test.service.session.SessionLoginService;

/**
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
@RestController
@RequestMapping("session")
public class SessionLoginController extends LogDeclare {
	
	@Autowired
	private SessionLoginService sessionLoginService;

	@GetMapping("/login")
	public ModelAndView login(ParamCollector paramCollector) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("session/login");
		
		return mav;
	}
	
	@PostMapping("/loginProc")
	public ModelAndView loginProc(ParamCollector paramCollector, RedirectAttributes attributes,
			HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String sRedirectUrl = "";
		
		ResultVo resultVo = sessionLoginService.loginAuth(paramCollector, response);
		
		if ( ResponseCodeEnum.SUCCESS.getCode().equals(resultVo.getRes_cd()) ) {
			sRedirectUrl = "redirect:/session/main";
		} else {
			sRedirectUrl = "redirect:/session/login";
			
			attributes.addFlashAttribute("res_msg", resultVo.getRes_msg());
		}
		
		mav.setViewName(sRedirectUrl);
		return mav;
	}
	
	@GetMapping("/logout")
	public ModelAndView logout(ParamCollector paramCollector, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		
		sessionLoginService.processLogout(paramCollector, response);
		
		mav.setViewName("redirect:/session/login");
		return mav;
	}
	
}
