package kr.co.test.mapper.account;

import org.apache.ibatis.annotations.Mapper;

import common.util.map.ParamMap;
import kr.co.test.model.UserVo;

/**
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
@Mapper
public interface LoginMapper {

	/**
	 * 아이디로 계정 정보 조회
	 * @param paramMap
	 * @return
	 */
	public UserVo selectById(ParamMap paramMap);
	
	/**
	 * 자동로그인 세션정보 저장
	 * @param paramMap
	 */
	public void insertKeepLogin(ParamMap paramMap);
	
	/**
	 * 자동로그인 세션정보 삭제
	 * @param paramMap
	 */
	public void deleteKeppLogin(ParamMap paramMap);
	
	/**
	 * 로그인 시 loginCookie 값과 SESSION_KEY가 일치하는 회원의 정보를 가져옴
	 * @param sessionId
	 * @return
	 */
	public UserVo selectChekUserWithSessionKey(String sessionId);
	
}
