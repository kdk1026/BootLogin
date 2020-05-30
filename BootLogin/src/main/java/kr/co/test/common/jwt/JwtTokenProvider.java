package kr.co.test.common.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.LogDeclare;
import common.util.json.GsonUtil;
import common.util.properties.PropertiesUtil;
import common.util.sessioncookie.SessionUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import kr.co.test.common.Constants;
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
public class JwtTokenProvider extends LogDeclare {

	private Properties commProp;
	private String profile;

	public JwtTokenProvider(String sProfile) {
		this.commProp = PropertiesUtil.getPropertiesClasspath("common.properties");
		this.profile = sProfile;
	}
	
	public static class JwtToken {
		public String accessToken;
		public String refreshToken;
	}
	
// ------------------------------------------------------------------------
// 토큰 생성
// ------------------------------------------------------------------------
	/**
	 * JWT 토큰 생성
	 * @param user
	 * @param sAutoLogin
	 * @param sDomain
	 * @param sDeviceId
	 */
	public JwtToken generateToken(UserVo user, String sAutoLogin, String sDomain, String sDeviceId) {
		JwtToken jwtToken = new JwtToken();
		jwtToken.accessToken = this.generateAccessToken(user, sAutoLogin, sDomain, sDeviceId);
		jwtToken.refreshToken = this.generateRefreshToken(user, sAutoLogin, sDomain, sDeviceId);
		return jwtToken; 
	}
	
	/**
	 * Access 토큰 생성
	 * @param user
	 * @param sAutoLogin
	 * @param sDomain
	 * @param sDeviceId
	 * @return
	 */
	public String generateAccessToken(UserVo user, String sAutoLogin, String sDomain, String sDeviceId) {
//		String sExpireTime = commProp.getProperty("jwt.access.expire.day");
		String sExpireTime = commProp.getProperty("jwt.access.expire.minute");
		
		if ( StringUtils.isBlank(sDeviceId) ) {
			sExpireTime = commProp.getProperty("jwt.web.access.expire.hour");
		}
		
		// XXX : API RestClient 테스트 시, 1년 설정 권장
		/*
		if ( StringUtils.isEmpty(sDeviceId) && "Y".equals(sAutoLogin) ) {
//			sExpireTime = "365";		// 1년
			sExpireTime = "1440";		// 1일
		}
		*/
		
		JwtBuilder builder = Jwts.builder();
		builder.setId(user.getId())
	 		.setIssuedAt(new Date())
	 		.setSubject(this.generateSubject(Constants.Jwt.ACCESS_TOKEN))
	 		.setIssuer(sDomain)
	 		.signWith(SignatureAlgorithm.HS256, commProp.getProperty("jwt.secret.key." + this.profile))
	 		.setExpiration( this.getExpirationTime(sExpireTime, sDeviceId) );
		
		String sUserJson = GsonUtil.ToJson.converterObjToJsonStr(user);
		
		builder.claim(Constants.Jwt.TOKEN_KIND, Constants.Jwt.ACCESS_TOKEN);
		builder.claim(SessionUtils.LoginInfo.SESSION_KEY, sUserJson);
	 	builder.claim(Constants.IsApp.DEVICE_ID_KEY, sDeviceId);
	 	
	 	return builder.compact();
	}
	
	/**
	 * Refresh 토큰 생성
	 * @param user
	 * @param sAutoLogin
	 * @param sDomain
	 * @param sDeviceId
	 * @return
	 */
	public String generateRefreshToken(UserVo user, String sAutoLogin, String sDomain, String sDeviceId) {
//		String sExpireTime = commProp.getProperty("jwt.refresh.expire.day");
		String sExpireTime = commProp.getProperty("jwt.refresh.expire.minute");
		
		JwtBuilder builder = Jwts.builder();
		builder.setId(user.getId())
	 		.setIssuedAt(new Date())
	 		.setSubject(this.generateSubject(Constants.Jwt.REFRESH_TOKEN))
	 		.setIssuer(sDomain)
	 		.signWith(SignatureAlgorithm.HS256, commProp.getProperty("jwt.secret.key." + this.profile))
	 		.setExpiration( this.getExpirationTime(sExpireTime, sDeviceId) );
		
		String sUserJson = GsonUtil.ToJson.converterObjToJsonStr(user);
		
		builder.claim(Constants.Jwt.TOKEN_KIND, Constants.Jwt.REFRESH_TOKEN);
		builder.claim(SessionUtils.LoginInfo.SESSION_KEY, sUserJson);
	 	builder.claim(Constants.IsApp.DEVICE_ID_KEY, sDeviceId);
	 	
		return builder.compact();
	}
	
