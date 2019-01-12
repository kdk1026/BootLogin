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
import kr.co.test.service.jwt.JwtRefreshService;

/**
 * 토큰 갱신
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
public class ApiJwtRefreshController extends LogDeclare {
	
	@Autowired
	private JwtRefreshService jwtRefreshService;

	@PostMapping("/refresh")
	public ParamMap refresh(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		retMap = this.valid(paramCollector);
		if ( !retMap.isEmpty() ) {
			return retMap;
		}
		retMap.clear();
		
		retMap = jwtRefreshService.processApiTokenRefresh(paramCollector);
		return retMap;
	}
	
	public ParamMap valid(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		//--------------------------------------------------
		// DeviceId 헤더 확인
		//--------------------------------------------------
		ParamMap headerMap = paramCollector.getHeardMap();
		
		if ( !headerMap.containsKey("device_id") ) {
			retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.NO_INPUT.getCode());
			retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.NO_INPUT.getMessage("디바이스 아이디"));
			return retMap;
		}
		
		if ( !paramCollector.containsKey(Constants.Jwt.REFRESH_TOKEN) 
				|| StringUtils.isBlank(paramCollector.getString(Constants.Jwt.REFRESH_TOKEN)) ) {
			
			retMap.put(Constants.RES.RES_CD, ResponseCodeEnum.NO_INPUT.getCode());
			retMap.put(Constants.RES.RES_MSG, ResponseCodeEnum.NO_INPUT.getMessage("갱신 토큰"));
			return retMap;
		}
		
		return retMap;
	}
	
}
