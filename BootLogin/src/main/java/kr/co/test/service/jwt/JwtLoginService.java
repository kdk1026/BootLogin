package kr.co.test.service.jwt;

import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.map.ParamMap;
import common.util.sessioncookie.CookieUtilVer2;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;
import kr.co.test.common.jwt.JwtTokenProvider;
import kr.co.test.common.jwt.JwtTokenProvider.JwtToken;
import kr.co.test.mapper.account.LoginMapper;
import kr.co.test.model.ResultVo;
import kr.co.test.model.UserVo;

/**
 * @since 2018. 12. 29.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 29. 김대광	최초작성
 * </pre>
 */
@Service
public class JwtLoginService extends LogDeclare {

	@Autowired
	private LoginMapper loginMapper;
	
	@Autowired
	private Environment env;
	
	@Value("#{common}")
	private Properties commProp;
	
	/**
	 * <pre>
	 * JWT 로그인 처리
	 *  - Web 전용
	 *  - 쿠키 기반
	 *    : Web의 경우, 서버에서 forwad 및 redirect 시키므로 단독 JWT 사용 못함
	 * </pre>
	 * @param paramCollector
	 * @param response
	 * @return
	 */
	public ResultVo processLogin(ParamCollector paramCollector, HttpServletResponse response) {
		ResultVo resultVo = new ResultVo();
		
		String sProfile = env.getActiveProfiles()[0];
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(sProfile);
		
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
		// JWT 토큰 생성
		//--------------------------------------------------
		String sAutoLogin = "N";
		if ( paramCollector.containsKey("login_chk") && "on".equals(paramCollector.getString("login_chk")) ) {
			sAutoLogin = "Y";
		}
		
		String sDomain = commProp.getProperty("domain");
		String sAccessToken = jwtTokenProvider.generateAccessToken(userVo, sAutoLogin, sDomain, "");
		
		//--------------------------------------------------
		// JWT 토큰 쿠키 생성
		//--------------------------------------------------
		String sSessionExpireSecond = commProp.getProperty("session.expire.second");
		
		//--------------------------------------------------
		// 자동 로그인 선택 시, 만료기간 7일 설정
		//--------------------------------------------------
		if ( "Y".equals(sAutoLogin) ) {
			sSessionExpireSecond = commProp.getProperty("auto.login.cookie.expire.second");
		}
		
		int nExpireSecond = Integer.parseInt(sSessionExpireSecond);
		CookieUtilVer2.addCookie(response, Constants.Jwt.ACCESS_TOKEN, sAccessToken, nExpireSecond, false, false, "");
		
		resultVo.setRes_cd(ResponseCodeEnum.SUCCESS.getCode());
		resultVo.setRes_msg(ResponseCodeEnum.SUCCESS.getMessage());
		
		return resultVo;
	}
	
	/**
	 * <pre>
	 * JWT 로그아웃 처리
	 *  - Web 전용
	 *  - 쿠키 기반이므로 쿠키만 제거
	 * </pre>
	 * @param response
	 */
	public void processLogout(HttpServletResponse response) {
		CookieUtilVer2.removeCookie(response, Constants.Jwt.ACCESS_TOKEN);
	}
	
	/**
	 * <pre>
	 * JWT 로그인 처리
	 *  - API 전용
	 *  - AccessToken, RefreshToken
	 * </pre>
	 * @param paramCollector
	 * @return
	 */
	public ParamMap processApiLogin(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		String sProfile = env.getActiveProfiles()[0];
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(sProfile);
		
		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.LOGIN_INVALID.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.LOGIN_INVALID.getMessage());
		
		//--------------------------------------------------
		// DeviceId 헤더 확인
		//--------------------------------------------------
		ParamMap headerMap = paramCollector.getHeardMap();
		
		if ( !headerMap.containsKey("device_id") ) {
			retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.NO_INPUT.getCode());
			retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.NO_INPUT.getMessage("디바이스 아이디"));
			return retMap;
		}
		
		//--------------------------------------------------
		// 아이디로 계정 정보 조회
		//--------------------------------------------------
		UserVo userVo = loginMapper.selectById(paramCollector.getMap());
		
		//--------------------------------------------------
		// 계정 유무 확인
		//--------------------------------------------------
		if ( userVo == null ) {
			return retMap;
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
			return retMap;
		}
		
		//--------------------------------------------------
		// JWT 토큰 생성
		//--------------------------------------------------
		String sDeviceId = paramCollector.getHeardMap().getString("device_id");
		String sAutoLogin = "N";
		JwtToken jwtToken = jwtTokenProvider.generateToken(userVo, sAutoLogin, sProfile, sDeviceId);

		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.SUCCESS.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.SUCCESS.getMessage());
		
		String sRefreshToken = jwtToken.refreshToken;
		String sAccessToken = jwtToken.accessToken;
		Date date = jwtTokenProvider.getExpirationFromJwt(sAccessToken);
		
		String sJwtTokenType = commProp.getProperty("jwt.token.type");
		
		retMap.put(Constants.Jwt.TOKEN_TYPE, 		sJwtTokenType);
		retMap.put(Constants.Jwt.REFRESH_TOKEN, 	sRefreshToken);
		retMap.put(Constants.Jwt.ACCESS_TOKEN, 		sAccessToken);
		retMap.put(Constants.Jwt.EXPIRES_IN, 		(date.getTime() / 1000));
		
		return retMap;
	}
	
}
