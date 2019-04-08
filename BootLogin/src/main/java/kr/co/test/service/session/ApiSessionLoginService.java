package kr.co.test.service.session;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.util.date.Jsr310DateUtil;
import common.util.map.MapUtil;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.mvc.service.CommonService;
import kr.co.test.model.ResultVo;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2019. 4. 9. 김대광	최초작성
 * </pre>
 * 
 *
 * @author 김대광
 */
@Service
public class ApiSessionLoginService extends CommonService {

	@Autowired
	private SessionLoginService sessionLoginService;
	
	/**
	 * 세션 로그인 처리
	 * @param paramCollector
	 * @return
	 */
	public ParamMap processLogin(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		ResultVo resultVo = sessionLoginService.loginAuth(paramCollector);
		
		Map<String, String> map = MapUtil.objectToMap(resultVo);
		retMap.putAll(map);
		
		this.getSessionOptionInfo(paramCollector, retMap);
		
		return retMap;
	}
	
	/**
	 * <pre>
	 * 세션 부가 정보
	 *  - API 전용
	 * </pre>
	 * @param paramCollector
	 * @param retMap
	 */
	private void getSessionOptionInfo(ParamCollector paramCollector, ParamMap retMap) {
		HttpSession session = paramCollector.getRequest().getSession(false);
		
		retMap.put("session_id", 				session.getId());
		retMap.put("session_create_time", 		Jsr310DateUtil.Convert.getDateToString(new Date(session.getCreationTime()), "yyyyMMddHHmmss"));
		retMap.put("session_active_interval",	session.getMaxInactiveInterval());
	}
	
	/**
	 * 세션 로그아웃 처리
	 * @param paramCollector
	 * @return
	 */
	public ParamMap processLogout(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		ResultVo resultVo = sessionLoginService.processLogout(paramCollector);
		
		Map<String, String> map = MapUtil.objectToMap(resultVo);
		retMap.putAll(map);
		
		return retMap;
	}
	
}
