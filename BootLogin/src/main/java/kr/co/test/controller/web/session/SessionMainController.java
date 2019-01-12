package kr.co.test.controller.web.session;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
public class SessionMainController {

	@GetMapping("main")
	public ModelAndView hello() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("session/main");
		
		return mav;
	}
	
}