	/**
	 * 만료일 계산
	 * @param sExpireIn
	 * @param sDeviceId
	 * @return
	 */
	private Date getExpirationTime(String sExpireIn, String sDeviceId) {
		int nExpireIn = Integer.parseInt(sExpireIn);
		Date date = null;
		
		/*
		 * Java 8 이하
		 *  - joda-time 라이브러리 사용 권장
		 *  - date = DateTime.now().plusDays(nExpireIn).toDate();
		 *  - date = DateTime.now().plusHours(nExpireIn).toDate();
		 *  - date = DateTime.now().plusMinutes(nExpireIn).toDate();
		 */
		
		LocalDateTime localDateTime = null;
		
//		localDateTime = LocalDateTime.now().plusDays(nExpireIn);
//		date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		
		if ( StringUtils.isBlank(sDeviceId) ) {
			localDateTime = LocalDateTime.now().plusHours(nExpireIn);
			date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		} else {
			localDateTime = LocalDateTime.now().plusMinutes(nExpireIn);
			date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		}
		
		return date;
    }
	
	/**
	 * 토큰 Subject 구성
	 * @param sTokenKind
	 * @return
	 */
	private String generateSubject(String sTokenKind) {
		StringBuilder sb = new StringBuilder();
		sb.append(commProp.getProperty("jwt.subject.prefix"));
		sb.append(" - ").append(sTokenKind);
		return sb.toString();
	}
	
// ------------------------------------------------------------------------
// 토큰 가져오기
// ------------------------------------------------------------------------
	/**
	 * 헤더에서 JWT 토큰 추출 
	 * @param request
	 * @return
	 */
	public String getTokenFromReqHeader(HttpServletRequest request) {
		String sToken = null;
		String sAuth = request.getHeader(commProp.getProperty("jwt.header"));
		String sTokenType = commProp.getProperty("jwt.token.type");
		
		if ( !StringUtils.isBlank(sAuth) && sAuth.startsWith(sTokenType) ) {
			sToken = sAuth.substring(sTokenType.length());
		}
		return sToken;
	}
	
	/**
	 * JWT 토큰 유효성 검증
	 * @param token
	 * @return (0: false, 1: true, 2: expired)
	 */
	public int isValidateJwtToken(String sToken) {
		try {
			Jwts.parser().setSigningKey(commProp.getProperty("jwt.secret.key." + this.profile)).parseClaimsJws(sToken);
			return 1;
			
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature");
			
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token");
			
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
			return 2;
			
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
			
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		
		return 0;
	}
	
	/**
	 * JWT 토큰에서 만료일 가져오기
	 * @param token
	 * @param sProfile
	 * @return
	 */
	public Date getExpirationFromJwt(String sToken) {
		Claims claims = Jwts.parser()
				.setSigningKey(commProp.getProperty("jwt.secret.key." + this.profile))
				.parseClaimsJws(sToken)
				.getBody();
		
		return claims.getExpiration();
	}
	
	/**
	 * JWT 토큰 유형 가져오기
	 * @param sToken
	 * @return
	 */
	public String getTokenKind(String sToken) {
		Claims claims = Jwts.parser()
				.setSigningKey(commProp.getProperty("jwt.secret.key." + this.profile))
				.parseClaimsJws(sToken)
				.getBody();
		
		return String.valueOf( claims.get(Constants.Jwt.TOKEN_KIND) );
	}
	
// ------------------------------------------------------------------------
// 토큰에서 로그인 정보 추출
// ------------------------------------------------------------------------
	/**
	 * JWT 토큰에서 로그인 정보 가져오기
	 * @param token
	 * @return
	 */
	public UserVo getAuthUserFromJwt(HttpServletRequest request) {
		UserVo user = null;
		
		if (request != null) {
			String sToken = this.getTokenFromReqHeader(request);
			user = this.getAuthUserFromJwt(sToken);
		}
		
		return user;
	}
	
	/**
	 * JWT 토큰에서 로그인 정보 가져오기
	 * @param token
	 * @return
	 */
	public UserVo getAuthUserFromJwt(String sToken) {
		UserVo user = null;
		
		if ( !StringUtils.isEmpty(sToken) ) {
			Claims claims = Jwts.parser()
					.setSigningKey(commProp.getProperty("jwt.secret.key." + this.profile))
					.parseClaimsJws(sToken)
					.getBody();
			
			String sUserJson = String.valueOf(claims.get(SessionUtils.LoginInfo.SESSION_KEY));
			
			// 토큰에서 추출한 JSON 값을 객체로 변환 
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			user = gson.fromJson(sUserJson, UserVo.class);
		}
		
		return user;
	}
	
}
