package kr.co.test.controller.api.jwt;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;
import kr.co.test.service.jwt.JwtLoginService;

/**
 * <pre>
 * API 로그인
 *  - 응답 이후, API 요청 시, 응답 항목의 token_type + access_token를 헤더에 설정하여 요청
 *  	> Authorization : token_type + access_token
 *  
 *  - Client에서 refresh_token은 자동 로그인 여부에 따라 활용
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
@RequestMapping("api/jwt")
public class ApiJwtLoginController extends LogDeclare {

	@Autowired
	private JwtLoginService jwtLoginService;

	@PostMapping("/login")
	public ParamMap login(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		retMap = this.valid(paramCollector);
		if ( !retMap.isEmpty() ) {
			return retMap;
		}
		retMap.clear();
		
		retMap = jwtLoginService.processApiLogin(paramCollector);
		
		return retMap;
	}
	
	public ParamMap valid(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		//--------------------------------------------------
		// DeviceId 헤더 확인
		//--------------------------------------------------
		ParamMap headerMap = paramCollector.getHeardMap();
		
		logger.debug("{}", headerMap);
		
		if ( !headerMap.containsKey("device_id") ) {
			retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.NO_INPUT.getCode());
			retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.NO_INPUT.getMessage("디바이스 아이디"));
			return retMap;
		}
		
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
	
	/*
	 * 로그아웃 API 없음
	 * Client 에서 저장해둔 토큰 날리고, 로그인 화면 띄움
	 */
	
}
