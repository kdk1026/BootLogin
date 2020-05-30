package config.mvc.interceptor.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import common.ResponseCodeEnum;
import common.util.json.GsonUtil;
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
public class ApiSessionInterceptor extends HandlerInterceptorAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession(false);
		
		ResultVo resultVo = new ResultVo();
		String sRetJson = "";
		
		//--------------------------------------------------
		// 세션 유무 확인
		//--------------------------------------------------
		if (session != null) {
			UserVo userVo = (UserVo) session.getAttribute(SessionUtils.LoginInfo.SESSION_KEY);

			//--------------------------------------------------
			// 로그인 사용자 정보 유무 확인
			//--------------------------------------------------
			if (userVo == null) {
				logger.info("[API Session] - UserInfo Null");
				
				resultVo.setRes_cd(ResponseCodeEnum.ACCESS_DENIED.getCode());
				resultVo.setRes_msg(ResponseCodeEnum.ACCESS_DENIED.getMessage());
				
				sRetJson = GsonUtil.ToJson.converterObjToJsonStr(resultVo);
				
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding(Constants.Encoding.UTF_8);
				response.getWriter().write(sRetJson);
				return false;				
			}
			
		} else {
			logger.info("[API Session] - Null");
			
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
