package kr.co.test.controller.web.cookie;

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
import kr.co.test.service.cookie.CookieLoginService;

/**
 * @since 2018. 12. 25.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 25. 김대광	최초작성
 * </pre>
 */
@RestController
@RequestMapping("cookie")
public class CookieLoginController extends LogDeclare {
	
	@Autowired
	private CookieLoginService cookieLoginService;

	@GetMapping("/login")
	public ModelAndView login(ParamCollector paramCollector) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("cookie/login");
		
		return mav;
	}
	
	@PostMapping("/loginProc")
	public ModelAndView loginProc(ParamCollector paramCollector, RedirectAttributes attributes, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String sRedirectUrl = "";
		
		ResultVo resultVo = cookieLoginService.processLogin(paramCollector, response);
		
		if ( ResponseCodeEnum.SUCCESS.getCode().equals(resultVo.getRes_cd()) ) {
			sRedirectUrl = "redirect:/cookie/main";
		} else {
			sRedirectUrl = "redirect:/cookie/login";
			
			attributes.addFlashAttribute("res_msg", resultVo.getRes_msg());
		}
		
		mav.setViewName(sRedirectUrl);
		return mav;
	}
	
	@GetMapping("/logout")
	public ModelAndView logout(ParamCollector paramCollector, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		
		cookieLoginService.processLogout(response);
		
		mav.setViewName("redirect:/cookie/login");
		return mav;
	}
	
}
