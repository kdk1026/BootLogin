package kr.co.test.controller.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HellowController {

	@GetMapping(value = "hello", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView hello() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("hello");
		
		mav.addObject("message", "Hello");
		return mav;
	}
	
}
