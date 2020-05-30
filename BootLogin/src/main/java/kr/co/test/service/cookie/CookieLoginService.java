package kr.co.test.service.cookie;

import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.crypto.AesCryptoUtil;
import common.util.date.Jsr310DateUtil;
import common.util.json.GsonUtil;
import common.util.map.ParamMap;
import common.util.sessioncookie.CookieUtilVer2;
import common.util.sessioncookie.SessionUtils;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;
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
public class CookieLoginService extends LogDeclare {

	@Autowired
	private LoginMapper loginMapper;
	
	@Value("#{common}")
	private Properties commProp;
	
	/**
	 * 쿠키 로그인 처리
	 * @param paramCollector
	 * @param response
	 * @return
	 */
	public ResultVo processLogin(ParamCollector paramCollector, HttpServletResponse response) {
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
		// 객체를 JSON String으로 변환
		//--------------------------------------------------
		String sUserJson = GsonUtil.ToJson.converterObjToJsonStr(userVo);
		
		//--------------------------------------------------
		// AES 128 암호화
		//--------------------------------------------------
		String sCookieEncryptionKey = commProp.getProperty("cookie.encryption.key");
		String sEnUserJson = AesCryptoUtil.aesEncrypt(sCookieEncryptionKey, AesCryptoUtil.AES_CBC_PKCS5PADDING, sUserJson);
		
		//--------------------------------------------------
		// 로그인 정보 쿠키 생성
		//--------------------------------------------------
		String sSessionExpireSecond = commProp.getProperty("session.expire.second");
		int nExpireSecond = Integer.parseInt(sSessionExpireSecond);
		
		//--------------------------------------------------
		// 자동 로그인 선택 시, 만료기간 7일 설정
		//--------------------------------------------------
		if ( paramCollector.containsKey("login_chk") && "on".equals(paramCollector.getString("login_chk")) ) {
			sSessionExpireSecond = commProp.getProperty("auto.login.cookie.expire.second");
			
			CookieUtilVer2.addCookie(response, Constants.Cookie.IS_AUTO_LOGIN, "Y", nExpireSecond, false, false, "");
		}
		
		CookieUtilVer2.addCookie(response, SessionUtils.LoginInfo.SESSION_KEY, sEnUserJson, nExpireSecond, false, false, "");
		
		resultVo.setRes_cd(ResponseCodeEnum.SUCCESS.getCode());
		resultVo.setRes_msg(ResponseCodeEnum.SUCCESS.getMessage());
		
		return resultVo;
	}
	
	/**
	 * 쿠키 로그아웃 처리
	 * @param response
	 * @return
	 */
	public ResultVo processLogout(HttpServletResponse response) {
		ResultVo resultVo = new ResultVo();
		
		CookieUtilVer2.removeCookie(response, SessionUtils.LoginInfo.SESSION_KEY);
		
		resultVo.setRes_cd(ResponseCodeEnum.SUCCESS.getCode());
		resultVo.setRes_msg(ResponseCodeEnum.SUCCESS.getMessage());
		
		return resultVo;
	}
	
	/**
	 * <pre>
	 * 쿠키 부가정보
	 *  - API 전용
	 * </pre>
	 * @param paramCollector
	 * @param retMap
	 */
	public void getCookieOptionInfo(ParamCollector paramCollector, ParamMap retMap) {
		retMap.put("cookie_name", 				SessionUtils.LoginInfo.SESSION_KEY);
		retMap.put("cookie_create_time", 		Jsr310DateUtil.Today.getTodayString("yyyyMMddHHmmss"));
	}
	
}
