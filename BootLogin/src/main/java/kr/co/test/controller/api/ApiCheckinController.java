package kr.co.test.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;

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
@RequestMapping("api/checkin")
public class ApiCheckinController extends LogDeclare {

	@PostMapping()
	public ParamMap checkin(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.SUCCESS.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.SUCCESS.getMessage());
		
		return retMap;
	}
	
}
