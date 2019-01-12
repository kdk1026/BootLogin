package kr.co.test.controller.web.jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("jwt")
public class JwtMainController {

	@GetMapping("main")
	public ModelAndView hello() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jwt/main");
		
		return mav;
	}
	
}
