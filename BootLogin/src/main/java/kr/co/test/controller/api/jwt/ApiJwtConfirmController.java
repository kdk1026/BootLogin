package kr.co.test.controller.api.jwt;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.map.MapUtil;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;
import kr.co.test.common.jwt.JwtTokenProvider;
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
@RequestMapping("api/jwt")
public class ApiJwtConfirmController extends LogDeclare {
	
	@Autowired
	private Environment env;

	@PostMapping("/confirm")
	public ParamMap confirm(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		retMap = this.valid(paramCollector);
		if ( !retMap.isEmpty() ) {
			return retMap;
		}
		retMap.clear();
		
		// ------------------------------------------------------------------------
		// 토큰에서 사용자 정보 추출하여 검증
		// ------------------------------------------------------------------------
		String sProfile = env.getActiveProfiles()[0];
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(sProfile);
		
		HttpServletRequest request = paramCollector.getRequest();
		UserVo user = jwtTokenProvider.getAuthUserFromJwt(request);
		
		Map<String, String> map = MapUtil.objectToMap(user);
		map.remove("pw");
		
		retMap.putAll(map);
		
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
		
		return retMap;
	}
	
}
