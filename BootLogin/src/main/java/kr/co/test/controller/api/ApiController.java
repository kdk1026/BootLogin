package kr.co.test.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.excel.JxlsUtil;
import common.util.map.ParamMap;
import kr.co.test.common.Constants;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2019. 5. 9. 김대광	최초작성
 * </pre>
 * 
 *
 * @author 김대광
 */
@RestController
@RequestMapping("api/test")
public class ApiController extends LogDeclare {

	@GetMapping
	public ParamMap checkin(HttpServletRequest request, HttpServletResponse response) {
		ParamMap retMap = new ParamMap();
		
		List<Map<String, Object>> contentsList = new ArrayList<>();
		Map<String, Object> map;
		
		map = new HashMap<>();
		map.put("no", 1);
		map.put("name", "aaa");
		contentsList.add(map);
		
		map = new HashMap<>();
		map.put("no", 2);
		map.put("name", "bbb");
		contentsList.add(map);
		
        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("listOrder", contentsList);
        
        //String sClassPath = this.getClass().getResource("/templates/excel/test.xlsx").getPath();
        String sClassPath = "c:/test/test.xlsx";
        logger.debug("=== {}", sClassPath);
		
		JxlsUtil.downloadExcel(request, response, excelMap, sClassPath, "니미럴.xlsx");
        
		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.SUCCESS.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.SUCCESS.getMessage());
		
		return retMap;
	}
	
}
