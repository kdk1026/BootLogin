package kr.co.test.controller.api.session;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.util.map.MapUtil;
import common.util.map.ParamMap;
import common.util.sessioncookie.SessionUtils;
import config.mvc.resolver.ParamCollector;
import kr.co.test.model.UserVo;

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
	
	@PostMapping("/confirm")
	public ParamMap confirm(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		HttpServletRequest request = paramCollector.getRequest();
		UserVo user = (UserVo) SessionUtils.LoginInfo.getSession(request);
		
		Map<String, String> map = MapUtil.objectToMap(user);
		map.remove("pw");
		
		retMap.putAll(map);
		
		return retMap;
	}

}
