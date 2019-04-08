package kr.co.test.service.session;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import common.util.map.MapUtil;
import common.util.map.ParamMap;
import common.util.sessioncookie.SessionUtils;
import config.mvc.resolver.ParamCollector;
import kr.co.test.model.UserVo;

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
public class ApiSessionConfirmService {

	public ParamMap processConfirm(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		HttpServletRequest request = paramCollector.getRequest();
		UserVo user = (UserVo) SessionUtils.LoginInfo.getSession(request);
		
		Map<String, String> map = MapUtil.objectToMap(user);
		map.remove("pw");
		
		retMap.putAll(map);
		
		return retMap;
	}
	
}
