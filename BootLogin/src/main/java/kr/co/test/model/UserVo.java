package kr.co.test.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 계정 정보 VO
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
@Getter
@Setter
@ToString(exclude = "pw")
public class UserVo {

	private String id;
	private String pw;

	private String name;
	private String last_login_dt;
	private String last_pwd_upd_dt;
	
}
