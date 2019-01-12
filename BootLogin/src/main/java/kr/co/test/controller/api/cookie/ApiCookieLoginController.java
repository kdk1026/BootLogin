package kr.co.test.controller.api.cookie;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.map.MapUtil;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;
import kr.co.test.model.ResultVo;
import kr.co.test.service.cookie.CookieLoginService;

/**
 * API 로그인
 *  - 응답 이후, API 요청 시, 응답 항목의 cookie_name 참고
 *  - 헤더에서 cookie_name 에 해당하는 쿠키 값 가져온 후, 쿠키 값만 설정하여 요청
 *  	> Cookie : cookie_name=헤더의 cookie_name 값
 * </pre>
 * @since 2018. 12. 30.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 30. 김대광	최초작성
 * </pre>
 */
@RestController
@RequestMapping("api/cookie")
public class ApiCookieLoginController extends LogDeclare {

	@Autowired
	private CookieLoginService cookieLoginService;
	
	@PostMapping("/login")
	public ParamMap login(ParamCollector paramCollector, HttpServletResponse response) {
		ParamMap retMap = new ParamMap();
		
		retMap = this.valid(paramCollector);
		if ( !retMap.isEmpty() ) {
			return retMap;
		}
		retMap.clear();
		
		ResultVo resultVo = cookieLoginService.processLogin(paramCollector, response);
		
		Map<String, String> map = MapUtil.objectToMap(resultVo);
		retMap.putAll(map);
		
		cookieLoginService.getCookieOptionInfo(paramCollector, retMap);
		
		return retMap;
	}
	
	public ParamMap valid(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		if ( !paramCollector.containsKey("id") || StringUtils.isBlank(paramCollector.getString("id")) ) {
			retMap.put(Constants.RES.RES_CD, ResponseCodeEnum.NO_INPUT.getCode());
			retMap.put(Constants.RES.RES_MSG, ResponseCodeEnum.NO_INPUT.getMessage("아이디"));
			return retMap;
		}
		
		if ( !paramCollector.containsKey("pw") || StringUtils.isBlank(paramCollector.getString("pw")) ) {
			retMap.put(Constants.RES.RES_CD, ResponseCodeEnum.NO_INPUT.getCode());
			retMap.put(Constants.RES.RES_MSG, ResponseCodeEnum.NO_INPUT.getMessage("비밀번호"));
			return retMap;
		}
		
		return retMap;
	}
	
	@PostMapping("/logout")
	public ParamMap logout(ParamCollector paramCollector, HttpServletResponse response) {
		ParamMap retMap = new ParamMap();
		
		ResultVo resultVo = cookieLoginService.processLogout(response);
		
		Map<String, String> map = MapUtil.objectToMap(resultVo);
		retMap.putAll(map);
		
		return retMap;
	}
	
}
