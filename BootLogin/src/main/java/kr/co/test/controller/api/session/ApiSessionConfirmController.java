package kr.co.test.controller.api.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;
import kr.co.test.service.session.ApiSessionConfirmService;

/**
 * @since 2018. 12. 30.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 30. 김대광	최초작성
 * </pre>
 */
@RestController
@RequestMapping("api/session")
public class ApiSessionConfirmController extends LogDeclare {
	
	@Autowired
	private ApiSessionConfirmService apiSessionConfirmService;
	
	@PostMapping("/confirm")
	public ParamMap confirm(ParamCollector paramCollector) {
		return apiSessionConfirmService.processConfirm(paramCollector);
	}

}
