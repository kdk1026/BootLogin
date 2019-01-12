package config.mvc.interceptor.web;

import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import common.util.properties.PropertiesUtil;
import common.util.sessioncookie.CookieUtilVer2;
import kr.co.test.common.Constants;
import kr.co.test.common.jwt.JwtTokenProvider;
import kr.co.test.model.UserVo;

/**
 * @since 2018. 12. 29.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 29. 김대광	최초작성
 * </pre>
 */
public class JwtInterceptor extends HandlerInterceptorAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String LOGIN_PAGE_URI = "/jwt/login";
	
	private String profile;
	
	public JwtInterceptor(String sProfile) {
		this.profile = sProfile;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String sCtxPath = request.getContextPath();
		Cookie cookie = CookieUtilVer2.getCookie(request, Constants.Jwt.ACCESS_TOKEN);
		
		Properties prop = PropertiesUtil.getPropertiesClasspath("common.properties");
		
		//--------------------------------------------------
		// JWT 쿠키 유무 확인
		//--------------------------------------------------
		if ( cookie == null ) {
			logger.info("[Jwt] - Cookie Null");
			
			response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
			return false;			
		}
		
		//--------------------------------------------------
		// JWT 쿠키 값 유무 확인
		//--------------------------------------------------
		String sAccessToken = cookie.getValue();
		if ( StringUtils.isBlank(sAccessToken) ) {
			logger.info("[Jwt] - AccessToken Null");
			
			response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
			return false;			
		}

		// ------------------------------------------------------------------------
		// 토큰 가져오기
		// ------------------------------------------------------------------------
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(profile);
		
		// ------------------------------------------------------------------------
		// 토큰 유효성 검증
		// ------------------------------------------------------------------------
		int nValid = jwtTokenProvider.isValidateJwtToken(sAccessToken);
		
		if ( nValid == 0 || nValid == 2 ) {
			logger.info("[Jwt] - InValid AccessToken");
			
			response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
			return false;			
		}
		
		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, access_token인지 검증
		// ------------------------------------------------------------------------
		String sTokenKind = jwtTokenProvider.getTokenKind(sAccessToken);
		
		if ( Constants.Jwt.REFRESH_TOKEN.equals(sTokenKind) ) {
			logger.info("[Jwt] - Is Not AccessToken");
			
			response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
			return false;
		}
		
		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, 사용자 정보 추출하여 검증
		// ------------------------------------------------------------------------
		UserVo user = jwtTokenProvider.getAuthUserFromJwt(sAccessToken);
		
		if ( (user == null) || (StringUtils.isBlank(user.getId())) ) {
			logger.info("[Jwt] - UserInfo null");
			
			response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
			return false;
		}
		
		//--------------------------------------------------
		// 자동 로그인이 아닌 경우에만 쿠키 재생성
		//--------------------------------------------------
		String sIsAutoLogin = CookieUtilVer2.getCookieValue(request, Constants.Cookie.IS_AUTO_LOGIN);
		
		if ( StringUtils.isBlank(sIsAutoLogin) ) {
			//--------------------------------------------------
			// 자동 로그아웃 처리를 위한 만료시간 Attribute
			//--------------------------------------------------
			String sSessionExpireSecond = prop.getProperty("session.expire.second");
			int nExpireSecond = Integer.parseInt(sSessionExpireSecond);
			
			request.setAttribute("expires_in", nExpireSecond);
			
			//--------------------------------------------------
			// 쿠키 만료시간 갱신
			// XXX : 세션과 동일하게 사용하기 위함, 상황에 따라 배제 가능
			//--------------------------------------------------
			cookie.setMaxAge(nExpireSecond);
			
			//--------------------------------------------------
			// 쿠키 재생성
			//--------------------------------------------------
			sAccessToken = cookie.getValue();
			CookieUtilVer2.addCookie(response, Constants.Jwt.ACCESS_TOKEN, sAccessToken, nExpireSecond, false, false, "");
		}
		
		return true;
	}

}
