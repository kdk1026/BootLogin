package kr.co.test.service.jwt;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.util.date.Jsr310DateUtil;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;
import kr.co.test.common.Constants;
import kr.co.test.common.jwt.JwtTokenProvider;
import kr.co.test.model.UserVo;

/**
 * @since 2018. 12. 30.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 30. 김대광	최초작성
 * </pre>
 */
@Service
public class JwtRefreshService extends LogDeclare {

	@Autowired
	private Environment env;
	
	@Value("#{common}")
	private Properties commProp;
	
	/**
	 * <pre>
	 * 토큰 갱신 처리
	 *  - AccessToken 갱신
	 *  - 조건 충족 시, RefreshToken 갱신
	 * </pre>
	 * @param paramCollector
	 * @return
	 */
	public ParamMap processApiTokenRefresh(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		String sProfile = env.getActiveProfiles()[0];
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(sProfile);
		
		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.LOGIN_INVALID.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.LOGIN_INVALID.getMessage());
		
		String sRefreshToken = paramCollector.getString(Constants.Jwt.REFRESH_TOKEN);

		//--------------------------------------------------
		// 토큰 유효성 검증
		//--------------------------------------------------
		retMap = this.validToken(paramCollector, sRefreshToken);
		
		if ( !ResponseCodeEnum.SUCCESS.getCode().equals(retMap.get(Constants.RES.RES_CD)) ) {
			return retMap;
		}
		
		// ------------------------------------------------------------------------
		// 토큰에서 사용자 정보 추출하여 검증
		// ------------------------------------------------------------------------
		UserVo user = jwtTokenProvider.getAuthUserFromJwt(sRefreshToken);
		
		String sDeviceId = paramCollector.getHeardMap().getString("device_id");
		
		// ------------------------------------------------------------------------
		// Access 토큰 갱신
		// ------------------------------------------------------------------------
		String sAutoLogin = "Y";
		String sAccessToken = jwtTokenProvider.generateAccessToken(user, sAutoLogin, sRefreshToken, sDeviceId);
		
		Date date = jwtTokenProvider.getExpirationFromJwt(sAccessToken);
		
		String sJwtTokenType = commProp.getProperty("jwt.token.type");
		
		retMap.put(Constants.Jwt.TOKEN_TYPE, 	sJwtTokenType);
		retMap.put(Constants.Jwt.ACCESS_TOKEN, 	sAccessToken);
		retMap.put(Constants.Jwt.EXPIRES_IN, 	(date.getTime() / 1000));
		
		// ------------------------------------------------------------------------
		// Refresh 토큰 갱신 조건 검증
		// ------------------------------------------------------------------------
		date = jwtTokenProvider.getExpirationFromJwt(sRefreshToken);
		
		String sExpireDate = Jsr310DateUtil.Convert.getDateToString(date);
		int nGap = Jsr310DateUtil.GetDateInterval.intervalDays(sExpireDate);

		// ------------------------------------------------------------------------
		// Refresh 토큰 갱신
		// ------------------------------------------------------------------------
		String sToken = "";
		
		if (nGap >= -7 && nGap <= 0) {
			// 조건 충족 시, Refresh 토큰 갱신
			sToken = jwtTokenProvider.generateRefreshToken(user, sAutoLogin, sRefreshToken, sDeviceId);
		} else {
			// 조건 미충족 시, 파라미터 응답
			sToken = paramCollector.getString(Constants.Jwt.REFRESH_TOKEN);
		}
		
		retMap.put(Constants.Jwt.REFRESH_TOKEN, sToken);
		
		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.SUCCESS.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.SUCCESS.getMessage());
		return retMap;
	}
	
	/**
	 * <pre>
	 * 토큰 갱신 시, 유효성 체크
	 *  - 인터셉터 체크 제외
	 *  - 헤더 없이 바디로만 요청하므로
	 * </pre>
	 * @param paramCollector
	 * @param sRefreshToken
	 * @return
	 */
	private ParamMap validToken(ParamCollector paramCollector, String sRefreshToken) {
		ParamMap retMap = new ParamMap();
		
		String sProfile = env.getActiveProfiles()[0];
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(sProfile);
		
		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
		
		// ------------------------------------------------------------------------
		// 토큰 유효성 검증
		// ------------------------------------------------------------------------
		int nValid = jwtTokenProvider.isValidateJwtToken(sRefreshToken);
		
		if ( nValid == 0 ) {
			logger.info("[API Jwt] - InValid RefreshToken");
			return retMap;
		}
		else if ( nValid == 2 ) {
			retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.REFRESH_TOKEN_EXPIRED.getCode());
			retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.REFRESH_TOKEN_EXPIRED.getMessage());
			return retMap;
		}
		
		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, refresh_token인지 검증
		// ------------------------------------------------------------------------
		String sTokenKind = jwtTokenProvider.getTokenKind(sRefreshToken);
		
		if ( !Constants.Jwt.REFRESH_TOKEN.equals(sTokenKind) ) {
			logger.info("[API Jwt] - Is Not RefreshToken");
			return retMap;
		}
		
		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, 사용자 정보 추출하여 검증
		// ------------------------------------------------------------------------
		UserVo user = jwtTokenProvider.getAuthUserFromJwt(sRefreshToken);
		
		if ( (user == null) || (StringUtils.isBlank(user.getId())) ) {
			logger.info("[API Jwt] - UserInfo null");
			return retMap;
		}
		
		retMap.put(Constants.RES.RES_CD, 	ResponseCodeEnum.SUCCESS.getCode());
		retMap.put(Constants.RES.RES_MSG, 	ResponseCodeEnum.SUCCESS.getMessage());
		return retMap;
	}
	
}
