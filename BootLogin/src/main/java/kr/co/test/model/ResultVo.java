package kr.co.test.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 응답 VO
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
@ToString
public class ResultVo {

	private String res_cd;
	private String res_msg;
	
}
