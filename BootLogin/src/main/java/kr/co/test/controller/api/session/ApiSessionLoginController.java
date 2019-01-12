package kr.co.test.controller.api.session;

import java.util.Map;

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
import kr.co.test.service.session.SessionLoginService;

/**
 * <pre>
 * API 로그인
 *  - 응답 이후, API 요청 시, 응답 항목의 session_id를 쿠키로 설정하여 요청
 *  	> Cookie : JSESSION_ID=session_id
 *  
 *  - 앱의 경우, 일반적으로 자동로그인 체계이므로 세션은 부적함
 *  - 세션 유효기간 제한 없을 경우, 서부 부담 상당함
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
@RequestMapping("api/session")
public class ApiSessionLoginController extends LogDeclare {

	@Autowired
	private SessionLoginService sessionLoginService;
	
	@PostMapping("/login")
	public ParamMap login(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		retMap = this.valid(paramCollector);
		if ( !retMap.isEmpty() ) {
			return retMap;
		}
		retMap.clear();
		
		ResultVo resultVo = sessionLoginService.processLogin(paramCollector);
		
		Map<String, String> map = MapUtil.objectToMap(resultVo);
		retMap.putAll(map);
		
		sessionLoginService.getSessionOptionInfo(paramCollector, retMap);
		
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
	public ParamMap logout(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		ResultVo resultVo = sessionLoginService.processLogout(paramCollector);
		
		Map<String, String> map = MapUtil.objectToMap(resultVo);
		retMap.putAll(map);
		
		return retMap;
	}
	
}
