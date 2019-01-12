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
	
}
