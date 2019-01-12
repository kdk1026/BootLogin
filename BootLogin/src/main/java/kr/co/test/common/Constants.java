package kr.co.test.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @since 2018. 12. 29.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 29. 김대광	최초작성
 * </pre>
 */
public class Constants {

	// ------------------------------------------------------------------------
	// 인코딩
	// ------------------------------------------------------------------------
	public static class Encoding {
		public static final String UTF_8 = StandardCharsets.UTF_8.name();
		public static final String EUC_KR = Charset.forName("EUC-KR").toString();
	}
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// 프로파일
	// ------------------------------------------------------------------------
	public static class Profile {
		public static final String LOCAL = "local";
		public static final String DEV = "dev";
		public static final String PROD = "prod";
	}
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	// 앱 식별
	// ------------------------------------------------------------------------
	public static class IsApp {
		public static final String DEVICE_ID_KEY = "device_id";
	}
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	// JWT 토큰
	// ------------------------------------------------------------------------
	public static class Jwt {
		public static final String TOKEN = "token";
		public static final String TOKEN_KIND = "token_kind";
		public static final String ACCESS_TOKEN = "access_token";
		public static final String REFRESH_TOKEN = "refresh_token";
		public static final String TOKEN_TYPE = "token_type";
		public static final String EXPIRES_IN = "expires_in";
	}
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	// 응답코드, 응답메시지
	// ------------------------------------------------------------------------
	public static class RES {
		public static final String RES_CD 					= "res_cd";
		public static final String RES_MSG 					= "res_msg";
	}
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	// 쿠키
	// ------------------------------------------------------------------------
	public static class Cookie {
		public static final String IS_AUTO_LOGIN 			= "is_auto_login";
	}
	// ------------------------------------------------------------------------
	
}
