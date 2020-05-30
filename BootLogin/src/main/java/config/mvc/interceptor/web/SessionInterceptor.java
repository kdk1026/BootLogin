package config.mvc.interceptor.web;

import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import common.util.properties.PropertiesUtil;
import common.util.sessioncookie.CookieUtilVer2;
import common.util.sessioncookie.SessionUtils;
import kr.co.test.common.Constants;
import kr.co.test.model.UserVo;
import kr.co.test.service.session.SessionLoginService;

/**
 * @since 2018. 12. 25.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 25. 김대광	최초작성
 * </pre>
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private SessionLoginService sessionLoginService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String LOGIN_PAGE_URI = "/session/login";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession(false);
		String sCtxPath = request.getContextPath();
		
		Properties prop = PropertiesUtil.getPropertiesClasspath("common.properties");
		
		//--------------------------------------------------
		// 세션 유무 확인
		//--------------------------------------------------
		if (session != null) {
			UserVo userVo = (UserVo) session.getAttribute(SessionUtils.LoginInfo.SESSION_KEY);

			//--------------------------------------------------
			// 로그인 사용자 정보 유무 확인
			//--------------------------------------------------
			if (userVo == null) {
				logger.info("[Session] - UserInfo Null");
				
				response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
				return false;				
			}
			
			//--------------------------------------------------
			// 자동 로그아웃 처리를 위한 만료시간 Attribute
			//--------------------------------------------------
			String sSessionExpireSecond = prop.getProperty("session.expire.second");
			int nExpireSecond = Integer.parseInt(sSessionExpireSecond);
			
			request.setAttribute("expires_in", nExpireSecond);
			
		} else {
			logger.info("[Session] - Null");
			
			Cookie loginCookie = CookieUtilVer2.getCookie(request, Constants.Cookie.IS_AUTO_LOGIN);
			
			if (loginCookie != null) {
				String sSessionId = loginCookie.getValue();
				UserVo userVo = sessionLoginService.checkUserWithSessionKey(sSessionId);
				
				if (userVo != null) {
					SessionUtils.LoginInfo.setAttribute(request, userVo);
					return true;
				} else {
					response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
					return false;					
				}
			}
			
			response.sendRedirect(sCtxPath + LOGIN_PAGE_URI);
			return false;
		}
		
		return true;
	}
	
}
