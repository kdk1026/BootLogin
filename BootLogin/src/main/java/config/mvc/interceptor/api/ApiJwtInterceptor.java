package config.mvc.interceptor.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import common.ResponseCodeEnum;
import common.util.json.GsonUtil;
import kr.co.test.common.Constants;
import kr.co.test.common.jwt.JwtTokenProvider;
import kr.co.test.model.ResultVo;
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
public class ApiJwtInterceptor extends HandlerInterceptorAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String profile;
	
	public ApiJwtInterceptor(String sProfile) {
		this.profile = sProfile;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		ResultVo resultVo = new ResultVo();
		String sRetJson = "";
		
		// ------------------------------------------------------------------------
		// 토큰 가져오기
		// ------------------------------------------------------------------------
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(profile);
		
		String sAccessToken = jwtTokenProvider.getTokenFromReqHeader(request);
		
		// ------------------------------------------------------------------------
		// 토큰 유효성 검증
		// ------------------------------------------------------------------------
		int nValid = jwtTokenProvider.isValidateJwtToken(sAccessToken);
		
		switch (nValid) {
		case 0:
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			
			sRetJson = GsonUtil.ToJson.converterObjToJsonStr(resultVo);		
			break;
			
		case 2:
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getMessage());
			
			sRetJson = GsonUtil.ToJson.converterObjToJsonStr(resultVo);		
			break;

		default:
			break;
		}
		
		if ( !StringUtils.isBlank(sRetJson) ) {
			logger.info("[API Jwt] - Invalid AccessToken");
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.Encoding.UTF_8);
			response.getWriter().write(sRetJson);
			return false;
		}
		
		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, access_token인지 검증
		// ------------------------------------------------------------------------
		String sTokenKind = jwtTokenProvider.getTokenKind(sAccessToken);
		
		if ( Constants.Jwt.REFRESH_TOKEN.equals(sTokenKind) ) {
			logger.info("[API Jwt] - Is Not AccessToken");
			
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_DENIED.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			
			sRetJson = GsonUtil.ToJson.converterObjToJsonStr(resultVo);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.Encoding.UTF_8);
			response.getWriter().write(sRetJson);
			return false;
		}
		
		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, 사용자 정보 추출하여 검증
		// ------------------------------------------------------------------------
		UserVo user = jwtTokenProvider.getAuthUserFromJwt(sAccessToken);
		
		if ( (user == null) || (StringUtils.isBlank(user.getId())) ) {
			logger.info("[API Jwt] - UserInfo null");
			
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_DENIED.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			
			sRetJson = GsonUtil.ToJson.converterObjToJsonStr(resultVo);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.Encoding.UTF_8);
			response.getWriter().write(sRetJson);
			return false;
		}
		
		return true;
	}

}
