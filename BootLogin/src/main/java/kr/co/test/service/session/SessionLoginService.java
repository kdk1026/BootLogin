package kr.co.test.service.session;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import common.ResponseCodeEnum;
import common.util.map.ParamMap;
import common.util.sessioncookie.CookieUtilVer2;
import common.util.sessioncookie.SessionUtils;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;
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
	public ResultVo loginAuth(ParamCollector paramCollector, HttpServletResponse response) {
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
		
		//--------------------------------------------------
		// 자동 로그인 처리
		//--------------------------------------------------
		if ( "on".equals(paramCollector.getString("login_chk")) ) {
			HttpSession session = paramCollector.getRequest().getSession();
			String sSessionId = session.getId();
			
			int nAmount = 60*60*24*7;
			CookieUtilVer2.addCookie(response, Constants.Cookie.IS_AUTO_LOGIN, sSessionId, nAmount, false, false, null);
			
			Date sessionLimit = new Date(System.currentTimeMillis() + (1000*nAmount));
			this.keppLogin(paramCollector.getString("id"), sSessionId, sessionLimit);
		}
		
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
	public ResultVo processLogout(ParamCollector paramCollector, HttpServletResponse response) {
		ResultVo resultVo = new ResultVo();
		
		//--------------------------------------------------
		// 자동 로그인 해제
		//--------------------------------------------------
		CookieUtilVer2.removeCookie(response, Constants.Cookie.IS_AUTO_LOGIN);
		
		//--------------------------------------------------
		// 세션 소멸
		//--------------------------------------------------
		paramCollector.getRequest().getSession().invalidate();
		
		resultVo.setRes_cd(ResponseCodeEnum.SUCCESS.getCode());
		resultVo.setRes_msg(ResponseCodeEnum.SUCCESS.getMessage());
		
		return resultVo;
	}
	
	/**
	 * 자동 로그인 체크한 경우 세션과 유효시간 처리
	 * @param userId
	 * @param sessionId
	 * @param next
	 */
	private void keppLogin(String userId, String sessionId, Date next) {
		ParamMap map = new ParamMap();
		map.put("id", userId);
		map.put("sessionId", sessionId);
		map.put("next", next);
		
		loginMapper.deleteKeppLogin(map);
		loginMapper.insertKeepLogin(map);
	}
	
	/**
	 * 이전에 로그인한 적이 있는지, 즉 유효시간이 넘지 않은 세션을 가지고 있는지 체크한다.
	 * @param sessionId
	 * @return
	 */
	public UserVo checkUserWithSessionKey(String sessionId) {
		return loginMapper.selectChekUserWithSessionKey(sessionId);
	}
	
}
