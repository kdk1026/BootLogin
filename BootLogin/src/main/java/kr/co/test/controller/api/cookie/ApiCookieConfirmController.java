package kr.co.test.controller.api.cookie;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.LogDeclare;
import common.util.crypto.AesCryptoUtil;
import common.util.map.MapUtil;
import common.util.map.ParamMap;
import common.util.sessioncookie.CookieUtilVer2;
import common.util.sessioncookie.SessionUtils;
import config.mvc.resolver.ParamCollector;
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
@RestController
@RequestMapping("api/cookie")
public class ApiCookieConfirmController extends LogDeclare {
	
	@Value("#{common['cookie.encryption.key']}")
	private String sCookieEncryptionKey;

	@PostMapping("/confirm")
	public ParamMap confirm(ParamCollector paramCollector) {
		ParamMap retMap = new ParamMap();
		
		HttpServletRequest request = paramCollector.getRequest();

		String sEnUserJson = CookieUtilVer2.getCookieValue(request, SessionUtils.LoginInfo.SESSION_KEY);
		
		//--------------------------------------------------
		// AES 128 복호화
		//--------------------------------------------------
		String sUserJson = AesCryptoUtil.aesDecrypt(sCookieEncryptionKey, AesCryptoUtil.AES_CBC_PKCS5PADDING, sEnUserJson);
		
		//--------------------------------------------------
		// JSON String을 객체로 변환
		//--------------------------------------------------
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		UserVo user = gson.fromJson(sUserJson, UserVo.class);

		Map<String, String> map = MapUtil.objectToMap(user);
		map.remove("pw");
		
		retMap.putAll(map);
		
		return retMap;
	}
	
}
