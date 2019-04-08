package kr.co.test.common.mvc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import common.LogDeclare;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2019. 4. 1. 김대광	최초작성
 * </pre>
 * 
 *
 * @author 김대광
 */
@Service
public class CommonService extends LogDeclare {

	@Autowired
	Environment env;
	
	/**
	 * 현재 프로파일 가져오기
	 * @return
	 */
	public String getActiveProfile() {
		return env.getActiveProfiles()[0];
	}
	
}
