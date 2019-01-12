package kr.co.test.controller.web.cookie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
public class CookieMainController {

	@GetMapping("main")
	public ModelAndView hello() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("cookie/main");
		
		return mav;
	}
	
}
