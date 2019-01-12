package config.mvc.interceptor.api;

import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.ResponseCodeEnum;
import common.util.crypto.AesCryptoUtil;
import common.util.json.GsonUtil;
import common.util.properties.PropertiesUtil;
import common.util.sessioncookie.CookieUtilVer2;
import common.util.sessioncookie.SessionUtils;
import kr.co.test.common.Constants;
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
public class ApiCookieInterceptor extends HandlerInterceptorAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		Cookie cookie = CookieUtilVer2.getCookie(request, SessionUtils.LoginInfo.SESSION_KEY);
		
		ResultVo resultVo = new ResultVo();
		String sRetJson = "";
		Properties prop = PropertiesUtil.getPropertiesClasspath("common.properties");
		
		//--------------------------------------------------
		// 로그인 쿠키 유무 확인
		//--------------------------------------------------
		if ( cookie == null ) {
			logger.info("[API Cookie] - Null");
			
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_DENIED.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			
			sRetJson = GsonUtil.converterObjToJsonStr(resultVo);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.Encoding.UTF_8);
			response.getWriter().write(sRetJson);
			return false;			
		}

		//--------------------------------------------------
		// 로그인 쿠키 값 유무 확인
		//--------------------------------------------------
		String sEnUserJson = cookie.getValue();
		if ( StringUtils.isBlank(sEnUserJson) ) {
			logger.info("[API Cookie] - Encoded UserInfo Null");
			
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_DENIED.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			
			sRetJson = GsonUtil.converterObjToJsonStr(resultVo);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.Encoding.UTF_8);
			response.getWriter().write(sRetJson);
			return false;			
		}
		
		//--------------------------------------------------
		// 로그인 쿠키 값의 복호화 값 유무 확인
		//--------------------------------------------------
		String sCookieEncryptionKey = prop.getProperty("cookie.encryption.key");
		String sUserJson = AesCryptoUtil.aesDecrypt(sCookieEncryptionKey, AesCryptoUtil.AES_CBC_PKCS5PADDING, sEnUserJson);
		if ( StringUtils.isBlank(sUserJson) ) {
			logger.info("[API Cookie] - Decoded UserInfo Null");
			
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_DENIED.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			
			sRetJson = GsonUtil.converterObjToJsonStr(resultVo);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.Encoding.UTF_8);
			response.getWriter().write(sRetJson);
			return false;	
		}
		
		//--------------------------------------------------
		// 복호화된 JSON 값을 객체로 변환 
		//--------------------------------------------------
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		UserVo userVo = gson.fromJson(sUserJson, UserVo.class);
		
		//--------------------------------------------------
		// 로그인 사용자 정보 유무 확인
		//--------------------------------------------------
		if (userVo == null) {
			logger.info("[API Cookie] - UserInfo Null");
			
			resultVo.setRes_cd(ResponseCodeEnum.ACCESS_DENIED.getCode());
			resultVo.setRes_msg(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			
			sRetJson = GsonUtil.converterObjToJsonStr(resultVo);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.Encoding.UTF_8);
			response.getWriter().write(sRetJson);
			return false;
		}
		
		//--------------------------------------------------
		// 자동 로그인이 아닌 경우에만 쿠키 재생성
		//--------------------------------------------------
		String sIsAutoLogin = CookieUtilVer2.getCookieValue(request, Constants.Cookie.IS_AUTO_LOGIN);
		
		if ( StringUtils.isBlank(sIsAutoLogin) ) {
			//--------------------------------------------------
			// 쿠키 만료시간 갱신
			// XXX : 세션과 동일하게 사용하기 위함, 상황에 따라 배제 가능
			//--------------------------------------------------
			String sSessionExpireSecond = prop.getProperty("session.expire.second");
			int nExpireSecond = Integer.parseInt(sSessionExpireSecond);
			cookie.setMaxAge(nExpireSecond);
			
			//--------------------------------------------------
			// 쿠키 재생성
			//--------------------------------------------------
			sEnUserJson = cookie.getValue();
			CookieUtilVer2.addCookie(response, SessionUtils.LoginInfo.SESSION_KEY, sEnUserJson, nExpireSecond, false, false, "");
		}
		
		return true;
	}
	
}
