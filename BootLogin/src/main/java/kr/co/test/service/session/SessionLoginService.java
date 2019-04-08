package kr.co.test.service.session;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import common.ResponseCodeEnum;
import common.util.sessioncookie.SessionUtils;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.mvc.service.CommonService;
import kr.co.test.mapper.account.LoginMapper;
import kr.co.test.model.ResultVo;
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
@Service
public class SessionLoginService extends CommonService {

	@Autowired
	private LoginMapper loginMapper;
	
	@Value("#{common['session.expire.second']}")
	private String sSessionExpireSecond;
	
	//processLogin
	
	/**
	 * 세션 로그인 처리
	 * @param paramCollector
	 * @return
	 */
	public ResultVo loginAuth(ParamCollector paramCollector) {
		ResultVo resultVo = new ResultVo();
		resultVo.setRes_cd(ResponseCodeEnum.LOGIN_INVALID.getCode());
		resultVo.setRes_msg(ResponseCodeEnum.LOGIN_INVALID.getMessage());
		
		//--------------------------------------------------
		// 아이디로 계정 정보 조회
		//--------------------------------------------------
		UserVo userVo = loginMapper.selectById(paramCollector.getMap());
		
		//--------------------------------------------------
		// 계정 유무 확인
		//--------------------------------------------------
		if ( userVo == null ) {
			return resultVo;
		}
		
		/*
		 * 로그인 프로세스 추가 고려 사항
		 *  - 비밀번호 N회 오류, 일정기간 미사용에 의한 계정 잠금 및 안내
		 *  - 비밀번호 변경 주기 경과 안내 
		 *    > 관리자 사이트는 해당 없으나, 일반적으로 권고 규칙에 의거해 최소 6개월, 규칙보다 짧은 3개월 등은 해당 없음
		 *    > 사고/신고에 의한 점검 등으로 위반에 해당 시, 과태료
		 */
		
		//--------------------------------------------------
		// 비밀번호 일치 여부 확인
		//--------------------------------------------------
		if ( !BCrypt.checkpw(paramCollector.getString("pw"), userVo.getPw()) ) {
			return resultVo;
		}
		
		// TODO : 자동 로그인 관련
		
		//--------------------------------------------------
		// 로그인 정보 세션 생성
		//--------------------------------------------------
		int nExpireSecond = Integer.parseInt(sSessionExpireSecond);
		SessionUtils.LoginInfo.setAttribute(paramCollector.getRequest(), userVo, nExpireSecond);
		
		resultVo.setRes_cd(ResponseCodeEnum.SUCCESS.getCode());
		resultVo.setRes_msg(ResponseCodeEnum.SUCCESS.getMessage());
		
		return resultVo;
	}
	
	/**
	 * 세션 로그아웃 처리
	 * @param paramCollector
	 * @return
	 */
	public ResultVo processLogout(ParamCollector paramCollector) {
		ResultVo resultVo = new ResultVo();
		
		paramCollector.getRequest().getSession().invalidate();
		
		resultVo.setRes_cd(ResponseCodeEnum.SUCCESS.getCode());
		resultVo.setRes_msg(ResponseCodeEnum.SUCCESS.getMessage());
		
		return resultVo;
	}
	
}
